var unBookStart,excelDown;
myApp.controller('bookParsinController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //图书解析任务列表
    services["_unBook_dataList"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/unBook/dataList', param, "POST");
    }
    //图书新增解析任务
    services["_unBook_save"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/unBook/save', param, "POST");
    }
    //图书管理任务-开始解析
    services["_unBook_start"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/unBook/unEpub', param, "POST");
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
                {
                    field: 'other', title: "成功解析", formatter: function (value, row) {
                        return '<a href="javascript:void(0)" onclick="excelDown(' + row.sid + ',event)">'+row.un_success+'</a>'
                    }
                },

                {
                    field: 'other', title: "无法解析", formatter: function (value, row) {
                        return '<a href="javascript:void(0)" onclick="excelDownErr(' + row.sid + ',event)">'+row.un_error+'</a>'
                    }
                },
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
                        return '<a href="javascript:void(0)" onclick="unBookStart(' + row.sid + ', event)">开始解析</em>'
                    }
                    return '';
                }
                }
            ]
        },
        reload: function (param) {
            services._unBook_dataList(param).success(function (res) {
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

     //excel下载
     excelDown = function (sid, event) {
        $rootScope.stopEvent(event);
        //创建下载对象
        var iframe = $('<iframe style="display: none" src="' + $rootScope.ctxPath + '/admin/unBook/exportTaskBook?task_id=' + sid + '"></iframe>');
        $("#bookParsin").append(iframe);
     }

     excelDownErr = function (sid, event) {
         $rootScope.stopEvent(event);
         //创建下载对象
         var iframe = $('<iframe style="display: none" src="' + $rootScope.ctxPath + '/admin/unBook/exportTaskErrBook?task_id=' + sid + '"></iframe>');
         $("#bookParsin").append(iframe);
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
        services._unBook_save($scope.selRow).success(function (res) {
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
    unBookStart = function (sid, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.sid == sid) {
                $scope.selRow = item;
            }
        });
        services._unBook_start($scope.selRow).success(function (res) {
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