$(document).ready(function () {

    //获取验证码
    var code_time = 0;
    //验证按钮可操作性
    function valide_code_btn() {
        if (code_time > 0) {
            code_time -= 1;
            $(".getCode").text(code_time + "秒后重新发送").attr("disabled", true);
            setTimeout(valide_code_btn, 1000);
        }
        else {
            code_time = 0;
            $(".getCode").text("获取验证码").attr("disabled", false);
        }
    }

    //发送验证码
    function getCode() {
        if (code_time)
            return false;
        var phone = $("#login_name").val();
        var type = "changePhone";

        if (/^1[34578]\d{9}$/.test(phone)) {
            type = "changePhone";
        }
        else if (/^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/.test(phone)) {
            type = "changeEmail";
        }
        else {
            layer.msg("请输入正确的手机号码或者邮箱地址");
            return false;
        }
        code_time = 60;
        valide_code_btn();
        $.ajax({
            url: ctxPath + "/v2/api/mobile/validCode/sendValidCode",
            type: 'post',
            data: {
                account: phone,
                type: type
            },
            success: function (res) {
                if (res.code != 0) {
                    layer.msg(res.message);
                }
            }
        })
    }

    $('.getCode').click(function () {
        getCode();
    })
    var back_pass_in = true;

    function sub_forget() {
        var login_name = document.getElementById("login_name").value;
        var login_code = document.getElementById("login_code").value;
        var login_pwd = document.getElementById("login_pwd").value;
        if (!back_pass_in)
            return false;
        if (!login_name) {
            layer.msg("请填写正确的手机号码/邮箱地址");
            return false;
        }
        if (!login_code) {
            layer.msg("验证码错误！");
            return false;
        }
        if (!login_pwd || login_pwd == "") {
            layer.msg("请填写密码");
            return false;
        }
        if (login_pwd.length < 6) {
            layer.msg("你的密码设置过于简单，建议设置6位以上长度~");
            return false;
        }

        var type = "changePhone";
        if (/^1[34578]\d{9}$/.test(login_name)) {
            type = "changePhone";
        }
        else if (/^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/.test(login_name)) {
            type = "changeEmail";
        }
        else {
            layer.msg("请输入正确的手机号码或者邮箱地址");
            return false;
        }
        back_pass_in = false;
        $.ajax({
            url: ctxPath + "/v2/api/mobile/validCode/matchValidCode",
            type: 'post',
            data: {
                account: login_name,
                type: type,
                code: login_code
            },
            success: function (res) {
                if (res.code == 0) {
                    $.ajax({
                        url: ctxPath + "/v2/api/mobile/forgetPwd",
                        type: 'post',
                        data: {
                            account: login_name,
                            newPwd: login_pwd
                        },
                        success: function (res) {
                            if (res.code == 0) {
                                alert(res.message);
                                window.location.href = ctxPath + "/web/login";
                            }
                            else {
                                layer.msg(res.message);
                            }
                            back_pass_in = true;
                        }
                    })
                }
                else {
                    layer.msg(res.message)
                    back_pass_in = true;
                }
            }
        })
    }

    $(".sin-btn").click(function () {
        sub_forget();
    })
    //回车时间绑定
    $("body").keyup(function (e) {
        if (e.keyCode == 13) {
            //登录
            sub_forget();
        }
    })
});