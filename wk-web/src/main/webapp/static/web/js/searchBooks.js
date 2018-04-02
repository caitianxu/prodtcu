if (searchKey) {
    $(".header .top_search input[name='key']").val(searchKey);
}
var param = {
    org_id: 1,
    book_cat_id: null,
    searchText: searchKey,
    pageNum: 1,
    pageSize: 35,
    pages: 1,
    total: 0
}
if (parseInt(all_pages) > 1) {
    $('.M-box').pagination({
        pageCount: parseInt(all_pages),
        prevContent: '<',
        nextContent: '>',
        callback: function (api) {
            param.pageNum = api.getCurrent();
            window.scrollTo(0, 0);
            var book_list = $(".book_list").html('<p class="unloading">数据加载中,请稍候...</p>');
            $.jsonAjax("/v2/api/book/getList", param, function (res) {
                book_list.empty();
                $.each(res.data.rows, function (index, item) {
                    var mm = $('<div href="' + ctxPath + "/web/bookinfo/" + item.book_id + '" class="book_item">' +
                        '<div class="cover"><a href="/web/bookinfo/'+ item.book_id +'" target="_blank"><img src="' + item.book_cover_small + '" /></a>' +
                        '<div class="book-pos-deal"><s></s>' +
                        '<div class="pos_name">'+ item.book_name +'</div>' +
                        '<div class="pos_author">' + item.book_author + '</div>' +
                        '<div class="pos_remark">' + item.book_remark + '</div>' +
                        '<div class="pos_button">' +
                        '<a href="/web/book/'+ item.book_id +'" target="_blank" class="btn btn-green">开始阅读</a>' +
                        '<button class="btn" onclick="_addBookShelf_tag('+ item.book_id +', this, event)">加入书架</button>' +
                        '</div>' +
                        '<div class="pos-zhichi">' +
                        '<p>适用阅读设备</p>' +
                        '<img src="/static/web/img/zhichi1.jpg" style="width: auto">' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '<h1>' + item.book_name + '</h1>' +
                        '</div>').appendTo(book_list);
                    if ((index + 1) % 7 == 0) {
                        mm.css("margin-right", 0);
                    }
                })
            })
        }
    });
}
//判断是否有分类
var active = $(".content_left ul li.active");
if (active.length) {
    $("#cat_name").html($("a", active).html());
}
//打开意见反馈
$(".un_msg a").click(function () {
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