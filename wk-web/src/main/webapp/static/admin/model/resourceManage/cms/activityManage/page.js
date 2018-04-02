var startStop;
myApp.controller('activityManageController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //活动管理列表
    services["_activity_Manage_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/activity/json/list', param, "POST");
    }
    //活动管理新增/修改
    services["_activity_add"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/activity/json/updateActivity', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }
    //活动管理删除
    services["_activity_del"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/activity/json/deleteActivities', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }
    //活动管理启用停用
    services["_activity_updateStatus"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/activity/json/updateStatus', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }

    $scope.tableControl = {
        config: {
            lines: true,
            check: true,
            param: {
                pages: 1,
                pageNum: 1,
                pageSize: 10,
                total: 0,
                org_id: $rootScope._USERINFO.org_id,
                searchText: null
            },
            columns: [
                {
                    field: 'cover_url_small', title: "封面", align: 'left',
                    formatter: function (value, row, index) {
                        if (value) {
                            return '<img style="display: block;width: auto;min-width: 50px;" class="df_book" src="' + $rootScope.ctxPath  + value + '">'
                        }
                        return ''
                    }
                },
                {field: 'activity_title', title: "标题", align: 'left'},
                {field: 'order_weight', title: "排序"},
                {
                    field: 'activity_status', title: "是否发布",
                    formatter: function (value, row, index) {
                        var bool = row.activity_status == 1 ? 'layui-form-onswitch' : '';
                        return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.activity_id + ', event)"><i></i></div>';
                    }
                }
            ]
        },
        reload: function (param) {
            $scope.load()
        }
    };
    <!--文件上传-->
    $('#prj-log').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["cover_url"] = res.data[0].url;
            })
        },
        init: function (par) {
            $(".input_group",par).removeClass("input_group");
        }
    });
    $("#img_cc").click(function () {
        $("#prj-log").click();
    })

    //重新查询
    $scope.reload = function (key, value) {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        services._activity_Manage_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }

    //加载
    $scope.load = function () {
        services._activity_Manage_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }
    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
        searchText: null
    }

    //新增
    $scope.addRow = function () {
        $rootScope.formOpen();
        //先清空
        $scope.selRow = {};
        var a = document.getElementById('myIframe').contentWindow
        a.ue.setContent("");

        $scope.status = true
        $rootScope.formOpen();

    }

    //修改
    $scope.row_update = function () {
        // //先清空
        var a = document.getElementById('myIframe').contentWindow
        a.ue.setContent("");

        var ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].id)
            }
        });
        if (ids.length == 0) {
            layer.alert("请选择你将要修改的数据");
            return false
        }
        if (ids.length > 1) {
            layer.alert("同时只能修改一条数据");
            return false
        }
        $scope.status = false
        $rootScope.formOpen();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                $scope.selRow = $scope.tableControl.data[index]
            }
        })
        var a = document.getElementById('myIframe').contentWindow
        a.ue.setContent($scope.selRow.activity_content);
    }
    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.activity_title) {
            layer.alert("请填写标题")
            return false;
        }
        if (!$scope.selRow.cover_url) {
            layer.alert("请上传封面")
            return false;
        }
        if (!$scope.selRow.activity_remark) {
            layer.alert("请填写概要")
            return false;
        }
        if (!$scope.selRow.start_time) {
            layer.alert("请填写活动开始时间")
            return false;
        }
        if (!$scope.selRow.end_time) {
            layer.alert("请填写活动截至时间")
            return false;
        }
        if (!$scope.selRow.order_weight) {
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        var a = document.getElementById('myIframe').contentWindow
        $scope.selRow.activity_content = a.ue.getContent()
        if (!$scope.selRow.activity_content || $scope.selRow.activity_content == "") {
            layer.alert("请上传内容")
            return false;
        }
        services._activity_add($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    var a = document.getElementById('myIframe').contentWindow
                    a.ue.setContent("");
                }
                $scope.load();
                layer.msg('信息保存成功');
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //删除
    $scope.delRow = function () {
        var activity_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                activity_ids.push($scope.tableControl.data[item.index].activity_id)
            }
        });
        if (activity_ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._activity_del({activity_ids: activity_ids}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功")
                        selNode = $("#comTree").tree("getSelected");
                        $scope.load();
                    }
                    else {
                        layer.msg(res.message)
                    }
                })
            })
        }
    }
    //是否显示
    startStop = function (car_id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.activity_id == car_id) {
                $scope.selRow = item;
                $scope.selRow.activity_status = item.activity_status == 1 ? 2 : 1
            }
        });
        services._activity_updateStatus($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.load();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

})
