package com.losaxa.core.autoservice.support;

/**
 * 实体数据初始化钩子
 */
public interface DoDataInit {

    default void beforeInsertDataInit(){}

}
