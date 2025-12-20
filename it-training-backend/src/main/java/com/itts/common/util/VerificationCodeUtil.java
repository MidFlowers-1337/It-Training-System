package com.itts.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类 (Redis版本)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationCodeUtil {

    private final StringRedisTemplate redisTemplate;

    private static final String EMAIL_CODE_PREFIX = "email_code:";
    private static final String PHONE_CODE_PREFIX = "phone_code:";
    private static final int CODE_EXPIRE_MINUTES = 5;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成6位随机数字验证码
     */
    public String generateCode() {
        int code = RANDOM.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    /**
     * 保存邮箱验证码到Redis
     * @param email 邮箱地址
     * @param code 验证码
     */
    public void saveEmailCode(String email, String code) {
        String key = EMAIL_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.info("邮箱验证码已保存到Redis: {} -> {}", email, code);
    }

    /**
     * 保存手机验证码到Redis
     * @param phone 手机号
     * @param code 验证码
     */
    public void savePhoneCode(String phone, String code) {
        String key = PHONE_CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.info("手机验证码已保存到Redis: {} -> {}", phone, code);
    }

    /**
     * 验证邮箱验证码
     * @param email 邮箱地址
     * @param code 用户输入的验证码
     * @return 是否验证通过
     */
    public boolean verifyEmailCode(String email, String code) {
        String key = EMAIL_CODE_PREFIX + email;
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null) {
            log.warn("邮箱验证码不存在或已过期: {}", email);
            return false;
        }

        boolean isValid = savedCode.equals(code);
        if (isValid) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
            log.info("邮箱验证码验证成功: {}", email);
        } else {
            log.warn("邮箱验证码验证失败: {}", email);
        }

        return isValid;
    }

    /**
     * 验证手机验证码
     * @param phone 手机号
     * @param code 用户输入的验证码
     * @return 是否验证通过
     */
    public boolean verifyPhoneCode(String phone, String code) {
        String key = PHONE_CODE_PREFIX + phone;
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null) {
            log.warn("手机验证码不存在或已过期: {}", phone);
            return false;
        }

        boolean isValid = savedCode.equals(code);
        if (isValid) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
            log.info("手机验证码验证成功: {}", phone);
        } else {
            log.warn("手机验证码验证失败: {}", phone);
        }

        return isValid;
    }

    /**
     * 检查验证码是否存在(用于防止频繁发送)
     * @param email 邮箱地址
     * @return 是否存在未过期的验证码
     */
    public boolean emailCodeExists(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 检查验证码是否存在(用于防止频繁发送)
     * @param phone 手机号
     * @return 是否存在未过期的验证码
     */
    public boolean phoneCodeExists(String phone) {
        String key = PHONE_CODE_PREFIX + phone;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
