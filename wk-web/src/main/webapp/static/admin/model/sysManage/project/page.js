var row_update;
myApp.controller('projectController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_project"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/project/listAll', param, "POST");
    }
    //新增项目
    services["_add_project"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/project/save', param, "POST");
    }
    $scope.tableControl = {
        config: {
            check: true,
            param: {
                orderField: "res_name",
                orderType: "desc",
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0,
                searchText: null
            },
            columns: [
                {field: 'project_code', title: "项目编号",align:'left',width: 250},
                {field: 'project_name', title: "项目名称",align:'left',width: 500},
                {field: 'create_time', title: "创建时间",align:'left',width: 250},
                {field: 'remark', title: "备注",align:'left'},

            ]
        },
        reload: function (param) {
            services._data_project(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };

    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
        searchText: null
    }
    //重新查询
    $scope.reload = function () {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.tableControl.reload($scope.tableControl.config.param);
    }

    //修改
    $scope.row_update = function () {
        var role_id = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                role_id.push($scope.tableControl.data[item.index].role_id)
            }
        });
        if (role_id.length == 0) {
            layer.alert("请选择你将要修改的数据");
            return false
        }
        if (role_id.length > 1) {
            layer.alert("同时只能修改一条数据");
            return false
        }
        $scope.status=false
        $rootScope.formOpen();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                $scope.selRow = $scope.tableControl.data[index]
            }
        });
    }

    //新增
    $scope.addRow = function () {
        $scope.selRow = {}
        $scope.status=true
        $rootScope.formOpen();
    }
    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.project_code) {
            layer.alert("请填写项编号")
            return false;
        }
        if (!$scope.selRow.project_name) {
            layer.alert("请填写项目名")
            return false;
        }
        services._add_project($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {};
                }
                $scope.reload();
                layer.msg('信息保存成功');
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    //删除
    $scope.delRow = function () {
        var ids = new Array();
        var mark = null;
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].role_id)
            }
        });
        if (ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._del_role({ids: ids, mark: mark}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功")
                        $scope.reload();
                    }
                    else if (res.code == 1) {
                        layer.confirm(res.message, {
                            btn: ['确定', '取消']
                        }, function () {
                            mark='del'
                            services._del_role({ids: ids, mark: mark}).success(function (res) {
                                if (res.code == 0) {
                                    layer.msg("删除成功")
                                    $scope.reload();
                                }else{
                                    layer.closeAll('dialog');
                                }
                            })
                        })
                    } else {
                        layer.msg(res.message)
                    }
                })
            })
        }
    }
});