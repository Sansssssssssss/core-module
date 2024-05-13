package com.losaxa.core.web.exception;

import com.losaxa.core.web.Result;

public class BusinessException extends RuntimeException {

    private Result<?> result;

    private BusinessException() {
    }

    private BusinessException(String message,
                              Result<?> result) {
        super(message);
        this.result = result;
    }

    public static BusinessException newInstance(String message) {
        return new BusinessException(message, Result.fail999(message));
    }

    public static BusinessException newInstance(String message,
                                                int code) {
        return new BusinessException(message, Result.fail(code, message));
    }

    public Result<?> getResult() {
        return result;
    }
}
