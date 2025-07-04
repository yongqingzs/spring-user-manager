$(document).ready(function () {
    // 设置 axios CSRF Token
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    if (csrfToken && csrfHeader) {
        axios.defaults.headers.common[csrfHeader] = csrfToken;
    }
    
    // 初始化页面
    let currentPage = 1;
    const pageSize = 10;
    let totalPages = 0;
    
    // 加载用户数据
    loadUsers(currentPage);
    
    // 分页事件处理
    $(document).on('click', '.pagination-link', function(e) {
        e.preventDefault();
        const page = $(this).data('page');
        if (page !== currentPage) {
            currentPage = page;
            loadUsers(page);
        }
    });
    
    // 跳转到添加用户页面
    $('#btn-add-user').on('click', function() {
        window.location.href = '/users/add';
    });
    
    // 编辑用户
    $(document).on('click', '.btn-edit', function() {
        const userId = $(this).data('id');
        window.location.href = `/users/edit/${userId}`;
    });
    
    // 启用/禁用用户
    $(document).on('click', '.btn-toggle-status', function() {
        const userId = $(this).data('id');
        const currentStatus = $(this).data('status');
        const newStatus = currentStatus === 1 ? 2 : 1;
        const statusText = newStatus === 1 ? '启用' : '禁用';
        
        if (confirm(`确定要${statusText}该用户吗？`)) {
            axios.put(`/api/users/${userId}/status`, { status: newStatus })
                .then(function(response) {
                    const res = response.data;
                    if (res.success) {
                        toastr.success(res.message);
                        loadUsers(currentPage);
                    } else {
                        toastr.error(res.message);
                    }
                })
                .catch(function(error) {
                    console.error('操作失败:', error);
                    toastr.error('操作失败，请重试');
                });
        }
    });
    
    // 删除用户
    $(document).on('click', '.btn-delete', function() {
        // console.log("删除用户按钮被点击");
        const userId = $(this).data('id');
        const username = $(this).data('username');
        
        if (confirm(`确定要删除用户 "${username}" 吗？此操作不可恢复！`)) {
            axios.delete(`/api/users/${userId}`)
                .then(function(response) {
                    const res = response.data;
                    if (res.success) {
                        console.log('删除用户 API响应:', res);
                        toastr.success(res.message);
                        if ($('#userTableBody tr').length === 1 && currentPage > 1) {
                            currentPage--;
                        }
                        loadUsers(currentPage);
                    } else {
                        toastr.error(res.message);
                    }
                })
                .catch(function(error) {
                    console.error('删除用户失败:', error.response?.data || error);
                    toastr.error('删除失败，请重试');
                });
        }
    });
    
    // 搜索用户
    $('#searchBtn').on('click', function() {
        currentPage = 1; // 重置为第一页
        loadUsers(currentPage);
    });
    
    // 加载用户数据函数
    function loadUsers(page) {
        const searchQuery = $('#searchInput').val().trim();

        axios.get('/api/users', {
            params: {
                page: page,
                per_page: pageSize,
                query: searchQuery
            }
        })
            .then(function(response) {
                const res = response.data;
                console.log('加载用户 API响应:', res);
                console.log('用户数据:', res.users);
        
                renderUsers(res.users);
                renderPagination(page, Math.ceil(res.total / pageSize));
                totalPages = Math.ceil(res.total / pageSize);
            })
            .catch(function(error) {
                console.error('加载失败:', error);
                toastr.error('加载用户数据失败，请重试');
            });
    }
    
    // 渲染用户列表
    function renderUsers(users) {
        const tbody = $('#userTableBody');
        tbody.empty();
        
        if (!users || users.length === 0) {
            tbody.append('<tr><td colspan="8" class="text-center">没有找到用户数据</td></tr>');
            return;
        }
        
        users.forEach(function(user) {
            const statusText = user.status === 1 ? 
                '<span class="badge bg-success">启用</span>' : 
                '<span class="badge bg-danger">禁用</span>';
            const statusActionText = user.status === 1 ? '禁用' : '启用';
            const statusActionClass = user.status === 1 ? 'btn-warning' : 'btn-success';
            
            // 格式化创建时间
            const createTime = user.created_time ? new Date(user.created_time).toLocaleDateString() : '-';
            const row = `
                <tr>
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.realname || '-'}</td>
                    <td>${user.mobile || '-'}</td>
                    <td>${user.email || '-'}</td>
                    <td>${statusText}</td>
                    <td>${createTime}</td>
                    <td class="user-actions">
                        <button type="button" class="btn btn-sm btn-info btn-edit" data-id="${user.id}">编辑</button>
                        <button type="button" class="btn btn-sm ${statusActionClass} btn-toggle-status" 
                            data-id="${user.id}" data-status="${user.status}">${statusActionText}</button>
                        <button type="button" class="btn btn-sm btn-danger btn-delete" 
                            data-id="${user.id}" data-username="${user.username}">删除</button>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
        
        // 更新记录总数显示
        $('#totalRecords').text(`共 ${users.length} 条记录`);
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
});