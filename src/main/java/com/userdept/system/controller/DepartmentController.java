package com.userdept.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.userdept.system.dto.DepartmentDTO;
import com.userdept.system.entity.Department;
import com.userdept.system.service.DepartmentService;
import com.userdept.system.vo.PageVO;
import com.userdept.system.vo.ResultVO;
import com.userdept.system.vo.UserDepartmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 获取部门列表
     */
    @GetMapping
    public ResultVO<?> getDepartmentList(
            @RequestParam(value = "tree", required = false) Boolean isTree,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "10") int perPage,
            @RequestParam(value = "search", required = false) String search) {
        if (isTree != null && isTree) {
            List<Department> departmentTree = departmentService.getDepartmentTree();
            return ResultVO.success(departmentTree);
        }
        Page<Department> departmentPage = departmentService.getDepartmentPage(page, perPage, search);
        PageVO<Department> pageVO = new PageVO<>(
                departmentPage.getTotal(),
                page,
                perPage,
                departmentPage.getRecords()
        );
        return ResultVO.success(pageVO);
    }

    /**
     * 获取部门详情
     */
    @GetMapping("/{deptId}")
    public ResultVO<Department> getDepartment(@PathVariable Long deptId) {
        Department department = departmentService.getById(deptId);
        if (department == null) {
            return ResultVO.error("部门不存在");
        }
        return ResultVO.success(department);
    }

    /**
     * 创建部门
     */
    @PostMapping
    public ResultVO<Long> createDepartment(@Validated(DepartmentDTO.Create.class) @RequestBody DepartmentDTO departmentDTO) {
        String currentUsername = getCurrentUsername();
        Long deptId = departmentService.createDepartment(departmentDTO, currentUsername);
        return ResultVO.success("部门创建成功", deptId);
    }

    /**
     * 更新部门
     */
    @PutMapping("/{deptId}")
    public ResultVO<Boolean> updateDepartment(
            @PathVariable Long deptId,
            @Validated(DepartmentDTO.Update.class) @RequestBody DepartmentDTO departmentDTO) {
        String currentUsername = getCurrentUsername();
        boolean success = departmentService.updateDepartment(deptId, departmentDTO, currentUsername);
        if (success) {
            return ResultVO.success("部门更新成功", true);
        } else {
            return ResultVO.error("部门不存在");
        }
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{deptId}")
    public ResultVO<Boolean> deleteDepartment(@PathVariable Long deptId) {
        boolean success = departmentService.deleteDepartment(deptId);
        if (success) {
            return ResultVO.success("部门删除成功", true);
        } else {
            return ResultVO.error("部门不存在或存在子部门");
        }
    }

    /**
     * 获取部门中的用户列表（带加入时间）
     */
    @GetMapping("/{deptCode}/users")
    public ResultVO<List<UserDepartmentVO>> getUsersInDepartment(@PathVariable String deptCode) {
        List<UserDepartmentVO> users = departmentService.getUserDepartmentVOs(deptCode);
        return ResultVO.success(users);
    }

    /**
     * 获取用户所属的部门列表
     */
    @GetMapping("/user/{username}")
    public ResultVO<List<Department>> getDepartmentsForUser(@PathVariable String username) {
        List<Department> departments = departmentService.getDepartmentsForUser(username);
        return ResultVO.success(departments);
    }

    /**
     * 将用户分配到部门
     */
    @PostMapping("/user/{username}/{deptCode}")
    public ResultVO<Boolean> assignUserToDepartment(
            @PathVariable String username,
            @PathVariable String deptCode) {
        String currentUsername = getCurrentUsername();
        boolean success = departmentService.assignUserToDepartment(username, deptCode, currentUsername);
        if (success) {
            return ResultVO.success("用户已分配至部门", true);
        } else {
            return ResultVO.error("用户已在该部门中");
        }
    }

    /**
     * 从部门中移除用户
     */
    @DeleteMapping("/user/{username}/{deptCode}")
    public ResultVO<Boolean> removeUserFromDepartment(
            @PathVariable String username,
            @PathVariable String deptCode) {
        boolean success = departmentService.removeUserFromDepartment(username, deptCode);
        if (success) {
            return ResultVO.success("用户已从部门中移除", true);
        } else {
            return ResultVO.error("用户不在该部门中");
        }
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
