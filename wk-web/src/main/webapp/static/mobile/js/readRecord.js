$(document).ready(function () {
    getRecord(1);
    max_h = document.documentElement.clientHeight - 50 || document.body.clientHeight - 50;
    $(".content").scroll(function () {
        if (this.scrollHeight - max_h - this.scrollTop < 200) {
            getRecord();
        }
    });
})
var max_h = 0;
var param = {
    member_id: memberInfo.member_id,
    member_token: token,
    pageNum: 0,
    pageSize: 24,
    pages: 1,
    total: 0
}
var _ajax = true;

function getRecord(bool) {
    var loading = null;
    if (_ajax) {
        _ajax = false;
        param.pageNum += 1;
        if (bool) {
            loading = layer.load();
        }
        $.jsonAjax("/v2/api/book/queryReadingRecord", param, function (res) {
            if (res.code == 0) {
                $.each(res.data.rows, function (index, item) {
                    $("#searchValue").append('<div class="swiper-container swiper-container-horizontal wire"><div class="swiper-wrapper from-cell-row">' +
                        '<div class="swiper-slide">' +
                        '<div class="from-cell-label">' +
                        '<img src="'+ ctxPath + item.book_cover_small +'">' +
                        '</div>' +
                        '<div class="from-cell-cont">' +
                        '<h3>'+ item.book_name +'</h3>' +
                        '<p><span>'+ item.name +'</span></p>' +
                        '<p><span>'+ formatDate(item.end_time) +'</span></p>' +
                        '</div>' +
                        '<a class="from-cell-ft" href="'+ ctxPath +'/mobile/book/'+ item.book_id +'">继续阅读</a>' +
                        '</div>' +
                        '<div class="swiper-slide del">' +
                        '<span onclick="delRead('+ item.book_id +')">删除</span>' +
                        '</div>' +
                        '</div></div>');
                })
                param.pages = res.data.pages;
                if (param.pageNum < param.pages) {
                    _ajax = true;
                }
                if (res.data.total == 0) {
                    $('.noContent').show();
                    $('.from-cells').hide();
                }
                new Swiper('.swiper-container', {
                    slidesPerView: 'auto',
                    initialSlide: 0,
                    resistanceRatio: .00000000000001,
                    slideToClickedSlide: true
                })
            }
            layer.close(loading);
        })
    }
}


function formatDate(str) {
    var date = new Date(Date.parse(str.replace(/-/g, "/")));
    return (date.getMonth() + 1) + "月" + date.getDate() + "日 " + date.getHours() + ":" + date.getMinutes();
}