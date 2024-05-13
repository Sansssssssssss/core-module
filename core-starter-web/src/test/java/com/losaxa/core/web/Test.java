package com.losaxa.core.web;

import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

public class Test {

    @org.junit.Test
    public void test(){
        Result<String> ok    = Result.ok("");
        List<String> strings = new ArrayList<>();
        Result<List<String>> ok1 = Result.ok(strings);
        Result<List<String>> ok2 = Result.ok(new PageImpl<>(strings));
        String[] strings1 = {"1"};
        Result<String[]> ok3 = Result.ok(strings1);
    }

}
