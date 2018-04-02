//切换
$(".order_title span").click(function () {
    $(".order_title span").removeClass("active");
    $(".book_order,.user_order").hide();
    $(this).addClass("active");
    var un = $(this).attr("no-target");
    $("." + un).show();
});
//图书切换
$("#book_group a").click(function () {
    $("#book_group a.active").removeClass("active");
    this.className = "active";
    book_page_num = 1;
    M_pager = null;
    getBookData();
});
//用户切换
$("#user_group a").click(function () {
    $("#user_group a.active").removeClass("active");
    this.className = "active";
    book_page_num_e = 1;
    E_pager = null;
    getUserData();
});
var book_page_num = 1;
var M_pager = null;
/**
 * 获取图书排行
 */
function getBookData() {
    layer.load(1);
    $.ajax({
        url: ctxPath + "/site/bookOrder/getBookData",
        data: {
            pageSize: 10,
            pageNum: book_page_num,
            type: $("#book_group a.active").attr("did"),
            org_id: org_id
        },
        type: 'post',
        success: function (res) {
            var book_order_items = $(".book_order_items").empty();
            $.each(res.data.rows, function (index, item) {
                var mitem = $('<div class="book_item">' +
                    '<div class="cover"><img src="' + ctxPath + item.book_cover_small + '" /></div>' +
                    '<div class="detail">' +
                    '<h1>' + item.book_name + '</h1>' +
                    '<h2>' + item.book_author + '</h2>' +
                    '<p>' + item.book_remark + '</p>' +
                    '<a href="' + ctxPath + '/web/bookinfo/' + item.book_id + '" target="_blank">开始阅读</a>' +
                    '</div>' +
                    '</div>');
                book_order_items.append(mitem);
            });
            if (res.data.pages > 1) {
                if (M_pager == null) {
                    M_pager = $('#book_M').pagination({
                        pageCount: res.data.pages,
                        prevContent: '<',
                        nextContent: '>',
                        callback: function (api) {
                            book_page_num = parseInt(api.getCurrent());
                            getBookData();
                        }
                    });
                }
            }
            else {
                $("#book_M").empty();
            }
            layer.closeAll();
        }
    })
}

var book_page_num_e = 1;
var E_pager = null;
/**
 * 获取用户排行
 */
function getUserData() {
    layer.load(1);
    $.ajax({
        url: ctxPath + "/site/userOrder/getMemberData",
        data: {
            pageSize: 10,
            pageNum: book_page_num_e,
            type: $("#user_group a.active").attr("did"),
            org_id: org_id
        },
        type: 'post',
        success: function (res) {
            var user_items = $(".user_items").empty();
            $.each(res.data.rows, function (index, item) {
                var mitem = $('<div class="user_item">' +
                    '<div class="cover"><img src="http://product.cjszyun.cn/static/admin/img/df_touxiang.png"/></div>' +
                    '<div class="detail">' +
                    '<h1>' + item.nick_name + '</h1>' +
                    '<div class="code_m">阅读指数: ' + item.read_index + '</div>' +
                    '</div>' +
                    '</div>');
                user_items.append(mitem);
            });
            if (res.data.pages > 1) {
                if (E_pager == null) {
                    E_pager = $('#book_E').pagination({
                        pageCount: res.data.pages,
                        prevContent: '<',
                        nextContent: '>',
                        callback: function (api) {
                            book_page_num_e = parseInt(api.getCurrent());
                            getUserData();
                        }
                    });
                }
            }
            else {
                $("#book_E").empty();
            }
            layer.closeAll();
        }
    })
}
getBookData();
getUserData();