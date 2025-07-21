package com.userdept.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.userdept.system.dto.UserRequest;
import com.userdept.system.dto.UserResponse;
import com.userdept.system.entity.User;
import com.userdept.system.service.UserService;
import com.userdept.system.vo.ApiResponse;
import com.userdept.system.vo.PageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageVO<UserResponse>>> getUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "10") int perPage,
            @RequestParam(value = "query", required = false) String query) {

        Page<User> userPage = userService.getUserPage(page, perPage, query);
        List<UserResponse> userResponses = userService.convertToResponseList(userPage.getRecords());

        PageVO<UserResponse> pageVO = new PageVO<>(
                userPage.getTotal(),
                page,
                perPage,
                userResponses
        );
        
        return ResponseEntity.ok(ApiResponse.success(pageVO));
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return ResponseEntity.ok(ApiResponse.error("用户不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(userService.convertToResponse(user)));
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Validated(UserRequest.Create.class) @RequestBody UserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(userService.convertToResponse(user)));
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Validated(UserRequest.Update.class) @RequestBody UserRequest request) {
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(userService.convertToResponse(user)));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 更新用户状态
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        userService.updateUserStatus(id, enabled);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
