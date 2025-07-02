package com.userdept.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * 用户实体类
 */
@Data
@TableName("sys_user")
public class User implements UserDetails {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 登录用户名，唯一
     */
    private String username;

    /**
     * 中文名
     */
    private String realname;

    /**
     * 登录密码，加密后存储
     */
    @JsonIgnore
    private String password;

    /**
     * 密码加盐
     */
    @JsonIgnore
    private String salt;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 身份证号码
     */
    private String idno;

    /**
     * 用户性别：1男；2女
     */
    private Integer sex;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 账户状态：1启用,2禁用
     */
    private Integer status;

    /**
     * 用户相关扩展字段
     */
    private String userExt;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String modifier;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 非数据库字段，用于临时存储部门信息
     */
    @TableField(exist = false)
    private String[] departments;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.status == 1;
    }
}
