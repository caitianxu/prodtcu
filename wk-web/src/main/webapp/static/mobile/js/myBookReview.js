$(document).ready(function () {
    getReview(1);
    max_h = document.documentElement.clientHeight - 50 || document.body.clientHeight - 50;
    $(".content").scroll(function () {
        if (this.scrollHeight - max_h - this.scrollTop < 200) {
            getReview();
        }
    });
})
var max_h = 0;
var param = {
    member_token: token,
    pageNum: 0,
    pageSize: 24,
    pages: 1,
    total: 0
}
var _ajax = true;

//获取评论列表
function getReview(bool) {
    var loading = null;
    if(_ajax){
        _ajax = false;
        param.pageNum += 1;
        if (bool) {
            loading = layer.load();
        }
        var comments = $('.comments');
        $.jsonAjax("/v2/api/member/bookReview", param, function (res) {
            if(res.code == 0){
                $.each(res.data.rows, function (index, item) {
                    var col = $('#clone').clone().show();
                    $('#name', col).html("《"+ item.book_name +"》");
                    $('#time', col).html(formatDate(item.create_time));
                    $('#content', col).html(item.review_content);
                    $(comments).append(col)
                })
                param.pages = res.data.pages;
                if(param.pageNum < param.pages){
                    _ajax = true;
                }
                if(res.data.total == 0){
                    $('.noContent').show();
                    $('.comments').hide();
                }
            }
            layer.close(loading);
        })
    }
}

function formatDate(str) {
    var date = new Date(Date.parse(str.replace(/-/g, "/")));
    return (date.getMonth() + 1) + "月" + date.getDate() + "日 " + date.getHours() + ":" + date.getMinutes();
}