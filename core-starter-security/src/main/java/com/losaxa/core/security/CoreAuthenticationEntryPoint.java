package com.losaxa.core.security;

import com.losaxa.core.web.ResponseUtil;
import com.losaxa.core.web.Result;
import com.losaxa.core.web.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 没有登录/认证失败时,返回json错误信息,覆盖默认的登录页面
 */
@Component
@Slf4j
public class CoreAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException, ServletException {
        //if (e instanceof BadCredentialsException) {
        //}
        log.warn(e.getMessage());
        if (e instanceof InsufficientAuthenticationException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ResponseUtil.writerJson(response, Result.fail(ResultCode.UNAUTHORIZED));
            return;
        }
        ResponseUtil.writerJson(response, Result.fail999(e.getMessage()));
        //response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //ResponseUtil.writerJson(response, Result.fail(ResultCode.UNAUTHORIZED));
    }
}
