package com.userdept.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.userdept.system.dto.DepartmentDTO;
import com.userdept.system.entity.Department;
import com.userdept.system.service.DepartmentService;
import com.userdept.system.vo.ApiResponse;
import com.userdept.system.vo.PageVO;
import com.userdept.system.vo.UserDepartmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 获取部门列表（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageVO<Department>>> getDepartmentPage(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "10") int perPage,
            @RequestParam(value = "search", required = false) String search) {
        Page<Department> departmentPage = departmentService.getDepartmentPage(page, perPage, search);
        PageVO<Department> pageVO = new PageVO<>(
                departmentPage.getTotal(),
                page,
                perPage,
                departmentPage.getRecords()
        );
        return ResponseEntity.ok(ApiResponse.success(pageVO));
    }

    /**
     * 获取部门树
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<Department>>> getDepartmentTree() {
        List<Department> departmentTree = departmentService.getDepartmentTree();
        return ResponseEntity.ok(ApiResponse.success(departmentTree));
    }

    /**
     * 获取部门详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> getDepartment(@PathVariable Long id) {
        Department department = departmentService.getById(id);
        if (department == null) {
            return ResponseEntity.ok(ApiResponse.error("部门不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(department));
    }

    /**
     * 创建部门
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Department>> createDepartment(@Validated(DepartmentDTO.Create.class) @RequestBody DepartmentDTO departmentDTO) {
        Department department = departmentService.createDepartment(departmentDTO);
        return ResponseEntity.ok(ApiResponse.success(department));
    }

    /**
     * 更新部门
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> updateDepartment(
            @PathVariable Long id,
            @Validated(DepartmentDTO.Update.class) @RequestBody DepartmentDTO departmentDTO) {
        Department department = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity.ok(ApiResponse.success(department));
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 获取部门中的用户列表（带加入时间）
     */
    @GetMapping("/{deptCode}/users")
    public ResponseEntity<ApiResponse<List<UserDepartmentVO>>> getUsersInDepartment(@PathVariable String deptCode) {
        List<UserDepartmentVO> users = departmentService.getUserDepartmentVOs(deptCode);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
}
