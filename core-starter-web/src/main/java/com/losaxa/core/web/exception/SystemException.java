package com.losaxa.core.web.exception;

public class SystemException extends RuntimeException {

    private SystemException(String message) {
        super(message);
    }

    public static  SystemException newInstance(String msg){
        return new SystemException(msg);
    }

}
