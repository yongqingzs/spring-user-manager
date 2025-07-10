$(function() {
    // 加载图形验证码
    function loadCaptcha() {
        axios.get('/api/captcha/image')
            .then(function(response) {
                if (response.data.code === 200) {
                    const uuid = response.data.data.uuid;
                    const base64 = response.data.data.base64;
                    $('#captchaImg').attr('src', base64);
                    $('#captchaUuid').val(uuid);
                    $('#captchaInput').val('');
                    $('#captchaTip').text('');
                } else {
                    $('#captchaTip').text(response.data.message || '获取验证码失败').removeClass('text-success').addClass('text-danger');
                }
            })
            .catch(function() {
                $('#captchaTip').text('获取验证码失败').removeClass('text-success').addClass('text-danger');
            });
    }

    // 弹窗工具
    function showError(msg) {
        if (window.toastr) {
            toastr.error(msg);
        } else {
            alert(msg);
        }
    }
    function showSuccess(msg) {
        if (window.toastr) {
            toastr.success(msg);
        } else {
            alert(msg);
        }
    }

    // 首次加载
    loadCaptcha();
    // 点击图片刷新
    $('#captchaImg').on('click', function() {
        loadCaptcha();
    });

    // 登录表单提交，直接提交所有参数到后端，由后端校验验证码
    $('#loginForm').submit(function(e) {
        e.preventDefault();
        const uuid = $('#captchaUuid').val();
        const code = $('#captchaInput').val().trim();
        if (!uuid || !code) {
            $('#captchaTip').text('请填写验证码').removeClass('text-success').addClass('text-danger');
            return;
        }
        doLogin();
    });

    // 登录表单实际提交
    function doLogin() {
        const username = $('#username').val();
        const password = $('#password').val();
        const uuid = $('#captchaUuid').val();
        const code = $('#captchaInput').val().trim();
        axios.post('/api/auth/login',
            new URLSearchParams({ username, password, uuid, code }),
            { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
        )
            .then(function(response) {
                console.log('登录响应:', response);
                if (response.data && response.data.code === 200) {
                    // 登录成功，跳转首页
                    window.location.href = '/';
                } else {
                    // 登录失败，弹窗提示
                    let msg = response.data && response.data.message ? response.data.message : '登录失败';
                    showError(msg);
                    loadCaptcha();
                }
            })
            .catch(function(error) {
                // 网络或服务器异常
                let msg = '登录失败';
                if (error.response && error.response.data && error.response.data.message) {
                    msg = error.response.data.message;
                }
                showError(msg);
                loadCaptcha();
            });
    }
});
