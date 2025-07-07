$(document).ready(function () {
    // 设置 axios CSRF Token
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    if (csrfToken && csrfHeader) {
        axios.defaults.headers.common[csrfHeader] = csrfToken;
    }
    
    // 获取用户ID - 从表单的 data-user-id 属性获取
    const userId = $('#editUserForm').data('user-id');
    console.log("正在编辑用户 ID:", userId);
    
    // 首先加载用户数据
    loadUserData(userId);
    
    // 表单验证
    $('#editUserForm').validate({
        rules: {
            realname: {
                required: true,
                maxlength: 32
            },
            password: {
                minlength: 6,
                maxlength: 20
            },
            confirmPassword: {
                equalTo: "#password"
            },
            mobile: {
                maxlength: 64
            },
            idno: {
                maxlength: 128
            },
            email: {
                email: true,
                maxlength: 128
            }
        },
        messages: {
            realname: {
                required: "真实姓名为必填项",
                maxlength: "真实姓名长度不能超过32个字符"
            },
            password: {
                minlength: "密码长度不能少于6个字符",
                maxlength: "密码长度不能超过20个字符"
            },
            confirmPassword: {
                equalTo: "两次输入的密码不一致"
            },
            mobile: {
                maxlength: "手机号长度不能超过64个字符"
            },
            idno: {
                maxlength: "身份证号长度不能超过128个字符"
            },
            email: {
                email: "请输入有效的邮箱地址",
                maxlength: "邮箱长度不能超过128个字符"
            }
        },
        errorElement: 'div',
        errorPlacement: function (error, element) {
            console.log("user-edit error");
            error.addClass('invalid-feedback');
            element.closest('.mb-3').append(error);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).addClass('is-invalid').removeClass('is-valid');
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).addClass('is-valid').removeClass('is-invalid');
        },
        submitHandler: function(form) {
            console.log("user-edit update");
            try {
                updateUser();
            } catch (e) {
                console.error("更新用户过程中出错:", e);
                toastr.error("处理表单时出错: " + e.message);
                $('#btnSubmit').prop('disabled', false).html('<i class="fas fa-save"></i> 保存');
            }
        }
    });
    
    // 加载用户数据函数
    function loadUserData(userId) {
        console.log("正在加载用户数据...");
        
        axios.get(`/api/users/${userId}`)
            .then(function(response) {
                const res = response.data;
                console.log("用户API响应:", res);
                
                if (res.code === 200 && res.data) {
                    const userData = res.data;
                    console.log("加载到用户数据:", userData);
                    
                    // 填充表单字段
                    $('#username').val(userData.username);
                    if (userData.username) {
                        // 用户名通常不允许修改，设为只读
                        $('#username').prop('readonly', true);
                    }
                    
                    $('#realname').val(userData.realname || '');
                    
                    // 设置性别选项
                    if (userData.sex !== undefined) {
                        $(`input[name="sex"][value="${userData.sex}"]`).prop('checked', true);
                    }
                    
                    $('#mobile').val(userData.mobile || '');
                    $('#idno').val(userData.idno || '');
                    $('#email').val(userData.email || '');
                    
                    // 设置状态选项
                    if (userData.status !== undefined) {
                        $(`input[name="status"][value="${userData.status}"]`).prop('checked', true);
                    }
                    
                    // 处理用户扩展信息
                    if (userData.user_ext) {
                        try {
                            let userExt = userData.user_ext;
                            if (typeof userExt === 'string') {
                                userExt = JSON.parse(userExt);
                            }
                            $('#user_ext').val(JSON.stringify(userExt, null, 2));
                        } catch (e) {
                            console.error("解析用户扩展信息失败:", e);
                            $('#user_ext').val('');
                        }
                    }
                    
                    // 加载用户部门
                    if ($('#departments').length > 0) {
                        loadDepartments(userData.username);
                    }
                    
                    // 表单加载完成，去除加载状态
                    $('#formLoader').hide();
                    $('#editUserForm').removeClass('d-none');
                    
                    toastr.success("用户数据加载完成");
                } else {
                    console.error('用户API返回错误:', res);
                    toastr.error(res.message || '加载用户数据失败');
                }
            })
            .catch(function(error) {
                console.error('加载用户数据失败:', error.response?.data || error);
                toastr.error('加载用户数据失败，请重试');
                
                // 显示错误信息
                $('#formLoader').hide();
                $('#loadError').removeClass('d-none')
                    .html(`<div class="alert alert-danger">
                        <i class="fas fa-exclamation-circle"></i> 加载用户数据失败: 
                        ${error.response?.data?.message || error.message || '未知错误'}
                    </div>`);
            });
    }
    
    // 加载部门列表
    function loadDepartments(username) {
        console.log("正在加载部门列表...");
        
        axios.get('/api/departments')
            .then(function(response) {
                const res = response.data;
                console.log("部门API响应:", res);
                
                if (res.code === 200 && res.data) {
                    const departments = res.data.list || [];
                    console.log("加载到部门列表:", departments);
                    
                    const departmentsSelect = $('#departments');
                    departmentsSelect.empty();
                    
                    // 添加"无部门"选项
                    departmentsSelect.append('<option value="">-- 无部门 --</option>');
                    
                    departments.forEach(function(dept) {
                        departmentsSelect.append(new Option(dept.name, dept.code));
                    });
                    
                    // 加载用户已有的部门
                    loadUserDepartments(userId);
                } else {
                    console.error('部门API返回错误:', res);
                    toastr.error(res.message || '加载部门数据失败');
                }
            })
            .catch(function(error) {
                console.error('加载部门失败:', error);
                toastr.error('加载部门数据失败');
            });
    }
    
    // 加载用户部门
    function loadUserDepartments(userId) {
        console.log("正在加载用户部门...");
        axios.get(`/api/users/${userId}/departments`)
            .then(function(response) {
                // 兼容后端返回格式
                let userDepts = [];
                if (Array.isArray(response.data.data)) {
                    userDepts = response.data.data;
                } else if (Array.isArray(response.data.departments)) {
                    userDepts = response.data.departments;
                }
                console.log("加载到用户部门:", userDepts);
                const deptSelect = $('#departments');
                // 设置已选部门
                userDepts.forEach(function(dept) {
                    deptSelect.find(`option[value="${dept.code}"]`).prop('selected', true);
                });
                // 如果使用了 Select2 插件，需要更新
                if ($.fn.select2 && deptSelect.data('select2')) {
                    deptSelect.trigger('change');
                }
            })
            .catch(function(error) {
                console.error('加载用户部门失败:', error);
                toastr.warning('加载用户部门失败，部门关联可能不完整');
            });
    }
    
    // 更新用户函数
    function updateUser() {
        console.log("正在更新用户数据...");
        
        // 安全获取值的辅助函数
        function safeVal(selector) {
            const el = $(selector);
            return el.length > 0 ? (el.val() || '') : '';
        }
        
        function safeValTrim(selector) {
            return safeVal(selector).trim();
        }
        
        function safeCheckedVal(selector, defaultVal = 0) {
            const checked = $(selector + ':checked');
            return checked.length > 0 ? parseInt(checked.val()) : defaultVal;
        }
        
        // 获取表单数据
        const userData = {
            realname: safeValTrim('#realname'),
            sex: safeCheckedVal('input[name="sex"]', 1),
            mobile: safeValTrim('#mobile'),
            idno: safeValTrim('#idno'),
            email: safeValTrim('#email'),
            status: safeCheckedVal('input[name="status"]', 1)
        };
        
        // 如果有部门选择器，添加部门信息
        if ($('#departments').length > 0) {
            userData.departments = $('#departments').val() || [];
        }
        
        // 如果输入了密码，添加到请求数据中
        const password = safeVal('#password');
        if (password) {
            userData.password = password;
        }
        
        // 处理用户扩展信息
        const userExtVal = safeValTrim('#user_ext');
        if (userExtVal) {
            try {
                userData.user_ext = JSON.parse(userExtVal);
            } catch (e) {
                console.error("解析 user_ext 失败:", e);
                toastr.error("用户扩展信息格式不正确");
                return;
            }
        }
        
        console.log("更新用户数据:", userData);
        
        // 禁用提交按钮，避免重复提交
        $('#btnSubmit').prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> 提交中...');
        
        // 使用 Axios 提交请求
        axios.put(`/api/users/${userId}`, userData)
            .then(function(response) {
                const res = response.data;
                console.log("更新用户响应:", res);
                
                if (res.code === 200) {
                    toastr.success(res.message || '更新用户成功');
                    
                    // 延迟跳转回用户列表页
                    setTimeout(function() {
                        window.location.href = '/users';
                    }, 1500);
                } else {
                    toastr.error(res.message || '操作失败');
                    $('#btnSubmit').prop('disabled', false).html('<i class="fas fa-save"></i> 保存');
                }
            })
            .catch(function(error) {
                console.error('更新用户失败:', error.response?.data || error);
                let errorMsg = '更新用户失败，请重试';
                if (error.response?.data?.message) {
                    errorMsg = error.response.data.message;
                }
                
                toastr.error(errorMsg);
                $('#btnSubmit').prop('disabled', false).html('<i class="fas fa-save"></i> 保存');
            });
    }
    
    // 返回按钮事件
    $('#btnBack').on('click', function() {
        window.location.href = '/users';
    });
});