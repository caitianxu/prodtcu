if (parseInt(all_pages) > 1) {
    $('.M-box').pagination({
        pageCount: parseInt(all_pages),
        prevContent: '<',
        nextContent: '>',
        callback: function (api) {
            var pageNum = api.getCurrent();
            var inf_list = $(".inf_list").html('<p class="unloading">数据加载中,请稍候...</p>');
            var pageSize = 5;
            window.scrollTo(0,0);
            $.ajax({
                url: ctxPath + "/site/njswAct/getList",
                type: "post",
                data: {
                    pageNum: pageNum,
                    pageSize: pageSize
                },
                success: function (res) {
                    inf_list.empty();
                    $.each(res.data.rows, function (index, item) {
                        var mm = $('<a href="' + ctxPath + '/web/act/' + item.activity_id + '" class="act_item" target="_blank">' +
                            '<div class="cover">' +
                            '<img src="' + item.cover_url_small + '">' +
                            '</div>' +
                            '<div class="act_detail">' +
                            '<h1>' + item.activity_title + '</h1>' +
                            '<p>' + item.activity_content + '</p>' +
                            '<div class="p_time">' + item.start_time + ' - ' + item.end_time + '</div>' +
                            '</div>' +
                            '</a>').appendTo(inf_list);
                    })
                }
            })
        }
    });
}