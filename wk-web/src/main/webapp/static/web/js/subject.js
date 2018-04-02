var mySwiper = new Swiper('.swiper-container',{
    loop: true,
    autoplay: 5000,
    pagination: '.swiper-pagination',
    nextButton: '.swiper-button-next',
    prevButton: '.swiper-button-prev',
    grabCursor: true,
    paginationClickable: true,
    autoplayDisableOnInteraction: false,
    onSlideChangeStart: function (swiper) {
        var index = swiper.activeIndex - 1;
        index = index >= jsonResult.length ? 0 : index;
        index = index < 0 ? jsonResult.length -1 : index;
        $(".book_home_news h1").html(jsonResult[index].adv_name)
        $(".mm-remark").html(jsonResult[index].remark)
    }
});

if (parseInt(all_pages) > 1) {
    $('.M-box').pagination({
        pageCount: parseInt(all_pages),
        callback: function (api) {
            var pageNum = api.getCurrent();
            var pageSize = 12;
            var periodical_cat_id = 0;
            window.scrollTo(0, 0);
            var per_list = $(".sub-plan-list").html('<p class="unloading">数据加载中,请稍候...</p>');
            $.ajax({
                url: ctxPath + "/site/subject/getList",
                type: "post",
                data: {
                    org_id: org_id,
                    pageNum: pageNum,
                    pageSize: pageSize
                },
                success: function (res) {
                    per_list.empty();
                    $.each(res.data.rows, function (index, item) {
                        a = $('<a target="_blank" href="'+ ctxPath +'/web/subInfo/'+ item.subject_id +'" class="mlink"><img src="'+ ctxPath + item.subject_cover +'" /><p>'+ item.subject_name +'</p></a>').appendTo(per_list);
                        if (index > 0 && (index + 1) % 5 == 0) {
                            a.css("margin-right",0)
                        }
                    });
                    $(".M-box a").removeClass("disabled");
                }
            })
        }
    });
}