package com.losaxa.core.mongo.converter;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

@ReadingConverter
@WritingConverter
public class Decimal128ToDoubleConverter implements Converter<Decimal128, Double> {
	public Double convert(Decimal128 decimal128) {
		return decimal128.bigDecimalValue().doubleValue();
	}
}