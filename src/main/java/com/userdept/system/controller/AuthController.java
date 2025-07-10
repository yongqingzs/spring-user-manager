package com.userdept.system.controller;

import com.userdept.system.service.AuthService;
import com.userdept.system.service.CaptchaService;
import com.userdept.system.vo.ResultVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * 认证控制器
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CaptchaService captchaService;
    private final UserDetailsService userDetailsService;

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    /**
     * 登录处理
     */
    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResultVO<String> login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String uuid,
            @RequestParam String code,
            HttpServletRequest request) {
        // 先校验验证码
        boolean captchaOk = captchaService.verifyCaptcha(uuid, code, true);
        log.debug("captchaOk: {}", captchaOk);
        if (!captchaOk) {
            return ResultVO.error("验证码错误或已过期");
        }
        // 再校验用户名密码
        boolean success = authService.login(username, password);
        if (success) {
            // 手动认证：将用户信息写入 SecurityContext 和 session
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            return ResultVO.success("登录成功");
        } else {
            return ResultVO.error("用户名或密码错误");
        }
    }

    /**
     * 注销处理
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        
        authService.logout();
        return "redirect:/login?logout=true";
    }
}
