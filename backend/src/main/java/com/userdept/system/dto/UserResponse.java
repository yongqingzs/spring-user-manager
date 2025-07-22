package com.userdept.system.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String realname;
    private String email;
    private String mobile;
    private Integer sex;
    private Boolean enabled;
    private List<String> departmentCodes; // 添加部门编码列表
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
