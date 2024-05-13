package com.losaxa.core.web;

import org.springframework.data.domain.Page;

import java.util.List;

public class Result<T> {

    private int code;

    private boolean success;

    private long total;

    private String message;

    private T data;

    public static Result<Void> ok() {
        Result<Void> voidResult = new Result<>();
        ok(voidResult);
        return voidResult;
    }

    public static <E> Result<E> ok(E data) {
        Result<E> result = new Result<>();
        ok(result);
        if (data == null) {
            result.total = 0;
        } else {
            result.total = 1;
        }
        result.data = data;
        return result;
    }

    public static <E> Result<List<E>> ok(List<E> data) {
        Result<List<E>> result = new Result<>();
        ok(result);
        result.total = data.size();
        result.data = data;
        return result;
    }

    public static <E> Result<E[]> ok(E[] data) {
        Result<E[]> result = new Result<>();
        ok(result);
        result.total = data.length;
        result.data = data;
        return result;
    }

    public static <E> Result<List<E>> ok(Page<E> data) {
        Result<List<E>> result = new Result<>();
        ok(result);
        result.total = data.getTotalElements();
        result.data = data.getContent();
        return result;
    }

    public static Result<Void> fail999(String msg) {
        Result<Void> result = new Result<>();
        fail(ResultCode.FAIL.getValue(), msg, result);
        return result;
    }

    public static Result<Void> fail(int code,
                                    String msg) {
        Result<Void> result = new Result<>();
        fail(code, msg, result);
        return result;
    }

    public static Result<Void> fail(ResultCode code) {
        Result<Void> result = new Result<>();
        fail(code.getValue(), code.getDisplay(), result);
        return result;
    }

    private static void fail(int code,
                             String msg,
                             Result<Void> result) {
        result.code = code;
        result.message = msg;
        result.success = false;
    }

    public static Result<Void> fallback() {
        return fail(ResultCode.FALLBACK);
    }

    public static Result<Void> fallback(String msg) {
        return fail(ResultCode.FALLBACK.getValue(), msg);
    }

    private static void ok(Result<?> result) {
        result.message = ResultCode.SUCCESS.getDisplay();
        result.success = true;
        result.code = ResultCode.SUCCESS.getValue();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
