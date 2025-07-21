package com.userdept.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    public interface Create {}
    public interface Update {}

    @NotBlank(groups = {Create.class}, message = "用户名不能为空")
    @Size(min = 4, max = 20, groups = {Create.class}, message = "用户名长度必须在 4 到 20 个字符之间")
    private String username;

    @NotBlank(groups = {Create.class}, message = "密码不能为空")
    @Size(min = 6, max = 20, groups = {Create.class}, message = "密码长度必须在 6 到 20 个字符之间")
    private String password;

    @NotBlank(groups = {Create.class, Update.class}, message = "真实姓名不能为空")
    private String realname;

    @Email(groups = {Create.class, Update.class}, message = "邮箱格式不正确")
    private String email;

    private String mobile;
    private String idno;
    private Integer sex;
    private Boolean enabled;
}
