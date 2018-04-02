var pageNum = 1;var pager = null;
function getAllBookRack() {
    var center_list = $(".center_list").html('<p class="unloading">数据加载中,请稍候...</p>');
    $.jsonAjax("/v2/api/bookShelf/getList",{
        member_token: _token
    },function (res) {
        center_list.empty();
        if(res.data) {
            $.each(res.data, function (index, item) {
                var str = item.bk_cover_small;
                if(!str ){
                    str = '/static/web/img/book_cover.png';
                }
                var mm = $('<div class="book_item">' +
                    '<div class="cover"><img src="' + str + '" /></div>' +
                    '<div class="detail">' +
                    '<h1>' + item.bk_name + '</h1>' +
                    '<h2>' + item.bk_author + '</h2>' +
                    '<h3>' + item.book_publisher + '</h3>' +
                    '<a href="' + ctxPath + '/web/book/' + item.bk_id + '" target="_blank">开始阅读</a>' +
                    '<span class="m_close_x" onclick="delbook(' + item.bk_id + ')"></span>' +
                    '</div>' +
                    '</div>').appendTo(center_list);
                if ((index + 1) % 3 == 0) {
                    mm.css("margin-right", 0);
                }
            })
        }
        if(res.data.length == 0){
            $('<p class="cmm">你还没有收藏图书呢~</p>').appendTo(center_list);
        }
    })

    return false
}
$(document).ready(function () {
    getAllBookRack();
});

/**
 * 移除书架
 * @param book_id
 */
function delbook(book_id) {
    $.jsonAjax("/v2/api/bookShelf/delBook",{
        member_token: _token,
        book_id: book_id
    },function (res) {
        getAllBookRack();
    })
}