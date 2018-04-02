var ajax = true;
jQuery.extend({

    jsonAjax: function (url, param, callback) {
        if (!param) {
            param = {};
        }
        param.token_type = 'pc';
        $.ajax({
            url: ctxPath + url,
            type: 'post',
            data: param,
            success: function (res) {
                if (res.code == 600) {
                    console.log(res)
                }
                else if (callback) {
                    callback(res);
                }
            },
            error: function (error) {
                console.log(error)
            }
        })
    }
});
jQuery.fn.extend({
    coord: function (e) {
        if (this.length == 0) {
            return param = {top: 0, left: 0, width: 0, height: 0}
        } else {
            var param = {};
            var obj = this.get(0);
            var param = {height: parseInt(obj.clientHeight), width: parseInt(obj.clientWidth), top: 0, left: 0};
            do {
                param.top += obj.offsetTop || 0;
                param.left += obj.offsetLeft || 0;
                obj = obj.offsetParent
            } while (obj);
            return param
        }
    }
});
function init() {
if(memberInfo.read_size){
            $("#fontSize").html(memberInfo.read_size);
            $(".book_html").css("font-size", memberInfo.read_size);
        }
    upSize();
    $(window.document).scroll(function () {
        var top = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop;
        if (top > 0) {
            $("#top_page").show();
        }
        else {
            $("#top_page").hide();
        }
    })
    $(window).resize(function () {
        upSize();
    });
    //减小字体
    $("#min_size").click(function () {
        var fontSize = parseInt($("#fontSize").html());
        if (fontSize > 12) {
            fontSize -= 1;
        }
        $("#fontSize").html(fontSize);
        $(".book_html").css("font-size", fontSize);
        setSizeSkin(fontSize,null)
    })
    //加大字体
    $("#max_size").click(function () {
        var fontSize = parseInt($("#fontSize").html());
        if (fontSize < 20) {
            fontSize += 1;
        }
        $("#fontSize").html(fontSize);
        $(".book_html").css("font-size", fontSize);
        setSizeSkin(fontSize,null)
    })
    //目录
    $("#book_menu,#content_mulu").click(function () {
        $(".book_menu_plan").toggle();
        $(".book_mark_plan").hide();
        //目录定位
        var site = $('a[chapterid="' + chapterid + '"]').coord();
        console.log(site)
        $('.skin_2 .book_menu_plan .book_ls_list')[0].scrollTop = site.top - 100;
    })
    //书签
    $("#book_mark").click(function () {
        $(".book_mark_plan").toggle();
        $(".book_menu_plan").hide();
    })
    //回顶部
    $("#top_page").click(function () {
        window.scrollTo(0, 0);
        window.scrollTo(0, 0);
    })
    $(document).keyup(function (event) {
        var e = event || window.event
        if (e.keyCode == 13) {
            $(".book_menu_plan").toggle();
            $(".book_mark_plan").hide();
        }
        else if (e.keyCode == 39) {
            var dd = $(".book_menu_plan a.active").parent();
            if (dd.next().length) {
                window.location.href = $("a", dd.next())[0].getAttribute("href");
            }
        }
        else if (e.keyCode == 37) {
            var dd = $(".book_menu_plan a.active").parent();
            if (dd.prev().length) {
                window.location.href = $("a", dd.prev())[0].getAttribute("href");
            }
        }
    })
    var nowindex = null;
    var allas = $(".book_menu_plan a");
    var nowChobj = null;
    allas.each(function (index, item) {
        if (item.getAttribute("chapterid") == chapterid) {
            nowindex = index;
            nowChobj = item;
        }
    })

    var prev_dd = null;
    if (nowindex > 0) {
        prev_dd = allas[nowindex - 1].getAttribute("href");
    }
    var next_dd = null;
    if (nowindex + 1 < allas.length) {
        next_dd = allas[nowindex + 1].getAttribute("href");
    }
    //判断上下显示
    if (prev_dd) {
        $("#prve_page").click(function () {
            window.location.href = prev_dd;
        }).show();
        $("#content_last_page").click(function () {
            window.location.href = prev_dd;
        })
    }
    if (next_dd) {
        $("#next_page").click(function () {
            window.location.href = next_dd;
        }).show();
        $("#content_next_page").click(function () {
            window.location.href = next_dd;
        })
    }
    //判断定位
    try {
        var code = nowChobj.getAttribute("code");
        if (code && code != "") {
            document.documentElement.scrollTop = $("#" + code).offset().top - 100
        }
    }
    catch (e) {
        console.log(e)
    }
    //获取书签
    get_book_mark();
    //添加书签
    $("#addBookMark").click(function () {
        var markIds = this.getAttribute("markIds");
        var mark_action = this;
        if (!ajax)
            return false;
        ajax = false;
        if (this.className.indexOf("mark_at") != -1) {
            $.jsonAjax("/v2/api/member/deleteMarks", {
                member_token: _token,
                marks: markIds
            }, function (res) {
                ajax = true;
                document.getElementById("addBookMark").className = "remark_bar";
                alert("已删除书签")
                get_book_mark();
            })
        }
        else {
            var content = $.trim($(".book_txt p").text());
            if (content.length < 10) {
                content = $(".book_menu_plan a[chapterid='746294']").text().replace("※", "");
            }
            else {
                content = content.substr(0, 100);
            }
            content.replace(/\n/g, "");
            $.jsonAjax("/v2/api/member/addBookMark", {
                member_token: _token,
                chapter_id: chapterid,
                chapter_name: $('a[chapterid="'+ chapterid +'"]').text().replace('※',''),
                content: content,
                book_id: book_id
            }, function (res) {
                ajax = true;
                alert("添加书签成功")
                document.getElementById("addBookMark").className = "remark_bar mark_at";
                get_book_mark();
            })
        }
    })
    //去评论
    $("#book_view").click(function () {
        window.open(ctxPath + "/web/bookinfo/" + book_id);
    })
    //返回书页
    $("#book_back").click(function () {
        window.location.href = ctxPath + "/web/bookinfo/" + book_id;
    })
    //加入书架
    $("#book_add").click(function () {
        $.jsonAjax("/v2/api/bookShelf/addBook", {
            member_token: _token,
            book_id: book_id,
            org_id: org_id
        }, function (res) {
            if (res.code == 600) {
                window.location.href = ctxPath + "/web?key=login";
            }
            else if (res.code == 0) {
                $("#book_del").show();
                $("#book_add").hide();
            }
            alert(res.message);
        })
    })
    //移除书架
    $("#book_del").click(function () {
        $.jsonAjax("/v2/api/bookShelf/delBook", {
            member_token: _token,
            book_id: book_id
        }, function (res) {
            if (res.code == 600) {
                window.location.href = ctxPath + "/web?key=login";
            }
            else if (res.code == 0) {
                $("#book_del").hide();
                $("#book_add").show();
            }
        })
    })


}
/**
 * 修改尺寸
 */
function upSize() {
    var h = document.documentElement.clientHeight ? document.documentElement.clientHeight : document.body.clientHeight;
    $(".book_txt").css("min-height", h - 253);
    $(".skin_bar").click(function () {
        $(".skin_bar").toggleClass("open");
    });
    $(".book_menu_plan,.book_mark_plan").css({
        height: h
    });
    var w = (document.body.clientWidth - 1000) / 2;
    $(".right_bar").css("right", w - 75).fadeIn();
    $(".left_bar").css("left", w - 75).fadeIn();
}
/**
 * 设置皮肤
 * @param skin
 */
function set_skin(skin) {
    $("html")[0].className = skin;
    setSizeSkin(null,skin);
}
/**
 * 获取书签
 */
function get_book_mark() {
    if(_token){
    $.jsonAjax("/v2/api/member/getMarkList", {
        member_token: _token,
        book_id: book_id
    }, function (res) {
        var bookmark_list = $(".bookmark_list").empty();
        $.each(res.data, function (index, item) {
            $('<dl>' +
                '<dt><a href="' + ctxPath + '/web/book/' + book_id + '/' + item.chapter_id + '" target="_blank">' + item.content + '</a></dt>' +
                '<dd>' + item.create_time + '<a href="javascript:void(0)" markid="' + item.mark_id + '" chapterid="' + item.chapter_id + '">删除</a></dd>' +
                '</dl>').appendTo(bookmark_list);
            if (item.chapter_id == chapterid) {
                document.getElementById("addBookMark").className = "remark_bar mark_at";
                document.getElementById("addBookMark").setAttribute("markIds", item.mark_id);
            }
        });
        $(".bookmark_list dd a").click(function () {
            var markid = this.getAttribute("markid");
            var cid = this.getAttribute("chapterid");
            if (!ajax)
                return false;
            ajax = false;
            if (cid == chapterid) {
                document.getElementById("addBookMark").className = "remark_bar";
            }
            $.jsonAjax("/v2/api/member/deleteMarks", {
                member_token: _token,
                marks: markid
            }, function (res) {
                ajax = true;
                get_book_mark();
            })
            return false;
        });
        if (res.data.length == 0) {
            $('<p class="undata">你还没有添加过书签呢~</p>').appendTo(".bookmark_list");
        }
    })
    }else{
        $('<p class="undata">你还没有添加过书签呢~</p>').appendTo(".bookmark_list");
    }
}
/**
 * 设置字体皮肤到用户表
 */
function setSizeSkin(size,skin){
    console.log(skin)
    console.log(size)
    $.jsonAjax("/site/member/readHabit",{read_size:size,read_skin:skin,member_token: _token,token_type:"pc"})
}



var html = $("textarea").val().replace(/<[html](.+?)>/, '<html>');
$('.book_txt').html(html);
init();
