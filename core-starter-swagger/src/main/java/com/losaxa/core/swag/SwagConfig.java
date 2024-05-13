package com.losaxa.core.swag;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static springfox.documentation.builders.PathSelectors.ant;

/**
 * swagger 基础配置
 */
@Configuration
@EnableSwagger2
@Import({SwagSchemaMapper.class, BeanValidatorPluginsConfiguration.class})
public class SwagConfig {

    @Bean
    public Docket apiDoc() {
        //已经配置全局 Authorization HEADER ,因此不需要再配置
        //RequestParameterBuilder requestParameterBuilder = new RequestParameterBuilder();
        //List<RequestParameter>  requestParameters       = new ArrayList<>();
        //requestParameterBuilder.name("Authorization").description("认证令牌").in(ParameterType.HEADER).required(false).query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)));
        //requestParameters.add(requestParameterBuilder.build());
        return new Docket(DocumentationType.OAS_30)
                .select()
                .paths(ant("/**")
                        .and(ant("/error").negate())
                        .and(ant("/**/error").negate())
                        .and(ant("/oauth/authorize").negate())
                        .and(ant("/oauth/token_key").negate())
                        .and(ant("/oauth/confirm_access").negate())
                        .and(ant("/actuator/**").negate())
                ).build()
                .securitySchemes(apiKeyList())
                .securityContexts(securityContextList())
                .ignoredParameterTypes(Map.class)
                .ignoredParameterTypes(Principal.class)
                //.globalRequestParameters(requestParameters)
                //.directModelSubstitute(LocalDateTime.class, String.class)
                .apiInfo(new ApiInfoBuilder()
                        .title("接口文档")
                        .description("接口文档")
                        .version("1.0")
                        .build())
                .alternateTypeRules();
    }


    private List<SecurityScheme> apiKeyList() {
        return Collections.singletonList(new ApiKey("Authorization", "Authorization", "header"));
    }

    private List<SecurityContext> securityContextList() {
        return Collections.singletonList(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(ant("/**")
                        .and(ant("/oauth/**").negate())
                        .and(ant("/error").negate())
                        .and(ant("/**/error").negate())).build());
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(
                new SecurityReference("Authorization", authorizationScopes));
    }

}
