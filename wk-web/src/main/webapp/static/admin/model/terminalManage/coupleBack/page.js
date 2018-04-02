myApp.controller('coupleBackController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //意见反馈列表
    services["_Feedback_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/deviceFeedback/messageList', param, "POST");
    }
    //回复
    services["_Feedback_Update"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/deviceFeedback/update', param, "POST");
    }
    //组织列表
    services["_dept_data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/organization/getOrgDept', param, "POST");
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
                searchText: null
            },
            columns: [
                {field: 'org_name', title: "机构", align: 'left'},
                {field: 'dept_name', title: "组织区域", align: 'left'},
                {field: 'user_name', title: "用户", align: 'left'},
                {field: 'opinion', title: "意见内容", align: 'left'},
                {field: 'reply', title: "回复内容", align: 'left'},
                {field: 'send_time', title: "发送时间"},
                {field: 'reply_time', title: "回复时间"}
            ]
        },
        reload: function (param) {
            $scope.load()
        }
    };
    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
        searchText: null,
        dept_id : null,
        org_name : null
    }
    //重新查询
    $scope.reload = function (key, value) {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.tableControl.config.param["dept_id"] = $scope.param.dept_id;
        services._Feedback_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }

    //加载
    $scope.load = function () {
        services._Feedback_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }
    var layer_reply = null;

    //回复
    $scope.reply_button = function (bool) {
        var audit_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                audit_ids.push($scope.tableControl.data[item.index].device_feedback_id)
                $scope.selRow = $scope.tableControl.data[item.index]
            }
        });
        if (audit_ids.length == 0) {
            layer.alert("请选择回复的意见");
            return false;
        }
        if (audit_ids.length > 1) {
            layer.alert("请一条一条回复");
            return false;
        }
        if($scope.selRow.reply_status==2){
            layer.alert("该意见已回复")
            return false;
        }
        layer_reply = layer.open({
            type: 1,
            title: "我的回复",
            area: ["600px", "260px"],
            content: $("#reply")
        });
    }

    $scope.deptData = [];
    services._dept_data({org_id : $rootScope._USERINFO.org_id}).success(function (res) {
        $scope.deptData = res.data
        $scope.deptData.unshift({org_id: null, org_name: '全部'});
    })

    //回复提交
    $scope.reply_submit = function () {
        if (!$scope.selRow.reply) {
            layer.alert("回复内容不能为空")
            return false
        }
        services._Feedback_Update($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message)
            }
            else {
                layer.msg(res.message)
            }
            layer.close(layer_reply);
            $scope.load();
        })
    }
});