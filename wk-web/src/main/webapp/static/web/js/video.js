$(".content_left ul li a").click(function () {
    var mcid = $(this).attr("cid");
    var ctext = $(this).html();
    c_cid = {
        mcid: mcid,
        ctext: ctext
    }
    $(".content_left ul li").removeClass("active");
    $(this).parent().addClass("active");
    var ccml_path = $("#ccml_path").css("display", "inline-block");
    $("label", ccml_path).html(c_cid.ctext);
    cid = mcid;
    getNodeList(1);
})
if (parseInt(all_pages)) {
    $('.M-box').pagination({
        pageCount: parseInt(all_pages),
        prevContent: '<',
        nextContent: '>',
        callback: function (api) {
            window.scrollTo(0, 0);
            var pageNum = api.getCurrent();
            var news_list = $(".inf_list").empty();
            $.ajax({
                url: ctxPath + "/site/njswVideo/getList",
                type: "post",
                data: {
                    pageNum: pageNum,
                    pageSize: 15,
                    cid: cid
                },
                success: function (res) {
                    $.each(res.data.rows, function (index, item) {
                        var mm = $('<a href="' + item.video_id + '" class="cf_item" target="_blank">' +
                            '<img src="' + item.cover_url_small + '"/>' +
                            '<p>' + item.video_title + '</p>' +
                            '</a>').appendTo(news_list);
                        if ((index + 1) % 3 == 0) {
                            mm.css("margin-right", 0);
                        }
                    })
                }
            })
        }
    });
}
function getNodeList(pageNum) {
    var news_list = $(".inf_list").html('<p class="unloading">数据加载中,请稍候...</p>');
    $.ajax({
        url: ctxPath + "/site/njswVideo/getList",
        type: "post",
        data: {
            pageNum: pageNum,
            pageSize: 15,
            cid: cid
        },
        success: function (res) {
            news_list.empty();
            $.each(res.data.rows, function (index, item) {
                var mm = $('<a href="' + item.video_id + '" class="cf_item" target="_blank">' +
                    '<img src="' + item.cover_url_small + '"/>' +
                    '<p>' + item.video_title + '</p>' +
                    '</a>').appendTo(news_list);
                if ((index + 1) % 3 == 0) {
                    mm.css("margin-right", 0);
                }
            });
            if (parseInt(res.data.pages) > 0) {
                $('.M-box').pagination({
                    pageCount: res.data.pages,
                    current: pageNum,
                    coping: true,
                    prevContent: '<',
                    nextContent: '>',
                    callback: function (api) {
                        window.scrollTo(0, 0);
                        var pageNum = api.getCurrent();
                        getNodeList(pageNum);
                    }
                });
            }
            else {
                $(".M-box").empty();
            }
        }
    })
}