package com.losaxa.core.web;

import com.losaxa.core.web.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@ControllerAdvice
public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result<Void> exceptionHandle(Exception ex) {
        log.error(ex.getMessage(), ex);
        return Result.fail(ResultCode.FAIL.getValue(), ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public Result<Void> exceptionHandle(BindException ex) {
        List<ObjectError> allErrors  = ex.getAllErrors();
        StringBuilder     msgBuilder = new StringBuilder();
        for (ObjectError allError : allErrors) {
            FieldError fieldError = (FieldError) allError;
            msgBuilder.append(",")
                    .append(fieldError.getObjectName())
                    .append(".")
                    .append(fieldError.getField())
                    .append(":")
                    .append(fieldError.getDefaultMessage());
        }
        if (msgBuilder.length() > 0) {
            msgBuilder.delete(0, 1).insert(0, "[").append("]");
        }
        String msg = msgBuilder.toString();
        log.warn(msg);
        return Result.fail(ResultCode.FAIL.getValue(), msg);
    }

    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public Result<?> exceptionHandle(BusinessException ex) {
        log.error(ex.getMessage());
        return ex.getResult();
    }
}
