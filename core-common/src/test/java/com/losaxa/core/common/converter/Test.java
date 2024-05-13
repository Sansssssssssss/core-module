package com.losaxa.core.common.converter;

public class Test {


    @org.junit.Test
    public void test(){
        ExampleConverter instances = ExampleConverter.INSTANCES;
        ExampleConverter.Source source = new ExampleConverter.Source();
        source.field = "12356";
        ExampleConverter.Target to = instances.to(source);
        System.out.println();
    }

}
