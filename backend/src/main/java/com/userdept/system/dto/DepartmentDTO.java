package com.userdept.system.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 部门数据传输对象
 */
@Data
public class DepartmentDTO {
    // 分组接口
    public interface Create {}
    public interface Update {}

    @NotBlank(message = "部门编号不能为空", groups = Create.class)
    @Size(min = 1, max = 32, message = "部门编号长度必须在1-32个字符之间")
    private String code;
    
    @NotBlank(message = "部门名称不能为空")
    @Size(min = 2, max = 32, message = "部门名称长度必须在2-32个字符之间")
    private String name;
    
    private String parentCode;
    
    @Size(max = 256, message = "描述信息最大长度为256个字符")
    private String description;
}
