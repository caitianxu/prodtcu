var param = {
    bookId: book_id,
    bookName: book_name,
    mark: null,
    isMark: null,
    dateTime: '00:00',
    link: null,
    skin: 'skin-1',
    isDaylight: 0, //是否夜间
    liangdu: 50, //亮度
    fontSize: 18, //字体大小
    chaoter: null, //章节
    chaoter_id: 0, //章节ID
    chaoter_index: 0, //章节索引
    chaoter_num: 0, //总章节数量
    percentum: 0, //百分比
    page_num: 0, //当前页
    pages: 0 //本章总页
}
var book = null;
$(document).ready(function () {
    //禁用菜单事件冒泡
    $('.menuContent').tap(function () {
        return false;
    })
    //关闭菜单
    $('#leftBookMenu').tap(function () {
        $("#leftBookMenu").removeClass('show');
        return false;
    })
    //下一页
    $('#book .book-right').tap(function () {
        toNextPage();
        return false;
    })
    //上一页
    $('#book .book-left').tap(function () {
        toLastPage();
        return false;
    })
    $('#book').on('swipeleft',function () {
        toNextPage();
        return false;
    })
    $('#book').on('swiperight',function () {
        toLastPage();
        return false;
    })
    //显示菜单
    $('#book .book-center').tap(function () {
        $('#leftBookMenu').addClass('show');
        return false;
    })

    function getNowTime() {
        var date = new Date();
        var h = date.getHours();
        if (h < 10) {
            h = '0' + h;
        }
        var mm = date.getMinutes();
        if (mm < 10) {
            mm = '0' + mm;
        }
        $('#chTime').html(h + ':' + mm);
        setTimeout(getNowTime, 60000);
    }

    getNowTime();

    function init() {
        $.ajax({
            url: ctxPath + "/v2/api/book/chapterTree",
            type: 'post',
            data: {
                bkid: book_id,
                member_id: member_id,
                token_type: 'weixin'
            },
            success: function (res) {
                book = res.data;

                //循环目录
                $.each(book.chapters, function (index, item) {
                    var mitem = $('<div class="m-item"></div>').appendTo(".menu-list");
                    var uuc = $('<ul><li class="item" did="' + item.id + '">' + item.name + '</li></ul>').appendTo(mitem);
                    $('li', uuc).tap(function () {
                        getChaoterContent(item);
                        return false;
                    })
                    if (item.child) {
                        var ul = $('<ul></ul>').appendTo(mitem);
                        $.each(item.child, function (index1, item1) {
                            var ll = $('<li class="item item-child" did="' + item1.id + '">' + item1.name + '</li>').appendTo(ul);
                            ll.tap(function () {
                                getChaoterContent(item1);
                                return false;
                            })
                        })
                    }
                })

                var perText = [0, 0];
                //判断是否有阅读历史  章节ID|整书百分比
                if (book.info.schedule && book.info.schedule != '') {
                    perText = book.info.schedule.split('|');
                }
                if (param.chaoter_id) {
                    perText = [param.chaoter_id, 0];
                }
                var cha = getChaoterById(perText[0]);
                getChaoterContent(cha);
            }
        })
    }

    //通过章节ID获取章节信息
    function getChaoterById(did) {
        var citem = null;
        if (did && did != '0') {
            $.each(book.chapters, function (index, item) {
                if (citem == null) {
                    if (item.id == did) {
                        citem = item;
                    }
                    else {
                        $.each(item.child, function (index_1, item_1) {
                            if (citem == null) {
                                if (item_1.id == did) {
                                    citem = item_1;
                                }
                            }
                        })
                    }
                }
            })
        }
        else {
            citem = book.chapters[0];
        }
        return citem;
    }

    //获取章节内容
    function getChaoterContent(chaoter, isEnd) {
        $('#chName').html(chaoter.name);
        $('.book_cover').hide();
        //关闭左菜单
        $("#leftBookMenu").removeClass('show');
        //判断是否存在章节
        if (!chaoter) {
            alert('没有找到章节内容!');
            return false;
        }
        //设置分页样式
        if (param.link == null) {
            var www = document.body.clientWidth - 30;
            $("#readBookHtml").css({
                'columnFill': 'auto',
                'width': 'auto',
                'columnGap': 15,
                'columnWidth': www,
                'height': '100%'
            });
            param.link = 1;
        }

        //判断是否跟当前章节是否是同一个文件
        if (param.chaoter && param.chaoter.id == chaoter.id) {
            return false;
        }
        console.log('///////////读取文件//////////////')
        console.log(chaoter)
        console.log('///////////读取文件//////////////')
        //继续判断是否相同文件
        if (param.chaoter && param.chaoter.url == chaoter.url) {
            //相同文件直做定位
            param.chaoter = chaoter;
            //设置定位
            setChaoterParam(isEnd);
        }
        else {
            $('#book').css('opacity', 0);
            param.chaoter = chaoter;
            ajaxHTML(isEnd);
        }
    }

    //获取章节内容
    function ajaxHTML(isEnd) {
        $('#loading_MM').show();
        $.ajax({
            url: ctxPath + "/v2/api/book/getChapterContent",
            type: 'post',
            data: {
                bkid: param.bookId,
                chapterid: param.chaoter.id,
                token_type: 'weixin'
            },
            success: function (res) {
                if (res.code == 0) {
                    $('#readBookHtml').html(res.data.content);
                    //删除本章节样式
                    $('#readBookHtml').find('link').remove();

                    //内容Img加载计算
                    loadImglist(isEnd);
                }
                else {
                    $('#loading_MM').hide();
                    alert(res.message)
                }
            }
        });
    }

    //图片加载计算
    function loadImglist(isEnd, num) {
        var bool = 0;
        if (!num)
            num = 1;
        $('#readBookHtml img').each(function (index, item) {
            if (!item.complete) {
                bool += 1;
            }
            else {
                if (item.parentNode.childNodes.length == 1) {
                    $(item.parentNode).addClass('tp-c');
                }
            }
        })
        console.log('循环图片' + bool + '/' + $('#readBookHtml img').length + '/' + num);
        if (bool > 0 && num <= 30) {
            setTimeout(function () {
                loadImglist(isEnd, num + 1)
            }, 500);
        }
        else {
            setChaoterParam(isEnd);
        }
    }

    //设置定位
    function setCss(size, animation) {
        if (animation) {
            $("#readBookHtml").css({
                'transform': 'translate3d(' + size + 'px,0px,0px) scale3d(1,1,1)',
                'transition': 'all 300ms'
            })
        }
        else {
            $("#readBookHtml").css({
                'transform': 'translate3d(' + size + 'px,0px,0px) scale3d(1,1,1)',
                'transition': 'all 0ms'
            })
        }

    }

    //初始化章节内容及参数
    function setChaoterParam(isEnd, setCssow) {
        //计算参数
        param.chaoter_id = param.chaoter.id;
        $('#leftBookMenu li').removeClass('active');
        var lis = $('#leftBookMenu li');
        console.log('lis:' + lis.length)
        lis.each(function (index, item) {
            if (param.chaoter_id == $(item).attr('did')) {
                param.chaoter_index = index;
                $(item).addClass('active');
                var top = $(item).coord().top;
                top = top > 300 ? top - 300 : 0;
                console.log('滚动指定位置')
                //$ionicScrollDelegate.$getByHandle('menuScroll').scrollTo(0, top, 0);
            }
        })
        param.chaoter_num = lis.length;
        param.percentum = (param.chaoter_index / param.chaoter_num * 100).toFixed(2);

        var dom = null;
        if (param.chaoter.code && param.chaoter.code != "") {
            dom = $('#' + param.chaoter.code);
        }

        //计算总页数
        var aw = $("#readBookHtml")[0].scrollWidth;
        var w = document.body.clientWidth - 15;
        var pages = parseInt(aw / w);
        if (aw % w != 0) {
            pages += 1;
        }
        param.pages = pages;
        //是否是本章最后
        if (isEnd) {
            param.page_num = pages - 1;
            setCss(-param.page_num * w);
            document.getElementById("footer_pages").innerHTML = (param.page_num + 1) + '/' + param.pages;
        }
        //是否是内容重新定位
        else if (dom && dom.length > 0) {
            var coord = $('#' + param.chaoter.code).coord();
            //计算当前页
            pages = parseInt(coord.left / w);
            if (w % coord.left != 0) {
                pages += 1;
            }
            pages = pages < 1 ? 1 : pages;
            param.pages = pages;
            param.page_num = pages - 1;
            setCss(-param.page_num * w);
            document.getElementById("footer_pages").innerHTML = (param.page_num + 1) + '/' + param.pages;
        }
        else {
            param.page_num = 0;
            setCss(0)
        }
        //设置历史纪录
        book.info.schedule = param.chaoter_id + '|' + param.percentum;
        //设置进度百分比
        document.getElementById("footer_bai").innerHTML = param.percentum + '%';
        document.getElementById("footer_pages").innerHTML = (param.page_num + 1) + '/' + param.pages;
        //显示
        $('#loading_MM').hide();
        $('#book').css('opacity', 1);
        //书签
        //更新阅读历史纪录
        $.ajax({
            url: ctxPath + "/v2/api/bookShelf/updateMemberReadRecord",
            type: 'post',
            data: {
                book_id: param.bookId,
                member_id: member_id,
                last_chapter_id: param.chaoter_id,
                org_id: org_id,
                member_token: token,
                schedule: book.info.schedule,
                token_type: 'weixin'
            }
        });
    }

    //下一页
    function toNextPage() {
        var aw = $("#readBookHtml")[0].scrollWidth;
        var w = document.body.clientWidth - 15;
        var pages = parseInt(aw / w);
        if (aw % w != 0) {
            pages += 1;
        }
        param.pages = pages;
        if (param.page_num < pages - 1) {
            param.page_num += 1;
            setCss(-param.page_num * w, 1);
            document.getElementById("footer_pages").innerHTML = (param.page_num + 1) + '/' + param.pages;
        }
        else if (param.chaoter_num > param.chaoter_index + 1) {
            var li = $('#leftBookMenu li').eq(param.chaoter_index + 1);
            if (li.length > 0) {
                var cha = getChaoterById(li.attr('did'));
                getChaoterContent(cha);
            }
            else {
                alert("很抱歉, 没有找到相关章节内容!")
            }
        }
        else {
            console.log('全书完')
        }
    }

    //上一页
    function toLastPage() {
        if (param.page_num > 0) {
            param.page_num -= 1;
            var w = document.body.clientWidth - 15;
            setCss(-param.page_num * w, 1);
            document.getElementById("footer_pages").innerHTML = (param.page_num + 1) + '/' + param.pages;
        }
        else if (param.chaoter_index > 0) {
            console.log('上一章')
            var li = $('#leftBookMenu li').eq(param.chaoter_index - 1);
            if (li.length > 0) {
                var cha = getChaoterById(li.attr('did'));
                getChaoterContent(cha, 1);
            }
            else {
                alert("很抱歉, 没有找到相关章节内容!");
            }
        }
        else {
            console.log('第一章')
        }
    }

    init();
})