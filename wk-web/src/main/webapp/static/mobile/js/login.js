var bool = true;
function submit(obj) {
    var account = document.getElementById("member_phone");
    var pwd = document.getElementById("send_code");
    if(!account.value || account.value == ''){
        layer.msg('请填写用户名!')
        account.focus();
        return false;
    }
    if(!pwd.value || pwd.value == ''){
        layer.msg('请输入密码!')
        pwd.focus();
        return false;
    }
    if(bool) {
        bool = false;
        obj.innerHTML = "正在登录..";
        $.jsonAjax("/v2/api/mobile/login", {
            account: account.value,
            pwd: pwd.value
        }, function (res) {
            if (res.code == 0) {
                if(window.location.href.indexOf('login') != -1) {
                    window.location.href = ctxPath + '/mobile';
                }
                else{
                    window.location.reload();
                }
            }
            else {
                bool = true;
                obj.innerHTML = "登录";
                layer.msg(res.message);
            }
        })
    }
}