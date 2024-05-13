package com.losaxa.core.web;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 当前请求持有者
 */
public class RequestHolder {

    private RequestHolder() {
    }

    public static Optional<HttpServletRequest> getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return Optional.empty();
        }
        return Optional.of(((ServletRequestAttributes) requestAttributes).getRequest());
    }

}
