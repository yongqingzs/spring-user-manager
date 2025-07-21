package com.userdept.system.controller;

import com.userdept.system.dto.AuthDTO;
import com.userdept.system.service.AuthService;
import com.userdept.system.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 登录处理
     * @param authDTO 认证数据传输对象
     * @return 包含 JWT 的响应
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody AuthDTO authDTO) {
        String jwt = authService.login(authDTO);
        return ResponseEntity.ok(ApiResponse.<String>success("登录成功", jwt));
    }

    /**
     * 注销处理
     * @return 成功响应
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // 在基于 Token 的认证中，注销通常由前端删除 Token 实现。
        // 后端可以提供一个接口，用于将 Token 加入黑名单，以增强安全性。
        // 此处仅为示例，具体实现取决于业务需求。
        return ResponseEntity.ok(ApiResponse.success());
    }
}
