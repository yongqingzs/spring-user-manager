package com.userdept.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDepartmentVO {
    private String username;
    private String realname;
    private String email;
    private Integer status;
    private LocalDateTime addedTime;
}
