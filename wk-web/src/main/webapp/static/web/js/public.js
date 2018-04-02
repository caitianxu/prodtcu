//发送验证码
var api_sendValidCode = "/v2/api/mobile/validCode/sendValidCode";
//登录
var api_login = "/v2/api/mobile/login";
//注册
var api_registe = "/v2/api/mobile/registe";
//注册获取机构下子机构列表
var api_getregiste = "/site/memberRegister/getOrgDept";
//热门关键词
var api_hotKey = "/v2/api/searchKey/getSearchkeyList";
//验证验证码
var api_validCode = "/v2/api/mobile/validCode/matchValidCode";
//通过手机邮箱修改密码
var api_forgetPwd = "/v2/api/mobile/forgetPwd";
//意见反馈
var api_commitSuggest = "/v2/api/suggest/commitSuggest";
jQuery.extend({
    jsonAjax: function (url, param, callback) {
        if (!param) {
            param = {};
        }
        param.token_type = 'pc';
        param.org_id = org_id;
        $.ajax({
            url: ctxPath + url,
            type: 'post',
            data: param,
            success: function (res) {
                if (res.code == 600) {
                    console.log(res)
                }
                else if (callback) {
                    callback(res);
                }
            },
            error: function (error) {
                console.log(error)
            }
        })
    }
});

$(document).ready(function () {
    var src_url = '/static/web/img/pkgApp/512.png';
    if (org_id == 225) {
        src_url = '/static/web/img/pkgApp/szjy-logo.png'
    }
    var thisUrl = "http://" + window.location.host + ctxPath + "/api/appStore/downLoad?org_id=" + org_id + "&t=2";
    $("#public_img_code").qrcode({
        render: "canvas",    //设置渲染方式，有table和canvas，使用canvas方式渲染性能相对来说比较好
        text: thisUrl,    //扫描二维码后显示的内容,可以直接填一个网址，扫描二维码后自动跳向该链接
        width: "170",               //二维码的宽度
        height: "158",              //二维码的高度
        background: "#ffffff",       //二维码的后景色
        foreground: "#000000",        //二维码的前景色
        src: src_url             //二维码中间的图片
    });

    $("#public_img_code_small").qrcode({
        render: "canvas",    //设置渲染方式，有table和canvas，使用canvas方式渲染性能相对来说比较好
        text: thisUrl,    //扫描二维码后显示的内容,可以直接填一个网址，扫描二维码后自动跳向该链接
        width: "140",               //二维码的宽度
        height: "130",              //二维码的高度
        background: "#ffffff",       //二维码的后景色
        foreground: "#000000",        //二维码的前景色
        src: src_url             //二维码中间的图片
    });

    document.getElementById("sing_in").disabled = false;
    document.getElementById("user_login").disabled = false;
    document.getElementById("back_pass_in").disabled = false;

    //获取关键词
    function getHotKeys() {
        $.jsonAjax(api_hotKey, {
            org_id: org_id,
            display: 5
        }, function (res) {
            if (res.data && res.data.length) {
                $.each(res.data, function (index, item) {
                    if (index < 5)
                        $('<span>' + item.name + '</span>').appendTo(".header .hot_key");
                })
                //search key
                $(".header .hot_key span").click(function () {
                    $(".header .top_search input[name='key']").val(this.innerHTML);
                    $(".header .top_search").submit();
                })
                $(".header .hot_key").show();
            }
        })
    }

    //登录
    function loginIn() {
        var user_login = document.getElementById("user_login");
        if (user_login.disabled) {
            return false;
        }
        var login_name_txt = document.getElementById("login_name_txt");
        var password_txt = document.getElementById("password_txt");
        if (!login_name_txt.value || login_name_txt.value == "") {
            layer.msg("请输入你的手机/邮箱");
            login_name_txt.focus();
            return false;
        }
        if (!password_txt.value || password_txt.value == "") {
            layer.msg("请输入登录密码");
            password_txt.focus();
            return false;
        }
        user_login.innerHTML = "登录中...";
        user_login.disabled = true;
        $.jsonAjax(api_login, {
            account: login_name_txt.value,
            pwd: password_txt.value
        }, function (res) {
            user_login.disabled = false;
            user_login.innerHTML = "立即登录";
            if (res.code == 0) {
                if (document.getElementById("tokenMessage").checked) {
                    $.cookie("_NJSW_LOGIN_USER", login_name_txt.value);
                    $.cookie("_NJSW_LOGIN_PASS", password_txt.value);
                    if (window.location.search && window.location.search.indexOf("login") != -1) {
                        window.location.href = ctxPath + "/web";
                    } else {
                        window.location.reload();
                    }
                }
                else {
                    if (window.location.search && window.location.search.indexOf("login") != -1) {
                        window.location.href = ctxPath + "/web";
                    } else {
                        window.location.reload();
                    }
                }
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //注册
    function singIn() {
        var sing_in = document.getElementById("sing_in");
        if (sing_in.disabled) {
            return false;
        }
        var sing_user_name = document.getElementById("sing_user_name");
        var sing_user_code = document.getElementById("sing_user_code");
        var sing_pass = document.getElementById("sing_pass");
        var sing_pass_clo = document.getElementById("sing_pass_clo");
        var sing_org_id = document.getElementById("sing_org_id");
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
        if (!sing_pass_clo.value || sing_pass_clo.value == "") {
            layer.msg("请再次输入密码");
            sing_pass_clo.focus();
            return false;
        }
        if (sing_pass_clo.value != sing_pass.value) {
            layer.msg("两次输入的密码不一致");
            sing_pass_clo.focus();
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
        if ($("#sing_org_id_parent dd").length > 0) {
            if (!sing_org_id.value || sing_org_id.value == "") {
                layer.msg("请选择组织/区域/乡镇");
                return false;
            }
        }
        sing_in.disabled = true;
        $.jsonAjax(api_validCode, {
            type: type,
            account: sing_user_name.value,
            code: sing_user_code.value,
        }, function (res) {
            if (res.code == 0) {
                $.jsonAjax(api_registe, {
                    account: sing_user_name.value,
                    pwd: sing_pass.value,
                    dept_id: sing_org_id.value
                }, function (res) {
                    sing_in.disabled = false;
                    if (res.code == 0) {
                        layer.msg("帐号注册成功!");
                        code_time = 0;
                        $(".sing_plan,.back_pass_plan,.fankui_pass_plan").hide();
                        $(".login_plan").show();
                    }
                    else {
                        layer.msg(res.message);
                    }
                })
            }
            else {
                layer.msg(res.message);
                sing_in.disabled = false;
            }
        })

    }

    var code_time = 0, currentPosition, timer;

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

    //通过手机邮箱修改密码
    function forgetPwd() {
        var back_pass_in = document.getElementById("back_pass_in");
        if (back_pass_in.disabled)
            return false;

        var back_pass_pass = document.getElementById("back_pass_pass");
        var back_pass_pass_clo = document.getElementById("back_pass_pass_clo");
        var back_pass_user_name = document.getElementById("back_pass_user_name");
        var back_pass_user_code = document.getElementById("back_pass_user_code");
        if (!back_pass_user_name.value) {
            layer.msg("请填写正确的手机号码/邮箱地址");
            back_pass_user_name.focus();
            return false;
        }
        if (!back_pass_user_code.value) {
            layer.msg("验证码错误！");
            back_pass_user_code.focus();
            return false;
        }
        if (!back_pass_pass.value || back_pass_pass.value == "") {
            layer.msg("请填写密码");
            back_pass_pass.focus();
            return false;
        }
        if (back_pass_pass.value.length < 6) {
            layer.msg("你的密码设置过于简单，建议设置6位以上长度~");
            back_pass_pass.focus();
            return false;
        }
        if (!back_pass_pass_clo.value) {
            layer.msg("请再次确认密码");
            back_pass_pass_clo.focus();
            return false;
        }
        if (back_pass_pass_clo.value != back_pass_pass.value) {
            layer.msg("两次密码输入不一致");
            back_pass_pass_clo.focus();
            return false;
        }
        var type = "changePhone";
        if (/^1[34578]\d{9}$/.test(back_pass_user_name.value)) {
            type = "changePhone";
        }
        else if (/^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/.test(back_pass_user_name.value)) {
            type = "changeEmail";
        }
        else {
            layer.msg("请输入正确的手机号码或者邮箱地址");
            back_pass_user_name.focus();
            return false;
        }
        back_pass_in.disabled = true;
        $.jsonAjax(api_validCode, {
            type: type,
            account: back_pass_user_name.value,
            code: back_pass_user_code.value
        }, function (res) {
            if (res.code == 0) {
                $.jsonAjax(api_forgetPwd, {
                    account: back_pass_user_name.value,
                    newPwd: back_pass_pass.value
                }, function (res) {
                    if (res.code == 0) {
                        //验证码已经成功发送
                        layer.msg('密码已成功找回，快去登录吧~');
                        code_time = 0;
                        $(".sing_plan,.back_pass_plan").hide();
                        $(".login_plan").show();
                    }
                    else {
                        layer.msg(res.message)
                    }
                    back_pass_in.disabled = false;
                })
            }
            else {
                layer.msg(res.message)
                back_pass_in.disabled = false;
            }
        })
    }

    //意见反馈
    function sendFankui() {
        var fankui_pass_in = document.getElementById("fankui_pass_in");
        if (fankui_pass_in.disabled)
            return false;
        var fankui_textarea = document.getElementById("fankui_textarea");
        if (!fankui_textarea.value || fankui_textarea.value.length < 1) {
            layer.msg("请填写意见反馈内容!");
            fankui_textarea.focus();
            return false;
        }
        fankui_pass_in.disabled = true;

        $.jsonAjax(api_commitSuggest, {
            content: fankui_textarea.value,
            member_token: _token
        }, function (res) {
            fankui_pass_in.disabled = false;
            if (res.code == 0) {
                layer.alert("你的意见已成功提交，相关工作人员会尽快处理；感谢你的宝贵意见和建议~", function () {
                    fankui_textarea.value = "";
                    $(".login_plan,.sing_plan,.back_pass_plan,.fankui_pass_plan").hide();
                    layer.closeAll();
                });
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //绑定事件
    function bandEvent() {
        //回到顶部
        $(".fiex_plan .fiex_back").click(function () {
            timer = setInterval(runToTop, 1);
        })

        //回到顶部
        function runToTop() {
            currentPosition = document.documentElement.scrollTop || document.body.scrollTop;
            currentPosition -= 10;
            if (currentPosition > 0) {
                window.scrollTo(0, currentPosition);
            }
            else {
                window.scrollTo(0, 0);
                clearInterval(timer);
            }
        }

        //登录
        $(".sing_form .form_link,.login_in").click(function () {
            code_time = 0;
            $(".sing_plan,.back_pass_plan,.fankui_pass_plan").hide();
            $(".login_plan").show();
        })

        //点击登录
        $("#user_login").click(function () {
            loginIn();
        })

        //是否登录
        if (window.location.search && window.location.search.indexOf('key=login') != -1) {
            $(".login_plan").show();
        }

        //退出弹出层
        $(".submit_cel").click(function () {
            $(".login_plan,.sing_plan,.back_pass_plan,.fankui_pass_plan").hide();
        })

        //回车时间绑定
        $("body").keyup(function (e) {
            if (e.keyCode == 13) {
                if ($(".login_plan").css('display') == 'block') {
                    //登录
                    loginIn();
                }
                else if ($(".back_pass_plan").css('display') == 'block') {
                    //找回密码
                    forgetPwd();
                }
                else if ($(".sing_plan").css('display') == 'block') {
                    //注册
                    singIn();
                }
                else if ($(".fankui_pass_plan").css('display') == 'block') {
                    //意见反馈
                    sendFankui();
                }
            }
        })

        //找回密码
        $(".login_plan .back_pass").click(function () {
            code_time = 0;
            $(".back_pass_plan .getCode,#back_pass_in").attr("disabled", false);
            $("#back_pass_user_name,#back_pass_user_code,#back_pass_pass,#back_pass_pass_clo").val("");
            $(".login_plan").hide();
            $(".sing_plan").hide();
            $(".back_pass_plan").show();
        })

        //找回密码-获取验证码
        $(".back_pass_plan .getCode").click(function () {
            if (code_time)
                return false;
            var phone = $("#back_pass_user_name").val();
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
            $.jsonAjax(api_sendValidCode, {
                account: phone,
                type: type
            }, function (res) {
                if (res.code != 0) {
                    layer.msg(res.message);
                }
            })
        })

        //通过手机或者邮箱修改密码
        $('#back_pass_in').click(function () {
            forgetPwd();
        })

        //进入注册
        $('.to_sing, .sing_in').click(function () {
            code_time = 0;
            $(".login_plan,.back_pass_plan,.fankui_pass_plan").hide();
            $(".sing_plan").show();
        })

        //注册-获取验证码
        $(".sing_plan .getCode").click(function () {
            if (code_time)
                return false;
            var phone = $("#sing_user_name").val();
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
            $.jsonAjax(api_sendValidCode, {
                account: phone,
                type: type
            }, function (res) {
                if (res.code != 0) {
                    layer.msg(res.message);
                }
            })
        })

        //注册
        $("#sing_in").click(function () {
            singIn();
        })

        //判断是否有用户密码
        if (!_token || _token == "") {
            var login_name = $.cookie("_NJSW_LOGIN_USER");
            var password = $.cookie("_NJSW_LOGIN_PASS");
            if (login_name && password) {
                $("#login_name_txt").val(login_name);
                $("#password_txt").val(password);
                document.getElementById("tokenMessage").checked = true;
            }
            else {
                document.getElementById("tokenMessage").checked = false;
            }
        }

        //打开意见反馈
        $(".fankui_sin").click(function () {
            //判断是否登录
            if (_token) {
                code_time = 0;
                $(".sing_plan,.back_pass_plan,.login_plan").hide();
                $(".fankui_pass_plan").show();
            }
            else {
                code_time = 0;
                $(".sing_plan,.back_pass_plan,.fankui_pass_plan").hide();
                $(".login_plan").show();
            }
        })
        //提交意见反馈
        $("#fankui_pass_in").click(function () {
            sendFankui();
        })
    }

    getHotKeys();
    bandEvent();


    var sWeek = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六");
    var dNow = new Date();
    var CalendarData = new Array(100);
    var madd = new Array(12);
    var tgString = "甲乙丙丁戊己庚辛壬癸";
    var dzString = "子丑寅卯辰巳午未申酉戌亥";
    var numString = "一二三四五六七八九十";
    var monString = "正二三四五六七八九十冬腊";
    var weekString = "日一二三四五六";
    var sx = "鼠牛虎兔龙蛇马羊猴鸡狗猪";
    var cYear, cMonth, cDay, TheDate;
    CalendarData = new Array(0xA4B, 0x5164B, 0x6A5, 0x6D4, 0x415B5, 0x2B6, 0x957, 0x2092F, 0x497, 0x60C96, 0xD4A, 0xEA5, 0x50DA9, 0x5AD, 0x2B6, 0x3126E, 0x92E, 0x7192D, 0xC95, 0xD4A, 0x61B4A, 0xB55, 0x56A, 0x4155B, 0x25D, 0x92D, 0x2192B, 0xA95, 0x71695, 0x6CA, 0xB55, 0x50AB5, 0x4DA, 0xA5B, 0x30A57, 0x52B, 0x8152A, 0xE95, 0x6AA, 0x615AA, 0xAB5, 0x4B6, 0x414AE, 0xA57, 0x526, 0x31D26, 0xD95, 0x70B55, 0x56A, 0x96D, 0x5095D, 0x4AD, 0xA4D, 0x41A4D, 0xD25, 0x81AA5, 0xB54, 0xB6A, 0x612DA, 0x95B,
        0x49B, 0x41497, 0xA4B, 0xA164B, 0x6A5, 0x6D4, 0x615B4, 0xAB6, 0x957, 0x5092F,
        0x497, 0x64B, 0x30D4A, 0xEA5, 0x80D65, 0x5AC, 0xAB6, 0x5126D, 0x92E, 0xC96, 0x41A95, 0xD4A, 0xDA5, 0x20B55, 0x56A, 0x7155B, 0x25D, 0x92D, 0x5192B, 0xA95, 0xB4A, 0x416AA, 0xAD5, 0x90AB5, 0x4BA, 0xA5B, 0x60A57, 0x52B, 0xA93, 0x40E95);
    madd[0] = 0;
    madd[1] = 31;
    madd[2] = 59;
    madd[3] = 90;
    madd[4] = 120;
    madd[5] = 151;
    madd[6] = 181;
    madd[7] = 212;
    madd[8] = 243;
    madd[9] = 273;
    madd[10] = 304;
    madd[11] = 334;

    function GetBit(m, n) {
        return (m >> n) & 1;
    }

    function e2c() {
        TheDate = (arguments.length != 3) ? new Date() : new Date(arguments[0], arguments[1], arguments[2]);
        var total, m, n, k;
        var isEnd = false;
        var tmp = TheDate.getFullYear();
        total = (tmp - 1921) * 365 + Math.floor((tmp - 1921) / 4) + madd[TheDate.getMonth()] + TheDate.getDate() - 38;
        if (TheDate.getYear() % 4 == 0 && TheDate.getMonth() > 1) {
            total++;
        }
        for (m = 0; ; m++) {
            k = (CalendarData[m] < 0xfff) ? 11 : 12;
            for (n = k; n >= 0; n--) {
                if (total <= 29 + GetBit(CalendarData[m], n)) {
                    isEnd = true;
                    break;
                }
                total = total - 29 - GetBit(CalendarData[m], n);
            }
            if (isEnd) break;
        }
        cYear = 1921 + m;
        cMonth = k - n + 1;
        cDay = total;
        if (k == 12) {
            if (cMonth == Math.floor(CalendarData[m] / 0x10000) + 1) {
                cMonth = 1 - cMonth;
            }
            if (cMonth > Math.floor(CalendarData[m] / 0x10000) + 1) {
                cMonth--;
            }
        }
    }

    function GetcDateString() {
        var tmp = "";
        tmp += tgString.charAt((cYear - 4) % 10);
        tmp += dzString.charAt((cYear - 4) % 12);
        tmp += "年 ";
        if (cMonth < 1) {
            tmp += "(闰)";
            tmp += monString.charAt(-cMonth - 1);
        } else {
            tmp += monString.charAt(cMonth - 1);
        }
        tmp += "月";
        tmp += (cDay < 11) ? "初" : ((cDay < 20) ? "十" : ((cDay < 30) ? "廿" : "三十"));
        if (cDay % 10 != 0 || cDay == 10) {
            tmp += numString.charAt((cDay - 1) % 10);
        }
        return tmp;
    }

    function GetLunarDay(solarYear, solarMonth, solarDay) {
        if (solarYear < 1921 || solarYear > 2020) {
            return "";
        } else {
            solarMonth = (parseInt(solarMonth) > 0) ? (solarMonth - 1) : 11;
            e2c(solarYear, solarMonth, solarDay);
            return GetcDateString();
        }
    }

    var D = new Date();
    var yy = D.getFullYear();
    var mm = D.getMonth() + 1;
    var dd = D.getDate();
    var ww = D.getDay();
    var ss = parseInt(D.getTime() / 1000);
    var hh = D.getHours();
    var mmm = D.getMinutes();
    var sss = D.getSeconds();

    function getFullYear(d) {// 修正firefox下year错误
        yr = d.getYear();
        if (yr < 1000)
            yr += 1900;
        return yr;
    }

    function showDate() {
        // var sValue = getFullYear(dNow) + "/" + (dNow.getMonth() + 1) + "/" + dNow.getDate() + " " + hh + ":" + mmm + ":" + sss + " " + sWeek[dNow.getDay()] + "  ";
        var sValue = getFullYear(dNow) + "年" + (dNow.getMonth() + 1) + "月" + dNow.getDate() + "日" + " " + sWeek[dNow.getDay()] + " ";
        // var sValue = getFullYear(dNow) + "/" + (dNow.getMonth() + 1) + "/" + dNow.getDate() + "/" + hh + ":" + mmm + ":" + sss + " " + sWeek[dNow.getDay()] + " ";
        sValue += GetLunarDay(yy, mm, dd);
        document.getElementById("headerTime").innerHTML = sValue;
    };
    //注册机构下拉菜单
    $.jsonAjax(api_getregiste, {
        org_id: org_id,
    }, function (res) {
        if (res.code == 0) {
            if (res.data.length > 0) {
                $('.sing_content').css("height", 550);
                $('#area').show();
                $.each(res.data, function (index, item) {
                    var mm = $('<option value="' + item.org_id + '">' + item.org_name + '</option>').appendTo('.org');
                    var cmm = $('<dd class="" lay-value="' + item.org_id + '">' + item.org_name + '</dd>').appendTo('#sing_org_id_parent');
                    cmm.click(function () {
                        vl = $(this).attr("lay-value");
                        vh = $(this).html();
                        $("#sing_org_id").val(vl);
                        $("#sing_org_id_sel input").val(vh)
                    })
                })
            } else {
                $('#area').hide();
            }
        }
    });
    showDate();


    $("body").delegate(".layui-form-select", "click", function () {
        $(".layui-form-select").not(this).removeClass("layui-form-selected");
        if (!$(".layui-select-title input", this).attr("disabled"))
            $(this).toggleClass("layui-form-selected");
        return false;
    })
    //下拉组件
    $("body").delegate(".dropdown", "click", function () {
        $(".dropdown").not(this).removeClass("open");
        $(this).toggleClass("open");
        return false;
    });
    $("body").bind("click", function () {
        $(".dropdown").removeClass("open");
        $(".layui-form-select").removeClass("layui-form-selected");
    });
})

//公共的加入书架
function _addBookShelf_tag(book_id, btn, event) {
    if (_token) {
        btn.disabled = true;
        $.jsonAjax("/v2/api/bookShelf/addBook", {
            member_token: _token,
            book_id: book_id,
            org_id: org_id
        }, function (res) {
            if (res.code == 600) {
                // window.location.href = ctxPath + "/web?key=login";
                $(".login_plan").show();
            }
            else if (res.code == 0) {
                $(btn).html("已加入书架").attr("onclick", "");
            }
            else {
                $(btn).html("已加入书架").attr("onclick", "");
            }
            btn.disabled = false;
        })
    }
    else {
        // window.location.href = ctxPath + "/web?key=login";
        $(".login_plan").show();
    }
    event = event || window.event;
    if (event && event.stopPropagation)
        event.stopPropagation();
    if (event && event.preventDefault)
        event.preventDefault();
}

//公共的打开阅读
function _toBookRead_public(book_src, event) {
    if (_token) {
        window.open(book_src)
    }
    else {
        $(".login_plan").show();
    }
    event = event || window.event;
    if (event && event.stopPropagation)
        event.stopPropagation();
    if (event && event.preventDefault)
        event.preventDefault();
}