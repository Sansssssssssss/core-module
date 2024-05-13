package com.losaxa.core.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.losaxa.core.common.JsonUtil;
import com.losaxa.core.web.filter.TraceFilter;
import com.losaxa.core.web.filter.WebLogFilter;
import com.losaxa.core.web.converter.IntToEnumConverter;
import com.losaxa.core.web.converter.StringToLocalDateTimeConverter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;

import java.time.LocalDateTime;

/**
 * MVC 相关配置
 */
@Configuration
@ComponentScan("com.losaxa.core.web")
public class CoreWebAutoConfig {

    /**
     * json 配置
     *
     * @return
     */
    @Bean
    public ObjectMapper jsonMapperJava8DateTimeModule() {
        return JsonUtil.OM;
    }

    /**
     * 表单提交 字符串转时间
     *
     * @return
     */
    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new StringToLocalDateTimeConverter();
    }


    /**
     * 表单提交 int/string转enum
     *
     * @return
     */
    @Bean
    public GenericConverter intToEnumConverter(){
        return new IntToEnumConverter();
    }

    /**
     * 请求 追踪id 配置
     * @return
     */
    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilterRegistrationBean() {
        FilterRegistrationBean<TraceFilter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter(new TraceFilter());
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setOrder(1);
        filterRegistration.setName("traceFilter");
        return filterRegistration;
    }

    /**
     * 请求 追踪id 配置
     * @return
     */
    @Bean
    public FilterRegistrationBean<WebLogFilter> webLogFilterRegistrationBean() {
        FilterRegistrationBean<WebLogFilter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter(new WebLogFilter());
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setOrder(2);
        filterRegistration.setName("webLogFilter");
        return filterRegistration;
    }
}
