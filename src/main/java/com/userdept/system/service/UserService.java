package com.userdept.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.userdept.system.dto.UserDTO;
import com.userdept.system.entity.User;
import com.userdept.system.entity.Department;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户列表
     * 
     * @param page 当前页
     * @param perPage 每页数量
     * @param query 查询条件
     * @return 分页结果
     */
    Page<User> getUserPage(int page, int perPage, String query);
    
    /**
     * 通过用户名获取用户
     * 
     * @param username 用户名
     * @return 用户对象
     */
    User getUserByUsername(String username);
    
    /**
     * 创建用户
     * 
     * @param userDTO 用户数据
     * @param creator 创建人
     * @return 创建的用户ID
     */
    Long createUser(UserDTO userDTO, String creator);
    
    /**
     * 更新用户
     * 
     * @param userId 用户ID
     * @param userDTO 用户数据
     * @param modifier 修改人
     * @return 是否成功
     */
    boolean updateUser(Long userId, UserDTO userDTO, String modifier);
    
    /**
     * 删除用户
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long userId);
    
    /**
     * 修改用户状态
     * 
     * @param userId 用户ID
     * @param status 状态
     * @param modifier 修改人
     * @return 是否成功
     */
    boolean changeUserStatus(Long userId, Integer status, String modifier);

    /**
     * 获取用户所属部门列表
     * @param userId 用户ID
     * @return 部门列表
     */
    List<Department> getDepartmentsByUserId(Long userId);
}
