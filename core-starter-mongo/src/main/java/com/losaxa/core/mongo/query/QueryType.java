package com.losaxa.core.mongo.query;

public enum QueryType {


    /**
     * 大于
     */
    $gt,

    /**
     * 大于等于
     */
    $gte,

    /**
     * 小于
     */
    $lt,

    /**
     * 小于等于
     */
    $lte,

    /**
     * 模糊匹配
     */
    $regex,

    /**
     * 降序
     */
    $desc,

    /**
     * 升序
     */
    $asc,

    /**
     * 忽略
     */
    $ignore,
    ;
}
