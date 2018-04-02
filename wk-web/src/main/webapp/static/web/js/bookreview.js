var pageNum = 1;
var center_list = $("#send_conn").empty();
var param = {
    member_token: _token,
    pageNum: 0,
    pageSize: 10,
    pages: 0,
    total: 0
}
/**
 * 获取所有评论
 */
function getBookReviews() {
    param.pageNum += 1;
    $.jsonAjax("/v2/api/member/bookReview",param,function (res) {
        $.each(res.data.rows, function (index, item) {
            var cm = $('<div class="send_item">' +
                '<div class="cover"><img src="' + item.book_cover_small + '"/></div>' +
                '<div class="send_detail">' +
                '<div class="line_1">' +
                '<h1>' + item.book_name + '</h1>' +
                // '<span class="send_ico" onclick="delReview('+ item.review_id +')">删除</span>' +
                '<div class="ling_time time">' + jQuery.timeago(item.create_time) + '</div>' +
                '</div>' +
                '<div class="ling_content">' + item.review_content + '</div>' +
                '<div class="back_list" style="display: none"></div>' +
                '</div>' +
                '</div>').appendTo(center_list);
        })
        if (res.data.total == 0) {
            $('<p class="cmm">你还没有发布过评论呢~</p>').appendTo(center_list);
        }
        else{
            $(".send_total").html("共"+ res.data.total +"条");
        }
        if(res.data.pages > 0 && res.data.pages > param.pageNum){
            $(".send_bottom").show();
            $('.send_bottom_end').hide();
        }
        else{
            $(".send_bottom").hide();
            $('.send_bottom_end').show();
        }
    })
    return false;
}


$(document).ready(function () {
    getBookReviews();
});


$(".send_bottom").click(function () {
    getBookReviews();
})
