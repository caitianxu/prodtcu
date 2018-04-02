var param = {
    book_cat_id: null,
    searchText: searchText,
    pageNum: 1,
    pageSize: 24,
    pages: 1,
    total: 0
}
var book_cat_id = null;
var max_h = 0;
$(document).ready(function () {
    var bookCats = [{
        book_cat_id: 0,
        book_cat_name: '热门图书'
    }, {
        book_cat_id: -1,
        book_cat_name: '最新推荐'
    }];
    max_h = document.documentElement.clientHeight - 50 || document.body.clientHeight - 50;
    //获取分类
    $.jsonAjax("/v2/api/bookCat/getList", null, function (res) {
        if (res.code == 0) {
            $.each(res.data, function (index, item) {
                bookCats.push({
                    book_cat_id: item.book_cat_id,
                    book_cat_name: item.book_cat_name
                })
            });
            var ll = $('#classify');
            $.each(bookCats, function (index, item) {
                var l = $('<div class="col col_3"><span>' + item.book_cat_name + '</span></div>').appendTo(ll);
                l.click(function () {
                    $('span', ll).removeClass("active")
                    $("span", this).addClass('active')
                    book_cat_id = item.book_cat_id;
                })
            })
        }
    })
    $(".shade").click(function () {
        $('#screen,.shade').hide();
    })
    getBookList(1);
    $('.content').scroll(function () {
        if (this.scrollHeight - max_h - this.scrollTop < 200) {
            getBookList();
        }
    });
})
var _ajax = false;
/**
 * 查询图书列表
 * @param bool
 * @returns {boolean}
 */
function getBookList(bool) {
    if (_ajax)
        return false;
    _ajax = true;
    var searchValue = $('#searchValue');
    var loading = null;
    if (bool) {
        searchValue.empty();
        param.pageNum = 0;
        loading = layer.load();
    }
    param.pageNum += 1;
    $.jsonAjax("/v2/api/book/getList", param, function (res) {
        if (res.code == 0) {
            $.each(res.data.rows, function (index, item) {
                $('<a class="from-cell-row wire" href="' + ctxPath + '/mobile/bookInfo/' + item.book_id + '">' +
                    '<div class="from-cell-label">' +
                    '<img src="' + ctxPath + item.book_cover_small + '">' +
                    '</div>' +
                    '<div class="from-cell-cont">' +
                    '<h3>' + item.book_name + '</h3>' +
                    '<p><span>' + item.book_remark + '</span></p>' +
                    '<p><span>' + item.book_author + '</span></p>' +
                    '</div>' +
                    '</a>').appendTo(searchValue);
            })
            param.pages = res.data.pages;
            if (param.pageNum >= param.pages) {
                $('<p class="undata">没有更多内容了</p>').appendTo(searchValue);
            }
            if(res.data.total == 0){
                $('.noContent').show();
            }
            else{
                $('.noContent').hide();
            }
        }
        else {
            layer.msg(res.message);
        }
        layer.close(loading);
        _ajax = false;
    })
}
/**
 * 重置
 */
function reset() {
    $('#classify .active').removeClass('active');
}
/**
 * open筛选
 */
function openScreen() {
    $('#screen').show();
    $('.shade').show();
}
/**
 *筛选确认
 */
function screen_submit() {
    $('#screen,.shade').hide();
    param.book_cat_id = book_cat_id;
    getBookList(1);
}