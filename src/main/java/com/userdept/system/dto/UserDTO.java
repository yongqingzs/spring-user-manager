package com.userdept.system.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户数据传输对象
 */
@Data
public class UserDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 32, message = "用户名长度必须在2-32个字符之间")
    private String username;
    
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 32, message = "真实姓名最大长度为32个字符")
    private String realname;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @Pattern(regexp = "(^$)|(^1\\d{10}$)", message = "手机号格式不正确")
    private String mobile;
    
    private String idno;
    
    private Integer sex = 1;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String userExt;
    
    private Integer status = 1;
    
    private String[] departments;
}
