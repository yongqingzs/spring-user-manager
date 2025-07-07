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

    // 加载父部门下拉框选项
    loadParentDepartments();

    // 表单验证
    $('#addDepartmentForm').validate({
        rules: {
            code: {
                required: true,
                maxlength: 32
            },
            name: {
                required: true,
                maxlength: 32
            },
            description: {
                maxlength: 256
            }
        },
        messages: {
            code: {
                required: "部门编码为必填项",
                maxlength: "部门编码长度不能超过32个字符"
            },
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
            addDepartment();
        }
    });

    // 添加部门函数
    function addDepartment() {
        // 获取表单数据
        const formData = {
            code: $('#code').val().trim(),
            name: $('#name').val().trim(),
            parentCode: $('#parentCode').val() || null,
            description: $('#description').val().trim() || null
        };

        console.log('提交部门数据:', formData);
        
        // 禁用提交按钮
        $('button[type="submit"]').prop('disabled', true)
            .html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> 提交中...');
        
        // 发送创建部门请求 - 使用 Axios
        axios.post('/api/departments', formData)
            .then(function(response) {
                const res = response.data;
                if (res.code === 200) {
                    toastr.success(res.message || '部门创建成功');
                    
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
                console.error('创建部门失败:', error);
                let errorMsg = '创建部门失败，请重试';
                if (error.response?.data?.message) {
                    errorMsg = error.response.data.message;
                }
                
                $('#errorMessage').removeClass('d-none').text(errorMsg);
                $('button[type="submit"]').prop('disabled', false).html('<i class="fas fa-save"></i> 保存');
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
                const res = response.data;
                
                if (res.code === 200 && res.data) {
                    const departments = res.data.list || [];
                    const parentSelect = $('#parentCode');
                    
                    // 已经有默认的空选项，不需要再添加
                    
                    // 添加部门选项
                    departments.forEach(function(dept) {
                        parentSelect.append(`<option value="${dept.code}">${dept.name} (${dept.code})</option>`);
                    });
                } else {
                    console.error('父部门API返回错误:', res);
                    toastr.warning(res.message || '加载父部门列表失败');
                }
            })
            .catch(function(error) {
                console.error('加载父部门失败:', error);
                toastr.warning('加载父部门列表失败，只能创建顶级部门');
            });
    }
    
    // 监听部门名称输入，自动生成编码（可选功能）
    $('#name').on('blur', function() {
        const name = $(this).val().trim();
        if (name && !$('#code').val()) {
            // 简单的拼音转换模拟，实际可以接入拼音库
            const code = 'D_' + name.replace(/\s+/g, '_').substring(0, 20).toUpperCase();
            $('#code').val(code);
        }
    });
});