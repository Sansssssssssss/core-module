package com.losaxa.core.web.filter;

import com.losaxa.core.common.JsonUtil;
import com.losaxa.core.security.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * web日志打印过滤器
 */
public class WebLogFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper  requestWrapper  = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        long                          start           = System.currentTimeMillis();
        filterChain.doFilter(requestWrapper, responseWrapper);
        String r = getResultStr(response, responseWrapper);
        responseWrapper.copyBodyToResponse();
        log.info("\n{} {} Start\n" +
                        "n= {}\n" +
                        "u= {}\n" +
                        "p= {}\n" +
                        "b= {}\n" +
                        "t= {}\n" +
                        "r= {}",
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr(),
                UserContextHolder.getId(),
                requestWrapper.getParameterMap().entrySet().stream().map(e -> e.getKey() + "=" + String.join(",", e.getValue())).collect(Collectors.joining("&")),
                new String(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding()),
                System.currentTimeMillis() - start,
                r
        );
    }

    private String getResultStr(HttpServletResponse response,
                                ContentCachingResponseWrapper responseWrapper) throws UnsupportedEncodingException {
        String rs = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (rs.startsWith("{") && rs.endsWith("}") && rs.contains("code")) {
            Optional<Map> r = JsonUtil.toObj(rs, Map.class);
            return r.map(result -> result.get("code") + " " + result.get("success") + " " + result.get("total") + " " + result.get("message")).orElse("");
        } else {
            return "";
        }
    }
}
