package com.userdept.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.userdept.system.dto.UserRequest;
import com.userdept.system.dto.UserResponse;
import com.userdept.system.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    Page<User> getUserPage(int page, int perPage, String query);

    User getUserByUsername(String username);

    User createUser(UserRequest request);

    User updateUser(Long id, UserRequest request);

    void deleteUser(Long id);

    void updateUserStatus(Long id, boolean enabled);

    void resetPassword(Long id);

    User getById(Long id);

    UserResponse convertToResponse(User user);

    List<UserResponse> convertToResponseList(List<User> users);
}
