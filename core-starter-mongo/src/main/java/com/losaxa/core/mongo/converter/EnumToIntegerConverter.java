package com.losaxa.core.mongo.converter;

import com.losaxa.core.common.enums.IEnum;
import org.springframework.core.convert.converter.Converter;

public class EnumToIntegerConverter implements Converter<IEnum<Integer>, Integer> {
    @Override
    public Integer convert(IEnum<Integer> source) {
        return source.getValue();
    }
}
