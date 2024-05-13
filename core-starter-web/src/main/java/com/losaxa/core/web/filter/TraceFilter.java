package com.losaxa.core.web.filter;

import com.losaxa.core.common.constant.Const;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 追踪Id 过滤器
 */
public class TraceFilter extends OncePerRequestFilter {

    /**
     * MDC 传入 追踪id
     * @param httpServletRequest
     * @param httpServletResponse
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String traceId = httpServletRequest.getParameter(Const.TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = httpServletRequest.getHeader(Const.TRACE_ID);
        }
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        try {
            MDC.put(Const.TRACE_ID, traceId);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            MDC.clear();
        }
    }

}
