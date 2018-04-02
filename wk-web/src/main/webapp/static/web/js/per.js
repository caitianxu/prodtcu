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
    pageSize: 25,
    org_id: org_id,
    cid: cat_id || 0,
    periodical_cat_id: cat_id || 0
}

function getPaperList() {
    window.scrollTo(0, 0);
    var per_list = $(".per_list").html('<p class="unloading">数据加载中,请稍候...</p>');
    $.jsonAjax("/site/periodical/periodicalList", param, function (res) {
        per_list.empty();
        var allPages = res.data.pages;
        $.each(res.data.rows, function (index, item) {
            a = $('<a href="' + ctxPath + '/web/perlist/' + item.periodical_id + '" target="_blank" class="per_item">' +
                '<div class="cover">' +
                '<img src="' + ctxPath + item.periodical_cover_small + '">' +
                '<label>' + item.periodical_name.replace(item.series_name, "") + '</label>' +
                '</div>' +
                '<p>' + item.periodical_name + '</p>' +
                '</a>').appendTo(per_list);
            if (index > 0 && (index + 1) % 5 == 0) {
                a.css("margin-right", 0)
            }
        });
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
function perList(cat_id ,num) {
    param.periodical_cat_id = cat_id;
    param.pageNum = 1;
    getPaperList();
    getPerCatName(cat_id)
    parent = num
}
function getPerCatName(id) {

    $('#perCats').html('');
    $.jsonAjax("/site/periodicalCats/getCats", {cat_id: id, org_id: org_id}, function (res) {
        console.log(res);
        var str = '';
        var catID = res.data.cat_name_id;
        if (res.data.cat_name) {
            str = '<em>&gt;</em>' +
                '<a href="javaScript:void(0)" onclick="perList( ' + catID + ')">' +
                res.data.cat_name + '</a>';
        }
        var str1 = '';
        if (res.data.c_cat_name) {
            str1 = '<em>&gt;</em> <label>' + res.data.c_cat_name + '</label>';
        }
        var all = '<a href="' + ctxPath + '/web" target="_blank">首页</a>' +
            '<em>&gt;</em>' +
            '<a href="' + ctxPath + '/web/per/" target="_blank">期刊</a>' + str + str1;
        $('#perCats').html(all);
    })
}

