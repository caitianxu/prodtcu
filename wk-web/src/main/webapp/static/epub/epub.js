$(document).ready(function () {
    var w = $(".book_title").width();
    var font_size = 5;
    var book_info = null;
    var chapterid = null;//当前章节编码
    var chapterid_name = null;
    var chapterid_index = 0;
    $("#loaders").show();
    //放大
    $(".nav_6").on("click", function () {
        if (font_size < 9) {
            font_size += 1;
            document.getElementById("book_html").className = "size_" + font_size;
        }
        $(".action_code").hide();
    })
    //缩小
    $(".nav_7").on("click", function () {
        if (font_size > 1) {
            font_size -= 1;
            document.getElementById("book_html").className = "size_" + font_size;
        }
        $(".action_code").hide();
    })
    //皮肤
    $(".nav_5").on("click", function () {
        $(".nav_2").removeClass("active");
        $(this).toggleClass("active");
        $(".action_code").hide();
    })
    //目录
    $(".nav_2").on("click", function () {
        $(".nav_5").removeClass("active");
        $(this).toggleClass("active");
        $(".action_code").hide();
    })
    //夜间模式
    $(".nav_4").on("click", function () {
        if (document.body.className == "skin_6") {
            document.body.className = $(".skin_list label.at").attr("data-id");
            $("> span", this).html("夜间");
            $(this).removeClass("at");
        }
        else {
            document.body.className = "skin_6";
            $("> span", this).html("日间");
            $(this).addClass("at");
        }
        $(".action_code").hide();
    })
    //切换皮肤
    $(".skin_list label").on("tap", function () {
        $(".skin_list label.at").removeClass("at");
        $(this).addClass("at");
        document.body.className = $(this).attr("data-id");
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


    //禁用点击
    $(".action_menu, .book_menu").on("click", function () {

        return false;
    })
    //隐藏
    $(".action_parent").on("click", function () {
        if (!$(".action_code").is(":hidden")) {
            $(".action_code").fadeOut();
        }
        else if ($(".nav_2").hasClass("active")) {
            $(".nav_2").removeClass("active");
        }
        else if ($(".nav_5").hasClass("active")) {
            $(".nav_5").removeClass("active");
        }
        else if (!$("#read").hasClass("single")) {
            $(".action_parent").hide();
        }
        return false;
    })
    //左侧操作
    $("#book_cover").on("click", function () {
        if ($(".nav_2").hasClass("active")) {
            $(".nav_2").removeClass("active");
        }
        else if ($(".nav_5").hasClass("active")) {
            $(".nav_5").removeClass("active");
        }
        return false;
    })
    //显示
    $("#book_html").on("click", function (e) {
        //双页才执行
        if ($("#read").hasClass("single")) {
            if ($(".nav_2").hasClass("active")) {
                $(".nav_2").removeClass("active");
            }
            else if ($(".nav_5").hasClass("active")) {
                $(".nav_5").removeClass("active");
            }
            return false;
        }
        else if (Math.abs(w / 2 - e.clientX) < 100) {
            $(".action_parent").show();
            return false;
        }
    })
    //单双切换
    $(".nav_9").on("click", function () {
        $(".action_code").hide();
        if ($(this).hasClass("active")) {
            $("#read").removeClass("single")
            $(this).removeClass("active")
            $("> span", this).html("单页")
        }
        else {
            $("#read").addClass("single")
            $(this).addClass("active")
            $("> span", this).html("双页")
        }
        set_gap();
    })
    //上一页
    $(".nav_10").on("click", function () {
        lastPage();
        return false;
    })
    //下一页
    $(".nav_11").on("click", function () {
        nextPage();
        return false;
    })
    //退出
    $(".nav_8").on("click",function(){

    })

    /**
     * 计算分页
     */
    function set_gap() {
        w = $(".book_title").width();
        if ($("#read").hasClass("single")) {
            $(".book_gap").css({
                "columnFill": "auto",
                "width": "auto",
                "columnGap": 100,
                "columnWidth": w,
                "height": '100%'
            });
        }
        else {
            $(".book_gap").css({
                "columnFill": "auto",
                "width": "auto",
                "columnGap": 100,
                "columnWidth": w / 2 - 100,
                "height": '100%'
            });
        }
        showPage(0,0);
        convert_page_num();
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

    set_code();
    set_gap();

    //获取目录
    $.ajax({
        url: "/api/bigscreen/book/chapterlist",
        data: {
            bkid: bkid,
            devcode: devcode
        },
        type: "post",
        success: function (res) {
            if (res.code == 0) {
                book_info = res.data;
                $(".book_detail h1, .book_title span").html(book_info.info.book_name)
                $(".book_detail p").html(book_info.info.book_author)
                $("#book_cover .cover img").attr("src", book_info.info.book_cover_small)
                //目录
                var copers = $("#copers").empty();
                $.each(book_info.chapters, function (index, item) {
                    $("<li did='" + item.id + "' dindex='" + index + "'></li>").html(item.name).appendTo(copers).click(function () {
                        if ($("#loaders").is(":hidden")) {
                            chapterid = $(this).attr("did");
                            $(".nav_2").removeClass("active");
                            _setCss(0, 0);
                            //重置到第一页
                            pageNum = 0;
                            getChaperContent();
                        }
                    });
                });

                //获取第一章
                getChaperContent();
            }
            else {
                showMsg(res.message);
            }
        }
    })

    /**
     * 获取章节内容
     */
    function getChaperContent(end) {
        $("#loaders").show();
        $("#book_html .book_content").css("opacity",0);
        chapterid = chapterid ? chapterid : $("#copers li").first().attr("did");
        $("#copers li").removeClass("active");
        var act = $("#copers li[did='" + chapterid + "']").addClass("active");
        chapterid_name = act.html();
        chapterid_index = parseInt(act.attr("dindex"));
        //标题头
        $(".book_title label").html(chapterid_name);
        $.ajax({
            url: "/api/bigscreen/book/chaptercontent",
            type: "post",
            data: {
                bkid: bkid,
                devcode: devcode,
                chapterid: chapterid
            },
            success: function (res) {
                console.log(res)
                $(".book_gap").html(res.data.content);
                setTimeout(function () {
                    convert_page_num(end);
                }, 500);
            }
        })
    }

    var pageNum = 0;
    var pages = 1;

    /**
     * 计算当前页数
     */
    function convert_page_num(end) {
        var aw = $(".book_gap")[0].scrollWidth - 100;
        pages = parseInt(aw / (w + 100));
        if (aw % w != 0) {
            pages += 1;
        }
        if (end == "end" || pageNum > pages - 1) {
            pageNum = pages - 1;
            showPage();
        }
        $("#loaders").hide();
        $("#book_html .book_content").css("opacity",1);
    }

    /**
     * 上一页
     */
    function lastPage() {
        if (!$("#loaders").is(":hidden")) {
            return false;
        }
        if (pageNum > 0) {
            pageNum -= 1;
            _setCss((w + 100) * pageNum, 300);
        }
        else {
            if (chapterid_index > 0) {
                var act = $("#copers li[dindex='" + (chapterid_index - 1) + "']").addClass("active");
                chapterid = act.attr("did");
                _setCss(0, 0);
                //重置到第一页
                pageNum = 0;
                getChaperContent("end");
            }
            else {
                showMsg("已经是第一页了")
            }
        }
    }

    /**
     * 现实当前页
     */
    function showPage() {
        _setCss((w + 100) * pageNum, 0);
    }

    /**
     * 下一页
     */
    function nextPage() {
        if (!$("#loaders").is(":hidden")) {
            return false;
        }
        if (pageNum < pages - 1) {
            pageNum += 1;
            _setCss((w + 100) * pageNum, 300);
        }
        else {
            if (chapterid_index < $("#copers li").length - 1) {
                var act = $("#copers li[dindex='" + (chapterid_index + 1) + "']").addClass("active");
                chapterid = act.attr("did");
                _setCss(0, 0);
                //重置到第一页
                pageNum = 0;
                getChaperContent();
            }
            else {
                showMsg("已经是最后一页了")
            }
        }
    }

    /**
     * 设置位置
     * @param x
     * @param s
     * @private
     */
    function _setCss(x, s) {
        s = s ? s : 0;
        $(".book_gap").css({
            'transform': 'translate3d(' + -x + 'px, 0px, 0px) scale3d(1, 1, 1)',
            '-webkit-transform': 'translate3d(' + -x + 'px, 0px, 0px) scale3d(1, 1, 1)',
            '-moz-transform': 'translate3d(' + -x + 'px, 0px, 0px) scale3d(1, 1, 1)',
            '-ms-transform': 'translate3d(' + -x + 'px, 0px, 0px) scale3d(1, 1, 1)',
            'transition': 'all ' + s + 'ms',
            '-webkit-transition': 'all ' + s + 'ms',
            '-moz-transition': 'all ' + s + 'ms',
            '-ms-transition': 'all ' + s + 'ms'
        });
    }
})

function  showMsg(t){
    $("#errMessage span").html(t);
    $("#errMessage").show();
    setTimeout(function(){
        $("#errMessage").hide();
    },5000);
}