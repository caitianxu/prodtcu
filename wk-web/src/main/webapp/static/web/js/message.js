var param = {
    org_id: 1,
    pageNum: 1,
    pageSize: 10,
    pages: 1,
    total: 0
}
function set_order_type(obj, t) {
    $('.order-type li').removeClass('active');
    $(obj).addClass('active');
    param.order_type = t;
    param.pageNum = 1;
    $('.M-box').pagination({
        callback: function (api) {
            param.pageNum = api.getCurrent();
            getMessagekData();
        }
    });
    getMessagekData();
}
function getMessagekData() {
    window.scrollTo(0, 0);
    var msg_list = $(".cont").html('<p class="unloading">数据加载中,请稍候...</p>');
    $.jsonAjax("/site/message/getMemberList", param, function (res) {
        msg_list.empty();
        var allPages = res.data.pages;
        if (res.data.rows.length < 1) {
        } else {
            $.each(res.data.rows, function (index, item) {
                var reply = '';
                if(item.reply && item.reply != ''){
                    reply = item.reply;
                }
                var reply_style = '';
                var icon = null;
                if(item.icon && item.icon != ''){
                    icon = item.icon;
                }else{
                    icon = '/static/web/img/lv/df.jpg';
                }
                item.reply_status ==2 ? reply_style ='block':reply_style ='none';
                var mm = $( '<li>'+
                '<div class="icon">'+
                '<img src="' + ctxPath + ''+icon+'">'+
                '</div>'+
                '<div class="txt">'+
                '<span><b style="font-size: 14px;">'+ item.nick_name +'</b>&nbsp;&nbsp;&nbsp;&nbsp;<i>'+item.send_time+'</i><i id="shanchu" style="width:18px;height:18px;float: right" onclick="delMessage('+item.device_feedback_id+')"><img src="' + ctxPath + '/static/web/img/delete.png"></i></span>'+
                '<p >'+ item.opinion +'</p>'+
                '<div class="reply" style="display: '+reply_style+'"><div class="row"><b style="color: #639e2a;">客&nbsp;&nbsp;服</b>&nbsp;&nbsp;:&nbsp;&nbsp;<span>'+ reply +'</span></div>'+
               '<p style="width: 100%;text-align: right">'+ item.reply_time +'</p>'+
               '</div>'+
                '</div>'+
                '</li>').appendTo(msg_list);
            });
            $('.M-box').show;
            $('.M-box').pagination({
                pageCount: allPages,
                prevContent: '<',
                nextContent: '>',
                current: res.data.pageNum,
                callback: function (api) {
                    param.pageNum = api.getCurrent();
                    getMessagekData();
                }
            });
        }
        if (allPages > 1) {
            $('.M-box').css("display", "block");
        } else {
            $('.M-box').css("display", "none");
        }

    })
}
if (parseInt(all_pages) > 1) {
    $('.M-box').pagination({
        pageCount: parseInt(all_pages),
        prevContent: '<',
        nextContent: '>',
        callback: function (api) {
            param.pageNum = api.getCurrent();
            getMessagekData();
        }
    });
}
//发送留言事件
$('#sendMsg').click(function(){
    $('.sendMsg').show();
    $('.myMsg').hide();
    $('.submit').show();
    $('.M-box').hide();
    $(this).addClass('active');
    $(this).siblings().removeClass("active");
});
//点击我的留言事件
$('#myMsg').click(function(){
    $('.sendMsg').hide();
    $('.myMsg').show();
    $('.submit').hide()
    $('.M-box').show();
    param.pageNum = 1;
    getMessagekData();
    $(this).addClass('active');
    $(this).siblings().removeClass("active");
});

//发表留言
$("#send").click(function(){
    var content = document.getElementById("textarea").value;
    if(content != ''){
        $.jsonAjax("/site/message/suggest", {content:content}, function (res) {
            if(res.code == 0){
                layer.msg('发送成功')
                $("#textarea").val('');
            }else{
                layer.msg('发送失败');
                return false
            }
        })
    }else{
        layer.msg('内容不能为空');
        return false
    }
});
//删除我的留言
function delMessage (id){
    layer.confirm('删除后数据无法找回,确认删除吗？', {
        btn: ['确定', '取消'] //按钮
    }, function () {
        $.jsonAjax("/site/message/deleteMessage", {device_feedback_id:id}, function (res) {
            if(res.code == 0 ){
                layer.msg('删除成功')
                getMessagekData();
            }else{
                layer.msg('删除失败')
            }
        })
    })
}

