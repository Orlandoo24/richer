package com.astrosea.richer.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BeijingTimeUtil {
    public static String getBeijingTime() {
        // 获取当前系统默认时区
        ZoneId systemZone = ZoneId.systemDefault();
        
        // 设置为亚洲/上海时区（北京时间）
        ZoneId beijingZone = ZoneId.of("Asia/Shanghai");
        
        // 获取当前时间
        ZonedDateTime now = ZonedDateTime.now(systemZone);
        
        // 转换为北京时间
        ZonedDateTime beijingTime = now.withZoneSameInstant(beijingZone);
        // 格式化为字符串，精确到秒
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String beijingTimeStr = beijingTime.format(formatter);

        return beijingTimeStr;
    }


}
