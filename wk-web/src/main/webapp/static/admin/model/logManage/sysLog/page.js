myApp.controller('sysLogController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_sysLog"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/sys_log/list', param, "POST");
    }
    var date = new Date();
    var dd = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
    var cc = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
    $scope.tableControl = {
        config: {
            check: false,
            param: {
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0,
                begin_time: dd,
                end_time: cc,
                sys_log_content: null
            },
            columns: [
                {field: 'sys_log_code', title: "日志编码",width:'180px'},
                {field: 'create_time', title: "时间",width:'180px'},
                {field: 'sys_log_content', title: "日志内容", align: 'left'}
            ]
        },
        reload: function (param) {
            services._data_sysLog(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };
    $scope.selRow = {
        begin_time: dd,
        end_time: cc,
        sys_log_content: null
    }
    //重新查询
    $scope.reload = function (key, value) {
        if (!$scope.selRow.begin_time && $scope.selRow.end_time) {
            layer.alert('请选择起始日期')
            return false
        }
        if (!$scope.selRow.end_time && $scope.selRow.begin_time) {
            layer.alert('请选择截止日期')
            return false
        }
        if ($scope.selRow.begin_time > $scope.selRow.end_time) {
            layer.alert('起始日期不能大于截止日期')
            return false
        }
        $scope.tableControl.config.param["begin_time"] = $scope.selRow.begin_time;
        $scope.tableControl.config.param["end_time"] = $scope.selRow.end_time;
        $scope.tableControl.config.param["sys_log_content"] = $scope.selRow.sys_log_content;
        $scope.tableControl.config.param["pageNum"] = 1;
        services._data_sysLog($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }

    //清空
    $scope.form_empty = function () {
        $scope.selRow = {
            begin_time: null,
            end_time: null,
            sys_log_content: null
        }
    }
});