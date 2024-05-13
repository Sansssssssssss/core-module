package com.losaxa.core.common.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExampleConverter extends ObjectConverter<ExampleConverter.Source, ExampleConverter.Target> {
    ExampleConverter INSTANCES = Mappers.getMapper(ExampleConverter.class);

    class Target {
        public String field;
    }

    class Source {
        public String field;
    }
}


