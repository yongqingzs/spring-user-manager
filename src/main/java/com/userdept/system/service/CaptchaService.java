package com.userdept.system.service;

public interface CaptchaService {
    /**
     * 生成验证码并存入 Redis，返回 uuid
     */
    String generateCaptcha(String type, String target, int expireSeconds);

    /**
     * 校验验证码
     */
    boolean verifyCaptcha(String uuid, String code, boolean removeIfSuccess);

    /**
     * 生成图形验证码，返回 uuid 和 base64 图片
     */
    CaptchaImageResult generateImageCaptcha(int width, int height, int length, int expireSeconds);

    class CaptchaImageResult {
        public String uuid;
        public String base64;
        public CaptchaImageResult(String uuid, String base64) {
            this.uuid = uuid;
            this.base64 = base64;
        }
    }
}
