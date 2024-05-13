package com.losaxa.core.web.converter;

import com.losaxa.core.common.enums.EnumUtils;
import com.losaxa.core.common.enums.IEnum;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 表单提交枚举类型转换器
 */
public class IntToEnumConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<ConvertiblePair> convertiblePairs = new HashSet<>();
        convertiblePairs.add(new ConvertiblePair(String.class, IEnum.class));
        convertiblePairs.add(new ConvertiblePair(int.class, IEnum.class));
        return convertiblePairs;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class<IEnum> type = (Class<IEnum>) targetType.getType();
        return EnumUtils.getIenum(type, Integer.valueOf(source.toString()));
    }

}
