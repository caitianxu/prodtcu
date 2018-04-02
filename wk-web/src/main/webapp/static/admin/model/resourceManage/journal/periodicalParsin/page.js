var unPeriodicalStart;
myApp.controller('periodicalParsinController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //期刊管理列表
    services["_unPeriodical_dataList"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/unPeriodical/dataList', param, "POST");
    }
    //期刊管理任务编辑
    services["_unPeriodical_save"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/unPeriodical/save', param, "POST");
    }
    //期刊管理任务-开始解析
    services["_unPeriodical_start"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/unPeriodical/unPDF', param, "POST");
    }

    $scope.tableControl = {
        config: {
            check: true,
            param: {
                pages: 1,
                pageSize: 50,
                pageNum: 1,
                total: 0,
                searchText: null
            },
            columns: [
                {field: 's_title', title: "任务名称", align: 'left'},
                {field: 's_path', title: "解析路径", align: 'left'},
                {field: 'create_time', title: "创建时间"},
                {field: 'end_time', title: "完成时间"},
                {field: 's_num', title: "文件总数"},
                {field: 'un_success', title: "成功解析"},
                {field: 'un_error', title: "无法解析"},
                {
                    field: 's_status', title: "状态", formatter: function (value, row, index) {
                    if (value == 0) {
                        return '<em style="color: #999">未解析</em>'
                    }
                    else if (value == 1) {
                        return '<em style="color: #f90">解析中</em>'
                    }
                    else if (value == 2) {
                        return '<em style="color: green">解析完成</em>'
                    }
                    else {
                        return '<em style="color: red">解析失败</em>'
                    }
                }
                },
                {
                    field: 'action', title: "操作", formatter: function (value, row, index) {
                    if (row.s_status != 1) {
                        if (row.s_status == 0)
                            return '<a href="javascript:void(0)" onclick="unPeriodicalStart(' + row.sid + ', event)">开始解析</em>'
                        else
                            return '<a href="javascript:void(0)" onclick="unPeriodicalStart(' + row.sid + ', event)">重新解析</em>'
                    }
                    return '';
                }
                }
            ]
        },
        reload: function (param) {
            services._unPeriodical_dataList(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };
    $scope.param = {
        searchText: null,
        pagesNum: 1
    }
    $scope.reload = function () {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.tableControl.reload($scope.tableControl.config.param);
    }


    //新建
    $scope.addRow = function () {
        $rootScope.formOpen();
        $scope.selRow = {
            sid: null
        }
        $scope.status = true;
    }

    //保存
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.s_title) {
            layer.msg("请填写任务名称!");
            return false;
        }
        if (!$scope.selRow.s_path) {
            layer.msg("请填写解析路径!");
            return false;
        }
        services._unPeriodical_save($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg("操作成功!");
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {
                        sid: null
                    }
                }
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //修改
    $scope.editRow = function () {
        var ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].id);
                $scope.selRow = $scope.tableControl.data[index];
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
        $scope.status = false;
        $rootScope.formOpen();
    }

    //开始解析
    unPeriodicalStart = function (sid, event) {
        $rootScope.stopEvent(event);
        if (window.location.href.indexOf("cjszyun") != -1) {
            layer.alert("此功能因为过于耗费内存，禁止在服务端直接使用；请使用独立程序解析!");
            return false;
        }
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.sid == sid) {
                $scope.selRow = item;
            }
        });
        services._unPeriodical_start($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message);
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }
});