package com.losaxa.core.security;

import com.losaxa.core.web.Result;
import com.losaxa.core.web.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

/**
 * oauth错误处理,返回自定义json信息
 */
@Component
@Slf4j
public class OauthExceptionHandler implements WebResponseExceptionTranslator {

    @Override
    public ResponseEntity<Result<Void>> translate(Exception e) {
        ResponseEntity.BodyBuilder status = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
        log.warn(e.getMessage(), e);
        return status.body(Result.fail(ResultCode.UNAUTHORIZED));
        //if (e instanceof InsufficientAuthenticationException) {
        //    if (e.getCause() instanceof InvalidTokenException) {
        //
        //    }
        //}
    }
}
