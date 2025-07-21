package com.userdept.system.config;

import com.userdept.system.entity.User;
import com.userdept.system.mapper.UserMapper;
import com.userdept.system.utils.PasswordUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;

/**
 * 系统初始化配置，创建管理员账户
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SystemInitializer {

    private final UserMapper userMapper;
    private final Environment env;
    
    @Value("${system.admin.username:admin}")
    private String adminUsername;
    
    @Value("${system.admin.password:admin123}")
    private String adminPassword;
    
    @Value("${system.admin.realname:系统管理员}")
    private String adminRealname;

    /**
     * 初始化系统设置，创建管理员账户
     */
    @PostConstruct
    public void init() {
        log.info("正在初始化系统...");
        
        // 检查是否需要创建管理员账户
        User admin = createAdminUserIfNotExists();
        if (admin != null) {
            log.info("已创建管理员账户: {}", admin.getUsername());
        }
        
        log.info("系统名称: {}", env.getProperty("system.name", "望子成龙小学职工管理系统"));
        log.info("系统初始化完成");
    }
    
    /**
     * 创建管理员账户（如果不存在）
     */
    private User createAdminUserIfNotExists() {
        User existingAdmin = userMapper.selectOne(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<User>lambdaQuery()
                        .eq(User::getUsername, adminUsername)
        );
        
        if (existingAdmin != null) {
            log.info("管理员账户已存在，跳过创建");
            return null;
        }
        
        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setRealname(adminRealname);
        admin.setStatus(1);
        admin.setCreator("system");
        admin.setModifier("system");
        admin.setCreatedTime(LocalDateTime.now());
        admin.setUpdatedTime(LocalDateTime.now());
        
        // 设置密码
        String salt = PasswordUtil.generateSalt();
        admin.setSalt(salt);
        admin.setPassword(PasswordUtil.hashPassword(adminPassword, salt));
        
        userMapper.insert(admin);
        return admin;
    }
}
