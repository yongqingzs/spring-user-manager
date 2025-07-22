package com.userdept.system.controller;

import com.userdept.system.service.DepartmentService;
import com.userdept.system.service.UserService;
import com.userdept.system.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final DepartmentService departmentService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getStats() {
        long userCount = userService.count();
        long departmentCount = departmentService.count();
        long activeUserCount = userService.countActiveUsers();
        Map<String, Long> stats = Map.of(
                "userCount", userCount,
                "departmentCount", departmentCount,
                "activeUserCount", activeUserCount
        );
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
