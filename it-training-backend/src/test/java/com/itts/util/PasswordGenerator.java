package com.itts.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具 - 用于生成 BCrypt 加密的密码
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("BCrypt哈希: " + encodedPassword);
        System.out.println();
        System.out.println("SQL UPDATE 语句:");
        System.out.println("UPDATE sys_user SET password = '" + encodedPassword + "' WHERE username IN ('admin', 'teacher1', 'teacher2', 'student1', 'student2');");
    }
}