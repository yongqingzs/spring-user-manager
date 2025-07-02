package com.userdept.system.service;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 登录结果，成功返回true
     */
    boolean login(String username, String password);
    
    /**
     * 用户注销
     */
    void logout();
}
