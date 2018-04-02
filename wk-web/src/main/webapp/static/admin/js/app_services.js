angular.module('myServices', []).factory('services', function ($http, $rootScope) {
    /**
     * 服务端请求模板
     * @param url 服务端请求url
     * @param param 请求参数
     * @param ajaxType post\get,默认是post
     * @param needFormPostCfg 是否需要表单提交参数
     * @returns {*}
     */
    $rootScope.serverAction = function (url, param, ajaxType, needFormPostCfg) {
        var type = ajaxType;
        if (type == null || type == "") {
            type = "POST";
        }
        if (!param) {
            param = {
                token: $rootScope.token
            };
        }
        if (type == "GET") {
            if ($.param(param)) {
                url = url + "?" + $.param(param);
            }
            return $http({
                method: 'GET',
                url: url
            }).error(function (data, state) {
                console.log("系统错误:" + url.replace("?", "") + state);
            })
        }
        else {
            var _postCfg = {};
            if (needFormPostCfg) {
                _postCfg = {
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    transformRequest: function (d) {
                        return $.param(d);
                    }
                };
            }
            var loading = layer.load(1);
            return $http.post(url, param, _postCfg).error(function (data, state) {
                console.log("系统错误:" + url.replace("?", "") + state);
                layer.close(loading);
            }).success(function (res) {
                if (res.code == 600) {
                    $rootScope.sessionOut();
                }
                layer.close(loading);
            })
        }
    }
    var serviceAPI = {
        //退出登录
        _logup: function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/logout', param, "POST");
        },
        //用户登录信息-登录
        _login: function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/login', param, "POST");
        },
        //修改密码
        _up_pass: function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/user/updatePwd', param, "POST");
        },
        //当前用户可见资源
        _menu: function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/sysResource/json/getMenus', param, "POST");
            //return $rootScope.serverAction(ctxPath+'/static/admin/json/menu.json', param, "POST");
        },
        //资源列表
        _menu_All: function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/sysResource/json/list', param, "POST");
            //return $rootScope.serverAction(ctxPath+'/static/admin/json/menu.json', param, "POST");
        },
        //角色列表
        _role: function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/role/listAll', param, "POST");
        },
        //项目列表
        _project: function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/project/list', param, "POST");
        },
        //组织列表
        _org: function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/organization/getTree', param, "POST");
        },
        //机构列表
        _outfit: function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/organization/getOrgExtendList', param, "POST");
        },
        //机构选择列表
        _outfit_list: function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/organization/getList', param, "POST");
        },
        //版本管理使用设备列表
        _Device_list:function(param){
            return $rootScope.serverAction(ctxPath + '/admin/appVersion/selectDevices', param, "POST");
        },
        //大屏配置使用设备列表
        _Version_list:function(param){
            return $rootScope.serverAction(ctxPath + '/admin/deviceset/getDeviceList', param, "POST");
        }


    };
    return serviceAPI;
})