package com.userdept.system.service.impl;

import com.userdept.system.service.CaptchaService;
import com.userdept.system.utils.CaptchaImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {
    private final StringRedisTemplate redisTemplate;
    private static final String CAPTCHA_PREFIX = "captcha:";

    @Override
    public String generateCaptcha(String type, String target, int expireSeconds) {
        // 生成6位数字验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String key = CAPTCHA_PREFIX + uuid;
        redisTemplate.opsForValue().set(key, code, expireSeconds, TimeUnit.SECONDS);
        return uuid + ":" + code; // 返回uuid和验证码，实际生产只返回uuid
    }

    @Override
    public boolean verifyCaptcha(String uuid, String code, boolean removeIfSuccess) {
        String key = CAPTCHA_PREFIX + uuid;
        String realCode = redisTemplate.opsForValue().get(key);
        if (realCode != null && realCode.equalsIgnoreCase(code)) {
            if (removeIfSuccess) {
                redisTemplate.delete(key);
            }
            return true;
        }
        return false;
    }

    @Override
    public CaptchaImageResult generateImageCaptcha(int width, int height, int length, int expireSeconds) {
        CaptchaImageUtil.CaptchaResult img = CaptchaImageUtil.createImage(width, height, length);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String key = CAPTCHA_PREFIX + uuid;
        redisTemplate.opsForValue().set(key, img.code, expireSeconds, TimeUnit.SECONDS);
        return new CaptchaImageResult(uuid, img.base64);
    }
}
