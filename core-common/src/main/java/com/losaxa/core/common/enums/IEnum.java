package com.losaxa.core.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 枚举基础接口
 * @param <T>
 */
public interface IEnum<T> {

    @JsonValue
    T getValue();

    default String getDisplay(){
        throw new UnsupportedOperationException();
    }

    //@JsonValue
    //default HashMap<String, Object> toJson() {
    //    HashMap<String, Object> map = new HashMap<String, Object>(2);
    //    map.put("value", getValue());
    //    map.put("dispaly", getDisplay());
    //    return map;
    //}

}
