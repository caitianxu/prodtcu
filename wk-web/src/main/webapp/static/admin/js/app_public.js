var myApp = angular.module('myApp', [
    'myDirectives',
    'myServices',
    'oc.lazyLoad',
    'ui.router']).run([
    '$rootScope',
    'services',
    function ($rootScope, services) {
        //路径前缀
        $rootScope.ctxPath = ctxPath;
        $rootScope.servers = services;
        //禁用默认事件冒泡
        $rootScope.stopEvent = function ($event) {
            var event = $event || window.event;
            if (event && event.stopPropagation)
                event.stopPropagation();
            if (event && event.preventDefault)
                event.preventDefault();
        }
        //格式转字符
        $rootScope.getUnixDate = function (dateStr) {
            var regEx = new RegExp("\\-", "gi");
            return Math.round(Date.parse(dateStr.replace(regEx, "/")));
        }
        //字符转格式
        $rootScope.getLocalDate = function (unixdate, datatime) {
            if (null == unixdate || "" == unixdate) {
                var date = new Date();
                unixdate = date.getTime();
            }
            var t = new Date(parseInt(unixdate));
            return $rootScope.getDateStr(t, datatime);
        }
        //返回日期格式
        $rootScope.getDateStr = function (t, datatime) {
            var tm = t.getMonth() + 1;
            if (tm < 10) {
                tm = "0" + tm.toString();
            }
            var day = t.getDate();
            if (day < 10) {
                day = "0" + day.toString();
            }
            var t_hour = t.getHours();
            if (t_hour < 10) {
                t_hour = "0" + t_hour.toString();
            }
            var t_Minutes = t.getMinutes();
            if (t_Minutes < 10) {
                t_Minutes = "0" + t_Minutes.toString();
            }
            var t_Seconds = t.getSeconds();
            if (t_Seconds < 10) {
                t_Seconds = "0" + t_Seconds.toString();
            }
            if (datatime)
                return t.getFullYear() + "-" + tm + "-" + day + " " + t_hour + ":" + t_Minutes;
            else
                return t.getFullYear() + "-" + tm + "-" + day;
        }
        //关闭
        $rootScope.closeLayer = function () {
            layer.close($rootScope.layerfunTree);
        }
    }
]);
myApp.factory('authInterceptor', function ($rootScope, $q, $window) {
    return {
        request: function (config) {
            config.headers = config.headers || {};
            if ($rootScope.token) {
                config.headers.token = $rootScope.token;
            }
            if (returnCitySN) {
                config.headers.IP = returnCitySN["cip"];
                config.headers.Address = encodeURI(returnCitySN["cname"]);
            }
            return config;
        },
        responseError: function (rejection) {
            if (rejection.status === 401) {
                $rootScope.sessionOut();
            }
            return $q.reject(rejection);
        },
        response: function (response) {
            //正常
            if (response.code == 600) {
                $rootScope.sessionOut();
            }
            return response;
        }
    };
});
myApp.config(function ($httpProvider) {
    $httpProvider.interceptors.push('authInterceptor');
});

/**
 * 总控制器
 */
myApp.controller('mainController', function ($rootScope, $scope, services, $sce, $window, $state, $stateParams) {
    $rootScope.sys_dateTime = getSyaDatetime();
    function getSyaDatetime() {
        var day = moment().format("d"), dtxt = "星期天";
        if (day == 1) {
            dtxt = "星期一";
        }
        else if (day == 2) {
            dtxt = "星期二";
        }
        else if (day == 3) {
            dtxt = "星期三";
        }
        else if (day == 4) {
            dtxt = "星期四";
        }
        else if (day == 5) {
            dtxt = "星期五";
        }
        else if (day == 6) {
            dtxt = "星期六";
        }
        setTimeout(function () {
            $rootScope.$apply(function () {
                $rootScope.sys_dateTime = getSyaDatetime();
            });
        }, 1000);
        return moment().format('YYYY年MM月DD日 ' + dtxt + ' HH:mm:ss');
    }

    $("body").delegate(".layui-form-select", "click", function () {
        $(".layui-form-select").not(this).removeClass("layui-form-selected");
        if (!$(".layui-select-title input", this).attr("disabled"))
            $(this).toggleClass("layui-form-selected");
        return false;
    })
    //下拉组件
    $("body").delegate(".dropdown", "click", function () {
        $(".dropdown").not(this).removeClass("open");
        $(this).toggleClass("open");
        return false;
    });
    $("body").bind("click", function () {
        $(".dropdown").removeClass("open");
        $(".layui-form-select").removeClass("layui-form-selected");
    });

    //用户登录信息
    $rootScope._USERINFO = null;
    //用户菜单信息
    $rootScope._ALLMENU = null;
    //token
    $rootScope.token = null;
    /**
     * 执行用户登录
     * @private
     */
    $rootScope._login = function (param, token) {
        if (param) {
            services._login(param).success(function (res) {
                $rootScope._loginToParam(res);
            });
        }
        else if (token) {
            console.log("token by user")
            services._login({
                token: token
            }).success(function (res) {
                $rootScope._loginToParam(res);
            });
        }
        else {
            $state.go("login");
        }
    }
    $rootScope._loginToParam = function (res) {
        if (res.code == 0) {
            $rootScope.token = res.data.token;
            services._menu().success(function (resMenu) {
                $rootScope._rloadSystemData(res.data, resMenu.data);
                if (!$rootScope.skipPage || $rootScope.skipPage.name == "login") {
                    $rootScope.skipPage = {
                        name: "home"
                    };
                }
                //判断如果没有系统设置权限
                if ($rootScope.skipPage.name == "home" && resMenu.data && resMenu.data.length > 0) {
                    var cfrist = true;
                    $.each(resMenu.data, function (m, mm) {
                        if (mm.res_key == "home") {
                            cfrist = false;
                        }
                    })
                    if (cfrist) {
                        $rootScope.skipPage = {
                            name: resMenu.data[0].res_key
                        };
                    }
                }
                try {
                    $state.go($rootScope.skipPage.name);
                }
                catch (er) {
                    console.log("target err to home")
                    $rootScope.skipPage = {
                        name: "home"
                    };
                    $state.go($rootScope.skipPage.name);
                }
                finally {
                    $("body").removeClass("un-login");
                }
            });
        }
        else {
            $("#sysLogin").attr("disabled", false).removeClass("layui-btn-disabled").html("登录");
            layer.msg("登录失败！" + res.message);
            $state.go("login");
        }
    }
    /**
     * 注销用户
     */
    $rootScope.sessionOut = function () {
        $window.sessionStorage.removeItem("_USERINFO");
        $window.sessionStorage.removeItem("_ALLMENU");
        $rootScope.sessionReload = null;
        $rootScope._USERINFO = null;
        $rootScope._ALLMENU = null;
        if ($rootScope.skipPage.name != "login" || $rootScope.skipPage.name != "404" || $rootScope.skipPage.name != "unauo") {
            $rootScope.skipPage = $state.current;
            $rootScope.skipPageParam = $stateParams;
            $state.go("login");
        }
        else {
            $state.go($rootScope.nowPage);
        }
        $.cookie('LOGINUSERINFO_TOKEN', "");
    }

    /**
     * 装载数据
     * @private
     */
    $rootScope._rloadSystemData = function (userData, menuData) {
        //用户信息
        $rootScope._USERINFO = userData;
        $window.sessionStorage.setItem("_USERINFO", JSON.stringify(userData));
        //获取菜单信息
        $rootScope._ALLMENU = menuData;
        $window.sessionStorage.setItem("_ALLMENU", JSON.stringify(menuData));
        //保存token
        $.cookie("LOGINUSERINFO_TOKEN", userData.token);
        $rootScope.token = userData.token;
        //rootScope存储
        $rootScope.sessionReload = {
            userData: userData,
            menuData: menuData,
            tokenArray: {}
        };
        $rootScope.eachToken(menuData);
    }
    //获取权限
    $rootScope.eachToken = function (array) {
        angular.forEach(array, function (item) {
            if (item.res_key)
                $rootScope.sessionReload.tokenArray[item.res_key] = item;
            if (item.children) {
                $rootScope.eachToken(item.children);
            }
        })
    }
    //验证是否有权限
    $rootScope.getBtnToken = function (key) {
        if ($rootScope.sessionReload && $rootScope.sessionReload.tokenArray)
            return $rootScope.sessionReload.tokenArray[key];
        else
            return undefined;
    }
    //左侧显示菜单
    $rootScope.selectTopMenu = null;
    $rootScope.selectFirstMenu = null;
    $rootScope.selectSecondMenu = null;
    /**
     * 判断菜单选中项
     * @private
     */
    $rootScope._covertMenuSelect = function () {
        angular.forEach($rootScope._ALLMENU, function (top) {
            if (top.res_id == $rootScope.showMenu.res_id) {
                $rootScope.selectTopMenu = top;
            }
            else if (top.children && top.res_type == 1) {
                angular.forEach(top.children, function (first) {
                    if (first.res_id == $rootScope.showMenu.res_id) {
                        $rootScope.selectTopMenu = top;
                        $rootScope.selectFirstMenu = first;
                    }
                    else if (first.children && first.res_type == 1) {
                        angular.forEach(first.children, function (second) {
                            if (second.res_id == $rootScope.showMenu.res_id) {
                                $rootScope.selectTopMenu = top;
                                $rootScope.selectFirstMenu = first;
                                $rootScope.selectSecondMenu = second;
                            }
                            if (second.children) {
                                angular.forEach(second.children, function (three) {
                                    if (three.res_id == $rootScope.showMenu.res_id) {
                                        $rootScope.selectTopMenu = top;
                                        $rootScope.selectFirstMenu = first;
                                        $rootScope.selectSecondMenu = second;
                                    }
                                });
                            }
                        })
                    }
                })
            }
        });
    }
    /**
     * 格式化html
     * @param html
     * @returns {*}
     * @private
     */
    $rootScope._trustAsHtml = function (html) {
        return $sce.trustAsHtml(html);
    }
    /**
     * 根据key找到页面
     * @param key
     */
    $rootScope.getPageByKey = function (key, parent) {
        var page = null;

        angular.forEach(parent, function (item) {
            if (key == item.res_key) {

                page = item;
            }
            if (!page && item.children && item.res_type == 1) {
                page = $rootScope.getPageByKey(key, item.children);
            }
        })
        return page;
    }

    /**
     * 退出登录
     */
    $rootScope.loginup = function () {
        layer.confirm("你确定要退出系统吗?", function () {
            $(".parent_view").empty();
            $window.sessionStorage.removeItem("_USERINFO");
            $window.sessionStorage.removeItem("_ALLMENU");
            $rootScope._ALLMENU = null;
            $rootScope._USERINFO = null;
            $rootScope.token = null;
            $.cookie("LOGINUSERINFO_TOKEN", "");
            //登出
            services._logup();
            $state.go("login");
            layer.closeAll();
        })
    }

    /**
     * 打开表单
     */
    $rootScope.formOpen = function () {
        $(".form_content").removeClass("fadeOutRightBig").removeClass("none").addClass("fadeInRightBig");
    }
    /**
     * 关闭表单
     */
    $rootScope.formClose = function () {
        $(".form_content").removeClass("fadeInRightBig").addClass("fadeOutRightBig");
    }


    $(function () {
        $("#publicMenu").mCustomScrollbar({
            theme: "minimal-dark",
            scrollInertia: 200
        });
    })
    /**
     * 修改密码
     */
    $scope.upPassword = function () {
        layer.open({
            type: 1,
            title: "修改密码",
            area: ["500px", "300px"],
            content: $("#upPassword")
        });
    }
    $scope.updatePassParam = {
        oldpwd: null,
        newpwd: null,
        newpwd_1: null
    }
    /**
     * 确认修改
     */
    $scope.cen_upPassword = function () {
        if (!$scope.updatePassParam.oldpwd) {
            layer.alert("请填写旧密码")
            return false;
        }
        if (!$scope.updatePassParam.newpwd || ($scope.updatePassParam.newpwd != $scope.updatePassParam.newpwd_1)) {
            layer.alert("请正确填写新密码")
            return false;
        }
        $rootScope._USERINFO["oldpwd"] = $scope.updatePassParam["oldpwd"];
        $rootScope._USERINFO["newpwd"] = $scope.updatePassParam["newpwd"];
        $rootScope._USERINFO["newpwd_1"] = $scope.updatePassParam["newpwd_1"];
        services._up_pass($rootScope._USERINFO).success(function (rees) {
            if (rees.code == 0) {
                layer.closeAll();
                layer.msg("密码修改成功,请重新登录!");

                $(".parent_view").empty();
                $window.sessionStorage.removeItem("_USERINFO");
                $window.sessionStorage.removeItem("_ALLMENU");
                $rootScope._ALLMENU = null;
                $rootScope._USERINFO = null;
                $rootScope.token = null;
                $.cookie("LOGINUSERINFO_TOKEN", "");
                //登出
                services._logup();
                $state.go("login");
                $scope.updatePassParam = {
                    oldpwd: null,
                    newpwd: null,
                    newpwd_1: null
                }
            }
            else {
                layer.msg(rees.message)
            }
        })
    }
});

myApp.run(['$rootScope', '$state', '$window', function ($rootScope, $state, $window) {
    //开始加载新页面
    $rootScope.$on('$stateChangeStart', function (event, toState, fromState, fromParams) {
        if (toState.name == "login" || toState.name == "404" || toState.name == "unauo") {
            $("body").addClass("un-login");
        }
        else if ($rootScope._USERINFO == null || !$rootScope._ALLMENU) {
            $("body").removeClass("un-login");
            $rootScope.skipPage = toState;
            var _USERINFO = $window.sessionStorage.getItem("_USERINFO");
            var _ALLMENU = $window.sessionStorage.getItem("_ALLMENU");
            var _userinfo = null, _allmenu = null;
            var token = $.cookie("LOGINUSERINFO_TOKEN");
            if (token) {
                $rootScope.token = token;
            }
            if (_USERINFO && _USERINFO != "" && _ALLMENU && _ALLMENU != "") {
                _userinfo = eval("(" + _USERINFO + ")");
                _allmenu = eval("(" + _ALLMENU + ")");
                $rootScope._rloadSystemData(_userinfo, _allmenu);
            }
            else if (token && token != "") {
                setTimeout(function () {
                    $rootScope._login(null, token);
                }, 500);
                event.preventDefault();
            }
            else {
                $state.go("login");
                event.preventDefault();
            }
        }
        else {
            $("body").removeClass("un-login");
        }
        /**
         * 循环处理layui加载情况
         */
        function unlayui() {
            if (!element || !layer || !form || !laypage) {
                setTimeout(unlayui, 500);
            }
            else if (!element_init) {
                element_init = true;
                $(".layui-nav-bar").remove();
                setTimeout(function () {
                    element.init();
                }, 500);
            }
            else if (toState.name == "login" || toState.name == "404" || toState.name == "unauo") {
                element_init = false;
            }
        }

        unlayui();

        if ($rootScope._ALLMENU && $rootScope._ALLMENU.length > 0) {
            //设置默认选择第一个模块
            if (toState.name != "404" && toState.name != "login") {
                $rootScope.selectTopMenu = null;
                $rootScope.selectFirstMenu = null;
                $rootScope.selectSecondMenu = null;
                $rootScope.showMenu = $rootScope.getPageByKey(toState.name, $rootScope._ALLMENU);
                //清除所有选中
                if ($rootScope.showMenu)
                    $rootScope._covertMenuSelect();
            }
        }
        if ($rootScope.sessionReload) {
            //权限判断
            var pName = $rootScope.sessionReload.tokenArray[toState.name];
            if (!pName && toState.name != "login" && toState.name != "404" && toState.name != "unauo") {
                $state.go("unauo");
                event.preventDefault();
            }
        }
    });
    //页面加载成功
    $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
        if (toState.name == "login" || toState.name == "404" || toState.name == "unauo") {
            $(".center_main_content").addClass("login_main_content");
        }
        else {
            $(".center_main_content").removeClass("login_main_content");
        }
    });
    //页面加载失败
    $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState, fromParams, error) {

    });
}]);

myApp.config(function ($stateProvider, $urlRouterProvider, $controllerProvider, $compileProvider, $filterProvider, $provide) {
    myApp.controller = $controllerProvider.register;
    myApp.directive = $compileProvider.directive;
    myApp.filter = $filterProvider.register;
    myApp.factory = $provide.factory;
    myApp.service = $provide.service;
    myApp.constant = $provide.constant;


    //默认页面
    $urlRouterProvider.when('', '/login');
    //不规则页面
    $urlRouterProvider.otherwise('/404');
    //登录
    $stateProvider.state("login", {
        url: "/login",
        templateUrl: ctxPath + "/static/admin/model/login/page.html",
        controller: 'loginController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/login/page.css',
                    ctxPath + '/static/admin/model/login/page.js'])
            }]
        }
    });
    //404
    $stateProvider.state("404", {
        url: "/404",
        templateUrl: ctxPath + "/static/admin/model/other/404/page.html",
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/other/404/page.css'])
            }]
        }
    });
    //没有权限
    $stateProvider.state("unauo", {
        url: "/unauo",
        templateUrl: ctxPath + "/static/admin/model/other/unauo/page.html",
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/other/unauo/page.css'])
            }]
        }
    });


    ////////////////////////////////////////默认首页////////////////////////////////////////
    //首页
    $stateProvider.state("home", {
        url: "/home",
        templateUrl: ctxPath + "/static/admin/model/home/page.html",
        controller: 'homeController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/home/page.css',
                    ctxPath + '/static/admin/model/home/page.js'])
            }]
        }
    });
    //终端配置
    $stateProvider.state("terminal", {
        url: "/terminal",
        templateUrl: ctxPath + "/static/admin/model/home/page.html",
        controller: 'homeController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/home/page.css',
                    ctxPath + '/static/admin/model/home/page.js'])
            }]
        }
    });

    //会员中心
    $stateProvider.state("member", {
        url: "/member",
        templateUrl: ctxPath + "/static/admin/model/home/page.html",
        controller: 'homeController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/home/page.css',
                    ctxPath + '/static/admin/model/home/page.js'])
            }]
        }
    });
    //统计中心
    $stateProvider.state("statistics", {
        url: "/statistics",
        templateUrl: ctxPath + "/static/admin/model/home/page.html",
        controller: 'homeController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/home/page.css',
                    ctxPath + '/static/admin/model/home/page.js'])
            }]
        }
    });
    //资源中心
    $stateProvider.state("resCenter", {
        url: "/resCenter",
        templateUrl: ctxPath + "/static/admin/model/home/page.html",
        controller: 'homeController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/home/page.css',
                    ctxPath + '/static/admin/model/home/page.js'])
            }]
        }
    });
    ////////////////////////////////////////默认首页////////////////////////////////////////


    ////////////////////////////////////////系统管理////////////////////////////////////////
    //操作日志
    $stateProvider.state("actionLog", {
        url: "/actionLog",
        templateUrl: ctxPath + "/static/admin/model/logManage/actionLog/page.html",
        controller: 'actionLogController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/logManage/actionLog/page.css',
                    ctxPath + '/static/admin/model/logManage/actionLog/page.js'])
            }]
        }
    });
    //帮助中心
    $stateProvider.state("helpCenter", {
        url: "/helpCenter",
        templateUrl: ctxPath + "/static/admin/model/help/helpCenter/page.html",
        controller: 'helpCenterController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/help/helpCenter/page.css',
                    ctxPath + '/static/admin/model/help/helpCenter/page.js'])
            }]
        }
    });
    //系统日志
    $stateProvider.state("sysLog", {
        url: "/sysLog",
        templateUrl: ctxPath + "/static/admin/model/logManage/sysLog/page.html",
        controller: 'sysLogController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/logManage/sysLog/page.css',
                    ctxPath + '/static/admin/model/logManage/sysLog/page.js'])
            }]
        }
    });
    //项目管理
    $stateProvider.state("project", {
        url: "/project",
        templateUrl: ctxPath + "/static/admin/model/sysManage/project/page.html",
        controller: 'projectController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/sysManage/project/page.css',
                    ctxPath + '/static/admin/model/sysManage/project/page.js'])
            }]
        }
    });
    //机构管理
    $stateProvider.state("outfit", {
        url: "/outfit",
        templateUrl: ctxPath + "/static/admin/model/sysManage/outfit/page.html",
        controller: 'outfitController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/sysManage/outfit/page.css',
                    ctxPath + '/static/admin/model/sysManage/outfit/page.js',
                    ctxPath + '/static/admin/plugin/cityData/city.data-3.js'])
            }]
        }
    });
    //组织管理
    $stateProvider.state("org", {
        url: "/org",
        templateUrl: ctxPath + "/static/admin/model/sysManage/org/page.html",
        controller: 'orgController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/sysManage/org/page.css',
                    ctxPath + '/static/admin/model/sysManage/org/page.js'])
            }]
        }
    });
    //角色管理
    $stateProvider.state("role", {
        url: "/role",
        templateUrl: ctxPath + "/static/admin/model/sysManage/role/page.html",
        controller: 'roleController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/sysManage/role/page.css',
                    ctxPath + '/static/admin/model/sysManage/role/page.js'])
            }]
        }
    });
    //用户管理
    $stateProvider.state("user", {
        url: "/user",
        templateUrl: ctxPath + "/static/admin/model/sysManage/user/page.html",
        controller: 'userController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/sysManage/user/page.css',
                    ctxPath + '/static/admin/model/sysManage/user/page.js'])
            }]
        }
    });
    //权限管理
    $stateProvider.state("authority", {
        url: "/authority",
        templateUrl: ctxPath + "/static/admin/model/sysManage/authority/page.html",
        controller: 'authorityController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/sysManage/authority/page.css',
                    ctxPath + '/static/admin/model/sysManage/authority/page.js'])
            }]
        }
    });
    //资源管理
    $stateProvider.state("resource", {
        url: "/resource",
        templateUrl: ctxPath + "/static/admin/model/sysManage/resource/page.html",
        controller: 'resourceController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/sysManage/resource/page.css',
                    ctxPath + '/static/admin/model/sysManage/resource/page.js'])
            }]
        }
    });
    ////////////////////////////////////////系统管理////////////////////////////////////////


    ////////////////////////////////////////终端管理////////////////////////////////////////
    //大屏反馈
    $stateProvider.state("coupleBack", {
        url: "/coupleBack",
        templateUrl: ctxPath + "/static/admin/model/terminalManage/coupleBack/page.html",
        controller: 'coupleBackController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/terminalManage/coupleBack/page.css',
                    ctxPath + '/static/admin/model/terminalManage/coupleBack/page.js'])
            }]
        }
    });
    //大屏监控
    $stateProvider.state("facilityMonitor", {
        url: "/facilityMonitor",
        templateUrl: ctxPath + "/static/admin/model/terminalManage/facilityMonitor/page.html",
        controller: 'facilityMonitorController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/terminalManage/facilityMonitor/page.css',
                    ctxPath + '/static/admin/model/terminalManage/facilityMonitor/page.js'])
            }]
        }
    });
    //大屏列表
    $stateProvider.state("facilityList", {
        url: "/facilityList",
        templateUrl: ctxPath + "/static/admin/model/terminalManage/facilityList/page.html",
        controller: 'facilityListController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/terminalManage/facilityList/page.css',
                    ctxPath + '/static/admin/model/terminalManage/facilityList/page.js', ctxPath + '/static/admin/plugin/cityData/city.data-3.js'])
            }]
        }
    });
    //大屏审核
    $stateProvider.state("facilityAudit", {
        url: "/facilityAudit",
        templateUrl: ctxPath + "/static/admin/model/terminalManage/facilityAudit/page.html",
        controller: 'facilityAuditController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/terminalManage/facilityAudit/page.css',
                    ctxPath + '/static/admin/model/terminalManage/facilityAudit/page.js', ctxPath + '/static/admin/plugin/cityData/city.data-3.js'])
            }]
        }
    });
    //大屏版本管理
    $stateProvider.state("largeVersion", {
        url: "/largeVersion",
        templateUrl: ctxPath + "/static/admin/model/terminalManage/largeVersion/page.html",
        controller: 'largeVersionController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/terminalManage/largeVersion/page.css',
                    ctxPath + '/static/admin/model/terminalManage/largeVersion/page.js'])
            }]
        }
    });
    //安卓版本管理
    $stateProvider.state("androidVersion", {
        url: "/androidVersion",
        templateUrl: ctxPath + "/static/admin/model/terminalManage/androidVersion/page.html",
        controller: 'androidVersionController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/terminalManage/androidVersion/page.css',
                    ctxPath + '/static/admin/model/terminalManage/androidVersion/page.js'])
            }]
        }
    });
    //ios版本管理
    $stateProvider.state("iosVersion", {
        url: "/iosVersion",
        templateUrl: ctxPath + "/static/admin/model/terminalManage/iosVersion/page.html",
        controller: 'iosVersionController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/terminalManage/iosVersion/page.css',
                    ctxPath + '/static/admin/model/terminalManage/iosVersion/page.js'])
            }]
        }
    });
    //图书推荐
    $stateProvider.state("recommendBooks", {
        url: "/recommendBooks",
        templateUrl: ctxPath + "/static/admin/model/terminalManage/recommendBooks/page.html",
        controller: 'recommendBooksController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/terminalManage/recommendBooks/page.css',
                    ctxPath + '/static/admin/model/terminalManage/recommendBooks/page.js'])
            }]
        }
    });
    //图书折扣
    $stateProvider.state("discountBooks", {
        url: "/discountBooks",
        templateUrl: ctxPath + "/static/admin/model/terminalManage/discountBooks/page.html",
        controller: 'discountBooksController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/terminalManage/discountBooks/page.css',
                    ctxPath + '/static/admin/model/terminalManage/discountBooks/page.js'])
            }]
        }
    });
    //会员签到
    $stateProvider.state("tbMemberSign", {
        url: "/tbMemberSign",
        templateUrl: ctxPath + "/static/admin/model/terminalManage/tbMemberSign/page.html",
        controller: 'tbMemberSignController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/terminalManage/tbMemberSign/page.css',
                    ctxPath + '/static/admin/model/terminalManage/tbMemberSign/page.js'])
            }]
        }
    });
    //大屏配置
    $stateProvider.state("largeConfig", {
        url: "/largeConfig",
        templateUrl: ctxPath + "/static/admin/model/terminalManage/largeConfig/page.html",
        controller: 'largeConfigController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/terminalManage/largeConfig/page.css',
                    ctxPath + '/static/admin/model/terminalManage/largeConfig/page.js'])
            }]
        }
    });
    //app管理
    $stateProvider.state("appTypeManage", {
        url: "/appTypeManage",
        templateUrl: ctxPath + "/static/admin/model/terminalManage/appTypeManage/page.html",
        controller: 'appTypeManageController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/terminalManage/appTypeManage/page.css',
                    ctxPath + '/static/admin/model/terminalManage/appTypeManage/page.js'])
            }]
        }
    });
    ////////////////////////////////////////终端管理////////////////////////////////////////

    ////////////////////////////////////////资源管理////////////////////////////////////////
    //广告管理
    $stateProvider.state("advManage", {
        url: "/advManage",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/adv/advManage/page.html",
        controller: 'advManageController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/adv/advManage/page.css',
                    ctxPath + '/static/admin/model/resourceManage/adv/advManage/page.js'
                ])
            }]
        }
    });
    //广告分类
    $stateProvider.state("advClassify", {
        url: "/advClassify",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/adv/advClassify/page.html",
        controller: 'advClassifyController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/adv/advClassify/page.css',
                    ctxPath + '/static/admin/model/resourceManage/adv/advClassify/page.js'])
            }]
        }
    });
    //资讯分类
    $stateProvider.state("cmsClassify", {
        url: "/cmsClassify",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/cms/cmsClassify/page.html",
        controller: 'cmsClassifyController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/cms/cmsClassify/page.css',
                    ctxPath + '/static/admin/model/resourceManage/cms/cmsClassify/page.js'
                ])
            }]
        }
    });
    //资讯管理
    $stateProvider.state("cmsManage", {
        url: "/cmsManage",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/cms/cmsManage/page.html",
        controller: 'cmsManageController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/cms/cmsManage/page.css',
                    ctxPath + '/static/admin/model/resourceManage/cms/cmsManage/page.js'
                ])
            }]
        }
    });
    //资讯模版
    $stateProvider.state("cmsTemplate", {
        url: "/cmsTemplate",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/cms/cmsTemplate/page.html",
        controller: 'cmsTemplateController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/cms/cmsTemplate/page.css',
                    ctxPath + '/static/admin/model/resourceManage/cms/cmsTemplate/page.js'
                ])
            }]
        }
    });
    //活动管理
    $stateProvider.state("activityManage", {
        url: "/activityManage",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/cms/activityManage/page.html",
        controller: 'activityManageController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/cms/activityManage/page.css',
                    ctxPath + '/static/admin/model/resourceManage/cms/activityManage/page.js'
                ])
            }]
        }
    });
    //期刊解析
    $stateProvider.state("bookParsin", {
        url: "/bookParsin",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/book/bookParsin/page.html",
        controller: 'bookParsinController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/book/bookParsin/page.css',
                    ctxPath + '/static/admin/model/resourceManage/book/bookParsin/page.js'])
            }]
        }
    });
    //图书仓库
    $stateProvider.state("bookEntrepot", {
        url: "/bookEntrepot",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/book/bookEntrepot/page.html",
        controller: 'bookEntrepotController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/book/bookEntrepot/page.css',
                    ctxPath + '/static/admin/model/resourceManage/book/bookEntrepot/page.js'
                ])
            }]
        }
    });
    //标签管理
    $stateProvider.state("bookTab", {
        url: "/bookTab",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/book/bookTab/page.html",
        controller: 'bookTabController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/book/bookTab/page.css',
                    ctxPath + '/static/admin/model/resourceManage/book/bookTab/page.js'
                ])
            }]
        }
    });
    //数据包
    $stateProvider.state("dataPackage", {
        url: "/dataPackage",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/book/dataPackage/page.html",
        controller: 'dataPackageController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/book/dataPackage/page.css',
                    ctxPath + '/static/admin/model/resourceManage/book/dataPackage/page.js'
                ])
            }]
        }
    });
    //数据包详情
    $stateProvider.state("dataPackageDetail", {
        url: "/dataPackageDetail/:id&:name",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/book/dataPackageInfo/page.html",
        controller: 'dataPackageInfoController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/book/dataPackageInfo/page.css',
                    ctxPath + '/static/admin/model/resourceManage/book/dataPackageInfo/page.js'
                ])
            }]
        }
    });
    //图书分类
    $stateProvider.state("bookClassify", {
        url: "/bookClassify",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/book/bookClassify/page.html",
        controller: 'bookClassifyController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/book/bookClassify/page.css',
                    ctxPath + '/static/admin/model/resourceManage/book/bookClassify/page.js'
                ])
            }]
        }
    });
    //图书管理
    $stateProvider.state("bookManage", {
        url: "/bookManage",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/book/bookManage/page.html",
        controller: 'bookManageController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/book/bookManage/page.css',
                    ctxPath + '/static/admin/model/resourceManage/book/bookManage/page.js'
                ])
            }]
        }
    });
    //图书离线管理
    $stateProvider.state("bookOffLine", {
        url: "/bookOffLine",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/book/bookOffLine/page.html",
        controller: 'bookOffLineController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/book/bookOffLine/page.css',
                    ctxPath + '/static/admin/model/resourceManage/book/bookOffLine/page.js'
                ])
            }]
        }
    });
    //报纸分类
    $stateProvider.state("newspaperClassify", {
        url: "/newspaperClassify",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/newspaper/newspaperClassify/page.html",
        controller: 'newspaperClassifyController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/newspaper/newspaperClassify/page.css',
                    ctxPath + '/static/admin/model/resourceManage/newspaper/newspaperClassify/page.js'
                ])
            }]
        }
    });
    //报纸管理
    $stateProvider.state("newspaperManage", {
        url: "/newspaperManage",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/newspaper/newspaperManage/page.html",
        controller: 'newspaperManageController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/newspaper/newspaperManage/page.css',
                    ctxPath + '/static/admin/model/resourceManage/newspaper/newspaperManage/page.js'
                ])
            }]
        }
    });
    //视频分类
    $stateProvider.state("videoClassify", {
        url: "/videoClassify",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/video/videoClassify/page.html",
        controller: 'videoClassifyController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/video/videoClassify/page.css',
                    ctxPath + '/static/admin/model/resourceManage/video/videoClassify/page.js'])
            }]
        }
    });
    //视频管理
    $stateProvider.state("videoManage", {
        url: "/videoManage",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/video/videoManage/page.html",
        controller: 'videoManageController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/video/videoManage/page.css',
                    ctxPath + '/static/admin/model/resourceManage/video/videoManage/page.js'])
            }]
        }
    });
    //音频分类
    $stateProvider.state("audioClassify", {
        url: "/audioClassify",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/audio/audioClassify/page.html",
        controller: 'audioClassifyController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/audio/audioClassify/page.css',
                    ctxPath + '/static/admin/model/resourceManage/audio/audioClassify/page.js'])
            }]
        }
    });
    //音频管理
    $stateProvider.state("audioManage", {
        url: "/audioManage",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/audio/audioManage/page.html",
        controller: 'audioManageController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/audio/audioManage/page.css',
                    ctxPath + '/static/admin/model/resourceManage/audio/audioManage/page.js'])
            }]
        }
    });
    //期刊分类
    $stateProvider.state("journalClassify", {
        url: "/journalClassify",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/journal/journalClassify/page.html",
        controller: 'journalClassifyController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/journal/journalClassify/page.css',
                    ctxPath + '/static/admin/model/resourceManage/journal/journalClassify/page.js'])
            }]
        }
    });
    //期刊管理
    $stateProvider.state("journalManage", {
        url: "/journalManage",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/journal/journalManage/page.html",
        controller: 'journalManageController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/journal/journalManage/page.css',
                    ctxPath + '/static/admin/model/resourceManage/journal/journalManage/page.js'])
            }]
        }
    });

    //期刊分类
    $stateProvider.state("periodicalCat", {
        url: "/periodicalCat",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/journal/periodicalCat/page.html",
        controller: 'periodicalCatController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/journal/periodicalCat/page.css',
                    ctxPath + '/static/admin/model/resourceManage/journal/periodicalCat/page.js'])
            }]
        }
    });
    //期刊管理
    $stateProvider.state("periodicalRepo", {
        url: "/periodicalRepo",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/journal/periodicalRepo/page.html",
        controller: 'periodicalRepoController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/journal/periodicalRepo/page.css',
                    ctxPath + '/static/admin/model/resourceManage/journal/periodicalRepo/page.js'])
            }]
        }
    });
    //期刊管理-列表
    $stateProvider.state("periodicalRepoList", {
        url: "/periodicalRepoList/:sn",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/journal/periodicalRepo/list.html",
        controller: 'periodicalRepoListController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/journal/periodicalRepo/page.css',
                    ctxPath + '/static/admin/model/resourceManage/journal/periodicalRepo/list.js'])
            }]
        }
    });
    //期刊解析
    $stateProvider.state("periodicalParsin", {
        url: "/periodicalParsin",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/journal/periodicalParsin/page.html",
        controller: 'periodicalParsinController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/journal/periodicalParsin/page.css',
                    ctxPath + '/static/admin/model/resourceManage/journal/periodicalParsin/page.js'])
            }]
        }
    });
    //期刊解析
    $stateProvider.state("subjectManage", {
        url: "/subjectManage",
        templateUrl: ctxPath + "/static/admin/model/resourceManage/subject/subjectManage/page.html",
        controller: 'subjectManageController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/resourceManage/subject/subjectManage/page.css',
                    ctxPath + '/static/admin/model/resourceManage/subject/subjectManage/page.js'])
            }]
        }
    });
    ////////////////////////////////////////资源管理////////////////////////////////////////


    ////////////////////////////////////////会员中心////////////////////////////////////////
    //会员列表
    $stateProvider.state("memberList", {
        url: "/memberList",
        templateUrl: ctxPath + "/static/admin/model/member/memberList/page.html",
        controller: 'memberListController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([
                    ctxPath + '/static/admin/model/member/memberList/page.css',
                    ctxPath + '/static/admin/model/member/memberList/page.js'])
            }]
        }
    });
    ////////////////////////////////////////会员中心////////////////////////////////////////


    ////////////////////////////////////////统计中心////////////////////////////////////////
    //机构资源统计
    $stateProvider.state("orgResourceCount", {
        url: "/orgResourceCount",
        templateUrl: ctxPath + "/static/admin/model/exponent/orgResourceCount/page.html",
        controller: 'orgResourceCountController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/exponent/orgResourceCount/page.css',
                    ctxPath + '/static/admin/model/exponent/orgResourceCount/page.js'])
            }]
        }
    });
    //图书综合记录
    $stateProvider.state("bookExponentLog", {
        url: "/bookExponentLog",
        templateUrl: ctxPath + "/static/admin/model/exponent/bookExponentLog/page.html",
        controller: 'bookExponentLogController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/exponent/bookExponentLog/page.css',
                    ctxPath + '/static/admin/model/exponent/bookExponentLog/page.js'])
            }]
        }
    });
    //图书热门指数
    $stateProvider.state("bookHotExponent", {
        url: "/bookHotExponent",
        templateUrl: ctxPath + "/static/admin/model/exponent/bookHotExponent/page.html",
        controller: 'bookHotExponentController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/exponent/bookHotExponent/page.css',
                    ctxPath + '/static/admin/model/exponent/bookHotExponent/page.js'])
            }]
        }
    });
    //用户综合记录
    $stateProvider.state("userExponentLog", {
        url: "/userExponentLog",
        templateUrl: ctxPath + "/static/admin/model/exponent/userExponentLog/page.html",
        controller: 'userExponentLogController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/exponent/userExponentLog/page.css',
                    ctxPath + '/static/admin/model/exponent/userExponentLog/page.js'])
            }]
        }
    });
    //用户热门指数
    $stateProvider.state("userHotExponent", {
        url: "/userHotExponent",
        templateUrl: ctxPath + "/static/admin/model/exponent/userHotExponent/page.html",
        controller: 'userHotExponentController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/exponent/userHotExponent/page.css',
                    ctxPath + '/static/admin/model/exponent/userHotExponent/page.js'])
            }]
        }
    });
    //搜索记录
    $stateProvider.state("searchCount", {
        url: "/searchCount",
        templateUrl: ctxPath + "/static/admin/model/proManage/searchCount/page.html",
        controller: 'searchCountController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/proManage/searchCount/page.css',
                    ctxPath + '/static/admin/model/proManage/searchCount/page.js'])
            }]
        }
    });
    ////////////////////////////////////////统计中心////////////////////////////////////////


    ////////////////////////////////////////项目设置////////////////////////////////////////
    //邮件设置
    $stateProvider.state("mail", {
        url: "/mail",
        templateUrl: ctxPath + "/static/admin/model/proManage/mail/page.html",
        controller: 'mailController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/proManage/mail/page.css',
                    ctxPath + '/static/admin/model/proManage/mail/page.js'])
            }]
        }
    });
    //邮件模板
    $stateProvider.state("mailTemp", {
        url: "/mailTemp",
        templateUrl: ctxPath + "/static/admin/model/proManage/mailTemp/page.html",
        controller: 'mailTempController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/proManage/mailTemp/page.css',
                    ctxPath + '/static/admin/model/proManage/mailTemp/page.js'])
            }]
        }
    });
    //机构网站配置
    $stateProvider.state("proOrgExtend", {
        url: "/proOrgExtend",
        templateUrl: ctxPath + "/static/admin/model/proManage/proOrgExtend/page.html",
        controller: 'proOrgExtendController',
        resolve: {
            loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load([ctxPath + '/static/admin/model/proManage/proOrgExtend/page.css',
                    ctxPath + '/static/admin/model/proManage/proOrgExtend/page.js'])
            }]
        }
    });
    ////////////////////////////////////////项目设置////////////////////////////////////////
});
