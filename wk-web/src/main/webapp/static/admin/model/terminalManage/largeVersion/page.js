var startStop;
myApp.controller('largeVersionController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //版本列表
    services["_edition_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/appVersion/pageList', param, "POST");
    }
    //版本新增-修改
    services["_edition_Update"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/appVersion/update_appVersion', param, "POST");
    }
    //版本删除
    services["_edition_Del"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/appVersion/delete_appVersions', param, "POST");
    }
    //当前使用版本
    services["_edition_enabled"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/appVersion/updateEnabled', param, "POST");
    }
    <!--文件上传-->
    $('#prj-log').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["package_url"] = res.data[0].url;
            })
        }
    });

    $scope.tableControl = {
        config: {
            lines: true,
            check: true,
            param: {
                pages: 1,
                pageNum: 1,
                pageSize: 10,
                total: 0,
                appVersion_id: 1,
                searchText: null
            },
            columns: [
                {field: 'package_name', title: "包名"},
                {field: 'version_code', title: "版本号"},
                {field: 'version_name', title: "版本名称"},
                {field: 'update_time', title: "更新时间"},
                {field: 'publish_url', title: "APK", align: 'left'},
                {
                    field: 'enabled', title: "当前使用版本",
                    formatter: function (value, row, index) {
                        var bool = row.enabled == 1 ? 'layui-form-onswitch' : '';
                        return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.enabled + ',' + row.version_id + ', event)"><i></i></div>';
                    }
                }
            ]
        },
        reload: function (param) {
            $scope.load()
        }
    };
    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
        searchText: null
    }
    //重新查询
    $scope.reload = function (key, value) {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        services._edition_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }

    //加载
    $scope.load = function () {
        services._edition_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }

    //新增
    $scope.addRow = function () {
        $scope.selRow = {}
        $scope.status = true;
        $('#prj-log + div > div > input').val("");
        $rootScope.formOpen();
    }

    //修改
    $scope.row_update = function () {
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
        if($scope.selRow.package_url)
        $('#prj-log + div > div > input').val($scope.selRow.package_url)
    }
    //删除
    $scope.delRow = function () {
        var version_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                version_ids.push($scope.tableControl.data[item.index].version_id)
            }
        });
        if (version_ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._edition_Del({ids: version_ids}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功")
                        $scope.load();
                    }
                    else {
                        layer.msg(res.message)
                    }
                })
            })
        }
    }
    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.package_name) {
            layer.alert("请填写包名")
            return false;
        }
        if (!$scope.selRow.version_code) {
            layer.alert("请填写版本号")
            return false;
        }
        if (!$scope.selRow.version_name) {
            layer.alert("请填写版本名称")
            return false;
        }
        if (!$scope.selRow.package_url) {
            layer.alert("请上传APK文件")
            return false;
        }
        if (!$scope.selRow.remark) {
            layer.alert("请填写描述")
            return false;
        }
        services._edition_Update($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {};
                }
                $scope.load();
                layer.msg('信息保存成功');
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //是否推荐
    startStop = function (enabled, version_id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.version_id == version_id) {
                $scope.selRow = item;
                $scope.selRow.enabled = enabled;
            }
        })
        services._edition_enabled($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.load()
            } else {
                layer.msg(res.message)
            }
        })
    }
});