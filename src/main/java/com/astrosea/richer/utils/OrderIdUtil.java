package com.astrosea.richer.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class OrderIdUtil {
    public static String generateOrderId(String input) {
        try {
            // 创建一个MD5的哈希函数
            MessageDigest digest = MessageDigest.getInstance("MD5");
            
            // 计算输入字符串的哈希值
            byte[] hashBytes = digest.digest(input.getBytes());
            
            // 将哈希值转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            // 截取字符串前9位作为订单ID
            return hexString.toString().substring(0, 9);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public static void main(String[] args) {
        String orderId = generateOrderId("some_input_data");
        System.out.println(orderId); // 输出生成的9位订单ID
    }
}
