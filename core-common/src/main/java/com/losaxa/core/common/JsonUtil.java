package com.losaxa.core.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.losaxa.core.common.constant.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * json工具
 */
public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private JsonUtil() {
    }

    public static final ObjectMapper OM;

    public static final SimpleModule SIMPLE_MODULE;

    static {
        SIMPLE_MODULE = new SimpleModule();
        SIMPLE_MODULE.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(Const.Data.YMDHMSS_FORMATTER));
        SIMPLE_MODULE.addSerializer(LocalDate.class, new LocalDateSerializer(Const.Data.YMD_FORMATTER));
        SIMPLE_MODULE.addSerializer(LocalTime.class, new LocalTimeSerializer(Const.Data.HMSS_FORMATTER));
        SIMPLE_MODULE.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(Const.Data.YMDHMSS_FORMATTER));
        SIMPLE_MODULE.addDeserializer(LocalDate.class, new LocalDateDeserializer(Const.Data.YMD_FORMATTER));
        SIMPLE_MODULE.addDeserializer(LocalTime.class, new LocalTimeDeserializer(Const.Data.HMSS_FORMATTER));
        SIMPLE_MODULE.addDeserializer(Enum.class, new IntToEnumDeserializer());
        OM = new ObjectMapper();
        OM.registerModule(SIMPLE_MODULE);
        OM.setDateFormat(Const.Data.YMDHMSS_DATE_FORMAT);
        //忽略目标对象没有的属性
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public static String toJson(Object obj) {
        try {
            return OM.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
            return "";
        }
    }

    public static <T> Optional<T> toObj(String json, Class<T> clazz) {
        try {
            return Optional.of(OM.readValue(json, clazz));
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
            return Optional.empty();
        }
    }

    public static <T> Optional<T> toObj(byte[] bytes, Class<T> clazz) {
        try {
            return Optional.of(OM.readValue(bytes, clazz));
        } catch (IOException e) {
            log.warn(e.getMessage());
            return Optional.empty();
        }
    }

}
