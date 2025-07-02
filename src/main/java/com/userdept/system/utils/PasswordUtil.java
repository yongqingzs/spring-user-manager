package com.userdept.system.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * 密码工具类
 */
public class PasswordUtil {
    
    /**
     * 生成盐值
     */
    public static String generateSalt() {
        return BCrypt.gensalt();
    }
    
    /**
     * 对密码进行哈希处理
     * 
     * @param password 原始密码
     * @param salt 盐值
     * @return 哈希后的密码
     */
    public static String hashPassword(String password, String salt) {
        return BCrypt.hashpw(password, salt);
    }
    
    /**
     * 验证密码
     * 
     * @param password 原始密码
     * @param hashedPassword 哈希后的密码
     * @return 是否匹配
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
