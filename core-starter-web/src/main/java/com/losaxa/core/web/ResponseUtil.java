package com.losaxa.core.web;

import com.losaxa.core.common.JsonUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {

    private ResponseUtil() {
    }

    public static void writerJson(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(JsonUtil.toJson(obj));
    }
}
