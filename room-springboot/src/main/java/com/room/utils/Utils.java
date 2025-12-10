package com.room.utils;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 工具
 */
public class Utils {

    // 固定盐值
    private static final String SALT = "room_booking_system_2025";

    /**
     * 加密密码 (使用 MD5 + 盐)
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        // 密码 + 盐值后进行 MD5 加密
        return DigestUtil.md5Hex(password + SALT);
    }

    /**
     * 校验密码
     * @param password 用户输入的密码
     * @param hashedPassword 加密后的密码
     * @return true/false
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        // 将输入密码加密后与存储的加密密码比对
        return encryptPassword(password).equals(hashedPassword);
    }

    public static void main(String[] args) {
        // 测试加密
        String password = "123456";
        String encrypted = encryptPassword(password);
        System.out.println("原始密码: " + password);
        System.out.println("加密后: " + encrypted);
        
        // 测试多次加密结果是否一致
        System.out.println("第二次加密: " + encryptPassword(password));
        System.out.println("两次加密结果相同: " + encryptPassword(password).equals(encrypted));
        
        // 测试密码校验
        System.out.println("密码校验通过: " + checkPassword("123456", encrypted));
        System.out.println("错误密码校验: " + checkPassword("wrong", encrypted));
    }
}
