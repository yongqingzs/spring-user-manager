package com.userdept.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.userdept.system.dto.DepartmentDTO;
import com.userdept.system.entity.Department;
import com.userdept.system.entity.User;
import com.userdept.system.entity.UserDepartment;
import com.userdept.system.mapper.DepartmentMapper;
import com.userdept.system.mapper.UserDepartmentMapper;
import com.userdept.system.mapper.UserMapper;
import com.userdept.system.service.DepartmentService;
import com.userdept.system.vo.UserDepartmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final UserDepartmentMapper userDepartmentMapper;
    private final UserMapper userMapper;

    @Override
    public Page<Department> getDepartmentPage(int page, int perPage, String search) {
        LambdaQueryWrapper<Department> queryWrapper = Wrappers.lambdaQuery();
        
        if (StringUtils.hasText(search)) {
            queryWrapper.like(Department::getCode, search)
                    .or()
                    .like(Department::getName, search)
                    .or()
                    .like(Department::getDescription, search);
        }
        
        queryWrapper.orderByDesc(Department::getId);
        
        return departmentMapper.selectPage(new Page<>(page, perPage), queryWrapper);
    }

    @Override
    public List<Department> getDepartmentTree() {
        List<Department> allDepartments = departmentMapper.selectList(null);
        
        // 创建部门代码到部门对象的映射
        Map<String, Department> deptMap = new HashMap<>();
        allDepartments.forEach(dept -> deptMap.put(dept.getCode(), dept));
        
        // 构建树形结构
        List<Department> rootDepartments = new ArrayList<>();
        allDepartments.forEach(dept -> {
            if (!StringUtils.hasText(dept.getParentCode())) {
                // 根部门
                rootDepartments.add(dept);
            } else {
                // 子部门
                Department parentDept = deptMap.get(dept.getParentCode());
                if (parentDept != null) {
                    if (parentDept.getChildren() == null) {
                        parentDept.setChildren(new ArrayList<>());
                    }
                    parentDept.getChildren().add(dept);
                }
            }
        });
        
        return rootDepartments;
    }

    @Override
    public Department getDepartmentByCode(String code) {
        return departmentMapper.selectOne(Wrappers.<Department>lambdaQuery()
                .eq(Department::getCode, code));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Department createDepartment(DepartmentDTO departmentDTO) {
        // 新增唯一性校验
        Department exist = departmentMapper.selectOne(Wrappers.<Department>lambdaQuery()
                .eq(Department::getCode, departmentDTO.getCode()));
        if (exist != null) {
            throw new IllegalArgumentException("部门编码已存在，请更换编码");
        }
        Department department = new Department();
        department.setCode(departmentDTO.getCode());
        department.setName(departmentDTO.getName());
        department.setParentCode(departmentDTO.getParentCode());
        department.setDescription(departmentDTO.getDescription());
        department.setCreator("system"); // 在无状态认证中，创建者信息需要其他方式获取
        department.setModifier("system");
        department.setCreatedTime(LocalDateTime.now());
        department.setUpdatedTime(LocalDateTime.now());
        
        departmentMapper.insert(department);
        
        return department;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Department updateDepartment(Long deptId, DepartmentDTO departmentDTO) {
        Department department = departmentMapper.selectById(deptId);
        if (department == null) {
            throw new IllegalArgumentException("部门不存在");
        }

        department.setName(departmentDTO.getName());
        department.setParentCode(departmentDTO.getParentCode());
        department.setDescription(departmentDTO.getDescription());

        // 从上下文中获取当前用户
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUsername = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : "unknown";
        department.setModifier(currentUsername);
        department.setUpdatedTime(LocalDateTime.now());

        departmentMapper.updateById(department);

        // 返回数据库中最新的完整 Department 对象
        return departmentMapper.selectById(deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long deptId) {
        Department department = departmentMapper.selectById(deptId);
        if (department == null) {
            throw new IllegalArgumentException("部门不存在");
        }
        
        // 检查是否有子部门
        Department childDept = departmentMapper.selectOne(Wrappers.<Department>lambdaQuery()
                .eq(Department::getParentCode, department.getCode()));
        
        if (childDept != null) {
            log.warn("部门[{}]存在子部门，无法删除", department.getCode());
            throw new IllegalStateException("部门存在子部门，无法删除");
        }
        
        // 删除部门用户关联
        userDepartmentMapper.delete(Wrappers.<UserDepartment>lambdaQuery()
                .eq(UserDepartment::getDepartmentCode, department.getCode()));
        
        // 删除部门
        departmentMapper.deleteById(deptId);
    }

    @Override
    public List<User> getUsersInDepartment(String departmentCode) {
        // 获取部门下的所有用户名
        List<UserDepartment> userDepts = userDepartmentMapper.selectList(
                Wrappers.<UserDepartment>lambdaQuery()
                        .eq(UserDepartment::getDepartmentCode, departmentCode)
        );
        
        if (userDepts.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取用户名列表
        List<String> usernames = userDepts.stream()
                .map(UserDepartment::getUsername)
                .collect(Collectors.toList());
        
        // 查询用户详情
        return userMapper.selectList(Wrappers.<User>lambdaQuery()
                .in(User::getUsername, usernames));
    }

    @Override
    public List<Department> getDepartmentsForUser(String username) {
        // 获取用户关联的所有部门代码
        List<UserDepartment> userDepts = userDepartmentMapper.selectList(
                Wrappers.<UserDepartment>lambdaQuery()
                        .eq(UserDepartment::getUsername, username)
        );
        
        if (userDepts.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取部门代码列表
        List<String> deptCodes = userDepts.stream()
                .map(UserDepartment::getDepartmentCode)
                .collect(Collectors.toList());
        
        // 查询部门详情
        return departmentMapper.selectList(Wrappers.<Department>lambdaQuery()
                .in(Department::getCode, deptCodes));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignUserToDepartment(String username, String departmentCode, String creator) {
        // 检查关联是否已存在
        UserDepartment existingRelation = userDepartmentMapper.selectOne(
                Wrappers.<UserDepartment>lambdaQuery()
                        .eq(UserDepartment::getUsername, username)
                        .eq(UserDepartment::getDepartmentCode, departmentCode)
        );
        
        if (existingRelation != null) {
            return false;
        }
        
        // 创建新关联
        UserDepartment userDept = new UserDepartment();
        userDept.setUsername(username);
        userDept.setDepartmentCode(departmentCode);
        userDept.setCreator(creator);
        userDept.setCreatedTime(LocalDateTime.now());
        
        userDepartmentMapper.insert(userDept);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeUserFromDepartment(String username, String departmentCode) {
        int deleted = userDepartmentMapper.delete(
                Wrappers.<UserDepartment>lambdaQuery()
                        .eq(UserDepartment::getUsername, username)
                        .eq(UserDepartment::getDepartmentCode, departmentCode)
        );
        
        return deleted > 0;
    }

    @Override
    public List<UserDepartment> getUserDepartmentRelations(String username) {
        return userDepartmentMapper.selectList(
                Wrappers.<UserDepartment>lambdaQuery()
                        .eq(UserDepartment::getUsername, username)
        );
    }

    @Override
    public List<UserDepartmentVO> getUserDepartmentVOs(String departmentCode) {
        List<UserDepartment> userDepts = userDepartmentMapper.selectList(
                Wrappers.<UserDepartment>lambdaQuery()
                        .eq(UserDepartment::getDepartmentCode, departmentCode)
        );
        if (userDepts.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> usernames = userDepts.stream().map(UserDepartment::getUsername).toList();
        List<User> users = userMapper.selectList(Wrappers.<User>lambdaQuery().in(User::getUsername, usernames));
        // username -> User
        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getUsername, u -> u));
        List<UserDepartmentVO> voList = new ArrayList<>();
        for (UserDepartment ud : userDepts) {
            User u = userMap.get(ud.getUsername());
            if (u != null) {
                UserDepartmentVO vo = new UserDepartmentVO();
                vo.setUsername(u.getUsername());
                vo.setRealname(u.getRealname());
                vo.setEmail(u.getEmail());
                vo.setStatus(u.getStatus());
                vo.setAddedTime(ud.getCreatedTime());
                voList.add(vo);
            }
        }
        return voList;
    }

    @Override
    public Department getById(Long deptId) {
        if (deptId == null) return null;
        return departmentMapper.selectById(deptId);
    }

    @Override
    public long count() {
        return departmentMapper.selectCount(null);
    }
}
