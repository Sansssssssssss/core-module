package com.losaxa.core.common.constant;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * 常量定义
 */
public interface Const {

    String TRACE_ID = "traceId";

    interface Data {
        String            YMDHMSS_STR       = "yyyy-MM-dd HH:mm:ss.SSS";
        String            YMDHMS_STR       = "yyyy-MM-dd HH:mm:ss";
        //localdatetime format
        DateTimeFormatter YMDHMSS_FORMATTER = DateTimeFormatter.ofPattern(YMDHMSS_STR);
        DateTimeFormatter YMDHMS_FORMATTER = DateTimeFormatter.ofPattern(YMDHMS_STR);
        DateTimeFormatter YMD_FORMATTER       = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter HMSS_FORMATTER      = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        //date format
        SimpleDateFormat  YMDHMSS_DATE_FORMAT = new SimpleDateFormat(YMDHMSS_STR);
        SimpleDateFormat  YMDHMS_DATE_FORMAT = new SimpleDateFormat(YMDHMS_STR);
    }


}
