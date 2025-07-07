package com.userdept.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userdept.system.dto.UserDTO;
import com.userdept.system.entity.Department;
import com.userdept.system.entity.User;
import com.userdept.system.entity.UserDepartment;
import com.userdept.system.mapper.DepartmentMapper;
import com.userdept.system.mapper.UserDepartmentMapper;
import com.userdept.system.mapper.UserMapper;
import com.userdept.system.service.UserService;
import com.userdept.system.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserDepartmentMapper userDepartmentMapper;
    private final DepartmentMapper departmentMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Page<User> getUserPage(int page, int perPage, String query) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        
        if (StringUtils.hasText(query)) {
            queryWrapper.like(User::getUsername, query)
                    .or()
                    .like(User::getRealname, query)
                    .or()
                    .like(User::getMobile, query);
        }
        
        queryWrapper.orderByDesc(User::getId);
        
        return userMapper.selectPage(new Page<>(page, perPage), queryWrapper);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserDTO userDTO, String creator) {
        // 新增唯一性校验
        User exist = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, userDTO.getUsername()));
        if (exist != null) {
            throw new IllegalArgumentException("用户名已存在，请更换用户名");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setRealname(userDTO.getRealname());
        user.setMobile(userDTO.getMobile());
        user.setIdno(userDTO.getIdno());
        user.setSex(userDTO.getSex());
        user.setEmail(userDTO.getEmail());
        user.setStatus(userDTO.getStatus());
        user.setCreator(creator);
        user.setModifier(creator);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        
        // 设置密码
        String salt = PasswordUtil.generateSalt();
        user.setSalt(salt);
        user.setPassword(PasswordUtil.hashPassword(userDTO.getPassword(), salt));
        
        // 设置扩展信息
        if (userDTO.getUserExt() != null) {
            try {
                user.setUserExt(objectMapper.writeValueAsString(userDTO.getUserExt()));
            } catch (JsonProcessingException e) {
                log.error("JSON序列化失败", e);
            }
        }
        
        userMapper.insert(user);
        
        // 处理部门关联
        if (userDTO.getDepartments() != null && userDTO.getDepartments().length > 0) {
            Arrays.stream(userDTO.getDepartments()).forEach(deptCode -> {
                // 检查部门是否存在
                Department dept = departmentMapper.selectOne(Wrappers.<Department>lambdaQuery()
                        .eq(Department::getCode, deptCode));
                
                if (dept != null) {
                    UserDepartment userDept = new UserDepartment();
                    userDept.setUsername(user.getUsername());
                    userDept.setDepartmentCode(deptCode);
                    userDept.setCreator(creator);
                    userDept.setCreatedTime(LocalDateTime.now());
                    
                    userDepartmentMapper.insert(userDept);
                }
            });
        }
        
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(Long userId, UserDTO userDTO, String modifier) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        user.setRealname(userDTO.getRealname());
        user.setMobile(userDTO.getMobile());
        user.setIdno(userDTO.getIdno());
        user.setSex(userDTO.getSex());
        user.setEmail(userDTO.getEmail());
        user.setStatus(userDTO.getStatus());
        user.setModifier(modifier);
        user.setUpdatedTime(LocalDateTime.now());
        
        // 更新密码（如果提供）
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(PasswordUtil.hashPassword(userDTO.getPassword(), user.getSalt()));
        }
        
        // 更新扩展信息
        if (userDTO.getUserExt() != null) {
            try {
                user.setUserExt(objectMapper.writeValueAsString(userDTO.getUserExt()));
            } catch (JsonProcessingException e) {
                log.error("JSON序列化失败", e);
            }
        }
        
        userMapper.updateById(user);
        
        // 处理部门关联
        if (userDTO.getDepartments() != null) {
            // 先删除现有关联
            userDepartmentMapper.delete(Wrappers.<UserDepartment>lambdaQuery()
                    .eq(UserDepartment::getUsername, user.getUsername()));
            
            // 添加新关联
            if (userDTO.getDepartments().length > 0) {
                Arrays.stream(userDTO.getDepartments()).forEach(deptCode -> {
                    Department dept = departmentMapper.selectOne(Wrappers.<Department>lambdaQuery()
                            .eq(Department::getCode, deptCode));
                    
                    if (dept != null) {
                        UserDepartment userDept = new UserDepartment();
                        userDept.setUsername(user.getUsername());
                        userDept.setDepartmentCode(deptCode);
                        userDept.setCreator(modifier);
                        userDept.setCreatedTime(LocalDateTime.now());
                        
                        userDepartmentMapper.insert(userDept);
                    }
                });
            }
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        // 删除用户部门关联
        userDepartmentMapper.delete(Wrappers.<UserDepartment>lambdaQuery()
                .eq(UserDepartment::getUsername, user.getUsername()));
        
        // 删除用户
        userMapper.deleteById(userId);
        
        return true;
    }

    @Override
    public boolean changeUserStatus(Long userId, Integer status, String modifier) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        user.setStatus(status);
        user.setModifier(modifier);
        user.setUpdatedTime(LocalDateTime.now());
        
        userMapper.updateById(user);
        
        return true;
    }

    @Override
    public List<Department> getDepartmentsByUserId(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return List.of();
        }
        // 假设 UserDepartment 关联表用 userId 关联
        List<UserDepartment> userDepartments = userDepartmentMapper.selectList(
                Wrappers.<UserDepartment>lambdaQuery().eq(UserDepartment::getUsername, user.getUsername())
        );
        if (userDepartments.isEmpty()) {
            return List.of();
        }
        List<String> deptCodes = userDepartments.stream()
                .map(UserDepartment::getDepartmentCode)
                .toList();
        if (deptCodes.isEmpty()) {
            return List.of();
        }
        return departmentMapper.selectList(
                Wrappers.<Department>lambdaQuery().in(Department::getCode, deptCodes)
        );
    }
}
