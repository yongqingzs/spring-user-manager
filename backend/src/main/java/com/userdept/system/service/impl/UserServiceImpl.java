package com.userdept.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.userdept.system.dto.UserRequest;
import com.userdept.system.dto.UserResponse;
import com.userdept.system.entity.User;
import com.userdept.system.entity.UserDepartment;
import com.userdept.system.mapper.UserDepartmentMapper;
import com.userdept.system.mapper.UserMapper;
import com.userdept.system.service.UserService;
import com.userdept.system.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserDepartmentMapper userDepartmentMapper;

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
        
        Page<User> userPage = userMapper.selectPage(new Page<>(page, perPage), queryWrapper);
        
        // 填充部门信息
        return userPage;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUser(UserRequest request) {
        User exist = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, request.getUsername()));
        if (exist != null) {
            throw new IllegalArgumentException("用户名已存在，请更换用户名");
        }
        User user = new User();
        BeanUtils.copyProperties(request, user);
        
        String salt = PasswordUtil.generateSalt();
        user.setSalt(salt);
        user.setPassword(PasswordUtil.hashPassword(request.getPassword(), salt));
        
        user.setCreator("system");
        user.setModifier("system");
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        
        userMapper.insert(user);
        
        // 处理用户部门关联
        if (StringUtils.hasText(request.getDepartmentCode())) {
            // 先删除现有的部门关联
            userDepartmentMapper.delete(Wrappers.<UserDepartment>lambdaQuery()
                    .eq(UserDepartment::getUsername, user.getUsername()));
            
            // 创建新的部门关联
            UserDepartment userDepartment = new UserDepartment();
            userDepartment.setUsername(user.getUsername());
            userDepartment.setDepartmentCode(request.getDepartmentCode());
            userDepartment.setCreator("system");
            userDepartment.setCreatedTime(LocalDateTime.now());
            userDepartmentMapper.insert(userDepartment);
        }
        
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(Long id, UserRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        BeanUtils.copyProperties(request, user, "password"); // 排除密码字段的自动复制

        if (StringUtils.hasText(request.getPassword())) {
            String salt = PasswordUtil.generateSalt();
            user.setSalt(salt);
            user.setPassword(PasswordUtil.hashPassword(request.getPassword(), salt));
        }

        // 从上下文中获取当前用户
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUsername = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : "unknown";
        user.setModifier(currentUsername);
        user.setUpdatedTime(LocalDateTime.now());

        userMapper.updateById(user);

        // 更新用户部门关联
        // 1. 删除用户现有的部门关联
        userDepartmentMapper.delete(Wrappers.<UserDepartment>lambdaQuery()
                .eq(UserDepartment::getUsername, user.getUsername()));

        // 2. 添加新的部门关联
        if (StringUtils.hasText(request.getDepartmentCode())) {
            UserDepartment userDepartment = new UserDepartment();
            userDepartment.setUsername(user.getUsername());
            userDepartment.setDepartmentCode(request.getDepartmentCode());
            userDepartment.setCreator(currentUsername);
            userDepartment.setCreatedTime(LocalDateTime.now());
            userDepartmentMapper.insert(userDepartment);
        }

        // 返回数据库中最新的完整 User 对象
        return userMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        userDepartmentMapper.delete(Wrappers.<UserDepartment>lambdaQuery()
                .eq(UserDepartment::getUsername, user.getUsername()));
        
        userMapper.deleteById(id);
    }

    @Override
    public void updateUserStatus(Long id, boolean enabled) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        user.setStatus(enabled ? 1 : 0);
        user.setModifier("system");
        user.setUpdatedTime(LocalDateTime.now());
        
        userMapper.updateById(user);
    }

    @Override
    public UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        response.setEnabled(user.getStatus() != null && user.getStatus() == 1);

        // 获取用户所属的部门编码列表
        List<String> departmentCodes = userDepartmentMapper.selectList(
                Wrappers.<UserDepartment>lambdaQuery()
                        .eq(UserDepartment::getUsername, user.getUsername())
        ).stream().map(UserDepartment::getDepartmentCode).collect(Collectors.toList());
        response.setDepartmentCodes(departmentCodes);

        return response;
    }

    @Override
    public List<UserResponse> convertToResponseList(List<User> users) {
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserDepartmentCodes(String username) {
        return userDepartmentMapper.selectList(
                Wrappers.<UserDepartment>lambdaQuery()
                        .eq(UserDepartment::getUsername, username)
        ).stream().map(UserDepartment::getDepartmentCode).collect(Collectors.toList());
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public long count() {
        return userMapper.selectCount(null);
    }

    @Override
    public long countActiveUsers() {
        return userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getStatus, 1));
    }
}
