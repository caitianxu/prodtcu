//昵称
$("#up_user_nike_name").click(function () {
    if ($(this).hasClass("form_action_col")) {
        $(this).removeClass("form_action_col").html("修改");
        $("#form_user_name_par").hide();
        $("#form_user_name").show();
    }
    else {
        $(this).addClass("form_action_col").html("取消");
        $("#form_user_name_par").show();
        $("#form_user_name").hide();
    }
})
//头像
$("#set_zdy_img span").click(function () {
    if (member_info.icon) {
        $("#set_xt_img span").show();
        $("#set_xt_img label").hide();
        $("#set_zdy_img span").hide();
        $("#set_zdy_img label").show();
    }
    else {
        layer.msg('你还没有上传过自定义头像，先上传一个吧~');
    }
})
$("#set_xt_img span").click(function () {
    $("#set_xt_img span").hide();
    $("#set_xt_img label").show();
    $("#set_zdy_img span").show();
    $("#set_zdy_img label").hide();
})
//手机
$("#up_phone_action").click(function () {
    if ($(this).hasClass("form_action_col")) {
        $(this).removeClass("form_action_col").html("修改");
        $("#form_phone_text").show();
        $("#form_phone_form").hide();
    }
    else {
        $(this).addClass("form_action_col").html("取消");
        $("#form_phone_text").hide();
        $("#form_phone_form").show();
        document.getElementById("u_phone_old_code_btn").disabled = false;
        document.getElementById("u_phone_old_btn").disabled = false;
        document.getElementById("u_phone_code_btn").disabled = false;
        document.getElementById("u_phone_btn").disabled = false;
        $("#u_phone_old_code,#u_phone,#u_phone_code").val("");
        code_time = 0;
        //判断是否有绑定手机
        if (member_info.phone) {
            $("#phone_step_2").show();
            $("#phone_step_1").hide();
            var iptxt = member_info.phone.substr(0, 3) + "*****" + member_info.phone.substr(member_info.phone.length - 3, 3);
            $("#u_phone_old").val(iptxt);
        }
        else {
            $("#phone_step_1").show();
            $("#phone_step_2").hide();
        }
    }
})
//邮箱验证
$("#up_email_action").click(function () {
    if ($(this).hasClass("form_action_col")) {
        $(this).removeClass("form_action_col").html("修改");
        $("#form_email_text").show();
        $("#form_email_form").hide();
    }
    else {
        $(this).addClass("form_action_col").html("取消");
        $("#form_email_text").hide();
        $("#form_email_form").show();
        document.getElementById("u_email_old_code_btn").disabled = false;
        document.getElementById("u_email_old_btn").disabled = false;
        document.getElementById("u_email_code_btn").disabled = false;
        document.getElementById("u_email_btn").disabled = false;
        $("#u_email_old_code,#u_email,#u_email_code").val("");
        code_time = 0;
        if (member_info.email) {
            $('#email_step_1').hide();
            $("#email_step_2").show();
            var spl = member_info.email.split("@");
            if (spl.length > 1) {
                var mlt = spl[0].substr(0, spl[0].length > 3 ? 3 : spl[0].length - 2) + "*****@" + spl[1];
                $("#u_email_old").val(mlt);
            }
        }
        else {
            $('#email_step_2').hide();
            $("#email_step_1").show();
        }
    }
})
//验证旧邮箱
$("#u_email_old_btn").click(function () {
    var code = $("#u_email_old_code").val();
    if (code && code.length == 6) {
        var email_reg = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
        if (email_reg.test(member_info.email)) {
            var u_phone_old_btn = this;
            u_phone_old_btn.disabled = true;
            //验证手机号码正确性
            $.jsonAjax("/v2/api/mobile/validCode/matchValidCode", {
                type: 'changeEmail',
                account: member_info.email,
                code: code
            }, function (res) {
                if (res.code == 0) {
                    layer.msg("验证成功!请绑定你新的邮箱地址");
                    //验证码已经成功发送
                    $("#email_step_2").hide();
                    $("#email_step_1").show();
                    code_time = 0;
                }
                else {
                    layer.msg(res.message)
                }
                u_phone_old_btn.disabled = false;
            })
        }
        else {
            layer.msg("请输入正确的手机号码!")
        }
    }
    else {
        layer.msg("验证码错误!");
    }
})
//获取旧邮箱验证码
$("#u_email_old_code_btn").click(function () {
    if (code_time)
        return false;
    var name = member_info.email;
    var email_reg = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
    if (email_reg.test(name)) {
        code_time = 60;
        valide_code_btn("#u_email_old_code_btn");

        $.jsonAjax("/v2/api/mobile/validCode/sendValidCode", {
            account: name,
            type: 'changeEmail'
        }, function (res) {
            if (res.code == 0) {
                //验证码已经成功发送
            }
            else {
                layer.msg(res.message)
            }
        })
    }
    else {
        layer.msg("请输入正确的手机号码");
    }
})
//密码
$("#form_pass_action").click(function () {
    if ($(this).hasClass("form_action_col")) {
        $(this).removeClass("form_action_col").html("修改");
        $("#form_pass_text").show();
        $("#form_pass_form").hide();
    }
    else {
        $(this).addClass("form_action_col").html("取消");
        $("#form_pass_text").hide();
        $("#form_pass_form").show();
        $("#u_pass,#u_pass_new,#u_pass_new_send").val("");
    }
})
$(".tx_item .t_cover p").click(function () {
    $("#user-head").click();
})
$('#user-head').prettyFile({
    text: "上传头像",
    change: function (recc, obj) {
        var cc = recc.data[0].url;
        $("#myusericon, #myusericon1, .u_img img").attr("src", ctxPath + cc);
        $.jsonAjax("/v2/api/mobile/memberInfo/updateIcon", {
            member_token: _token,
            icon: cc
        }, function (res) {
            if (res.code == 0) {
                layer.msg("修改头像成功!")
                upSetUserInfo(res.data);
            }
            else {
                layer.msg(res.message);
            }
        })
    }
});
//修改昵称
$("#form_user_name_btn").click(function () {
    var nick_name = $("#form_user_name_input").val();
    var mbtn = this;
    if (nick_name) {
        mbtn.disabled = true;
        $.jsonAjax("/v2/api/mobile/modifyUserInfo", {
            token: _token,
            sex: member_info.sex,
            nickName: nick_name
        }, function (res) {
            if (res.code == 0) {
                layer.msg('修改昵称成功!');
                $("#up_user_nike_name").removeClass("form_action_col").html("修改");
                $("#form_user_name_par").hide();
                $("#form_user_name").show();
                $("#form_user_name,.u_head .u_name,.login_user_name").html(nick_name);
                upSetUserInfo(res.data);
            }
            else {
                layer.msg(res.message);
            }
            mbtn.disabled = false;
        })
    }
    else {
        layer.msg("请填写昵称!");
    }
})
//更新用户信息
function upSetUserInfo(data) {
    _token = data.token;
    member_info = data;
}
//获取旧手机验证码
$("#u_phone_old_code_btn").click(function () {
    if (code_time)
        return false;
    var name = member_info.phone;
    var phone_reg = /^1[34578]\d{9}$/;
    if (phone_reg.test(name)) {
        code_time = 60;
        valide_code_btn("#u_phone_old_code_btn");

        $.jsonAjax("/v2/api/mobile/validCode/sendValidCode", {
            account: name,
            type: 'changePhone'
        }, function (res) {
            if (res.code == 0) {
                //验证码已经成功发送
            }
            else {
                layer.msg(res.message)
            }
        })
    }
    else {
        layer.msg("请输入正确的手机号码");
    }
})
//获取新手机验证码
$("#u_phone_code_btn").click(function () {
    if (code_time)
        return false;
    var name = $("#u_phone").val();
    var phone_reg = /^1[34578]\d{9}$/;
    if (phone_reg.test(name)) {
        if (name == member_info.phone) {
            layer.msg("你更换的新手机号码与原手机号码重复");
        }
        else {
            code_time = 60;
            valide_code_btn("#u_phone_code_btn");
            //手机验证 | 邮箱验证

            $.jsonAjax("/v2/api/mobile/validCode/sendValidCode", {
                account: name,
                type: 'changePhone'
            }, function (res) {
                if (res.code == 0) {
                    //验证码已经成功发送
                }
                else {
                    layer.msg(res.message)
                }
            })
        }
    }
    else {
        layer.msg("请输入正确的手机号码");
    }
})
//获取新邮箱验证码
$("#u_email_code_btn").click(function () {
    if (code_time)
        return false;
    var name = $("#u_email").val();
    var email_reg = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
    if (email_reg.test(name)) {
        if (name == member_info.email) {
            layer.msg("你更换的新邮箱与原邮箱地址重复");
        }
        else {
            code_time = 60;
            valide_code_btn("#u_email_code_btn");
            //手机验证 | 邮箱验证
            $.jsonAjax("/v2/api/mobile/validCode/sendValidCode", {
                account: name,
                type: 'changeEmail'
            }, function (res) {
                if (res.code == 0) {
                    //验证码已经成功发送
                }
                else {
                    layer.msg(res.message)
                }
            })
        }
    }
    else {
        layer.msg("请输入正确的邮箱地址");
    }
})
var code_time = 0;
//验证按钮可操作性
function valide_code_btn(btnid) {
    if (code_time > 0) {
        code_time -= 1;
        $(btnid).text(code_time + "秒后重新发送").attr("disabled", true);
        setTimeout(function () {
            valide_code_btn(btnid);
        }, 1000);
    }
    else {
        code_time = 0;
        $(btnid).text("获取验证码").attr("disabled", false);
    }
}
//更换新手机
$("#u_phone_btn").click(function () {
    var code = $("#u_phone_code").val();
    var account = $("#u_phone").val();
    if (code && code.length == 6) {
        var phone_reg = /^1[34578]\d{9}$/;
        if (phone_reg.test(account)) {
            var u_phone_old_btn = this;
            u_phone_old_btn.disabled = true;
            $.jsonAjax("/v2/api/mobile/memberInfo/updatePhone", {
                token: _token,
                phone: account,
                code: code
            }, function (res) {
                if (res.code == 0) {
                    //验证码已经成功发送
                    $("#phone_step_2").hide();
                    $("#phone_step_1").hide();
                    code_time = 0;
                    $("#up_phone_action").removeClass("form_action_col").html("修改");
                    var iptxt = account.substr(0, 3) + "*****" + account.substr(account.length - 3, 3);
                    $("#form_phone_text").html(iptxt).show();
                    $("#form_phone_form").hide();

                    layer.msg('你的手机号码已经更新成功!', function () {
                        layer.closeAll();
                        window.location.reload();
                    })
                }
                else {
                    layer.msg(res.message)
                }
                u_phone_old_btn.disabled = false;
            })
        }
        else {
            layer.msg("请输入正确的手机号码");
        }
    }
    else {
        layer.msg("验证码错误!");
    }
})
//更换新邮箱
$("#u_email_btn").click(function () {
    var code = $("#u_email_code").val();
    var account = $("#u_email").val();
    if (code && code.length == 6) {
        var email_reg = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
        if (email_reg.test(account)) {
            var u_phone_old_btn = this;
            u_phone_old_btn.disabled = true;
            $.jsonAjax("/v2/api/mobile/memberInfo/updateEmail", {
                token: _token,
                email: account,
                code: code
            }, function (res) {
                if (res.code == 0) {
                    //验证码已经成功发送
                    $("#email_step_2").hide();
                    $("#email_step_1").hide();
                    code_time = 0;
                    var spl = account.split("@");
                    var mlt = "";
                    if (spl.length > 1) {
                        mlt = spl[0].substr(0, spl[0].length > 3 ? 3 : spl[0].length - 2) + "*****@" + spl[1];
                    }
                    $("#form_action form_action_col").removeClass("form_action_col").html("修改");
                    $("#form_email_text").html(mlt).show();
                    $("#form_email_form").hide();
                    layer.msg('你的邮箱地址已经更新成功!', function () {
                        layer.closeAll();
                        window.location.reload();
                    })
                }
                else {
                    layer.msg(res.message)
                }
                u_phone_old_btn.disabled = false;
            })
        }
        else {
            layer.msg("请输入正确的邮箱地址!");
        }
    }
    else {
        layer.msg("验证码错误!");
    }
})
//验证旧手机
$("#u_phone_old_btn").click(function () {
    var code = $("#u_phone_old_code").val();
    if (code && code.length == 6) {
        var phone_reg = /^1[34578]\d{9}$/;
        if (phone_reg.test(member_info.phone)) {
            var u_phone_old_btn = this;
            u_phone_old_btn.disabled = true;
            //验证手机号码正确性
            $.jsonAjax("/v2/api/mobile/validCode/matchValidCode", {
                type: 'changePhone',
                account: member_info.phone,
                code: code
            }, function (res) {
                if (res.code == 0) {
                    layer.msg("验证成功!请绑定你新的手机号码");
                    //验证码已经成功发送
                    $("#phone_step_2").hide();
                    $("#phone_step_1").show();
                    code_time = 0;
                }
                else {
                    layer.msg(res.message)
                }
                u_phone_old_btn.disabled = false;
            })
        }
        else {
            layer.msg("请输入正确的手机号码!")
        }
    }
    else {
        layer.msg("验证码错误!");
    }
})
//修改密码
$("#up_pass_btn").click(function () {
    var u_pass_new = $("#u_pass_new").val();
    var u_pass_new_send = $("#u_pass_new_send").val();
    if (!u_pass_new || u_pass_new == "" || u_pass_new.length < 6) {
        layer.msg("请设置6位长度以上的密码~");
    }
    else if (u_pass_new && u_pass_new == u_pass_new_send) {
        var up_pass_btn = this;
        up_pass_btn.disabled = true;
        $.jsonAjax("/v2/api/mobile/changePwd", {
            token: _token,
            newPwd: u_pass_new
        }, function (res) {
            if (res.code == 0) {
                //验证码已经成功发送
                layer.msg("密码已修改成功!");
                $("#form_pass_action").removeClass("form_action_col").html("修改");
                $("#form_pass_text").show();
                $("#form_pass_form").hide();
                window.location.href = ctxPath + "/web/login";
            }
            else {
                layer.msg(res.message)
            }
            up_pass_btn.disabled = false;
        })
    }
    else {
        layer.msg("请输入新密码，并确保两次密码保持一致")
    }
})
if (member_info.phone) {
    var iptxt = member_info.phone.substr(0, 3) + "*****" + member_info.phone.substr(member_info.phone.length - 3, 3);
    $("#form_phone_text").html(iptxt);
}
if (member_info.email) {
    var spl = member_info.email.split("@");
    if (spl.length > 1) {
        var mlt = spl[0].substr(0, spl[0].length > 3 ? 3 : spl[0].length - 2) + "*****@" + spl[1];
        $("#form_email_text").html(mlt);
    }
}