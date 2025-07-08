package com.userdept.system.controller;

import com.userdept.system.service.CaptchaService;
import com.userdept.system.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {
    private final CaptchaService captchaService;

    /**
     * 生成验证码
     * @param type 验证码类型（如 sms/email/image，可选）
     * @param target 目标（如手机号/邮箱，可选）
     * @return uuid
     */
    @PostMapping("/send")
    public ResultVO<Map<String, String>> sendCaptcha(@RequestParam(defaultValue = "default") String type,
                                                    @RequestParam(required = false) String target) {
        String uuidAndCode = captchaService.generateCaptcha(type, target, 300); // 5分钟
        String[] arr = uuidAndCode.split(":");
        Map<String, String> data = new HashMap<>();
        data.put("uuid", arr[0]);
        data.put("code", arr[1]); // 实际生产环境不返回 code，仅用于开发演示
        return ResultVO.success("验证码已生成", data);
    }

    /**
     * 校验验证码
     */
    @PostMapping("/verify")
    public ResultVO<Boolean> verifyCaptcha(@RequestParam String uuid, @RequestParam String code) {
        boolean ok = captchaService.verifyCaptcha(uuid, code, true);
        if (ok) {
            return ResultVO.success("验证码校验通过", true);
        } else {
            return ResultVO.error("验证码错误或已过期");
        }
    }

    /**
     * 获取图形验证码图片
     */
    @GetMapping("/image")
    public ResultVO<Map<String, String>> getImageCaptcha() {
        CaptchaService.CaptchaImageResult img = captchaService.generateImageCaptcha(110, 40, 5, 300);
        Map<String, String> data = new HashMap<>();
        data.put("uuid", img.uuid);
        data.put("base64", "data:image/png;base64," + img.base64);
        return ResultVO.success("验证码图片生成成功", data);
    }
}
