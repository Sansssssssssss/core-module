package com.losaxa.core.web.converter;

import com.losaxa.core.common.constant.Const;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;

public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(String source) {
        return LocalDateTime.parse(source, Const.Data.YMDHMSS_FORMATTER);
    }
}
