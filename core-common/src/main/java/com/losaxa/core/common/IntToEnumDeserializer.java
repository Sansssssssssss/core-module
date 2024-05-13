package com.losaxa.core.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.losaxa.core.common.enums.EnumUtils;

import java.io.IOException;

/**
 * int value 转枚举类型
 */
public class IntToEnumDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {

    private Class enumClass;

    private IntToEnumDeserializer(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
    }

    public IntToEnumDeserializer() {
    }

    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Integer value = Integer.valueOf(p.getText());
        return EnumUtils.valueOf(enumClass, value);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        return new IntToEnumDeserializer((Class<? extends Enum<?>>) property.getType().getRawClass());
    }
}
