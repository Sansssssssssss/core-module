package com.losaxa.core.security;

import com.losaxa.core.web.ResponseUtil;
import com.losaxa.core.web.Result;
import com.losaxa.core.web.ResultCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理权限不足返回json信息
 */
@Component
public class CoreAccessDeniedHandler  implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ResponseUtil.writerJson(response, Result.fail(ResultCode.FORBIDDEN));
    }

}
