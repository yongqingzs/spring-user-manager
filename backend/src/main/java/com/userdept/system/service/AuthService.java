package com.userdept.system.service;

import com.userdept.system.dto.AuthDTO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param authDTO 认证数据传输对象
     * @return JWT
     */
    String login(AuthDTO authDTO);
}
