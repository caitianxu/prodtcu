if (searchKey) {
    $(".header .top_search input[name='key']").val(searchKey);
    //判断是否有分类
    if (book_cat_id) {
        $('<input type="hidden" name="cid" value="' + book_cat_id + '"/>').appendTo(".top_search");
    }
}
var param = {
    org_id: 1,
    book_cat_id: book_cat_id,
    searchText: searchKey,
    order_type: null,
    pageNum: 1,
    pageSize: 25,
    pages: 1,
    total: 0
}
function set_order_type(obj, t) {
    $('.order-type li').removeClass('active');
    $(obj).addClass('active');
    param.order_type = t;
    param.pageNum = 1;
    $('.M-box').pagination({
        callback: function (api) {
             param.pageNum = api.getCurrent();
            getBookData();
        }
    });
    getBookData();
}
function getBookData() {
    window.scrollTo(0, 0);
    var book_list = $(".book_list").html('<p class="unloading">数据加载中,请稍候...</p>');
    $.jsonAjax("/v2/api/book/getList", param, function (res) {
        book_list.empty();
        var allPages = res.data.pages;
        if (res.data.rows.length < 1) {
            $(".book_list").html('<p class="un_msg">没搜到？把你想找的书<a href="javascript:void(0)">告诉我们</a>~</p>');
        } else {
            $.each(res.data.rows, function (index, item) {
                var df_cover = item.book_cover_small ? item.book_cover_small : ctxPath + '/static/web/img/book_cover.png';
                var mm = $('<div href="' + ctxPath + "/web/bookinfo/" + item.book_id + '" class="book_item">' +
                    '<div class="cover"><a href="' + ctxPath + '/web/bookinfo/' + item.book_id + '" target="_blank"><img src="' + df_cover + '" /></a>' +
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

            $('.M-box').show;
            $('.M-box').pagination({
                pageCount: allPages,
                prevContent: '<',
                nextContent: '>',
                current: res.data.pageNum,
                callback: function (api) {
                    param.pageNum = api.getCurrent();
                    getBookData();
                }
            });
        }
        if (allPages > 1) {
            $('.M-box').css("display", "block");
        } else {
            $('.M-box').css("display", "none");
        }

    })
}
if (parseInt(all_pages) > 1) {
    $('.M-box').pagination({
        pageCount: parseInt(all_pages),
        prevContent: '<',
        nextContent: '>',
        callback: function (api) {
            param.pageNum = api.getCurrent();
            getBookData();
        }
    });
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

$('ul').on('click', 'li', function () {
    var style = $(this).find(".cat-child-list").attr("style");
    if (style == "display: none;" && parent == 1 || parent == 2) {
        $(this).css("backgroundColor", "#f8f8f8")
        $(this).find(".cat-child-list").css("display", "block")
        $(this).find("i").attr("class", "cat-bottom")
        parent = 0;
    }
    else {
        $(this).css("backgroundColor", "#fff")
        $(this).find(".cat-child-list").css("display", "none")
        $(this).find("i").attr("class", "cat-right")
    }

});
var parent = 0
function book(id,num) {
    param.book_cat_id = id;
    param.pageNum = 1;
    getBookData();
    getBookCatName(id);
    parent = num
}

function getBookCatName(id) {
    $('#bookCats').html('');
    $.jsonAjax("/site/bookCat/getCats", {cat_id: id, org_id: org_id}, function (res) {
        var str = '';
        var catID = res.data.cat_name_id;
        if (res.data.cat_name) {
            str = '<em>&gt;</em>' +
                '<a href="javaScript:void(0)" onclick="book( ' + catID + ')">' +
                res.data.cat_name + '</a>';
        }
        var str1 = '';
        if (res.data.c_cat_name) {
            str1 = '<em>&gt;</em> <label>' + res.data.c_cat_name + '</label>';
        }
        var all = '<a href="' + ctxPath + '/web" target="_blank">首页</a>' +
            '<em>&gt;</em>' +
            '<a href="' + ctxPath + '/web/booklist" target="_blank">图书</a>' + str + str1;
        $('#bookCats').html(all);
    })
}