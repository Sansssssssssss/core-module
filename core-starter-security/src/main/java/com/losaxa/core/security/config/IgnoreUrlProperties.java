package com.losaxa.core.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "app.security.ignore")
public class IgnoreUrlProperties {

    /**
     * 默认忽略验证地址
     */
    private static final String[] SECURITY_ENDPOINTS = {
            "/auth/**",
            "/oauth/token",
            "/login",
            "/actuator/**",
            "/doc.html",
            "/webjars/**",
            "/favicon.ico",
            "/swagger-ui/**",
            "/v*/api-docs",
            "/error",
            "/swagger-resources/**"
    };

    private List<String> urls = new ArrayList<>();

    /**
     * 首次加载合并 默认忽略验证地址
     */
    @PostConstruct
    public void initIgnoreSecurity() {
        Collections.addAll(urls, SECURITY_ENDPOINTS);
    }

}
