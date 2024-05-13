package com.losaxa.core.security.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 系统安全配置,系统用户认证鉴权配置
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(IgnoreUrlProperties.class)
@ComponentScan("com.losaxa.core.security")
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    /**
     * 配置密码加密方式
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 必须要定义，否则oauth不支持grant_type=password模式
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //由于 ResourceServerConfig 已经进行了配置,所以在此处不需要再配置
        //ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config
        //        = http.requestMatchers().anyRequest()
        //        .and()
        //        //允许表单登录
        //        .formLogin()
        //        //登录成功时,返回json信息
        //        .successHandler(new CoreAuthenticationSuccessHandler())
        //        //登录失败时,返回json错误信息
        //        .failureHandler(new CoreAuthenticationFailureHandler())
        //        .and()
        //        //拦截请求认证鉴权
        //        .authorizeRequests();
        //ignoreUrlProperties.getUrls().forEach(url -> {
        //    config.antMatchers(url).permitAll();
        //});
        //config
        //        //任何请求
        //        .anyRequest()
        //        //都需要身份认证
        //        .authenticated()
        //        //csrf跨站请求
        //        .and()
        //        .csrf().disable()
        //        //没有登录/认证失败时,返回json错误信息,覆盖默认的登录页面
        //        .exceptionHandling().authenticationEntryPoint(new CoreAuthenticationEntryPoint());
    }


}
