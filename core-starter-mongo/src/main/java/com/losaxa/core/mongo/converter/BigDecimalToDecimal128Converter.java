package com.losaxa.core.mongo.converter;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.math.BigDecimal;

@WritingConverter
@ReadingConverter
public class BigDecimalToDecimal128Converter implements Converter<BigDecimal, Decimal128> {
	public Decimal128 convert(BigDecimal bigDecimal) {
        return new Decimal128(bigDecimal);
    }
}