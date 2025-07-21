package com.userdept.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String realname;
    private String email;
    private String mobile;
    private Integer sex;
    private Boolean enabled;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
