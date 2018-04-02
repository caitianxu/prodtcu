var _bookid = "14275"; //图书ID-14275-27800
var _uid = 0;//用户token
var _org_id = 1;
var _step_1 = 0, _step_2 = 0; //章节、进度
var _devcode = "A0Y9GMKIL8"; //设备号
var book_info = null;
var link = null; //引入样式
var chapterid = null;//读取章节
var chapterid_index = 0;//章节索引
var _url = null;//二维码地址
var _ajax = true;
var page_size = 1;//显示模式
var prev = null;
var _zitem = null;//当前章节对象
var Android_Version = false;
var _allChcount = 0; //总章数

//禁用
function loading_start() {
    $("#loaders").show();
    _ajax = false;
}

//开启
function loading_end() {
    //判断内容定位
    if (_zitem.code && _zitem.code != "") {
        var top = $("#" + _zitem.code).offset().top;
        document.getElementById("book_html_content").scrollTop = top;
    }
    $("#loaders").hide();
    _ajax = true;
    prev = null;
    _zitem = null;
    $("#copers li").removeClass("active");
    $("#copers li[did='" + chapterid + "']").addClass("active");
}

/**
 * 消息提示
 * @param t
 */
function showMsg(t) {
    $("#errMessage span").html(t);
    $("#errMessage").show();
    setTimeout(function () {
        $("#errMessage").hide();
    }, 5000);
}
/**
 * 计算二维码
 */
function set_code() {
    $("#previewQrcode,#cover_code").empty();
    var qrcode = new QRCode(document.getElementById("previewQrcode"), {
        text: _url,
        width: 150,
        height: 150,
        colorDark: "#000000",
        colorLight: "#ffffff",
        correctLevel: QRCode.CorrectLevel.H
    });
    var cover_code = new QRCode(document.getElementById("cover_code"), {
        text: _url,
        width: 150,
        height: 150,
        colorDark: "#000000",
        colorLight: "#ffffff",
        correctLevel: QRCode.CorrectLevel.H
    });
}

/**
 * 初始化参数
 */
function pageReady(paramter) {
    loading_start();
    //初始化参数
    if (paramter) {
        paramter = eval("(" + paramter + ")");
        _bookid = paramter.bkid;
        _devcode = paramter.devcode;
        _uid = paramter.uid;
        _org_id = paramter.org_id;
    }
    else {
        paramter = window.location.search.replace("?", "");
        paramter = paramter.split("&");
        var arr = {};
        for (var i = 0; i < paramter.length; i++) {
            var obj = paramter[i].split("=");
            if (obj && obj.length == 2) {
                arr[obj[0]] = obj[1];
            }
        }
        _bookid = arr.bkid ? arr.bkid : _bookid;
        _bookid = arr.devcode ? arr.devcode : _bookid;
        _uid = arr.uid ? arr.uid : _uid;
        _org_id = arr.org_id ? arr.uid : org_id;
        if (arr.schedule && arr.schedule != "") {
            p1 = parseInt(arr.schedule.split('|')[0]);
            p2 = parseInt(arr.schedule.split('|')[1]);
        }
        _devcode = arr.devcode ? arr.devcode : _devcode;
    }
    _url = "http://" + window.location.host + "/api/book/info?o=" + _org_id + "&b=" + _bookid



        + "&d=" + _devcode; //测试设备;
    //二维码
    set_code();
    //获取图书信息
    if (Android_Version) {
        Android.getChapterList(_bookid, _devcode);
    }
    else {
        get_chapterlist();
    }
}


/**
 * 获取图书信息
 */
function passChapter(res) {
    if (Android_Version) {
        res = eval("(" + res + ")");
    }
    if (res.code == 0) {
        book_info = res.data;
        $(".book_detail h1, .book_title span").html(book_info.info.book_name)
        $(".book_detail p").html(book_info.info.book_author)
        $("#book_cover .book_cover img").attr("src", book_info.info.book_cover_small)
        //目录
        var copers = $("#copers").empty();
        $.each(book_info.chapters, function (index, item) {
            $("<li did='" + item.id + "' ></li>").html(item.name).appendTo(copers).click(function () {
                $(".nav_2").removeClass("active");
                chapterid = $(this).attr("did")
                getChaperContent();
                return false;
            });
            if (item.child && item.child.length > 0) {
                var ul = $('<ul class="child_ul"></ul>').appendTo(copers);
                $.each(item.child, function (mindex, mitem) {
                    $("<li did='" + mitem.id + "'></li>").html(mitem.name).appendTo(ul).click(function () {
                        $(".nav_2").removeClass("active");
                        chapterid = $(this).attr("did")
                        getChaperContent();
                        return false;
                    });
                })
            }
        });
        //获取章节内容
        getChaperContent();
    }
    else {
        showMsg(res.message);
    }
}
/**
 * 获取图书信息
 */
function get_chapterlist() {
    $.ajax({
        url: "/v2/api/book/chapterTree",
        data: {
            bkid: _bookid.toString(),
            member_id: _uid.toString(),
            token_type: 'max'
        },
        type: "post",
        success: function (res) {
            passChapter(res);
        }
    })
}

function getChbyId() {
    var zitem = null;
    chapterid = parseInt(chapterid);
    if (chapterid) {
        $.each(book_info.chapters, function (index, item) {
            if (item.id == chapterid) {
                zitem = item;
            }
            else if (item.child && item.child.length > 0) {
                $.each(item.child, function (mindex, mitem) {
                    if (mitem.id == chapterid) {
                        zitem = mitem;
                    }
                })
            }
        })
    }
    else {
        zitem = book_info.chapters[0];
        chapterid = zitem.id;
    }
    //判断索引
    var lis = $("#copers li");
    _allChcount = lis.length;
    lis.each(function (index, item) {
        if (item.getAttribute("did") == chapterid.toString()) {
            chapterid_index = index;
        }
    })
    return zitem;
}
/**
 * 加载章节内容
 */
function getChaperContent() {
    loading_start();
    $("#book_html_content").empty();
    _zitem = getChbyId();
    if (!_zitem) {
        showMsg("没有找到章节内容")
        Android.exit();
    }
    else if (_zitem.html) {
        bind_book_html();
    }
    else {
        if (Android_Version) {
            Android.getChapterContent(_bookid.toString(), _devcode.toString(), _zitem.id.toString(), prev);
            Android.getNetStatus();
        }
        else {
            $.ajax({
                url: "/v2/api/book/getChapterContent",
                data: {
                    bkid: _bookid.toString(),
                    chapterid: _zitem.id.toString(),
                    token_type: 'max'
                },
                type: "post",
                success: function (res) {
                    //当前阅读章节
                    _step_1 = _zitem.id;
                    passChapterContent(res);
                    appendRead();
                }
            })
        }
    }
}
function appendRead() {
    // $.ajax({
    //     url: "/v2/api/bookShelf/updateMemberReadRecord",
    //     data: {
    //         book_id: _bookid,
    //         member_id: _uid,
    //         last_chapter_id: _zitem.id,
    //         org_id: _org_id,
    //         member_token: null,
    //         schedule: '0|0',
    //         token_type: 'max'
    //     },
    //     type: "post"
    // })
}
/**
 * 解析内容
 */
function passChapterContent(res) {
    $("#book_html_content").empty();
    var content = null;
    if (Android_Version) {
        content = escape2Html(res);
    }
    else {
        content = res.data.content;
    }
    $("#book_html_content").append(content);
    //link判断
    if (!link) {
        var css = $("#book_html_content link");
        if (css.length > 0) {
            link = $("head").append(css);
        }
        else {
            link = null;
        }
    }
    loading_end();
}
if (Android_Version) {
    Android.getParams();
}
else {
    pageReady();
}


var event_time = true;
//下一页
$(".nav_11,.slide-next").click(function () {
    if (event_time && $("#loaders").is(":hidden")) {
        event_time = false;
        page_next();
        setTimeout(function () {
            event_time = true;
        }, 300)
    }
})
//上一页
$(".nav_10,.slide-prev").click(function () {
    if (event_time && $("#loaders").is(":hidden")) {
        event_time = false;
        page_prev();
        setTimeout(function () {
            event_time = true;
        }, 300)
    }
})
/**
 * 上一页
 */
function page_prev() {
    if (chapterid_index > 0) {
        chapterid_index -= 1;
        prev = "prev";
        chapterid = $("#copers li").eq(chapterid_index).attr("did")
        getChaperContent();
    }
    else {
        showMsg("已经是第一章了");
    }
}
/**
 * 下一页
 */
function page_next() {
    if (chapterid_index < _allChcount - 1) {
        chapterid_index += 1;
        chapterid = $("#copers li").eq(chapterid_index).attr("did")
        getChaperContent();
    }
    else {
        showMsg("已经是最后一章了");
    }
}

//单双切换
$(".nav_9").on("click", function () {
    $(".action_code").hide();
    if ($(this).hasClass("active")) {
        $("body").removeClass("single").addClass("double")
        $(this).removeClass("active")
        $("> span", this).html("单页")
        page_size = 2;
    }
    else {
        $("body").removeClass("double").addClass("single")
        $(this).addClass("active")
        $("> span", this).html("双页")
        $(".action_parent").show();
        page_size = 1;
    }
    getChaperContent();
})

function escape2Html(str) {
    var arrEntities = {'lt': '<', 'gt': '>', 'nbsp': ' ', 'amp': '&', 'quot': '"'};
    return str.replace(/&(lt|gt|nbsp|amp|quot);/ig, function (all, t) {
        return arrEntities[t];
    });
}
//目录
$(".nav_2").on("click", function () {
    $(".nav_5").removeClass("active");
    $(this).toggleClass("active");
    $(".action_code").hide();
})
//皮肤
$(".nav_5").on("click", function () {
    $(".nav_2").removeClass("active");
    $(this).toggleClass("active");
    $(".action_code").hide();
})
//总选项
$(".slide-center, .action_parent").click(function () {
    if ($("body").hasClass("double")) {
        $(".action_parent").toggle();
    }
})
//禁用点击
$(".action_menu, .action_code").on("click", function () {
    return false;
})
//夜间模式
$(".nav_4").on("click", function () {
    var ss = $("body").hasClass("single") ? "single" : "double";
    if ($("body").hasClass("skin_6")) {
        document.body.className = ss + " " + $(".skin_list label.at").attr("data-id");
        $("> span", this).html("夜间");
        $(this).removeClass("at");
    }
    else {
        document.body.className = ss + " skin_6";
        $("> span", this).html("日间");
        $(this).addClass("at");
    }
    $(".action_code").hide();
})
//切换皮肤
$(".skin_list label").on("tap", function () {
    $(".skin_list label.at").removeClass("at");
    $(this).addClass("at");
    var ss = $("body").hasClass("single") ? "single" : "double";
    document.body.className = ss + " " + $(this).attr("data-id");
    //退出夜间模式
    $(".nav_4 > span").html("夜间");
    $(".nav_4").removeClass("at");

    $(".action_code").hide();
    return false;
})
//显示二维码
$(".nav_1").on("click", function () {
    $(".action_code").fadeToggle();
})
//退出
$(".nav_8").on("click", function () {
    Android.exit();
})