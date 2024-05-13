package com.losaxa.core.feign.config;

import com.losaxa.core.common.JsonUtil;
import com.losaxa.core.common.constant.Const;
import com.losaxa.core.web.RequestHolder;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Feign 基础配置
 */
@Configuration
public class FeignConfig {

    /**
     * 配置 traceId 追踪Id
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String traceId = MDC.get(Const.TRACE_ID);
            if (StringUtils.isNotBlank(traceId)) {
                requestTemplate.header(Const.TRACE_ID, traceId);
                return;
            }
            Optional<HttpServletRequest> request = RequestHolder.getRequest();
            if (!request.isPresent()) {
                return;
            }
            traceId = request.get().getHeader(Const.TRACE_ID);
            requestTemplate.header(Const.TRACE_ID, traceId);
        };
    }

}
