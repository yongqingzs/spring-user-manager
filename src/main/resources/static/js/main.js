// CSRF token 处理，获取 Spring Security 的 CSRF token
const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

axios.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
if (csrfToken && csrfHeader) {
    axios.defaults.headers.common[csrfHeader] = csrfToken;
}

// 错误处理
axios.interceptors.response.use(
    response => response,
    error => {
        const response = error.response;
        if (response && response.status === 401) {
            // 未授权，重定向到登录页
            window.location.href = '/login';
        } else if (response && response.data && response.data.message) {
            // 显示错误消息
            showErrorMessage(response.data.message);
        } else {
            // 显示通用错误
            showErrorMessage('发生错误，请稍后重试');
        }
        return Promise.reject(error);
    }
);

// 显示错误消息
function showErrorMessage(message) {
    const errorDiv = document.getElementById('errorMessage');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.classList.remove('d-none');
        setTimeout(() => {
            errorDiv.classList.add('d-none');
        }, 5000);
    } else {
        alert(message);
    }
}

// 显示成功消息
function showSuccessMessage(message) {
    // 如果使用了 Bootstrap Toast，可以在这里使用 Toast 显示成功消息
    alert(message);
}

// 格式化日期
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

// 生成分页组件
function generatePagination(currentPage, totalPages, onPageChange) {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;

    pagination.innerHTML = '';

    // 前一页按钮
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
    prevLi.innerHTML = '<a class="page-link" href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a>';
    if (currentPage > 1) {
        prevLi.onclick = () => onPageChange(currentPage - 1);
    }
    pagination.appendChild(prevLi);

    // 页码按钮
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, startPage + 4);

    for (let i = startPage; i <= endPage; i++) {
        const pageLi = document.createElement('li');
        pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
        pageLi.innerHTML = `<a class="page-link" href="#">${i}</a>`;
        pageLi.onclick = () => onPageChange(i);
        pagination.appendChild(pageLi);
    }

    // 后一页按钮
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
    nextLi.innerHTML = '<a class="page-link" href="#" aria-label="Next"><span aria-hidden="true">&raquo;</span></a>';
    if (currentPage < totalPages) {
        nextLi.onclick = () => onPageChange(currentPage + 1);
    }
    pagination.appendChild(nextLi);
}