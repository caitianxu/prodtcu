if (parseInt(all_pages) > 1) {
    $('.M-box').pagination({
        pageCount: parseInt(all_pages),
        prevContent: '<',
        nextContent: '>',
        callback: function (api) {
            param.pageNum = api.getCurrent();
            getPaperList();
        }
    });
}

var param = {
    pageNum: 1,
    pageSize: 15,
    org_id: org_id,
    cid: cat_id || 0
}

function getPaperList() {
    window.scrollTo(0, 0);
    var inf_list = $(".inf_list").html('<p class="unloading">数据加载中,请稍候...</p>');
    $.jsonAjax("/site/inf/getList", param, function (res) {
        inf_list.empty();
        var allPages = res.data.pages;
        $.each(res.data.rows, function (index, item) {
            var article_type = item.article_type;
            var path = article_type == 2 ? item.article_content : ctxPath + '/web/inf/' + item.article_cat_id + "/" + item.article_id;
            var mm = $('<a href="' + path + '" class="inf_item" target="_blank" style="float: left;display:inline-block;position: relative;"> ' +
                '<div style="position: absolute;height: 100%"> ' +
                '   <img src="' + ctxPath + item.cover_url_small + '" >' +
                '</div>' +
                '<div style="float:left;width:100%;padding-left:270px;">' +
                '   <div style="padding:0 20px;">' +
                '       <span>' + item.article_title + '</span>' +
                '       <p>' + item.article_remark + '</p>' +
                '       <label>' + item.publish_time + '</label>' +
                '   </div>' +
                '</div>' +
                '</a>').appendTo(inf_list);
            if ((index + 1) % 3 == 0) {
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
        if (allPages > 1) {
            $('.M-box').css("display", "block");
        } else {
            $('.M-box').css("display", "none");
        }
    })
}
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
var  parent = 0;
function infList(cat_id ,num) {
    param.cid = cat_id;
    param.pageNum = 1;
    getPaperList();
    getInfCatName(cat_id)
    parent = num;
}
function getInfCatName(id) {
    $('#infCats').html('');
    $.jsonAjax("/site/articleCats/getCats", {cat_id: id, org_id: org_id}, function (res) {
        var str = '';
        var catID = res.data.cat_name_id;
        if (res.data.cat_name) {
            str = '<em>&gt;</em>' +
                '<a href="javaScript:void(0)" onclick="infList( ' + catID + ')">' +
                res.data.cat_name + '</a>';
        }
        var str1 = '';
        if (res.data.c_cat_name) {
            str1 = '<em>&gt;</em> <label>' + res.data.c_cat_name + '</label>';
        }
        var all = '<a href="' + ctxPath + '/web" target="_blank">首页</a>' +
            '<em>&gt;</em>' +
            '<a href="' + ctxPath + '/web/inf" target="_blank">资讯</a>' + str + str1;
        $('#infCats').html(all);
    })
}




