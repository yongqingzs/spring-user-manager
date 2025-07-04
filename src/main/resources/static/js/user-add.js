$(document).ready(function () {
    // 设置 axios CSRF Token
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    if (csrfToken && csrfHeader) {
        axios.defaults.headers.common[csrfHeader] = csrfToken;
    }
    
    // 初始化 toastr
    // toastr.options = {
    //     "closeButton": true,
    //     "progressBar": true,
    //     "positionClass": "toast-top-right",
    //     "showDuration": "300",
    //     "hideDuration": "1000",
    //     "timeOut": "5000"
    // };

    // 加载部门列表到下拉框
    loadDepartments();
    
    // 表单验证
    $('#addUserForm').validate({
        rules: {
            username: {
                required: true,
                minlength: 3,
                maxlength: 32
            },
            realname: {
                required: true,
                maxlength: 32
            },
            password: {
                required: true,
                minlength: 6,
                maxlength: 20
            },
            confirmPassword: {
                required: true,
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
            username: {
                required: "用户名为必填项",
                minlength: "用户名长度不能少于3个字符",
                maxlength: "用户名长度不能超过32个字符"
            },
            realname: {
                required: "真实姓名为必填项",
                maxlength: "真实姓名长度不能超过32个字符"
            },
            password: {
                required: "密码为必填项",
                minlength: "密码长度不能少于6个字符",
                maxlength: "密码长度不能超过20个字符"
            },
            confirmPassword: {
                required: "请确认密码",
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
            console.log("user-add error");
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
            console.log("user-add add");
            try {
                addUser();
            } catch (e) {
                console.error("添加用户过程中出错:", e);
                toastr.error("处理表单时出错: " + e.message);
                $('#btnSubmit').prop('disabled', false).html('<i class="fas fa-save"></i> 保存');
            }
        }
    });
    
    // 加载部门列表函数
    function loadDepartments() {
        console.log("正在加载部门列表...");
        
        axios.get('/api/departments', {
            params: { per_page: 1000 } // 获取所有部门
        })
            .then(function(response) {
                const res = response.data;
                console.log("部门API响应:", res);
                
                if (res.code === 200 && res.data) {
                    const departments = res.data.list || [];
                    console.log("加载到部门列表:", departments);
                    
                    const departmentsSelect = $('#departments');
                    if (departmentsSelect.length > 0) {
                        // 添加"无部门"选项
                        departmentsSelect.append('<option value="">-- 无部门 --</option>');
                        
                        // 添加部门选项
                        departments.forEach(function(dept) {
                            departmentsSelect.append(`<option value="${dept.code}">${dept.name}</option>`);
                        });
                        
                        if ($.fn.select2 && typeof departmentsSelect.select2 === 'function') {
                            departmentsSelect.select2({
                                placeholder: "-- 选择部门 --",
                                allowClear: true
                            });
                        }
                    }
                } else {
                    console.error('部门API返回错误:', res);
                    toastr.warning(res.message || '加载部门列表失败');
                }
            })
            .catch(function(error) {
                console.error('加载部门失败:', error);
                toastr.warning('加载部门列表失败，无法选择部门');
            });
    }
    
    // 添加用户函数
    function addUser() {
        // 获取表单数据
        const userData = {
            username: $('#username').val().trim(),
            realname: $('#realname').val().trim(),
            password: $('#password').val(),
            sex: parseInt($('input[name="sex"]:checked').val()),
            mobile: $('#mobile').val().trim() || '',
            idno: $('#idno').val().trim() || '',
            email: $('#email').val().trim() || '',
            status: parseInt($('input[name="status"]:checked').val()) || 0,
            user_ext: (() => {
                const extVal = $('#user_ext').val()?.trim();
                try {
                    return extVal ? JSON.parse(extVal) : null; 
                } catch (e) {
                    console.error("解析 user_ext 失败:", e);
                    return null;
                }
            })()
        };
        
        // 添加所属部门数据（如果有选择）
        const departmentSelect = $('#departments');
        if (departmentSelect.length > 0) {
            const selectedDept = departmentSelect.val();
            if (selectedDept) {
                userData.departments = Array.isArray(selectedDept) ? selectedDept : [selectedDept];
            }
        }

        console.log("提交用户数据:", userData);

        // 禁用提交按钮，避免重复提交
        $('#btnSubmit').prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> 提交中...');

        // 发送请求
        axios.post('/api/users', userData)
            .then(function(response) {
                const res = response.data;
                if (res.code === 200) {
                    console.log('API响应:', res);
                    toastr.success(res.message);
                    setTimeout(function() {
                        window.location.href = '/users';
                    }, 1500);
                } else {
                    toastr.error(res.message || '操作失败');
                    $('#btnSubmit').prop('disabled', false).html('<i class="fas fa-save"></i> 保存');
                }
            })
            .catch(function(error) {
                console.error('添加用户失败:', error.response?.data || error);
                let errorMsg = '添加用户失败，请重试';
                if (error.response?.data?.message) {
                    errorMsg = error.response.data.message;
                }
                
                toastr.error(errorMsg);
                $('#btnSubmit').prop('disabled', false).html('<i class="fas fa-save"></i> 保存');
            });
    }
});