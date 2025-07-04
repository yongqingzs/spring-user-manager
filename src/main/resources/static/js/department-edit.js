$(document).ready(function() {
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
    
    // 获取部门ID - 从表单的 data-dept-id 属性获取
    const deptId = $('#editDepartmentForm').data('dept-id');
    console.log("正在编辑部门 ID:", deptId);
    
    // 加载部门信息
    loadDepartmentInfo(deptId);
    
    // 表单验证
    $('#editDepartmentForm').validate({
        rules: {
            name: {
                required: true,
                maxlength: 32
            },
            description: {
                maxlength: 256
            }
        },
        messages: {
            name: {
                required: "部门名称为必填项",
                maxlength: "部门名称长度不能超过32个字符"
            },
            description: {
                maxlength: "描述不能超过256个字符"
            }
        },
        errorElement: 'div',
        errorPlacement: function (error, element) {
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
            updateDepartment();
        }
    });
    
    // 加载部门信息函数
    function loadDepartmentInfo(deptId) {
        axios.get(`/api/departments/${deptId}`)
            .then(function(response) {
                const department = response.data;
                console.log("加载到部门数据:", department);
                
                // 填充表单字段
                $('#code').val(department.code);
                $('#name').val(department.name);
                $('#description').val(department.description || '');
                
                // 保存部门数据，以便后续使用
                window.departmentData = department;
                
                // 加载父部门下拉框选项
                loadParentDepartments();
            })
            .catch(function(error) {
                console.error('加载部门信息失败:', error);
                let errorMsg = '加载部门信息失败，请重试';
                if (error.response?.data?.message) {
                    errorMsg = error.response.data.message;
                }
                
                toastr.error(errorMsg);
                setTimeout(function() {
                    window.location.href = '/departments';
                }, 1500);
            });
    }
    
    // 加载父部门列表
    function loadParentDepartments() {
        axios.get('/api/departments', {
            params: {
                per_page: 1000 // 获取足够多的部门以供选择
            }
        })
            .then(function(response) {
                const departments = response.data.departments || [];
                const parentSelect = $('#parentCode');
                
                // 添加部门选项，但排除当前部门及其子部门
                departments.forEach(function(dept) {
                    // 如果当前部门不是自己，且不是当前部门的子部门
                    if (window.departmentData && dept.code !== window.departmentData.code) {
                        // 检查是否可能形成循环引用
                        let canBeParent = true;
                        let currentDept = dept;
                        
                        // 检查这个部门的父部门链是否包含当前部门
                        while (currentDept && currentDept.parent_code) {
                            if (currentDept.parent_code === window.departmentData.code) {
                                canBeParent = false;
                                break;
                            }
                            
                            // 在实际应用中，这需要额外的API调用来获取父部门信息
                            // 此处简化处理
                            const parentDept = departments.find(d => d.code === currentDept.parent_code);
                            if (!parentDept) break;
                            currentDept = parentDept;
                        }
                        
                        if (canBeParent) {
                            parentSelect.append(`<option value="${dept.code}">${dept.name} (${dept.code})</option>`);
                        }
                    }
                });
                
                // 如果已加载了部门数据，则设置父部门选择
                if (window.departmentData && window.departmentData.parent_code) {
                    parentSelect.val(window.departmentData.parent_code);
                }
            })
            .catch(function(error) {
                console.error('加载父部门失败:', error);
                toastr.warning('加载父部门列表失败');
            });
    }
    
    // 更新部门函数
    function updateDepartment() {
        // 获取表单数据
        const formData = {
            name: $('#name').val().trim(),
            parent_code: $('#parentCode').val() || null,
            description: $('#description').val().trim() || null
        };
        
        console.log('更新部门数据:', formData);
        
        // 验证父部门不是自己
        if (formData.parent_code === $('#code').val()) {
            toastr.error('不能选择自己作为父部门');
            return;
        }
        
        // 禁用提交按钮
        $('button[type="submit"]').prop('disabled', true)
            .html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> 提交中...');
        
        // 发送更新部门请求
        axios.put(`/api/departments/${deptId}`, formData)
            .then(function(response) {
                const res = response.data;
                if (res.success) {
                    toastr.success(res.message || '部门更新成功');
                    
                    // 延迟跳转到部门列表页
                    setTimeout(function() {
                        window.location.href = '/departments';
                    }, 1500);
                } else {
                    $('#errorMessage').removeClass('d-none').text(res.message || '操作失败');
                    $('button[type="submit"]').prop('disabled', false).html('<i class="fas fa-save"></i> 保存');
                }
            })
            .catch(function(error) {
                console.error('更新部门失败:', error);
                let errorMsg = '更新部门失败，请重试';
                if (error.response?.data?.message) {
                    errorMsg = error.response.data.message;
                }
                
                $('#errorMessage').removeClass('d-none').text(errorMsg);
                $('button[type="submit"]').prop('disabled', false).html('<i class="fas fa-save"></i> 保存');
            });
    }
});