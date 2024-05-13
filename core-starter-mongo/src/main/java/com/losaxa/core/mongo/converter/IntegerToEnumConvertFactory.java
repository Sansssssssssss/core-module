package com.losaxa.core.mongo.converter;

import com.losaxa.core.common.enums.EnumUtils;
import com.losaxa.core.common.enums.IEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import javax.annotation.Nullable;

public class IntegerToEnumConvertFactory implements ConverterFactory<Integer, IEnum<Integer>> {

    @Override
    public <T extends IEnum<Integer>> Converter<Integer, T> getConverter(@Nullable Class<T> targetType) {
        return new IntegerToEnumConverter(targetType);
    }

    public static class IntegerToEnumConverter<T extends Enum<?> & IEnum<Integer>> implements Converter<Integer,  IEnum<Integer>> {

        private final Class<T> enumType;

        public IntegerToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public  IEnum<Integer> convert(@Nullable Integer source) {
            return EnumUtils.valueOf(enumType, source);
        }

    }
}
