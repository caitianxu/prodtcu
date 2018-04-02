myApp.controller('facilityMonitorController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //设备管理列表
    services["_facility_Monitor_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/device/auditList', param, "POST");
    }
    services["_change_status"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/device/changeIsSync', param, "POST");
    }

    $scope.tableControl = {
        config: {
        	lines: true,
            check: true,
            param: {
                pages: 1, //总页数
                pageNum: 1, //当前页
                pageSize: 10, //每页条数
                total: 0, //总条数
                searchText: null
            },
            columns: [
                {field: 'device_code', title: "设备编号",align:'left'},
                {field: 'org_name', title: "所属机构",align:'left'},
                {field: 'address', title: "区域",align:'left'},
                {field: 'status', title: "设备状态",formatter:function(value,row,index){
                    var str="";
                    if(value==1) str='<label style="color: #999">离线</label>'
                    if(value==2) str='<label style="color: green">在线</label>'
                    return str
                }},
                {field:'off_line', title:"文件同步状态"},
                {field: 'memory', title: "剩余内存"},
                {field: 'version', title: "使用版本"},
                {field: 'sync_time', title: "最后同步时间"},
                {
                    field: 'is_sync', title: "开启同步", align: 'center',
                    formatter: function (value, row, index) {
                        var bool = row.is_sync == 1 ? 'layui-form-onswitch' : '';
                        return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.device_id + ',' + row.is_sync + ', event)"><i></i></div>';
                    }
                }
            ]
        },
        reload: function (param) {
            var loading = layer.load(1);
            services._facility_Monitor_Data(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
                layer.close(loading);
            })
        }
    };

    //页面操作内容
    $scope.param = {
        searchText: null
    }
    //重新查询
    $scope.reload = function (key, value) {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        services._facility_Monitor_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }

    //用户启用停用
    startStop = function (device_id, bool, event) {
        $rootScope.stopEvent(event);
        bool = bool == 1 ? 2 : 1;
        services._change_status({device_id:device_id, is_sync: bool}).success(function (res) {
            if (res.code == 0) {
                $scope.tableControl.reload($scope.tableControl.config.param);
            }
            else {
                layer.msg(res.message);
            }
        })
    }
});