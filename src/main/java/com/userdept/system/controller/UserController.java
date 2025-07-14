package com.userdept.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.userdept.system.dto.UserDTO;
import com.userdept.system.dto.UserDTO.Create;
import com.userdept.system.dto.UserDTO.Update;
import com.userdept.system.entity.Department;
import com.userdept.system.entity.User;
import com.userdept.system.service.UserService;
import com.userdept.system.vo.PageVO;
import com.userdept.system.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping
    public ResultVO<PageVO<User>> getUserList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "10") int perPage,
            @RequestParam(value = "query", required = false) String query) {

        Page<User> userPage = userService.getUserPage(page, perPage, query);

        PageVO<User> pageVO = new PageVO<>(
                userPage.getTotal(),
                page,
                perPage,
                userPage.getRecords()
        );
        
        return ResultVO.success(pageVO);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{userId}")
    public ResultVO<User> getUser(@PathVariable Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return ResultVO.error("用户不存在");
        }
        return ResultVO.success(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ResultVO<Long> createUser(@Validated(Create.class) @RequestBody UserDTO userDTO) {
        String currentUsername = getCurrentUsername();
        Long userId = userService.createUser(userDTO, currentUsername);
        return ResultVO.success("用户创建成功", userId);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{userId}")
    public ResultVO<Boolean> updateUser(
            @PathVariable Long userId,
            @Validated(Update.class) @RequestBody UserDTO userDTO) {
        String currentUsername = getCurrentUsername();
        boolean success = userService.updateUser(userId, userDTO, currentUsername);
        if (success) {
            return ResultVO.success("用户更新成功", true);
        } else {
            return ResultVO.error("用户不存在");
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public ResultVO<Boolean> deleteUser(@PathVariable Long userId) {
        boolean success = userService.deleteUser(userId);
        if (success) {
            return ResultVO.success("用户删除成功", true);
        } else {
            return ResultVO.error("用户不存在");
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{userId}/status")
    public ResultVO<Boolean> changeUserStatus(
            @PathVariable Long userId,
            @Validated(Update.class) @RequestBody UserDTO userDTO) {
        String currentUsername = getCurrentUsername();
        boolean success = userService.changeUserStatus(userId, userDTO.getStatus(), currentUsername);
        if (success) {
            String statusText = userDTO.getStatus() == 1 ? "启用" : "禁用";
            return ResultVO.success("用户已" + statusText, true);
        } else {
            return ResultVO.error("用户不存在");
        }
    }

    /**
     * 获取用户所属部门列表
     */
    @GetMapping("/{userId}/departments")
    public ResultVO<List<Department>> getUserDepartments(@PathVariable Long userId) {
        List<Department> departments = userService.getDepartmentsByUserId(userId);
        return ResultVO.success(departments);
    }

    /**
     * 获取当前登录用户名
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }
        return authentication.getName();
    }
}
