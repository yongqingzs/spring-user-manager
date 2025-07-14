package com.userdept.system.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 主页控制器
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class IndexController {

    @Value("${system.name:望子成龙小学职工管理系统}")
    private String systemName;

    /**
     * 首页
     */
    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        model.addAttribute("systemName", systemName);
        model.addAttribute("username", authentication.getName());
        return "index";
    }

    /**
     * 用户管理页面
     */
    @GetMapping("/users")
    public String usersPage(Model model) {
        model.addAttribute("systemName", systemName);
        return "user/index";
    }

    /**
     * 添加用户页面
     */
    @GetMapping("/users/add")
    public String addUserPage(Model model) {
        model.addAttribute("systemName", systemName);
        return "user/add";
    }

    /**
     * 编辑用户页面
     */
    @GetMapping("/users/edit/{userId}")
    public String editUserPage(@PathVariable Long userId, Model model) {
        model.addAttribute("systemName", systemName);
        model.addAttribute("userId", userId);
        return "user/edit";
    }

    /**
     * 部门管理页面
     */
    @GetMapping("/departments")
    public String departmentsPage(Model model) {
        model.addAttribute("systemName", systemName);
        return "department/index";
    }

    /**
     * 添加部门页面
     */
    @GetMapping("/departments/add")
    public String addDepartmentPage(Model model) {
        model.addAttribute("systemName", systemName);
        return "department/add";
    }

    /**
     * 编辑部门页面
     */
    @GetMapping("/departments/edit/{deptId}")
    public String editDepartmentPage(@PathVariable Long deptId, Model model) {
        model.addAttribute("systemName", systemName);
        model.addAttribute("deptId", deptId);
        return "department/edit";
    }
}
