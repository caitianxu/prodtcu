if (parseInt(all_pages) > 1) {
    $('.M-box').pagination({
        pageCount: parseInt(all_pages),
        prevContent: '<',
        nextContent: '>',
        callback: function (api) {
            param.pageNum = api.getCurrent();
            getPaperList()
        }
    });
}
$('ul').on('click', 'li', function (e) {
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

var param = {
    pageNum: 1,
    pageSize: 20,
    org_id: org_id,
    newspaper_cat_id: cat_id
}

function getPaperList() {
    window.scrollTo(0, 0);
    var news_list = $(".news_list").html('<p class="unloading">数据加载中,请稍候...</p>');
    $.jsonAjax("/site/newsPaper/getList", param, function (res) {
        news_list.empty();
        var allPages = res.data.pages;
        $.each(res.data.rows, function (index, item) {
            var ss = item.paper_url;
            if (!_token || _token == "") {
                ss = ctxPath + '/web/login';
            }
            var mm = $('<div class="paper_item">' +
                '<a href="' + ss + '" target="_blank" class="cover">' +
                '<img src="' + item.paper_img_small + '">' +
                '</a><p>' + item.paper_name + '</p>' +
                '<div class="detail">' +
                '</div>').appendTo(news_list);
            if ((index + 1) % 5 == 0) {
                mm.css("margin-right", 0);
            }
        })

        $('.M-box').pagination({
            pageCount: allPages,
            prevContent: '<',
            nextContent: '>',
            current: res.data.pageNum,
            callback: function (api) {
                param.pageNum = api.getCurrent();
                getPaperList();
            }
        });
        if(allPages > 1){
            $('.M-box').css("display", "block");
        }else{
            $('.M-box').css("display", "none");
        }
    })
}
function getList(paper_cat_id, num) {
    param.newspaper_cat_id = paper_cat_id;
    param.pageNum = 1;
    getPaperList();
    getPaperCatName(paper_cat_id);
    parent = num;
}
    var parent = 0;
function getPaperCatName(id) {
    $('#paperCats').html('');
    $.jsonAjax("/site/paper/getCats", {cat_id: id, org_id: org_id}, function (res) {
        var str = '';
        var catID = res.data.cat_name_id;
        if (res.data.cat_name) {
            str = '<em>&gt;</em>' +
                '<a href="javaScript:void(0)" onclick="getList( ' + catID + ')">' +
                res.data.cat_name + '</a>';
        }
        var str1 = '';
        if (res.data.c_cat_name) {
            str1 = '<em>&gt;</em> <label>' + res.data.c_cat_name + '</label>';
        }
        var all = '<a href="' + ctxPath + '/web" target="_blank">首页</a>' +
            '<em>&gt;</em>' +
            '<a href="' + ctxPath + '/web/paper" target="_blank">报纸</a>' + str + str1;
        $('#paperCats').html(all);
    })
}

