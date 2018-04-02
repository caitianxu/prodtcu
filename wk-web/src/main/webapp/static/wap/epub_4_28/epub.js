var w = $(".book_content").width();
var chapterid = p1 ? p1 : null;//当前章节编码
var chapterid_name = null;
var chapterid_index = 0;
var book_info = null;

$(document).ready(function () {
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
                $(".title_book_name").html("《" + res.data.info.book_name + "》");
                //目录
                var copers = $("#copers").empty();
                $.each(book_info.chapters, function (index, item) {
                    $("<li did='" + item.id + "' dindex='" + index + "'></li>").html(item.name).appendTo(copers).on("tap", function () {
                        if ($("#loaders").is(":hidden")) {
                            chapterid = $(this).attr("did");
                            _setCss(0, 0);
                            //重置到第一页
                            pageNum = 0;
                            getChaperContent();
                        }
                        $("#book_info").hide();
                        return false;
                    });
                });
                $("#read").css("opacity", 1);
                //获取第一章
                getChaperContent();
            }
            else {
                alert(res.message);
            }
        }
    })
    /**
     * 获取章节内容
     */
    function getChaperContent(end) {
        $("#loaders").show();
        $("#read .book_content").css("opacity", 0);
        chapterid = chapterid ? chapterid : $("#copers li").first().attr("did");
        $("#copers li").removeClass("active");
        var act = $("#copers li[did='" + chapterid + "']").addClass("active");
        chapterid_name = act.html();
        chapterid_index = parseInt(act.attr("dindex"));
        var fixd = (chapterid_index / book_info.chapters.length * 100).toFixed(2)
        $(".action_step .step_line").width(fixd + "%");
        $("#book_step").val(fixd);
        //标题头
        $(".book_title label").html(chapterid_name);
        $(".title_book_step_name").html(chapterid_name);
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
        });
        $.ajax({
            url: "/api/book/shelf/schedule",
            type: "post",
            data: {
                token: token,
                bkid: bkid,
                schedule: chapterid + "|" + 0
            }
        })
    }

    var pageNum = p2 ? p2 : 0;
    var pages = 1;

    /**
     * 计算当前页数
     */
    function convert_page_num(end) {
        var aw = $(".book_gap")[0].scrollWidth;
        pages = parseInt(aw / w);
        if (aw % w != 0) {
            pages += 1;
        }
        if (end == "end" || pageNum > pages - 1) {
            pageNum = pages - 1;
            showPage();
        }
        $("#loaders").hide();
        $("#read .book_content").css("opacity", 1);
    }

    /**
     * 现实当前页
     */
    function showPage() {
        _setCss(w * pageNum, 0);
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

    /**
     * 计算分页
     */
    function set_gap() {
        w = $(".book_content").width();
        $(".book_gap").css({
            "columnFill": "auto",
            "width": "auto",
            "columnGap": 0,
            "columnWidth": w,
            "height": '100%'
        });
        showPage(0, 0);
        //第一次不允许分页
        //convert_page_num();
    }

    /**
     * 计算当前页数
     */
    function convert_page_num(end) {
        var aw = $(".book_gap")[0].scrollWidth;
        pages = parseInt(aw / w);
        if (aw % w != 0) {
            pages += 1;
        }
        if (end == "end" || pageNum > pages - 1) {
            pageNum = pages - 1;
            showPage();
        }
        $("#loaders").hide();
        $("#read .book_content").css("opacity", 1);
    }

    //头
    $("#read").on("tap", function (e) {
        console.log(e.clientX, e.clientY);
        $(".book_title,#book_action").toggle();
        return false;
    })
    //目录
    $(".book_title i").on("tap", function () {
        $("#book_info").toggle();
    })
    //退出
    $(".book_title span").on("tap", function () {
        Android.exit();
    })
    //退出目录
    $("#book_info").on("tap", function () {
        $("#book_info").hide();
        return false;
    })
    /**
     * 上一页
     */
    function lastPage() {
        if (!$("#loaders").is(":hidden")) {
            return false;
        }
        if (pageNum > 0) {
            pageNum -= 1;
            _setCss(w * pageNum, 300);
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
     * 下一页
     */
    function nextPage() {
        if (!$("#loaders").is(":hidden")) {
            return false;
        }
        if (pageNum < pages - 1) {
            pageNum += 1;
            _setCss(w * pageNum, 300);
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

    //左
    $("#read").on("swiperight", function () {
        lastPage();
    })
    //左
    $("#read").on("swipeleft", function () {
        nextPage();
    })
    //左
    $(".action_last").on("click", function () {
        if(chapterid_index){
            chapterid_index -= 1;
            chapterid = $("#copers li").eq(chapterid_index).attr("did");
            getChaperContent();
        }
    })
    //左
    $(".action_next").on("click", function () {
        if(chapterid_index < book_info.chapters.length - 1){
            chapterid_index += 1;
            chapterid = $("#copers li").eq(chapterid_index).attr("did");
            getChaperContent();
        }
    })
    $("#book_step").change(function () {
        chapterid_index = parseInt(book_info.chapters.length * (this.value / 100));
        $(".action_step .step_line").width(this.value + "%");
        chapterid = $("#copers li").eq(chapterid_index).attr("did");
        getChaperContent();
    });
    $('#book_step').on('input propertychange',function(){
        $(".action_step .step_line").width(this.value + "%");
    });

    //换肤
    $(".action_color > span").click(function () {
        var skin = $(this).attr("data-skin");
        document.body.className  = skin;
    })
    set_gap();
})
