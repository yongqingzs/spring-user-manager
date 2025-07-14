package com.userdept.system.service.impl;

import com.userdept.system.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    @Override
    public boolean login(String username, String password) {
        try {
            // 尝试认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // 认证成功，将认证信息存入SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.info("用户[{}]登录成功", username);
            return true;
        } catch (Exception e) {
            log.warn("用户[{}]登录失败: {}", username, e.getMessage());
            return false;
        }
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
