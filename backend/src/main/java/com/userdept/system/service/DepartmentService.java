package com.userdept.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.userdept.system.dto.DepartmentDTO;
import com.userdept.system.entity.Department;
import com.userdept.system.entity.User;
import com.userdept.system.entity.UserDepartment;
import com.userdept.system.vo.UserDepartmentVO;

import java.util.List;

/**
 * 部门服务接口
 */
public interface DepartmentService {

    /**
     * 分页查询部门列表
     * 
     * @param page 当前页
     * @param perPage 每页数量
     * @param search 查询条件
     * @return 分页结果
     */
    Page<Department> getDepartmentPage(int page, int perPage, String search);
    
    /**
     * 获取部门树形结构
     * 
     * @return 树形结构的部门列表
     */
    List<Department> getDepartmentTree();
    
    /**
     * 通过编号获取部门
     * 
     * @param code 部门编号
     * @return 部门对象
     */
    Department getDepartmentByCode(String code);
    
    /**
     * 创建部门
     * 
     * @param departmentDTO 部门数据
     * @return 创建的部门
     */
    Department createDepartment(DepartmentDTO departmentDTO);
    
    /**
     * 更新部门
     * 
     * @param deptId 部门ID
     * @param departmentDTO 部门数据
     * @return 更新后的部门
     */
    Department updateDepartment(Long deptId, DepartmentDTO departmentDTO);
    
    /**
     * 删除部门
     * 
     * @param deptId 部门ID
     */
    void deleteDepartment(Long deptId);
    
    /**
     * 获取指定部门中的用户
     * 
     * @param departmentCode 部门编号
     * @return 用户列表
     */
    List<User> getUsersInDepartment(String departmentCode);
    
    /**
     * 获取指定用户所属的部门
     * 
     * @param username 用户名
     * @return 部门列表
     */
    List<Department> getDepartmentsForUser(String username);
    
    /**
     * 分配用户到部门
     * 
     * @param username 用户名
     * @param departmentCode 部门编号
     * @param creator 创建人
     * @return 是否成功
     */
    boolean assignUserToDepartment(String username, String departmentCode, String creator);
    
    /**
     * 从部门中移除用户
     * 
     * @param username 用户名
     * @param departmentCode 部门编号
     * @return 是否成功
     */
    boolean removeUserFromDepartment(String username, String departmentCode);
    
    /**
     * 获取用户部门关联记录
     * 
     * @param username 用户名
     * @return 关联记录列表
     */
    List<UserDepartment> getUserDepartmentRelations(String username);

    /**
     * 获取用户部门视图对象列表
     * 
     * @param departmentCode 部门编号
     * @return 用户部门视图对象列表
     */
    List<UserDepartmentVO> getUserDepartmentVOs(String departmentCode);

    /**
     * 根据部门ID获取部门对象
     * @param deptId 部门ID
     * @return 部门对象
     */
    Department getById(Long deptId);

    /**
     * 获取部门总数
     * @return 部门总数
     */
    long count();
}
