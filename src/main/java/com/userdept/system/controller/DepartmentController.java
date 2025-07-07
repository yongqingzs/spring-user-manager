package com.userdept.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.userdept.system.dto.DepartmentDTO;
import com.userdept.system.entity.Department;
import com.userdept.system.entity.User;
import com.userdept.system.entity.UserDepartment;
import com.userdept.system.service.DepartmentService;
import com.userdept.system.vo.PageVO;
import com.userdept.system.vo.ResultVO;
import com.userdept.system.vo.UserDepartmentVO;
import jakarta.validation.Valid;
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
        
        // 如果需要树形结构
        if (isTree != null && isTree) {
            List<Department> departmentTree = departmentService.getDepartmentTree();
            return ResultVO.success(departmentTree);
        }
        
        // 否则返回分页数据
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
        try {
            String currentUsername = getCurrentUsername();
            Long deptId = departmentService.createDepartment(departmentDTO, currentUsername);
            return ResultVO.success("部门创建成功", deptId);
        } catch (IllegalArgumentException e) {
            // 唯一性校验失败，返回友好提示
            return ResultVO.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建部门失败", e);
            return ResultVO.error("创建部门失败: " + e.getMessage());
        }
    }

    /**
     * 更新部门
     */
    @PutMapping("/{deptId}")
    public ResultVO<Boolean> updateDepartment(
            @PathVariable Long deptId,
            @Validated(DepartmentDTO.Update.class) @RequestBody DepartmentDTO departmentDTO) {
        try {
            String currentUsername = getCurrentUsername();
            boolean success = departmentService.updateDepartment(deptId, departmentDTO, currentUsername);
            if (success) {
                return ResultVO.success("部门更新成功", true);
            } else {
                return ResultVO.error("部门不存在");
            }
        } catch (Exception e) {
            log.error("更新部门失败", e);
            return ResultVO.error("更新部门失败: " + e.getMessage());
        }
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{deptId}")
    public ResultVO<Boolean> deleteDepartment(@PathVariable Long deptId) {
        try {
            boolean success = departmentService.deleteDepartment(deptId);
            
            if (success) {
                return ResultVO.success("部门删除成功", true);
            } else {
                return ResultVO.error("部门不存在或存在子部门");
            }
        } catch (Exception e) {
            log.error("删除部门失败", e);
            return ResultVO.error("删除部门失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取部门中的用户列表（带加入时间）
     */
    @GetMapping("/{code}/users")
    public ResultVO<List<UserDepartmentVO>> getUsersInDepartment(@PathVariable String code) {
        try {
            List<UserDepartmentVO> users = departmentService.getUserDepartmentVOs(code);
            return ResultVO.success(users);
        } catch (Exception e) {
            log.error("获取部门用户失败", e);
            return ResultVO.error("获取部门用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户所属的部门列表
     */
    @GetMapping("/user/{username}")
    public ResultVO<List<Department>> getDepartmentsForUser(@PathVariable String username) {
        try {
            List<Department> departments = departmentService.getDepartmentsForUser(username);
            return ResultVO.success(departments);
        } catch (Exception e) {
            log.error("获取用户部门失败", e);
            return ResultVO.error("获取用户部门失败: " + e.getMessage());
        }
    }
    
    /**
     * 将用户分配到部门
     */
    @PostMapping("/user/{username}/{code}")
    public ResultVO<Boolean> assignUserToDepartment(
            @PathVariable String username,
            @PathVariable String code) {
        
        try {
            String currentUsername = getCurrentUsername();
            boolean success = departmentService.assignUserToDepartment(username, code, currentUsername);
            
            if (success) {
                return ResultVO.success("用户已分配至部门", true);
            } else {
                return ResultVO.error("用户已在该部门中");
            }
        } catch (Exception e) {
            log.error("分配用户到部门失败", e);
            return ResultVO.error("分配失败: " + e.getMessage());
        }
    }
    
    /**
     * 从部门中移除用户
     */
    @DeleteMapping("/user/{username}/{code}")
    public ResultVO<Boolean> removeUserFromDepartment(
            @PathVariable String username,
            @PathVariable String code) {
        
        try {
            boolean success = departmentService.removeUserFromDepartment(username, code);
            
            if (success) {
                return ResultVO.success("用户已从部门中移除", true);
            } else {
                return ResultVO.error("用户不在该部门中");
            }
        } catch (Exception e) {
            log.error("从部门移除用户失败", e);
            return ResultVO.error("移除失败: " + e.getMessage());
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
