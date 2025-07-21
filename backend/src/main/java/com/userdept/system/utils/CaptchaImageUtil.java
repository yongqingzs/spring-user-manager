package com.userdept.system.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.Base64;

public class CaptchaImageUtil {
    // 生成验证码图片和内容
    public static CaptchaResult createImage(int width, int height, int length) {
        String chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Arial", Font.BOLD, height - 8));
        // 干扰线
        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            int x1 = random.nextInt(width), y1 = random.nextInt(height);
            int x2 = random.nextInt(width), y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }
        // 绘制验证码字符
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(String.valueOf(code.charAt(i)), 18 * i + 8, height - 10);
        }
        g.dispose();
        // 转为base64
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            return new CaptchaResult(code.toString(), base64);
        } catch (Exception e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }
    public static class CaptchaResult {
        public final String code;
        public final String base64;
        public CaptchaResult(String code, String base64) {
            this.code = code;
            this.base64 = base64;
        }
    }
}
