$(document).ready(function () {

})
var code_time = 0, timer;

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

function sendCode() {
    if (code_time)
        return false;
    var phone = $("#member_phone").val();
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
    //手机验证 | 邮箱验证
    $.jsonAjax("/v2/api/mobile/validCode/sendValidCode", {
        account: phone,
        type: type
    }, function (res) {
        if (res.code != 0) {
            layer.msg(res.message);
        }
    })
}
var sing_in_disabled = false;
function singIn() {
    if (sing_in_disabled) {
        return false;
    }
    var sing_user_name = document.getElementById("member_phone");
    var sing_user_code = document.getElementById("send_code");
    var sing_pass = document.getElementById("member_pwd");
    if (!sing_user_name.value || sing_user_name.value == "") {
        layer.msg("请输入你的手机/邮箱");
        sing_user_name.focus();
        return false;
    }
    if (!sing_user_code.value || sing_user_code.value == "") {
        layer.msg("请输入验证码");
        sing_user_code.focus();
        return false;
    }
    if (!sing_pass.value || sing_pass.value == "") {
        layer.msg("请输入密码");
        sing_pass.focus();
        return false;
    }
    if (sing_pass.value.length < 6) {
        layer.msg("你的密码设置过于简单，建议设置6位以上长度~");
        sing_pass.focus();
        return false;
    }
    var type = "changePhone";
    if (/^1[34578]\d{9}$/.test(sing_user_name.value)) {
        type = "changePhone";
    }
    else if (/^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/.test(sing_user_name.value)) {
        type = "changeEmail";
    }
    else {
        layer.msg("请输入正确的手机号码或者邮箱地址");
        return false;
    }
    sing_in_disabled = true;
    $.jsonAjax("/v2/api/mobile/validCode/matchValidCode", {
        type: type,
        account: sing_user_name.value,
        code: sing_user_code.value
    }, function (res) {
        if (res.code == 0) {
            $.jsonAjax("/v2/api/mobile/registe", {
                account: sing_user_name.value,
                pwd: sing_pass.value
            }, function (res) {
                sing_in_disabled = false;
                if (res.code == 0) {
                    alert("帐号注册成功!");
                    window.location.href = ctxPath + "/mobile/login";
                }
                else {
                    layer.msg(res.message);
                }
            })
        }
        else {
            layer.msg(res.message);
            sing_in_disabled = false;
        }
    })
}