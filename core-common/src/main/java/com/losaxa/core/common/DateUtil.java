package com.losaxa.core.common;


import com.losaxa.core.common.constant.Const;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具
 */
public class DateUtil {

    private DateUtil() {

    }

    public static String toStr(LocalDateTime localDateTime) {
        return localDateTime.format(Const.Data.YMDHMSS_FORMATTER);
    }

    public static String toStr(LocalDateTime localDateTime, String format) {
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

    public static Long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
