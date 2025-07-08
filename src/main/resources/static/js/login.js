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

    // 登录表单提交前校验验证码
    $('#loginForm').submit(function(e) {
        e.preventDefault();
        const uuid = $('#captchaUuid').val();
        const code = $('#captchaInput').val().trim();
        if (!uuid || !code) {
            $('#captchaTip').text('请填写验证码').removeClass('text-success').addClass('text-danger');
            return;
        }
        axios.post('/api/captcha/verify', null, { params: { uuid: uuid, code: code } })
            .then(function(response) {
                if (response.data.code === 200) {
                    $('#captchaTip').text('验证码正确，正在登录...').removeClass('text-danger').addClass('text-success');
                    // 验证码正确，手动提交表单，拦截响应
                    doLogin();
                } else {
                    $('#captchaTip').text(response.data.message || '验证码错误').removeClass('text-success').addClass('text-danger');
                    showError(response.data.message || '验证码错误');
                    loadCaptcha();
                }
            })
            .catch(function() {
                $('#captchaTip').text('验证码校验失败').removeClass('text-success').addClass('text-danger');
                showError('验证码校验失败');
                loadCaptcha();
            });
    });

    // 登录表单实际提交
    function doLogin() {
        const form = $('#loginForm');
        const data = form.serialize();
        axios.post(form.attr('action'), data)
            .then(function(response) {
                // 登录成功，跳转首页
                window.location.href = '/';
            })
            .catch(function(error) {
                // 登录失败，弹窗提示
                let msg = '登录失败';
                if (error.response && error.response.data && error.response.data.message) {
                    msg = error.response.data.message;
                }
                showError(msg);
                loadCaptcha();
            });
    }
});
