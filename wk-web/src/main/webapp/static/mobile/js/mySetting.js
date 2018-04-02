$(document).ready(function () {
    <!--头像上传-->
    $('#head-log').prettyFile({
        text: "上传头像",
        change: function (res, obj) {
            $('.member_head').attr('src', ctxPath + res.data[0].url);
            $.jsonAjax("/v2/api/mobile/memberInfo/updateIcon", {
                member_token: token,
                icon: res.data[0].url
            }, function (res) {
                layer.msg('头像上传成功!')
            })
        }
    });
    <!--调用上传-->
    $('#update_head').click(function () {
        $('#head-log').click()
    });

})