package com.losaxa.core.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
public class ErrorController extends BasicErrorController {

    public ErrorController(ErrorAttributes errorAttributes,
                           ServerProperties serverProperties) {
        super(errorAttributes, serverProperties.getError());
    }

    @RequestMapping
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body    = super.getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        Object              errMsg  = body.get("error");
        Object              msg     = body.get("message");
        boolean             error   = errMsg != null;
        boolean             message = msg != null;
        String              msgStr  = error && message ? errMsg + " , " + msg : (error ? errMsg : message ? msg : "no msg") + "";
        log.error("{} {} {} {}", request.getRequestURI(), request.getMethod(), this.getStatus(request), msgStr);
        return new ResponseEntity(Result.fail((int) body.get("status"), msgStr), this.getStatus(request));
    }

}
