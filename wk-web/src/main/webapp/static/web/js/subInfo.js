var b_param = {
    org_id: 1,
    subject_id: subject_id,
    searchText: null,
    pageNum: 1,
    pageSize: 20,
    pages: 1,
    total: 0
}
if (parseInt(all_pages) > 1) {
    $('#book_page').pagination({
        pageCount: parseInt(all_pages),
        prevContent: '<',
        nextContent: '>',
        callback: function (api) {
            b_param.pageNum = api.getCurrent();
            window.scrollTo(0, 0);
            var book_list = $(".book_list").html('<p class="unloading">数据加载中,请稍候...</p>');
            $.jsonAjax("/site/subject/bookList", b_param, function (res) {
                book_list.empty();
                $.each(res.data.rows, function (index, item) {
                    var mm = $('<div href="' + ctxPath + "/web/bookinfo/" + item.book_id + '" class="book_item">' +
                        '<div class="cover"><a href="/web/bookinfo/' + item.book_id + '" target="_blank"><img src="' + item.book_cover_small + '" /></a>' +
                        '<div class="book-pos-deal"><s></s>' +
                        '<div class="pos_name">' + item.book_name + '</div>' +
                        '<div class="pos_author">' + item.book_author + '</div>' +
                        '<div class="pos_remark">' + item.book_remark + '</div>' +
                        '<div class="pos_button">' +
                        '<a href="javascript:void(0)" onclick="_toBookRead_public(\'/web/book/' + item.book_id + '\',event)" target="_blank" class="btn btn-green">开始阅读</a>' +
                        '<button class="btn" onclick="_addBookShelf_tag(' + item.book_id + ', this, event)">加入书架</button>' +
                        '</div>' +
                        '<div class="pos-zhichi">' +
                        '<p>适用阅读设备</p>' +
                        '<img src="/static/web/img/zhichi1.jpg" style="width: auto">' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '<h1>' + item.book_name + '</h1>' +
                        '</div>').appendTo(book_list);
                    if ((index + 1) % 5 == 0) {
                        mm.css("margin-right", 0);
                    }

                })
            })
        }
    });
}
var a_param = {
    org_id: 1,
    subject_id: subject_id,
    searchText: null,
    pageNum: 1,
    pageSize: 20,
    pages: 1,
    total: 0
}
if (parseInt(art_pages) > 1) {
    $('#art_page').pagination({
        pageCount: parseInt(art_pages),
        prevContent: '<',
        nextContent: '>',
        callback: function (api) {
            a_param.pageNum = api.getCurrent();
            window.scrollTo(0, 0);
            var inf_list = $(".inf_list").html('<p class="unloading">数据加载中,请稍候...</p>');
            $.jsonAjax("/site/subject/articleList", a_param, function (res) {
                inf_list.empty();
                $.each(res.data.rows, function (index, item) {
                    var str = "";
                    if(item.article_type == 2){
                        str = item.article_content;
                    }else{
                        str = ctxPath + "/web/inf/" + item.article_cat_id + "/" + item.article_id;
                    }
                    var times = "";
                    if(item.publish_time){
                        times = '<label>' + item.publish_time + '</label>';
                    }
                    var mm = $('<a href="' + str + '" class="inf_item" target="_blank">' +
                        ' <h1>' + item.article_title+ '</h1> ' + times +
                        '</a>').appendTo(inf_list);
                    if ((index + 1) % 3 == 0) {
                        mm.css("margin-right", 0);
                    }

                })
            })
        }
    });
}
$("#newsContentHtml").click(function () {
    $(".newsContentHtml").show()
    $(".bookContentHtml").hide()
    $(".articlesContentHtml").hide()
    $(".cmcc").removeClass("active");
    $("#newsContentHtml").addClass("active");
})
$("#bookContentHtml").click(function () {
    $(".newsContentHtml").hide()
    $(".bookContentHtml").show()
    $(".articlesContentHtml").hide()
    $(".cmcc").removeClass("active");
    $("#bookContentHtml").addClass("active");
})

$("#articlesContentHtml").click(function () {
    $(".newsContentHtml").hide()
    $(".bookContentHtml").hide()
    $(".articlesContentHtml").show()
    $(".cmcc").removeClass("active");
    $("#articlesContentHtml").addClass("active");
})