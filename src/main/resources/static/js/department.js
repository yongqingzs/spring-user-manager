$(document).ready(function() {
    // 设置 axios CSRF Token
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    if (csrfToken && csrfHeader) {
        axios.defaults.headers.common[csrfHeader] = csrfToken;
    }
    
    // 初始化变量
    let currentPage = 1;
    const pageSize = 10;
    let totalPages = 0;
    let departments = [];

    // 初始化页面
    loadDepartments(currentPage);
    
    // 搜索按钮点击事件
    $('#searchBtn').click(function() {
        // console.log("搜索按钮被点击");
        currentPage = 1;
        loadDepartments(currentPage);
    });

    // 添加部门按钮点击事件 - 直接在HTML中使用链接

    // 分页事件处理
    $(document).on('click', '.pagination-link', function(e) {
        e.preventDefault();
        const page = $(this).data('page');
        if (page !== currentPage) {
            currentPage = page;
            loadDepartments(page);
        }
    });

    // 视图切换
    $('#btnTreeView').click(function() {
        $(this).addClass('active');
        $('#btnListView').removeClass('active');
        $('#listViewContainer').addClass('d-none');
        $('#treeViewContainer').removeClass('d-none');
        loadDepartmentTree();
    });

    $('#btnListView').click(function() {
        $(this).addClass('active');
        $('#btnTreeView').removeClass('active');
        $('#treeViewContainer').addClass('d-none');
        $('#listViewContainer').removeClass('d-none');
    });

    // 编辑部门事件
    $(document).on('click', '.btn-edit', function() {
        const deptId = $(this).data('id');
        window.location.href = `/departments/edit/${deptId}`;
    });
    
    // 查看部门详情事件
    $(document).on('click', '.btn-view', function() {
        const deptId = $(this).data('id');
        const deptCode = $(this).data('code');
        viewDepartmentDetail(deptId, deptCode);
    });

    // 删除部门事件
    $(document).on('click', '.btn-delete', function() {
        const deptId = $(this).data('id');
        const deptName = $(this).data('name');
        
        // 设置模态框内容
        $('#deleteDepartmentName').text(deptName);
        
        // 显示模态框
        const deleteModal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
        deleteModal.show();
        
        // 确认删除按钮点击事件
        $('#confirmDeleteBtn').off('click').on('click', function() {
            deleteDepartment(deptId, deleteModal);
        });
    });

    // 加载部门列表数据
    function loadDepartments(page) {
        const searchQuery = $('#searchInput').val().trim();
        
        axios.get('/api/departments', {
            params: {
                page: page,
                per_page: pageSize,
                search: searchQuery
            }
        })
            .then(function(response) {
                // console.log("get /api/departments response:", response);
                const res = response.data;
                
                if (res.code === 200 && res.data) {
                    const pageData = res.data;
                    departments = pageData.list || [];
                    totalPages = Math.ceil(pageData.total / pageSize);
                    
                    console.log('加载部门数据:', departments);

                    renderDepartmentTable(departments);
                    renderPagination(page, totalPages);
                    
                    // 更新记录总数显示
                    $('#totalRecords').text(`共 ${pageData.total || 0} 条记录`);
                } else {
                    console.error('部门API返回错误:', res);
                    toastr.error(res.message || '加载部门数据失败');
                }
            })
            .catch(function(error) {
                console.error('加载部门失败:', error);
                let errorMsg = '加载部门数据失败';
                if (error.response?.data?.message) {
                    errorMsg = error.response.data.message;
                }
                
                toastr.error(errorMsg);
            });
    }

    // 渲染部门表格
    function renderDepartmentTable(departments) {
        const tbody = $('#departmentTableBody');
        tbody.empty();

        if (!departments || departments.length === 0) {
            tbody.append('<tr><td colspan="6" class="text-center">暂无部门数据</td></tr>');
            return;
        }

        departments.forEach(function(department) {
            // 格式化创建时间
            const createTime = department.createdTime ? new Date(department.createdTime).toLocaleDateString() : '-';
            
            const row = `
                <tr>
                    <td>${department.id}</td>
                    <td>${department.code}</td>
                    <td>${department.name}</td>
                    <td>${department.parentCode || department.parent_code || '无'}</td>
                    <td>${createTime}</td>
                    <td class="department-actions">
                        <button type="button" class="btn btn-sm btn-info btn-view" data-id="${department.id}" data-code="${department.code}">
                            <i class="fas fa-eye"></i> 查看
                        </button>
                        <button type="button" class="btn btn-sm btn-primary btn-edit" data-id="${department.id}">
                            <i class="fas fa-edit"></i> 编辑
                        </button>
                        <button type="button" class="btn btn-sm btn-danger btn-delete" data-id="${department.id}" data-name="${department.name}">
                            <i class="fas fa-trash"></i> 删除
                        </button>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
    }

    // 渲染分页
    function renderPagination(currentPage, totalPages) {
        const pagination = $('#pagination');
        pagination.empty();
        
        if (totalPages <= 1) {
            return;
        }
        
        let paginationHTML = '<ul class="pagination justify-content-center">';
        
        // 上一页按钮
        if (currentPage > 1) {
            paginationHTML += `
                <li class="page-item">
                    <a class="page-link pagination-link" href="#" data-page="${currentPage - 1}">上一页</a>
                </li>
            `;
        } else {
            paginationHTML += `
                <li class="page-item disabled">
                    <a class="page-link" href="#" tabindex="-1">上一页</a>
                </li>
            `;
        }
        
        // 页码按钮
        let startPage = Math.max(1, currentPage - 2);
        let endPage = Math.min(totalPages, currentPage + 2);
        
        // 确保显示5个页码按钮（如果可能）
        if (endPage - startPage + 1 < 5 && totalPages >= 5) {
            if (startPage === 1) {
                endPage = Math.min(5, totalPages);
            } else if (endPage === totalPages) {
                startPage = Math.max(1, totalPages - 4);
            }
        }
        
        for (let i = startPage; i <= endPage; i++) {
            if (i === currentPage) {
                paginationHTML += `
                    <li class="page-item active">
                        <a class="page-link" href="#">${i}</a>
                    </li>
                `;
            } else {
                paginationHTML += `
                    <li class="page-item">
                        <a class="page-link pagination-link" href="#" data-page="${i}">${i}</a>
                    </li>
                `;
            }
        }
        
        // 下一页按钮
        if (currentPage < totalPages) {
            paginationHTML += `
                <li class="page-item">
                    <a class="page-link pagination-link" href="#" data-page="${currentPage + 1}">下一页</a>
                </li>
            `;
        } else {
            paginationHTML += `
                <li class="page-item disabled">
                    <a class="page-link" href="#" tabindex="-1">下一页</a>
                </li>
            `;
        }
        
        paginationHTML += '</ul>';
        pagination.html(paginationHTML);
    }

    // 删除部门
    function deleteDepartment(deptId, modal) {
        axios.delete(`/api/departments/${deptId}`)
            .then(function(response) {
                const res = response.data;
                if (res.code === 200) {
                    toastr.success(res.message || '删除部门成功');
                    
                    // 关闭模态框
                    modal.hide();
                    
                    // 重新加载当前页数据，如果当前页没有数据了则加载上一页
                    if ($('#departmentTableBody tr').length === 1 && currentPage > 1) {
                        currentPage--;
                    }
                    loadDepartments(currentPage);
                } else {
                    toastr.error(res.message || '删除部门失败');
                    modal.hide();
                }
            })
            .catch(function(error) {
                console.error('删除部门失败:', error);
                let errorMsg = '删除部门失败';
                if (error.response?.data?.message) {
                    errorMsg = error.response.data.message;
                }
                
                toastr.error(errorMsg);
                modal.hide();
            });
    }

    // 查看部门详情
    function viewDepartmentDetail(deptId, deptCode) {
        // 加载部门详情
        axios.get(`/api/departments/${deptId}`)
            .then(function(response) {
                // 兼容后端返回格式
                let dept = response.data.data || response.data;
                // 填充详情模态框
                $('#detailCode').text(dept.code);
                $('#detailName').text(dept.name);
                $('#detailParentCode').text(dept.parentCode || dept.parent_code || '无');
                $('#detailCreatedTime').text(dept.createdTime ? new Date(dept.createdTime).toLocaleDateString() : '-');
                $('#detailDescription').text(dept.description || '无');
                
                // 加载部门用户
                loadDepartmentUsers(deptCode);
                
                // 加载可添加的用户列表
                loadAvailableUsers(deptCode);
                
                // 显示模态框
                const modal = new bootstrap.Modal(document.getElementById('departmentDetailModal'));
                modal.show();
                
                // 添加用户到部门事件
                $('#addUserToDeptForm').off('submit').on('submit', function(e) {
                    e.preventDefault();
                    const username = $('#userToAdd').val();
                    if (!username) {
                        toastr.warning('请选择要添加的用户');
                        return;
                    }
                    
                    addUserToDepartment(username, deptCode);
                });
            })
            .catch(function(error) {
                console.error('加载部门详情失败:', error);
                toastr.error('加载部门详情失败');
            });
    }

    // 加载部门用户
    function loadDepartmentUsers(deptCode) {
        axios.get(`/api/departments/${deptCode}/users`)
            .then(function(response) {
                const users = response.data.data || [];
                const tbody = $('#departmentUsers');
                tbody.empty();
                
                if (users.length === 0) {
                    $('#departmentUsers').hide();
                    $('#noDepartmentUsers').removeClass('d-none');
                } else {
                    $('#departmentUsers').show();
                    $('#noDepartmentUsers').addClass('d-none');
                    
                    users.forEach(function(user) {
                        const row = `
                            <tr>
                                <td>${user.username}</td>
                                <td>${user.realname || '-'}</td>
                                <td>${user.addedTime ? new Date(user.addedTime).toLocaleDateString() : '-'}</td>
                                <td>
                                    <button type="button" class="btn btn-sm btn-danger btn-remove-user" 
                                        data-username="${user.username}" 
                                        data-realname="${user.realname || user.username}"
                                        data-dept-code="${deptCode}">
                                        <i class="fas fa-user-minus"></i> 移除
                                    </button>
                                </td>
                            </tr>
                        `;
                        tbody.append(row);
                    });
                    
                    // 移除用户事件
                    $('.btn-remove-user').off('click').on('click', function() {
                        const username = $(this).data('username');
                        const realname = $(this).data('realname');
                        const deptCode = $(this).data('dept-code');
                        
                        // 设置模态框内容
                        $('#removeUserName').text(`${realname} (${username})`);
                        $('#removeDepartmentName').text($('#detailName').text());
                        
                        // 显示模态框
                        const removeModal = new bootstrap.Modal(document.getElementById('removeUserConfirmModal'));
                        removeModal.show();
                        
                        // 确认移除按钮点击事件
                        $('#confirmRemoveBtn').off('click').on('click', function() {
                            removeUserFromDepartment(username, deptCode, removeModal);
                        });
                    });
                }
            })
            .catch(function(error) {
                console.error('加载部门用户失败:', error);
                toastr.error('加载部门用户失败');
            });
    }

    // 加载可添加的用户列表
    function loadAvailableUsers(deptCode) {
        axios.get('/api/users', {
            params: {
                per_page: 1000 // 获取足够多的用户
            }
        })
            .then(function(response) {
                // 兼容分页和非分页格式
                let users = [];
                if (response.data.data) {
                    if (Array.isArray(response.data.data.list)) {
                        users = response.data.data.list;
                    } else if (Array.isArray(response.data.data.records)) {
                        users = response.data.data.records;
                    } else if (Array.isArray(response.data.data)) {
                        users = response.data.data;
                    }
                }
                const select = $('#userToAdd');
                select.empty();
                select.append('<option value="">选择用户...</option>');
                users.forEach(function(user) {
                    select.append(`<option value="${user.username}">${user.username} (${user.realname || '未设置姓名'})</option>`);
                });
            })
            .catch(function(error) {
                console.error('加载用户列表失败:', error);
                toastr.warning('加载用户列表失败');
            });
    }

    // 移除用户从部门
    function removeUserFromDepartment(username, deptCode, modal) {
        axios.delete(`/api/departments/user/${username}/${deptCode}`)
            .then(function(response) {
                const res = response.data;
                if (res.code === 200) {
                    toastr.success(res.message || '移除用户成功');
                    modal.hide();
                    // 重新加载部门用户
                    loadDepartmentUsers(deptCode);
                    // 刷新可添加用户列表
                    loadAvailableUsers(deptCode);
                } else {
                    toastr.error(res.message || '移除用户失败');
                    modal.hide();
                }
            })
            .catch(function(error) {
                console.error('移除用户失败:', error);
                let errorMsg = '移除用户失败';
                if (error.response?.data?.message) {
                    errorMsg = error.response.data.message;
                }
                toastr.error(errorMsg);
                modal.hide();
            });
    }

    // 添加用户到部门
    function addUserToDepartment(username, deptCode) {
        axios.post(`/api/departments/user/${username}/${deptCode}`)
            .then(function(response) {
                const res = response.data;
                if (res.code === 200) {
                    toastr.success(res.message || '添加用户成功');
                    // 清空选择
                    $('#userToAdd').val('');
                    // 重新加载部门用户
                    loadDepartmentUsers(deptCode);
                } else {
                    toastr.error(res.message || '添加用户失败');
                }
            })
            .catch(function(error) {
                console.error('添加用户失败:', error);
                let errorMsg = '添加用户失败';
                if (error.response?.data?.message) {
                    errorMsg = error.response.data.message;
                }
                toastr.error(errorMsg);
            });
    }

    // 加载部门树结构
    function loadDepartmentTree() {
        axios.get('/api/departments', {
            params: { tree: true }
        })
            .then(function(response) {
                const res = response.data;
                
                if (res.code === 200 && res.data) {
                    // 树形结构直接在 res.data
                    const depts = res.data || [];
                    const treeContainer = $('#departmentTree');
                    treeContainer.empty();
                    
                    if (depts.length === 0) {
                        treeContainer.html('<div class="text-center py-3">暂无部门数据</div>');
                    } else {
                        treeContainer.html(buildTreeHtml(depts));
                        
                        // 为树节点添加展开/折叠事件
                        $('.department-tree .caret').click(function() {
                            $(this).parent().find('.nested').first().toggleClass('active');
                            $(this).toggleClass('caret-down');
                        });
                    }
                } else {
                    console.error('部门树API返回错误:', res);
                    toastr.error(res.message || '加载部门树失败');
                }
            })
            .catch(function(error) {
                console.error('加载部门树失败:', error);
                toastr.error('加载部门树结构失败');
            });
    }

    // 构建树HTML结构
    function buildTreeHtml(departments, level = 0) {
        let html = '';
        if (!departments || departments.length === 0) {
            return html;
        }

        html += '<ul class="' + (level === 0 ? 'tree' : 'nested') + '">';
        departments.forEach(function(dept) {
            if (dept.children && dept.children.length > 0) {
                html += `
                    <li>
                        <span class="caret">${dept.name} (${dept.code})</span>
                        ${buildTreeHtml(dept.children, level + 1)}
                    </li>
                `;
            } else {
                html += `<li>${dept.name} (${dept.code})</li>`;
            }
        });
        html += '</ul>';
        return html;
    }
});