<script type="text/javascript">
    var ctxPath = '${ctxPath}';
    var organization = '缺省机构';
    var roleid='0';
    var utype='0';
   <#if user?exists>
        roleid ='${user.roleid}';
        organization ='${user.login_org_name!''}';
        utype='${user.user_type}';
   </#if>
</script>
<link href="${ctxPath}/static/favicon.ico" rel="shortcut icon" type="image/x-icon"/>
<link rel="stylesheet" href="${ctxPath}/static/plugin/easyui/easyui.css"/>
<link rel="stylesheet" href="${ctxPath}/static/plugin/bootstrap/css/bootstrap.min.css"/>
<link rel="stylesheet" href="${ctxPath}/static/plugin/bootstrap/css/font-awesome.min.css"/>
<link rel="stylesheet" href="${ctxPath}/static/plugin/bootstrap/css/animate.css"/>
<link rel="stylesheet" href="${ctxPath}/static/css/base.css"/>
<script type="text/javascript" src="${ctxPath}/static/plugin/jquery/jquery.min.js"></script>
<script type="text/javascript" src="${ctxPath}/static/plugin/jquery/jquery-ui.min.js"></script>
<script type="text/javascript" src="${ctxPath}/static/plugin/jquery/jquery.cookie.js"></script>
<script type="text/javascript" src="${ctxPath}/static/plugin/jquery/jquery.validate.min.js"></script>
<script type="text/javascript" src="${ctxPath}/static/plugin/jquery/jquery.form.js"></script>
<script type="text/javascript" src="${ctxPath}/static/plugin/jquery/messages_zh.js"></script>
<script type="text/javascript" src="${ctxPath}/static/plugin/angular/angular.min.js"></script>
<script type="text/javascript" src="${ctxPath}/static/plugin/angular/angular-ui-router.min.js"></script>
<script type="text/javascript" src="${ctxPath}/static/plugin/angular/angular-sanitize.min.js"></script>
<script type="text/javascript" src="${ctxPath}/static/plugin/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${ctxPath}/static/plugin/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${ctxPath}/static/js/app_directives.js?v=${v}"></script>
<script type="text/javascript" src="${ctxPath}/static/js/app_controllers.js?v=${v}"></script>
<script type="text/javascript" src="${ctxPath}/static/js/app_services.js?v=${v}"></script>

<script type="text/javascript" src="${ctxPath}/static/js/json2.js?v=${v}"></script>

<!--日历-->
<script type="text/javascript" src="${ctxPath}/static/plugin/datetimepicker/jquery.datetimepicker.js"></script>
<link rel="stylesheet" href="${ctxPath}/static/plugin/datetimepicker/jquery.datetimepicker.css" type="text/css" />
<!--滚动条-->
<link rel="stylesheet" href="${ctxPath}/static/plugin/scrollbar/jquery.mCustomScrollbar.css"/>
<script type="text/javascript" src="${ctxPath}/static/plugin/scrollbar/jquery.mousewheel.min.js"></script>
<script type="text/javascript" src="${ctxPath}/static/plugin/scrollbar/jquery.mCustomScrollbar.min.js"></script>
<!--信息框-->
<link rel="stylesheet" href="${ctxPath}/static/plugin/sweetalert/sweetalert.css"/>
<script type="text/javascript" src="${ctxPath}/static/plugin/sweetalert/sweetalert.min.js"></script>
<!--图表-->
<script type="text/javascript" src="${ctxPath}/static/plugin/chart/echarts.common.min.js"></script>
<!--加载层-->
<link rel="stylesheet" href="${ctxPath}/static/plugin/layer/skin/layer.css"/>
<script type="text/javascript" src="${ctxPath}/static/plugin/layer/layer.min.js"></script>
<!--图片上传-->
<script type="text/javascript" src="${ctxPath}/static/plugin/prettyfile/bootstrap-prettyfile.js"></script>
