$(document).ready(function () {
    function sub_login() {
        var login_name = document.getElementById("login_name");
        var login_pwd = document.getElementById("login_pwd");
        if (!login_name.value) {
            layer.msg("请输入用户名!");
            login_name.focus();
            return false;
        }
        if (!login_pwd.value) {
            layer.msg("请输入密码!");
            login_pwd.focus();
            return false;
        }

        $.ajax({
            url: ctxPath + "/v2/api/mobile/login",
            type: 'post',
            data: {
                org_id: org_id,
                account: login_name.value,
                pwd: login_pwd.value,
                token_type: 'pc'
            },
            success: function (res) {

                if (res.code == 0) {
                    if(window.location.href.indexOf('web/login') != -1) {
                        window.location.href = ctxPath + "/web";
                    }
                    else{
                        window.location.reload();
                    }
                }
                else {
                    layer.msg(res.message);
                }
            }
        })
    }

    $(".sin-btn").click(function () {
        sub_login();
    })
    //回车时间绑定
    $("body").keyup(function (e) {
        if (e.keyCode == 13) {
            //登录
            sub_login();
        }
    })
});