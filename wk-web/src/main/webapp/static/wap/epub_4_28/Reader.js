document.ontouchmove = function (e) {
    // e.preventDefault();
};
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
$(document).ready(function () {
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
        _bookid = arr.bookId ? arr.bookId : _bookid;
        _token = arr.token ? arr.token : _token;
        if (arr.schedule && arr.schedule != "") {
            p1 = parseInt(arr.schedule.split('|')[0]);
            p2 = parseInt(arr.schedule.split('|')[1]);
        }
        _devcode = arr.devcode ? arr.devcode : _devcode;
        //获取图书信息
        get_chapterlist();
    }

    //禁用
    function loading_start() {
        $("#loaders").show();
        _ajax = false;
    }

    //开启
    function loading_end() {
        $("#loaders").hide();
        _ajax = true;
        $("#copers li").removeClass("active");
        $("#copers li").eq(chapterid_index).addClass("active");
    }

    init_param();
    /**
     * 获取图书信息
     */
    function get_chapterlist(prev) {
        //获取章节信息
        $.ajax({
            url: "/api/bigscreen/book/chapterlist",
            data: {
                bkid: _bookid,
                devcode: _devcode
            },
            type: "post",
            success: function (res) {
                if (res.code == 0) {
                    book_info = res.data;
                    $(".book_title label").html(book_info.info.book_name);
                    //目录
                    var copers = $("#copers").empty();
                    $.each(book_info.chapters, function (index, item) {
                        $("<li did='" + item.id + "' dindex='" + index + "'></li>").html(item.name).appendTo(copers).on("tap", function () {
                            $("#book_info").hide();
                            chapterid_index = parseInt($(this).attr("dindex"));
                            getChaperContent();
                            return false;
                        });
                    });
                    //获取章节内容
                    getChaperContent(prev);
                }
                else {
                    showMsg(res.message);
                }
            }
        })
    }

    /**
     * 显示信息
     */
    function showMsg(t) {
        alert(t)
    }

    /**
     * 加载章节内容
     */
    function getChaperContent(prev) {
        loading_start();
        var zitem = book_info.chapters[chapterid_index];
        if(zitem) {
            chapterid = zitem.id;
            chapterid_name = zitem.name;
            var fixd = (chapterid_index / book_info.chapters.length * 100).toFixed(2)
            $(".action_step .step_line").width(fixd + "%");
            $("#book_step").val(fixd);
        }
        if (!zitem) {
            showMsg("没有找到章节内容")
        }
        else if (zitem.html) {
            bind_book_html(zitem, prev);
        }
        else {
            $.ajax({
                url: "/api/bigscreen/book/chaptercontent",
                data: {
                    bkid: _bookid,
                    devcode: _devcode,
                    chapterid: zitem.id
                },
                type: "post",
                success: function (res) {
                    passChapterContent(res, zitem, prev);
                }
            })
        }
    }

    /**
     * 解析内容
     */
    function passChapterContent(res, zitem, prev) {
        $("#factory .book_html").empty();
        $(res.data.content).each(function (index, item) {
            if (item.nodeName == "LINK" && !link) {
                var style = document.createElement("link");
                style.href = item.href;
                style.rel = "stylesheet";
                style.onload = function () {
                    link = item;
                    split_html_data("#factory .book_html", "root", zitem, prev);
                }
                document.head.appendChild(style);
            }
            else if (item.nodeName != "#comment" && item.nodeName != "TITLE" && item.nodeName != "LINK" && item.nodeName != "H1" && item.nodeName != "H2") {
                $("#factory .book_html").append(item);
            }
        })
        if (link) {
            split_html_data("#factory .book_html", "root", zitem, prev);
        }
    }

    /**
     * 拆分数据
     * @param ctx
     */
    function split_html_data(ctx, root, zitem, prev) {
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
            $("#factory .book_html").children().attr("data-id", zitem.id);
            var img = $("#factory .book_html").find("img");
            if (img.length > 0) {
                img.each(function (img_i, img_item) {
                    if (!$(img_item).parent().parent().hasClass("book_html")) {
                        $(img_item).parent().parent().addClass("un_index");
                    }
                    $(img_item).addClass("unload");
                })
                loadimg(zitem, prev);
            }
            else {
                zitem.html = $("#factory .book_html").html();
                //清空仓库
                $("#factory .book_html").empty();
                //拆分
                bind_book_html(zitem, prev);
            }
        }
    }

    /**
     * 等待图片加载
     * @param parent
     */
    function loadimg(zitem, prev) {
        var max_height = $("#factory .content").height() * 0.8;
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
                loadimg(zitem, prev);
            }, 500)
        }
        else {
            zitem.html = $("#factory .book_html").html();
            //清空仓库
            $("#factory .book_html").empty();
            //拆分
            bind_book_html(zitem, prev);
        }
    }

    /**
     * 装载图书内容
     * @param zitem
     */
    function bind_book_html(zitem, prev) {
        if (zitem.html) {
            $(".book_factory").append(zitem.html);
            console.log("装载完成:" + zitem.id);
            $("#copers li[data-id='" + zitem.id + "']").addClass("loaded");
            //延迟1秒再计算
            setTimeout(function () {
                remave_bind_html(prev);
            }, 500)
        }
        else {
            setTimeout(function () {
                console.log("等待加载完成")
                bind_book_html(zitem, prev);
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
    function remave_bind_html(prev) {
        var parent = $("#factory .book_factory");
        html = parent;
        var pageHeight = 0;
        var par_coord_height = $("#factory .content").height();
        html.children().each(function (index, item) {
            var coord_height = item.clientHeight;
            if (pageHeight + coord_height + 15 < par_coord_height) {
                $(item).addClass("selected")
                pageHeight += coord_height + 15;
                if (index == html.children().length - 1) {
                    //移除
                    add_end_html(prev);
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
                        $(".del", item).remove();
                    }
                    else {
                        //清除最后一个多余的
                        p.children().last().remove();
                        $(".del", item).last().removeClass("del");
                        $(".del", item).remove();
                    }
                }
                $(item).addClass("un_index");
                pageHeight = par_coord_height;
                add_end_html(prev);
            }
        })
    }

    var prependSlideArray = [];

    /**
     * 追加内容
     * @param ctx
     */
    function add_end_html(prev) {
        var phtml = $('<div class="book_html"></div>')
        $('<div class="page_step_title"><label class="book_name">' + book_info.info.book_name + '</label><label class="step_name">' + chapterid_name + '</label></div>').appendTo(phtml);
        $("#factory .book_factory .selected").appendTo(phtml);
        prependSlideArray.push(phtml);
        //检查是否已经完成移除
        if ($("#factory .book_factory").children().length == 0) {
            var content = $("#market .content").empty();
            var page = null;
            for (var ii = 0; ii < prependSlideArray.length; ii++) {
                if (ii % page_size == 0) {
                    page = $('<div class="slide"></div>').appendTo(content);
                    page.css("z-index", prependSlideArray.length - ii);
                }
                $(prependSlideArray[ii]).appendTo(page);
            }
            slides_count = $("#market .slide").length;
            prependSlideArray = [];
            _step_1 = chapterid_index;
            _step_2 = 0;
            //判断是否是倒叙
            if (prev && prev == "prev") {
                _step_2 = slides_count - 1;
                $("#market .slide").each(function (iindex, iitem) {
                    if (iindex < _step_2) {
                        $(iitem).addClass("readed");
                    }
                })
            }
            loading_end();
        }
        else {
            remave_bind_html(prev);
        }
    }

    //内容总宽度
    var content_width = $("#doc").width();
    var start_x = 0;
    var move_left = false;
    var move_right = false;
    var slides_count = 0;
    document.getElementById("doc").addEventListener('touchstart', function (event) {
        var event = event || window.event;
        if (event_time && event) {
            var _touch = event.changedTouches[0];
            start_x = _touch.pageX;
            move_left = false;
            move_right = false;
        }
    }, false);
    document.getElementById("doc").addEventListener('touchmove', function (event) {
        var event = event || window.event;
        if (event_time && event && start_x) {
            var _touch = event.changedTouches[0];
            var w = (_touch.pageX - start_x);
            if (w > 0) {
                if (_step_2 > 0) {
                    $("#market .slide").eq(_step_2 - 1).css({
                        "margin-left": w
                    });
                }
                else {
                    console.log("上一章，不处理")
                }
            }
            else {
                if (_step_2 < slides_count) {
                    $("#market .slide").eq(_step_2).css({
                        "margin-left": w
                    });
                }
                else {
                    console.log("下一章不处理")
                }
            }
        }
    }, false);
    document.getElementById("doc").addEventListener('touchend', function (event) {
        if (event_time && !move_left && !move_right) {
            $("#market .slide").animate({
                "margin-left": 0
            });
        }
        if (move_left) {
            page_next();
        }
        if (move_right) {
            page_prve();
        }
    }, false);
    $("#doc").on("swipeleft", function () {
        move_left = true;
    }).on("swiperight", function () {
        move_right = true;
    })
    /**
     * 上一页
     */
    function page_prve() {
        if (_ajax) {
            if (_step_2 > 0) {
                $("#market .slide").eq(_step_2 - 1).animate({
                    "margin-left": content_width
                }, function () {
                    $("#market .slide").eq(_step_2 - 1).removeClass("readed").css("margin-left", 0);
                    $("#market .readed").css("margin-left", 0);
                    _step_2 -= 1;
                    move_right = false;
                    move_left = false;
                });
            }
            else if (chapterid_index > 0) {
                chapterid_index -= 1;
                getChaperContent("prev");
            }
            else {
                showMsg("已经是第一章了");
            }
        }
        else {
            console.log("频繁")
        }
    }

    /**
     * 下一页
     */
    function page_next() {
        if (_ajax) {
            if (_step_2 < slides_count - 1) {
                $("#market .slide").eq(_step_2).animate({
                    "margin-left": (-content_width)
                }, function (obj) {
                    $("#market .slide").eq(_step_2).addClass("readed").css("margin-left", 0);
                    $("#market .readed").css("margin-left", 0);
                    _step_2 += 1;
                    move_right = false;
                    move_left = false;
                });
            }
            else if (_step_2 >= slides_count) {
                _step_2 = slides_count - 1;
                console.log("err max")
            }
            else {
                $("#market .slide").eq(_step_2).animate({
                    "margin-left": (-content_width)
                }, function (obj) {
                    $("#market .slide").eq(_step_2).addClass("readed").css("margin-left", 0);
                    $("#market .readed").css("margin-left", 0);
                    _step_2 += 1;
                    move_right = false;
                    move_left = false;
                    if (chapterid_index < book_info.chapters.length - 1) {
                        chapterid_index += 1;
                        getChaperContent();
                    }
                    else {
                        $("#market .slide").last().removeClass("readed").css("margin-left", 0);
                        $("#market .readed").css("margin-left", 0);
                        _step_2 = slides_count - 1;
                        showMsg("已经是最后一章了");
                    }
                });
            }
        }
        else {
            console.log("频繁")
        }

    }

    var event_time = true;
    //下一页
    $(".slide-next").on("tap", function () {
        if (event_time && _ajax) {
            event_time = false;
            page_next();
            setTimeout(function () {
                event_time = true;
            }, 500)
        }
    })
    //上一页
    $(".slide-prev").on("tap", function () {
        if (event_time && _ajax) {
            event_time = false;
            page_prve();
            setTimeout(function () {
                event_time = true;
            }, 500)
        }
    })
    //左
    $(".action_last").on("tap", function () {
        if(chapterid_index > 0){
            chapterid_index -= 1;
            getChaperContent();
        }
    })
    //左
    $(".action_next").on("tap", function () {
        if(chapterid_index < book_info.chapters.length - 1){
            chapterid_index += 1;
            getChaperContent();
        }
    })

    //总控
    $(".slide-center").on("tap", function () {
        var par = $("#book_info_parent").toggleClass("unshow");
        if (par.hasClass("unshow")) {
            $(".book_title").animate({
                "top": 0
            }, "fast");
            $("#book_action").animate({
                "bottom":0
            },"fast")
        }
        else {
            $(".book_title").animate({
                "top": -60
            }, "fast");
            $("#book_action").animate({
                "bottom":-200
            },"fast")
        }
    })
    $("#book_info_parent").on("tap", function () {
        $(".book_title").animate({
            "top": -60
        }, "fast", function () {
            $("#book_info_parent").removeClass("unshow");
        });
        $("#book_action").animate({
            "bottom":-200
        },"fast")
    })
    //禁止生效
    $("#book_info,.book_title,#book_info .info_content .ml,#book_action").on("tap", function () {
        return false;
    })
    //菜单
    $(".book_title i").on("tap", function () {
        $("#book_info").toggle();
    })
    //关闭菜单
    $("#book_info").on("tap", function () {
        $("#book_info").hide();
    })
    //退出
    $(".book_title span").on("tap", function () {
        Android.exit();
    })
    //换肤
    $(".action_color > span").tap(function () {
        var skin = $(this).attr("data-skin");
        document.body.className  = skin;
    })
    $("#book_step").change(function () {
        chapterid_index = parseInt(book_info.chapters.length * (this.value / 100));
        chapterid_index = chapterid_index >= book_info.chapters.length ? book_info.chapters.length - 1 : chapterid_index;
        $(".action_step .step_line").width(this.value + "%");
        getChaperContent();
    });
    $('#book_step').on('input propertychange',function(){
        $(".action_step .step_line").width(this.value + "%");
    });
});