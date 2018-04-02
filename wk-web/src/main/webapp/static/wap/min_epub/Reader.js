document.addEventListener('touchmove', function (e) {
    e.preventDefault();
}, false);
var _bookid = "14275"; //图书ID-14275-27800
var _token = "b52ce0d02030be7df63894a85d69c593";//用户token
var _devcode = "A0Y9GMKIL8"; //设备号
var book_info = null;//图书详情
var link = null; //引入样式
var chapterid = null;//读取章节
var chapterid_index = 0;//章节索引
var url = "http://cjszyun.cn/pkgApp";//二维码地址
var _zitem = null;//当前章节
var _swiper = null;
var myScroll = null;
var _ajax = true;
$(document).ready(function () {
    _swiper = new Swiper('.swiper-container', {
        nextButton: '.swiper-button-next',
        prevButton: '.swiper-button-prev',
        slidesPerView: 1,
        freeModeSticky: true,
        onSlideChangeEnd: function (sw) {
            if (sw.activeIndex == 0 && _ajax) {
                //判断是否是第一章
                if (chapterid_index > 0) {
                    chapterid_index -= 1;
                    getChaperContent("prev");
                }
            }
            else if (sw.activeIndex + 1 == _swiper.slides.length && _ajax) {
                //判断是否是最后一章
                if (chapterid_index + 1 < book_info.chapters.length) {
                    chapterid_index += 1;
                    getChaperContent("next");
                }
            }
        },
        onClick: function (sw, event) {
            if (event && event.changedTouches) {
                var pX = event.changedTouches[0].pageX;
                var wX = document.body.clientWidth / 2;
                if (pX > wX - 50 && pX < wX + 50) {
                    var par = $("#book_info_parent").toggleClass("unshow");
                    if (par.hasClass("unshow")) {
                        $(".book_title").animate({
                            "top": 0
                        }, "fast");
                        $("#book_action").animate({
                            "bottom": 0
                        }, "fast")
                    }
                    else {
                        $(".book_title").animate({
                            "top": -60
                        }, "fast");
                        $("#book_action").animate({
                            "bottom": -200
                        }, "fast")
                    }
                }
            }
        }
    });
    /**
     * 初始化参数
     */
    function init_param() {
        loading_start();
        //初始化参数
        var param = window.location.search.replace("?", "");
        param = param.split("&");
        var arr = {};
        for (var i = 0; i < param.length; i++) {
            var obj = param[i].split("=");
            if (obj && obj.length == 2) {
                arr[obj[0]] = obj[1];
            }
        }
        //图书ID
        _bookid = arr.bookId ? arr.bookId : _bookid;
        //会员token
        _token = arr.token ? arr.token : _token;
        //进度
        if (arr.schedule && arr.schedule != "") {
            p1 = parseInt(arr.schedule.split('|')[0]);
            p2 = parseInt(arr.schedule.split('|')[1]);
        }
        //设备
        _devcode = arr.devcode ? arr.devcode : _devcode;
        //获取图书信息
        get_chapterlist();
    }

    //禁用
    function loading_start() {
        $("#loaders").show();
        _swiper.removeAllSlides();
        _swiper.disableTouchControl();
        _ajax = false;
    }

    //开启
    function loading_end() {
        $("#loaders").hide();
        _ajax = true;
        $("#book_info .ml_content ul li.active").removeClass("active");
        $("#book_info .ml_content ul li").eq(chapterid_index).addClass("active");

        var fixd = (chapterid_index / book_info.chapters.length * 100).toFixed(2)
        $(".action_step .step_line").width(fixd + "%");
        _swiper.enableTouchControl();
    }

    /**
     * 获取图书信息
     */
    function get_chapterlist() {
        //获取章节信息
        $.ajax({
            url: "/api/bigscreen/book/chapterlist",
            data: {
                bkid: _bookid,
                devcode: _devcode,
                token: _token
            },
            type: "post",
            success: function (res) {
                _ajax = true;
                if (res.code == 0) {
                    book_info = res.data;
                    $(".book_title label").html(book_info.info.book_name);
                    document.title = book_info.info.book_name;
                    //目录
                    var copers = $("#copers").empty();
                    $.each(book_info.chapters, function (index, item) {
                        $("<li did='" + item.id + "' dindex='" + index + "'></li>").html(item.name).appendTo(copers).on("tap", function () {
                            $("#book_info").hide();
                            $(".book_title").animate({
                                "top": -60
                            }, "fast", function () {
                                $("#book_info_parent").removeClass("unshow");
                            });
                            $("#book_action").animate({
                                "bottom": -200
                            }, "fast")

                            chapterid_index = parseInt($(this).attr("dindex"));
                            getChaperContent("next");
                            return false;
                        });
                    });
                    //获取章节内容
                    getChaperContent("first");
                }
                else {
                    showMsg(res.message);
                }
            }
        })
    }

    /**
     * 加载章节内容
     */
    function getChaperContent(prev) {
        if (_ajax) {
            loading_start();
            _zitem = book_info.chapters[chapterid_index];
            if (!_zitem) {
                showMsg("没有找到章节内容")
            }
            else if (_zitem.htmlArray) {
                bind_book_html(_zitem, prev);
            }
            else {
                $.ajax({
                    url: "/api/bigscreen/book/chaptercontent",
                    data: {
                        bkid: _bookid.toString(),
                        devcode: _devcode.toString(),
                        chapterid: _zitem.id.toString(),
                        token: _token.toString()
                    },
                    type: "post",
                    success: function (res) {
                        passChapterContent(res, prev);
                    }
                })
            }
        }
    }


    /**
     * 解析内容
     */
    function passChapterContent(res, prev) {
        var book_html = $("#factory .book_html").empty();
        $(res.data.content).each(function (index, item) {
            if (item.nodeName == "#text") {
                if ($(item).text().trim().length) {
                    var p = $('<p></p>').appendTo(book_html);
                    p.append(item);
                }
            }
            else if (item.nodeName == "LINK" && !link) {
                var style = document.createElement("link");
                style.href = item.href;
                style.rel = "stylesheet";
                style.onload = function () {
                    link = item;
                    //检查图片下载
                    var img = $("#factory .book_html").find("img");
                    if (img.length > 0) {
                        img.each(function (img_i, img_item) {
                            if (!$(img_item).parent().parent().hasClass("book_html")) {
                                $(img_item).parent().parent().addClass("un_index");
                            }
                            $(img_item).addClass("unload");
                        })
                        loadimg(prev);
                    }
                    else {
                        split_html_data(prev);
                    }
                }
                document.head.appendChild(style);
            }
            else if (item.nodeName != "#comment" && item.nodeName != "TITLE" && item.nodeName != "H1" && item.nodeName != "H2" && item.nodeName != "LINK") {
                book_html.append(item);
            }
        });
        $("#factory .book_html a").attr("href","javascript:void(0)");
        if (link) {
            //检查图片下载
            var img = $("#factory .book_html").find("img");
            if (img.length > 0) {
                img.each(function (img_i, img_item) {
                    if (!$(img_item).parent().parent().hasClass("book_html")) {
                        $(img_item).parent().parent().addClass("un_index");
                    }
                    $(img_item).addClass("unload");
                })
                loadimg(prev);
            }
            else {
                split_html_data(prev);
            }
        }
    }

    /**
     * 等待图片加载
     * @param parent
     */
    function loadimg(prev) {
        var max_height = $("#factory .content").height() * 0.8;
        $("#factory .book_html img.unload").each(function (index, item) {
            if (item.complete && item.width) {
                $(item).removeClass("unload");
                if (item.height > 100) {
                    $(item).css({
                        "max-height": max_height,
                        "max-width": "100%",
                        "display": "block",
                        "margin": "0 auto"
                    })
                }
                else if (item.width > 100) {
                    $(item).css({
                        "max-width": "100%"
                    })
                }
            }
        });
        if ($("#factory .book_html img.unload").length > 0) {
            setTimeout(function () {
                loadimg(prev);
            }, 500)
        }
        else {
            split_html_data(prev);
        }
    }

    //内容拆分
    function split_html_span(item) {
        var codeArray = [];
        for (var i = 0; i < item.childNodes.length; i++) {
            codeArray.push(item.childNodes[i]);
        }
        for (var i = 0; i < codeArray.length; i++) {
            if (codeArray[i].nodeName == "#text") {
                var htmls = $(codeArray[i]).text().split('');
                var mmm = "";
                $.each(htmls, function (ii, zi) {
                    mmm += ('<span class="split_span_text">' + zi + '</span>');
                })
                $(codeArray[i]).replaceWith(mmm);
            }
            else if (codeArray[i].childNodes.length > 0) {
                for (var j = 0; j < codeArray[i].childNodes.length; j++) {
                    if(codeArray[i].childNodes[j].nodeName == "#text"){
                        var htmls_x = $(codeArray[i].childNodes[j]).text().split('');
                        var mmm_x = "";
                        $.each(htmls_x, function (ii, zi) {
                            mmm_x += ('<span class="split_span_text">' + zi + '</span>');
                        })
                        $(codeArray[i].childNodes[j]).replaceWith(mmm_x);
                    }
                    else if(codeArray[i].childNodes[j].nodeName == "IMG" && codeArray[i].childNodes[j].clientHeight > 30){
                        $(codeArray[i].childNodes[j]).replaceWith('<span class="split_span_img">' + codeArray[i].childNodes[j].outerHTML + '</span>');
                    }
                    else {
                        $(codeArray[i].childNodes[j]).replaceWith('<span class="split_span_text">' + codeArray[i].childNodes[j].outerHTML + '</span>');
                    }
                }
            }
            else {
                if (codeArray[i].nodeName == "IMG" && codeArray[i].clientHeight > 30) {
                    $(codeArray[i]).replaceWith('<span class="split_span_img">' + codeArray[i].outerHTML + '</span>');
                }
                else {
                    $(codeArray[i]).replaceWith('<span class="split_span_text">' + codeArray[i].outerHTML + '</span>');
                }
            }
        }
    }

    /**
     * 子项内容处理
     * @param pitem
     * @param root
     */
    function split_html_data_children(prev) {
        var page_height = document.body.clientHeight ? document.body.clientHeight : document.documentElement.clientHeight;
        var split_del = $("#factory .book_html .split_del");
        split_del.children().each(function (index, item) {
            if (item.offsetTop + $(item).height() + 10 < page_height) {
                $(item).addClass("delete_row");
            }
            else {
                return false;
            }
        });
        var pp = $('<p class="new_row delete_row"></p>');
        var dels = $("#factory .book_html .split_del .delete_row");
        if (dels.length > 0) {
            dels.appendTo(pp);
            split_del.before(pp);
            $(".delete_row", pp).removeClass("delete_row");
            //是否已经被移除过一次
            if (split_del.hasClass("un_index")) {
                pp.addClass("un_index");
            }
            else {
                split_del.addClass("un_index");
            }
        }
        else {
            pp.addClass("split_new_row");
        }
        //判断是否还有子项
        if (split_del.children().length == 0) {
            split_del.remove();
        }
        set_book_html(prev);
    }

    /**
     * 拆分数据
     * @param ctx
     */
    function split_html_data(prev) {
        var page_height = document.body.clientHeight ? document.body.clientHeight : document.documentElement.clientHeight;
        var un_index = false;
        $("#factory .book_html").children().each(function (index, item) {
            if (item.offsetTop + item.clientHeight + 10 < page_height) {
                $(item).addClass("delete_row").removeClass("split_del");
            }
            else if (page_height - item.offsetTop >= 34) {
                if (!$(item).hasClass("split_del")) {
                    split_html_span(item);
                    $(item).addClass("split_del");
                }
                un_index = true;
                return false;
            }
            else {
                return false;
            }
        });
        if (un_index) {
            split_html_data_children(prev);
        }
        else {
            set_book_html(prev);
        }
    }


    /**
     * 设置图书内容
     */
    function set_book_html(prev) {
        if (!_zitem.htmlArray) {
            _zitem.htmlArray = [];
        }
        var html = $('<div class="book_html"></div>');
        $("#factory .book_html .delete_row").appendTo(html);
        _zitem.htmlArray.push(html);
        if ($("#factory .book_html").children().length > 0) {
            split_html_data(prev);
        }
        else {
            bind_book_html(_zitem, prev);
        }
    }

    /**
     * 绑定阅读内容
     */
    function bind_book_html(mitem, prev) {
        if (prev == "prev") {
            for (var ii = mitem.htmlArray.length - 1; ii >= 0; ii--) {
                var item = mitem.htmlArray[ii];
                var page = $('<div class="swiper-slide"></div>');
                var slide = $('<div class="slide"></div>').appendTo(page);
                $('<div class="page_step_title"><label class="book_name">' + _zitem.name + '</label><label class="step_name">' + (ii + 1) + "/" + mitem.htmlArray.length + '</label></div>').appendTo(slide);
                var clone = item.clone();
                slide.append(clone);
                _swiper.prependSlide(page);
            }
        }
        else {
            for (var ii = 0; ii < mitem.htmlArray.length; ii++) {
                var item = mitem.htmlArray[ii];
                var page = $('<div class="swiper-slide"></div>');
                var slide = $('<div class="slide"></div>').appendTo(page);
                $('<div class="page_step_title"><label class="book_name">' + _zitem.name + '</label><label class="step_name">' + ((ii + 1) + "/" + mitem.htmlArray.length) + '</label></div>').appendTo(slide);
                var clone = item.clone();
                slide.append(clone);
                _swiper.appendSlide(page);
            }
        }

        //加一个上空白页和一个下空白页
        if (chapterid_index > 0) {
            _swiper.prependSlide('<div class="swiper-slide"><div class="slide"><span class="prev_page_txt"></span></div></div>');
        }
        if (chapterid_index + 1 < book_info.chapters.length) {
            _swiper.appendSlide('<div class="swiper-slide"><div class="slide"><span class="next_page_txt"></span></div></div>');
        }
        loading_end();
    }

    //父级点击
    $("#book_info_parent").on("tap", function () {
        $(".book_title").animate({
            "top": -60
        }, "fast", function () {
            $("#book_info_parent").removeClass("unshow");
        });
        $("#book_action").animate({
            "bottom": -200
        }, "fast")
    })
    //菜单
    $(".book_title i").on("tap", function () {
        $("#book_info").toggle();
        myScroll = new IScroll('#wrapper', {mouseWheel: true});
        var off = $("#book_info .ml_content ul li.active").offset();
        myScroll.scrollTo(0, -(off.top - 60), 0);
        return false;
    })
    //关闭菜单
    $("#book_info").on("tap", function () {
        $("#book_info").hide();
        return false;
    })
    //退出
    $(".book_title span").on("tap", function () {
        Android.exit();
        return false;
    })
    //换肤
    $(".action_color > span").tap(function () {
        var skin = $(this).attr("data-skin");
        document.body.className = skin;
        return false;
    })
    //左
    $(".action_last").on("tap", function () {
        if (chapterid_index > 0) {
            chapterid_index -= 1;
            getChaperContent();
        }
        return false;
    })
    //左
    $(".action_next").on("tap", function () {
        if (chapterid_index < book_info.chapters.length - 1) {
            chapterid_index += 1;
            getChaperContent();
        }
        return false;
    })

    //初始化执行
    init_param();
});

