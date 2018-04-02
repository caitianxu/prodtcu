myApp.controller('loginController', function ($rootScope, $scope, services, $window, $sce, $stateParams) {
    element_init = false;
    $scope.services = services;
    $rootScope.skipPage = {
        name: "home"
    };
    $window.sessionStorage.removeItem("_USERINFO");
    $window.sessionStorage.removeItem("_ALLMENU");
    $rootScope._ALLMENU = null;
    $rootScope._USERINFO = null;
    $rootScope.token = null;
    $.cookie("LOGINUSERINFO_TOKEN", "");



    $scope.loginInfo = {
        user_name: null,
        user_pwd: null,
        cheched: false
    }
    //检测是否有登录信息
    var uname = $.cookie("LOGINUSERINFO_NAME");
    var upass = $.cookie("LOGINUSERINFO_PASS");
    if (uname && uname != "" && upass && upass !== "") {
        $scope.loginInfo = {
            user_name: uname,
            user_pwd: upass,
            cheched: true
        }
    }
    layer.closeAll();
    /**
     * 登录
     * @private
     */
    $scope._singIn = function () {
        if ($scope.loginInfo.user_name && $scope.loginInfo.user_pwd) {
            $("#sysLogin").addClass("layui-btn-disabled").attr("disabled", true).html("正在登录...");
            $rootScope._login($scope.loginInfo);
            //判断是否有保存密码
            if ($scope.loginInfo.cheched) {
                $.cookie("LOGINUSERINFO_NAME", $scope.loginInfo.user_name);
                $.cookie("LOGINUSERINFO_PASS", $scope.loginInfo.user_pwd);
            }
            else {
                $.cookie("LOGINUSERINFO_NAME", "");
                $.cookie("LOGINUSERINFO_PASS", "");
            }
        }
        else {
            layer.msg("请正确输入用户名和密码");
        }
    }
});