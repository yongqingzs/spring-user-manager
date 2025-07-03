package com.userdept.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门实体类
 */
@Data
@TableName("department")
public class Department {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 部门编号，唯一
     */
    private String code;
    
    /**
     * 父部门编号
     */
    private String parentCode;
    
    /**
     * 部门名称
     */
    private String name;
    
    /**
     * 描述信息
     */
    private String description;
    
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
     * 非数据库字段，部门层级结构
     */
    @TableField(exist = false)
    private List<Department> children;
}
