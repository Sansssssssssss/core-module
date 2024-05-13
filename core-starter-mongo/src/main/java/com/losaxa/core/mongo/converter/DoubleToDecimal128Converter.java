package com.losaxa.core.mongo.converter;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.math.BigDecimal;

@WritingConverter
@ReadingConverter
public class DoubleToDecimal128Converter implements Converter<Double, Decimal128> {
	public Decimal128 convert(Double dobule) {
        return new Decimal128(BigDecimal.valueOf(dobule));
    }
}