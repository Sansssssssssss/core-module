package com.losaxa.core.security.config;

import com.losaxa.core.security.CoreAccessDeniedHandler;
import com.losaxa.core.security.CoreAuthenticationEntryPoint;
import com.losaxa.core.security.OauthExceptionHandler;
import com.losaxa.core.security.ResourceUserAuthenticationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@ConditionalOnMissingBean(ResourceServerConfigurerAdapter.class)
@Configuration
@EnableResourceServer
@SuppressWarnings("all")
public class CoreResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private IgnoreUrlProperties ignoreUrlProperties;

    @Autowired
    private CoreAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CoreAccessDeniedHandler coreAccessDeniedHandler;

    @Autowired
    private OauthExceptionHandler exceptionHandler;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config
                = http.requestMatchers().anyRequest()
                .and()
                .authorizeRequests();
        ignoreUrlProperties.getUrls().forEach(url -> {
            config.antMatchers(url).permitAll();
        });
        config
                //任何请求
                .anyRequest()
                //都需要身份认证
                .authenticated()
                //.withObjectPostProcessor()
                .and()
                //禁用session
                .sessionManagement().disable()
                //禁用csrf跨站请求
                .csrf().disable()
                //没有登录/认证失败时,返回json错误信息,覆盖默认的登录页面
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        //region 资源服务oauth异常自定义json返回
        OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        oAuth2AuthenticationEntryPoint.setExceptionTranslator(exceptionHandler);
        resources.authenticationEntryPoint(oAuth2AuthenticationEntryPoint);
        //endregion

        //region 解析自定义 access 时的 el ; 解决 EL1057E: No bean resolver registered in the context to resolve access to bean 问题
        OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        resources.expressionHandler(expressionHandler);
        //endregion

        resources
                //配置token存储必须与授权服务的存储位置一致
                //.tokenStore(tokenStore)
                //权限不足,返回自定义json信息
                .accessDeniedHandler(coreAccessDeniedHandler);
    }

    /**
     * 配置自定义 AccessTokenConverter
     *
     * @param resource
     * @return
     */
    @Primary
    @Bean
    public RemoteTokenServices resourceRemoteTokenServices(ResourceServerProperties resource) {
        RemoteTokenServices         services             = new RemoteTokenServices();
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(new ResourceUserAuthenticationConverter());
        services.setAccessTokenConverter(accessTokenConverter);
        services.setCheckTokenEndpointUrl(resource.getTokenInfoUri());
        services.setClientId(resource.getClientId());
        services.setClientSecret(resource.getClientSecret());
        return services;
    }
}
