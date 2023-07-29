package com.astrosea.richer.utils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class InviteCodeUtil {
    private static final int CODE_LENGTH = 8;

    public static Long generateInviteCode(String userUrl) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(userUrl.getBytes(StandardCharsets.UTF_8));

            long hashCode = byteArrayToLong(hashBytes);
            String inviteCodeStr = String.valueOf(Math.abs(hashCode)).substring(0, CODE_LENGTH);
            Long inviteCode = Long.parseLong(inviteCodeStr);

            return inviteCode;
        } catch (NoSuchAlgorithmException e) {
            // 处理算法不支持的异常
            e.printStackTrace();
        }

        return null;
    }

    private static long byteArrayToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < Math.min(bytes.length, 8); i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }
}