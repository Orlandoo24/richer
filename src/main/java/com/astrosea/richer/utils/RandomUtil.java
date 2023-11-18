package com.astrosea.richer.utils;

import java.util.Random;

public class RandomUtil {
    private static final Random RANDOM = new Random();

    /**
     * 生成1~10之间的随机数
     * @return 随机数
     */
    public static int getRandomNum() {
        return RANDOM.nextInt(10) + 1;
    }
}