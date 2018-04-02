myApp.controller('actionLogController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_actionLog"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/sys_action_log/list', param, "POST");
    }
    var date = new Date();
    var dd = date.getFullYear() + "-" +(date.getMonth() + 1) + "-" + date.getDate();
    var cc = date.getFullYear() + "-" +(date.getMonth() + 1) + "-" + date.getDate();
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
                action_log_content: null,
                action_type: null,
                searchText: null
            },
            columns: [
                {field: 'action_type', title: "操作类型", align: 'center', width: '150px', order: true},
                {field: 'action_user_name', title: "操作用户名",width:"150px"},
                {field: 'action_time', title: "操作时间",width: "150px"},
                {field: 'action_log_module_name',title: "操作模块",width:"150px"},
                {field: 'action_ip', title: "操作IP",width:'250px',formatter: function (val, row, index) {
                    return decodeURIComponent(val);
                }},
                {field: 'action_log_content', title: "日志内容", align: 'left'}
            ]
        },
        reload: function (param) {
            services._data_actionLog(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };
    $scope.selRow = {
        begin_time: dd,
        end_time: cc,
        action_log_content: null,
        action_type: "全部",
        searchText:null
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
        $scope.tableControl.config.param["searchText"] = $scope.selRow.searchText;
        $scope.tableControl.config.param["action_type"] = $scope.selRow.action_type == "全部" ? null : $scope.selRow.action_type;
        $scope.tableControl.config.param["action_log_content"] = $scope.selRow.action_log_content;
        $scope.tableControl.config.param["pageNum"] = 1;
        services._data_actionLog($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }

    //清空
    $scope.form_empty = function () {
        $scope.selRow = {
            begin_time: null,
            end_time: null,
            action_log_content: null,
            action_type: "全部"
        }
    }
});