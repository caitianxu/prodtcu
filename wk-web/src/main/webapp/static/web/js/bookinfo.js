$("#mm_img_code").empty();
// var qrcodeUrl = "http://" + window.location.host + ctxPath + "/api/appStore/downLoad?o=" + org_id + "&t=2&b=" + book_id + "&d=000000000000"; //测试设备
// var qrcodeUrl = "http://" + window.location.host + ctxPath + "/mobile/bookInfo/" + book_id; //微信页面
var qrcodeUrl = "http://" + window.location.host + ctxPath + "/api/book/info?o=" + org_id + "&t=2&b=" + book_id + "&d=000000000000"; //测试设备
// var qrcode = new QRCode(document.getElementById("mm_img_code"), {
//     text: qrcodeUrl,
//     width: 168,
//     height: 168,
//     colorDark: "#000000",
//     colorLight: "#ffffff",
//     correctLevel: QRCode.CorrectLevel.H,
//     src: 'www.cjszyun.net/static/web/img/weixin.jpg'
// });
$("#mm_img_code").qrcode({
    render: "canvas",    //设置渲染方式，有table和canvas，使用canvas方式渲染性能相对来说比较好
    text: qrcodeUrl,    //扫描二维码后显示的内容,可以直接填一个网址，扫描二维码后自动跳向该链接
    width: "110",               //二维码的宽度
    height: "110",              //二维码的高度
    background: "#ffffff",       //二维码的后景色
    foreground: "#000000"
});
console.log(qrcodeUrl)
var param = {
    org_id: org_id,
    book_id: book_id,
    pageNum: 0,
    pageSize: 12,
    pages: 0,
    total: 0
}
/**
 * 获取评论
 */
function getPllist(objbtn) {
    $(".send_bottom").hide();
    if (objbtn)
        objbtn.disabled = true;
    param.pageNum += 1;
    $.jsonAjax("/v2/api/mobile/bookReview/list", param, function (res) {
        if (res.data && res.data.rows.length > 0) {
            $.each(res.data.rows, function (index, item) {
                var icon = item.icon;
                if (!icon || icon == "") {
                    icon = ctxPath + "/static/web/img/lv/df.jpg"
                }
                var pitem = $('<div class="send_item" review_id="' + item.review_id + '">' +
                    '<div class="cover"><img src="' + icon + '"/></div>' +
                    '<div class="send_detail">' +
                    '<div class="line_1">' +
                    '<h1>' + item.nick_name + '</h1>' +
                    // '<span class="send_ico">回复</span>' +
                    '<div class="ling_time time">' + jQuery.timeago(item.create_time) + '</div>' +
                    '</div>' +
                    '<div class="ling_content">' + item.review_content + '</div>' +
                    '<div class="send_back_txt"><textarea placeholder=""></textarea><button class="send_back_btn" pid="' + item.review_id + '">回复</button></div>' +
                    '<div class="back_list" style="display: none"></div>' +
                    '</div>' +
                    '</div>').appendTo(".send_list");

                // //回复
                // $(".send_ico", pitem).click(function () {
                //     var ptxt = $(".send_back_txt", this.parentNode.parentNode).toggle();
                //     $("textarea", ptxt).val("");
                // });
                // $(".send_back_btn", pitem).click(function () {
                //     sendback(this);
                // })
            })
            param.pages = res.data.pages;
            param.total = res.data.total;
        }
        $(".send_top label").html("共" + res.data.total + "条").show();
        if (param.pageNum < param.pages) {
            $(".send_bottom").show();
        }
        else {
            $(".send_bottom").hide();
        }
        if (objbtn)
            objbtn.disabled = false;
    })
    return false;
}
getPllist();

/**
 * 获取更多评论
 */
$(".send_bottom").click(function () {
    getPllist(this);
})
/**
 * 加入书架
 */
function addBookShelf(btn) {
    btn.disabled = true;
    if (_token) {
        $.jsonAjax("/v2/api/bookShelf/addBook", {
            member_token: _token,
            book_id: book_id,
            org_id: org_id
        }, function (res) {
            if (res.code == 600) {
                window.location.href = ctxPath + "/web?key=login";
            }
            else if (res.code == 0) {
                $(btn).html("已加入书架").attr("onclick", "");
            }
        })
    }
    else {
        window.location.href = ctxPath + "/web?key=login";
    }
}
//发布评论
$("#send_msg_btn").click(function () {
    var send_msg = $("#send_msg").val();
    var thisbtn = this;
    thisbtn.disabled = true;
    if (_token) {
        if (send_msg) {
            $.jsonAjax("/v2/api/mobile/bookReview/addReview", {
                book_id: book_id,
                pid: null,
                member_token: _token,
                device_type: "pc",
                review_content: send_msg
            }, function (res) {
                if (res.code == 0) {
                    $(".send_list").empty();
                    param.pageNum = 0;
                    getPllist();
                    layer.msg("评论发布成功!");
                    $("#send_msg").val("");
                }
                else {
                    layer.msg(res.message);
                }
                thisbtn.disabled = false;
            })
            return false
        }
        else {
            thisbtn.disabled = false;
            layer.msg("请填写评论内容")
        }
    }
    else {
        thisbtn.disabled = false;
        layer.msg("请先登录")
    }
})
//回复评论
function sendback(thisbtn) {
    if (_token) {
        var pid = $(thisbtn).attr("pid");
        var send_msg = $("textarea", thisbtn.parentNode).val();
        if (send_msg) {
            if (thisbtn)
                thisbtn.disabled = true;
            $.jsonAjax("/v2/api/mobile/bookReview/addReview", {
                book_id: book_id,
                pid: pid,
                member_token: _token,
                device_type: "pc",
                review_content: send_msg
            }, function (res) {
                if (res.code == 0) {
                    layer.msg("回评成功!");
                    var parent_m = $(".send_item[review_id='" + pid + "']");
                    var back_list = $(".back_list", parent_m).show();
                    $('<div class="back_item">' +
                        '<div class="back_name">' +
                        '<p>' + res.data.nick_name + '：</p>' +
                        '<div class="time">' + jQuery.timeago(res.data.create_time) + '</div>' +
                        '</div>' +
                        '<div class="back_text">' + res.data.review_content + '</div>' +
                        '</div>').appendTo(back_list);
                    $(".send_back_txt", parent_m).hide();
                }
                else {
                    layer.msg(res.message);
                }
                thisbtn.disabled = false;
            })
        }
        else {
            thisbtn.disabled = false;
            layer.msg("请填写评论内容")
        }
    }
    else {
        thisbtn.disabled = false;
        layer.msg("请先登录")
    }
}