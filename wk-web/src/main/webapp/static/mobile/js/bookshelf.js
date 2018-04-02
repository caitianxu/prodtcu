$(document).ready(function () {
    $.jsonAjax("/v2/api/bookShelf/getList", {
        member_token: token
    }, function (res) {
        var ranking = $('.ranking').empty();
        $.each(res.data, function (index, item) {
            $('<div class="col">' +
                '<a href="' + ctxPath + '/mobile/book/' + item.bk_id + '" class="img-box">' +
                '<span class="icon_box"><i class="icon_gou"></i></span>' +
                '<img src="' + ctxPath + item.bk_cover_small + '">' +
                '</a>' +
                '<div class="hr"></div>' +
                '</div>').appendTo(ranking);
        });
        if (res.data.length % 3 != 0) {
            var ii = 3 - res.data.length % 3;
            for (var i = 0; i < ii; i++) {
                $('<div class="col">' +
                    '<a  class="img-box" style="opacity:0">' +
                    '<span class="icon_box"><i class="icon_gou"></i></span>' +
                    '</a>' +
                    '<div class="hr"></div>' +
                    '</div>').appendTo(ranking);
            }
        }

    })
});