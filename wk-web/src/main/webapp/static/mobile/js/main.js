var swiper1 = null, swiper2 = null, max_h = 0, _ajax = false;
$(document).ready(function () {
    swiper2 = new Swiper('#sw_title_2', {
        paginationClickable: true,
        onSlideChangeEnd: function (swiper) {
            $('#sw_title_1 .active').removeClass('active');
            $('#sw_title_1 .swiper-slide').eq(swiper.activeIndex).addClass('active');
            addBoos();
        }
    });
    swiper1 = new Swiper('#sw_title_1', {
        slidesPerView: 'auto',
        spaceBetween: 0,
        onClick: function (swiper) {
            $('#sw_title_1 .active').removeClass('active');
            $('#sw_title_1 .swiper-slide').eq(swiper.clickedIndex).addClass('active');
            swiper2.slideTo(swiper.clickedIndex, 0, function () {
                addBoos();
            })
        }
    });
    max_h = document.getElementById("book_list").clientHeight;
    var bookCats = [{
        book_cat_id: 0,
        book_cat_name: '热门图书'
    }, {
        book_cat_id: -1,
        book_cat_name: '最新推荐'
    }];

    $.jsonAjax("/v2/api/bookCat/getList", null, function (res) {
        if (res.code == 0) {
            $('#titleList').empty();
            $.each(res.data, function (index, item) {
                bookCats.push({
                    book_cat_id: item.book_cat_id,
                    book_cat_name: item.book_cat_name
                })
            });
            $.each(bookCats, function (index, item) {
                swiper1.appendSlide('<div class="swiper-slide ' + (index == 0 && "active") + '">' + item.book_cat_name + '</div>')
                swiper2.appendSlide('<div class="swiper-slide"><div class="min-content"></div></div>')
            })
            $("#book_list .min-content").scroll(function () {
                if (this.scrollHeight - max_h - this.scrollTop < 200) {
                    addBoos();
                }
            });
            addBoos();
        } else {
            layer.msg(res.message)
        }
    })
    //查询图书列表
    function addBoos() {
        if (_ajax) {
            return false;
        }
        //获取当前分类
        var index = swiper2.activeIndex;
        console.log(index)
        if (index - 2 >= 0) {
            swiper1.slideTo(index - 2, 300)
        }
        else {
            swiper1.slideTo(0, 300)
        }
        var cat = bookCats[index];
        if (!cat.param) {
            cat.param = {
                book_cat_id: cat.book_cat_id,
                searchText: null,
                pageNum: 0,
                pageSize: 24,
                pages: 1,
                total: 0
            }
        }
        var con = $('#book_list .swiper-slide')[swiper2.activeIndex];
        var html = $('.min-content', con);
        //判断是否需要加载
        if (html[0].scrollHeight - max_h - html[0].scrollTop > 200) {
            return false;
        }
        if (cat.param.pageNum < cat.param.pages) {
            var loading = null;
            if (cat.param.pageNum == 0)
                loading = layer.load();
            _ajax = true;
            cat.param.pageNum += 1;
            console.log(cat)
            $.jsonAjax("/v2/api/book/getList", cat.param, function (res) {
                if (res.code == 0) {
                    $.each(res.data.rows, function (mm, item) {
                        $('<a href="' + ctxPath + '/mobile/bookInfo/' + item.book_id + '" class="col">' +
                            '<div class="img-box">' +
                            '<img src="' + ctxPath + item.book_cover_small + '">' +
                            '</div>' +
                            '<span>' + item.book_name + '</span>' +
                            '</a>').appendTo(html);
                    })
                    cat.param.pages = res.data.pages;
                    if (cat.param.pageNum >= cat.param.pages) {
                        $('<p class="undata">没有更多内容了</p>').appendTo(html);
                    }
                }
                else {
                    layer.msg(res.message);
                }
                layer.close(loading);
                _ajax = false;
            })
        }
        else {
            console.log('没有更多数据了!')
        }
    }
})

