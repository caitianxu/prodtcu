myApp.controller('memberListController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //会员列表
    services["_member_data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/unifyMember/listAll', param, "POST");
    }
    //启用停用
    services["_member_stauts"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/member/updateStatus', param, "POST");
    }
    services["_import_member"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/unifyMember/importMember', param, "POST");
        }
    $scope.tableControl = {
        config: {
            check: true,
            param: {
                pages: 1, //总页数
                pageNum: 1, //当前页
                pageSize: 10, //每页条数
                total: 0, //总条数
                searchText: null
            },
            columns: [
                {field: 'icon', title: "头像", align: 'center',
                    formatter: function (value, row, index) {
                        if (!value || value == "") {
                            return '<img style="display: block;height: 50px;width:50px;margin: 10px auto;border-radius:30px;" src="' + $rootScope.ctxPath + '/static/web/img/lv/df.jpg">'
                        }
                        return '<img style="display: block;height: 50px;width:50px;margin: 10px auto;border-radius:30px;" src="' + row.icon + '">'
                    }
                },
                {field: 'org_name', title: "机构", align: 'left'},
                {field: 'dept_name', title: "组织区域", align: 'left'},
                {field: 'account', title: "账号", align: 'left'},

                {field: 'nick_name', title: "昵称", align: 'left'},
                {field: 'sex', title: "性别", align: 'center',formatter: function (value){
                    if(value == 1){
                    return "男"
                    }else if(value == 2){
                    return "女"
                    }else{
                    return "保密"
                    }
                }
                },
                {field: 'email', title: "邮箱", align: 'left', formatter: function (value) {
                    if (value) {
                        var spl = value.split("@");
                        if (spl.length > 1) {
                            return spl[0].substr(0, spl[0].length > 3 ? 3 : spl[0].length - 2) + "*****@" + spl[1];
                        }
                        else {
                            return '';
                        }
                    }
                    return '';
                }
                },
                {
                    field: 'phone', title: "手机", align: 'left', formatter: function (value) {
                    if (value) {
                        return value.substr(0, 3) + "*****" + value.substr(value.length - 3, 3);
                    }
                    return '';
                }
                },

            ]
        },
        reload: function (param) {
            services._member_data(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };

    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
        "pageNum": 1,
        "pageSize": 10,
        searchText: null
    }
    //重新查询
    $scope.reload = function () {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.tableControl.reload($scope.tableControl.config.param);
    }
    //批量导入账号
    $('#excel_upload').prettyFile({
        text: "批量导入账号",
        change: function (res, obj) {
            services._import_member(res).success(function (res) {
                if (res.code == 0) {
                    layer.alert(res.message)
                    $scope.load();
                }
                else {
                    layer.alert(res.message);
                }
            })
        },
        init: function (obj) {
            $(".file-btn-ku", obj).remove();
            $(".file-btn-text", obj).addClass("btn-link").removeClass("layui-btn-primary")
        }
    });

    // 下载模板
    $scope.load_model = function () {
        var src = ctxPath + "/static/admin/model/member/memberList/memberImport.xlsx";
        var iframe = $('<iframe style="display: none" src="' + src + '?v=' + (new Date()).getTime().toString() + '"></iframe>');
        $("#memberList").append(iframe);
    }
    //启用停用
    $scope.startStop = function (bool) {
        var enabled = null;
        var member_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                member_ids.push($scope.tableControl.data[item.index].member_id)
                $scope.selRow = $scope.tableControl.data[index]
                $scope.selRow['type'] = 'update'
            }
        });
        if (member_ids.length == 0) {
            layer.alert("请选择你将要启用停用的用户");
            return false;
        }
        if (bool) {
            enabled = 1;
        } else {
            enabled = 2;
        }
        services._member_stauts({ids: member_ids, enabled: enabled}).success(function (res) {
            if (res.code == 0) {
                $scope.tableControl.reload($scope.tableControl.config.param);
            }
            else {
                layer.msg(res.message)
            }
        })

    }
});