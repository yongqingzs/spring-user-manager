package com.userdept.system.service.impl;

import com.userdept.system.dto.AuthDTO;
import com.userdept.system.service.AuthService;
import com.userdept.system.service.UserService;
import com.userdept.system.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    public String login(AuthDTO authDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authDTO.getUsername(),
                        authDTO.getPassword()
                )
        );
        final UserDetails userDetails = (UserDetails) userService.getUserByUsername(authDTO.getUsername());
        return jwtUtil.generateToken(userDetails);
    }
}
