package com.losaxa.core.web;

import com.losaxa.core.common.enums.IEnum;
import lombok.Getter;

@Getter
public enum ResultCode implements IEnum<Integer> {

    SUCCESS(200, "success"),
    UNAUTHORIZED(401, "未经认证"),
    FORBIDDEN(403,"未经授权"),
    /**
     * 系统错误/未知错误;注:不包含业务错误
     */
    SYSTEM_ERROR(500,"系统异常"),
    FALLBACK(501,"服务异常回滚"),
    /**
     * 业务失败/已知错误
     */
     FAIL(999,"操作失败"),
    ;

    private final Integer value;

    private final String display;


    ResultCode(Integer value,
               String display) {
        this.value = value;
        this.display = display;
    }
}
