var _bookid = "14275"; //图书ID-14275-27800
var _token = "b52ce0d02030be7df63894a85d69c593";//用户token
var _step_1 = 0, _step_2 = 0; //章节、进度
var _devcode = "A0Y9GMKIL8"; //设备号
var book_info = null;
var link = null; //引入样式
var chapterid = null;//读取章节
var chapterid_index = 0;//章节索引
var chapterid_name = null;//当前章节名称
var url = "www.cjzww.com";//二维码地址
var _ajax = true;
var page_size = 1;//显示模式
var prev = null;
var _zitem = null;//当前章节对象
var Android_Version = false;
var swiper = new Swiper('.swiper-container', {
    nextButton: '.swiper-button-next',
    prevButton: '.swiper-button-prev',
    slidesPerView: 1,
    effect: 'fade'
});

//禁用
function loading_start() {
    $("#loaders").show();
    _ajax = false;
}

//开启
function loading_end() {
    $("#loaders").hide();
    _ajax = true;
    prev = null;
    _zitem = null;
    $("#copers li").removeClass("active");
    $("#copers li").eq(chapterid_index).addClass("active");
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
        text: url,
        width: 150,
        height: 150,
        colorDark: "#000000",
        colorLight: "#ffffff",
        correctLevel: QRCode.CorrectLevel.H
    });
    var cover_code = new QRCode(document.getElementById("cover_code"), {
        text: url,
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
        _url = paramter.url;
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
        _bookid = arr.bookId ? arr.bookId : _bookid;
        _token = arr.token ? arr.token : _token;
        if (arr.schedule && arr.schedule != "") {
            p1 = parseInt(arr.schedule.split('|')[0]);
            p2 = parseInt(arr.schedule.split('|')[1]);
        }
        _devcode = arr.devcode ? arr.devcode : _devcode;
    }
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
            $("<li did='" + item.id + "' dindex='" + index + "'></li>").html(item.name).appendTo(copers).click(function () {
                $(".nav_2").removeClass("active");
                chapterid_index = parseInt($(this).attr("dindex"));
                getChaperContent();
                return false;
            });
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
    //获取章节信息
    $.ajax({
        url: "/api/bigscreen/book/chapterlist",
        data: {
            bkid: _bookid.toString(),
            devcode: _devcode.toString()
        },
        type: "post",
        success: function (res) {
            passChapter(res);
        }
    })
}
/**
 * 加载章节内容
 */
function getChaperContent() {
    swiper.removeAllSlides();
    loading_start();
    _zitem = book_info.chapters[chapterid_index];
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
        }
        else {
            $.ajax({
                url: "/api/bigscreen/book/chaptercontent",
                data: {
                    bkid: _bookid.toString(),
                    devcode: _devcode.toString(),
                    chapterid: _zitem.id.toString()
                },
                type: "post",
                success: function (res) {
                    //当前阅读章节
                    _step_1 = _zitem.id;
                    passChapterContent(res);
                }
            })
        }
    }
}
/**
 * 解析内容
 */
function passChapterContent(res) {
    $("#factory .book_html").empty();
    var content = null;
    if(Android_Version){
        content = escape2Html(res);
    }
    else{
        content = res.data.content;
    }
    var styleRoot = null;
    $(content).each(function (index, item) {
        if (item.nodeName == "LINK" && !link) {
            styleRoot = item.href;
            var style = document.createElement("link");
            style.href = item.href;
            style.rel = "stylesheet";
            style.onload = function () {
                link = item;
                split_html_data("#factory .book_html", "root");
            }
            document.head.appendChild(style);
        }
        else if (item.nodeName != "#comment" && item.nodeName != "TITLE" && item.nodeName != "LINK" && item.nodeName != "H1" && item.nodeName != "H2") {
            $("#factory .book_html").append(item);
        }
    })
    if (link || styleRoot == null) {
        split_html_data("#factory .book_html", "root");
    }
}
/**
 * 拆分数据
 * @param ctx
 */
function split_html_data(ctx, root) {
    $(ctx).children().each(function (index, item) {
        var codeArray = [];
        for (var i = 0; i < item.childNodes.length; i++) {
            codeArray.push(item.childNodes[i]);
        }
        for (var i = 0; i < codeArray.length; i++) {
            if (codeArray[i].nodeName == "#text") {
                var htmls = $(codeArray[i]).text().split('');
                var mmm = "";
                $.each(htmls, function (ii, zi) {
                    mmm += ('<span>' + zi + '</span>');
                })
                $(codeArray[i]).replaceWith(mmm);
            }
            else if (codeArray[i].childNodes.length > 0) {
                if ($(codeArray[i]).children().length) {
                    split_html_data(codeArray[i])
                }
                else {
                    var htmls = $(codeArray[i].childNodes[0]).text().split('');
                    var mmm = "";
                    $.each(htmls, function (ii, zi) {
                        mmm += ('<span>' + zi + '</span>');
                    })
                    $(codeArray[i].childNodes[0]).replaceWith(mmm);
                }
            }
            else {
                $(codeArray[i]).replaceWith('<span>' + codeArray[i].outerHTML + '</span>');
            }
        }
    })
    //如果是根节点拆分完成
    if (root) {
        $("#factory .book_html").children().attr("data-id", _zitem.id);
        var img = $("#factory .book_html").find("img");
        if (img.length > 0) {
            img.each(function (img_i, img_item) {
                if (!$(img_item).parent().parent().hasClass("book_html")) {
                    $(img_item).parent().parent().addClass("un_index");
                }
                $(img_item).addClass("unload");
            })
            loadimg();
        }
        else {
            _zitem.html = $("#factory .book_html").html();
            //清空仓库
            $("#factory .book_html").empty();
            //拆分
            bind_book_html();
        }
    }
}

/**
 * 等待图片加载
 * @param parent
 */
function loadimg() {
    var max_height = $("#factory .book_content").height() * 0.8;
    var img = $("#factory .book_html img.unload");
    if (img.length > 0) {
        img.each(function (index, item) {
            if (item.complete && item.width) {
                $(item).removeClass("unload");
                if (item.height > 100) {
                    $(item).css({
                        "max-height": max_height,
                        "display": "block",
                        "margin": "0 auto"
                    })
                }
            }
        });
        setTimeout(function () {
            loadimg();
        }, 500)
    }
    else {
        _zitem.html = $("#factory .book_html").html();
        //清空仓库
        $("#factory .book_html").empty();
        //拆分
        bind_book_html();
    }
}

/**
 * 装载图书内容
 * @param _zitem
 */
function bind_book_html() {
    if (_zitem.html) {
        $(".book_factory").append(_zitem.html);
        console.log("装载完成:" + _zitem.id);
        $("#copers li[data-id='" + _zitem.id + "']").addClass("loaded");
        //延迟1秒再计算
        setTimeout(function () {
            remave_bind_html();
        }, 500)
    }
    else {
        setTimeout(function () {
            console.log("等待加载完成")
            bind_book_html();
        }, 500);
    }
}

/**
 * 循环匹配章节
 */
function eachChapterId(root, cid) {
    var citem = null;
    $.each(root, function (index, item) {
        if (item.id == cid) {
            citem = item;
        }
    })
    return citem;
}
var html = null;

/**
 * 绑定阅读内容
 */
function remave_bind_html() {
    var parent = $("#factory .book_factory");
    html = parent;
    var pageHeight = 0;
    var par_coord_height = $("#factory .book_content").height();
    html.children().each(function (index, item) {
        var coord_height = item.clientHeight;
        if (pageHeight + coord_height + 15 < par_coord_height) {
            $(item).addClass("selected")
            pageHeight += coord_height + 15;
            if (index == html.children().length - 1) {
                //移除
                add_end_html();
            }
        }
        else if (par_coord_height - pageHeight > 0) {
            var cyHeight = par_coord_height - pageHeight - 15;
            //在此之前插入一个P
            var p = $('<p class="selected"></p>');
            p.className = item.className;
            $(item).before(p);
            $(item).children().each(function (mi, mn) {
                if (p.height() <= cyHeight) {
                    $(mn).clone().appendTo(p);
                    $(mn).addClass("del");
                }
            });
            if (p.height() > cyHeight) {
                //判断最后一个是否是 ，。！；“”?
                var h = p.children().last().html();
                //强制换行
                if (h == "，" || h == "。" || h == "!" || h == "?" || h == ";" || h == '”' || h == '“' || h == '）' || h == '（' || h == '【' || h == '】') {
                    //清除最后两个多余的
                    p.children().last().remove();
                    p.children().last().remove();
                    $(".del", item).last().removeClass("del");
                    $(".del", item).last().removeClass("del");
                }
                else {
                    //清除最后一个多余的
                    p.children().last().remove();
                    $(".del", item).last().removeClass("del");
                }
                if($(".del", item).length > 0){
                    $(item).addClass("un_index");
                }
                $(".del", item).remove();
            }
            pageHeight = par_coord_height;
            add_end_html();
        }
    })
}

var prependSlideArray = [];
/**
 * 追加内容
 * @param ctx
 */
function add_end_html() {
    var page = $('<div class="swiper-slide"></div>');
    var phtml = $('<div class="book_html"></div>').appendTo(page);
    $('<div class="page_step_title"><label class="book_name"></label><label class="step_name"></label></div>').appendTo(phtml);
    $("#factory .book_factory .selected").appendTo(phtml);
    prependSlideArray.push(page);

    //检查是否已经完成移除
    if ($("#factory .book_factory").children().length == 0) {
        swiper.removeAllSlides();
        if(page_size == 1) {
            for (var ii = 0; ii < prependSlideArray.length; ii++) {
                $(".page_step_title .book_name", prependSlideArray[ii]).html(_zitem.name);
                $(".page_step_title .step_name", prependSlideArray[ii]).html((ii + 1) + "/" + prependSlideArray.length);
                swiper.appendSlide(prependSlideArray[ii]);
            }
        }
        else{
            var p_slide = null;
            for (var ii = 0; ii < prependSlideArray.length; ii++) {
                if(ii % 2 == 0) {
                    p_slide = $('<div class="swiper-slide"></div>');
                    swiper.appendSlide(p_slide);
                }
                $(".page_step_title .book_name", prependSlideArray[ii]).html(_zitem.name);
                $(".page_step_title .step_name", prependSlideArray[ii]).html((ii + 1) + "/" + prependSlideArray.length);
                $(prependSlideArray[ii]).removeClass("swiper-slide").addClass("double_slide");
                p_slide.append(prependSlideArray[ii]);

                if(ii == prependSlideArray.length - 1 && ii % 2 == 0){
                    p_slide.append('<div class="double_slide"><div class="book_html"></div></div>');
                }
            }
        }
        if (prev && prev == 'prev') {
            swiper.slideTo(swiper.slides.length - 1, 0, false);
        }
        else {
            swiper.slideTo(0, 0, false);
        }
        loading_end();
        prependSlideArray = [];
    }
    else {
        remave_bind_html();
    }
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
    _step_2 = swiper.realIndex;
    if (_step_2 == 0) {
        if (chapterid_index > 0) {
            chapterid_index -= 1;
            prev = "prev";
            getChaperContent();
        }
        else {
            showMsg("已经是第一章了");
        }
    }
    else {
        swiper.slidePrev();
    }
}
/**
 * 下一页
 */
function page_next() {
    _step_2 = swiper.realIndex;
    if (_step_2 >= swiper.slides.length - 1 || (_step_2 == 0 && swiper.slides.length <= 1)) {
        if (chapterid_index < book_info.chapters.length - 1) {
            chapterid_index += 1;
            getChaperContent();
        }
        else {
            showMsg("已经是最后一章了");
        }
    }
    else {
        swiper.slideNext();
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