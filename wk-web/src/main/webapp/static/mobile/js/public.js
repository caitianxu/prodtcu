jQuery.extend({
    jsonAjax: function (url, param, callback) {
        if (!param) {
            param = {};
        }
        param.token_type = 'weixin';
        param.org_id = org_id;
        $.ajax({
            url: ctxPath + url,
            type: 'post',
            data: param,
            success: function (res) {
                if (res.code == 600) {
                    window.location.href = ctxPath + "/mobile/login";
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
function close_mm() {
    $('.app_down_top').slideUp();
}