package com.userdept.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.userdept.system.entity.UserDepartment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户部门关联Mapper接口
 */
@Mapper
public interface UserDepartmentMapper extends BaseMapper<UserDepartment> {
}
