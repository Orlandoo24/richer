package com.astrosea.richer.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.concurrent.ThreadLocalRandom;

public class OrderUtils {



    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static long generateOrderId() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = DATE_FORMATTER.format(now);

        // 生成3位随机数
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int randomNumber = random.nextInt(1000); // 生成0到999之间的随机数

        // 将时间戳和随机数拼接起来
        StringBuilder orderIdBuilder = new StringBuilder(timestamp);
        orderIdBuilder.insert(10, String.format("%03d", randomNumber));

        String orderIdString = orderIdBuilder.toString();

        return Long.parseLong(orderIdString);
    }


    public static void main(String[] args) {
        Long orderId = OrderUtils.generateOrderId();

        System.out.println(orderId);
    }


}