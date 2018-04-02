myApp.controller('facilityAuditController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //设备管理列表
    services["_facility_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/deviceAudit/listAll', param, "POST");
    }
    //同意-驳回
    services["_facility_Update"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/deviceAudit/update', param, "POST");
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
                {field: 'device_code', title: "设备编号", align: 'left'},
                {field: 'org_name', title: "所属机构", align: 'left'},
                {field: 'user_name', title: "登录用户", align: 'left'},
                {field: 'apply_time', title: "申请时间"},
                {
                    field: 'audit_status', title: "审核结果", formatter: function (value, row, index) {
                    var str = "", cor = "";
                    if (value == 1) {
                        str = '驳回';
                        cor = "red"
                    }
                    if (value == 2) {
                        str = '同意';
                        cor = "#6fd96f"
                    }
                    if (value == 3) {
                        str = '待审核';
                        cor = "#000"
                    }
                    return '<i style="color:' + cor + '">' + str + '</i>'
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
        services._facility_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }

    //加载
    $scope.load = function () {
        services._facility_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }
    var layer_info = null;
    //同意-驳回
    $scope.updateType = function (bool) {
        var mark = null;
        //bool为true-同意,否则驳回
        if (bool) {
            mark = 'yes';
        } else {
            mark = 'no';
        }
        var audit_ids = new Array();
        var audit_status = null;
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                audit_ids.push($scope.tableControl.data[item.index].device_audit_id)
                audit_status = $scope.tableControl.data[item.index].audit_status;
            }
        });
        if (audit_ids.length != 1) {
            layer.alert("请选择你将要操作的数据,并且同时只能操作一条数据;");
        } else {
            if (audit_status ==3 && mark == 'yes') { //待审核点了同意需要补全地址信息
                layer_info = layer.open({
                    type: 1,
                    title: "信息补全",
                    area: ["500px", "380px"],
                    content: $("#facilityAuditForm")
                });
                $scope.provinceList = []
                angular.forEach(cityData3, function (item, index) {
                    $scope.provinceList.push(item)
                });
                $scope.selRow = {};
                $scope.selRow["ids"] = audit_ids;
                $scope.selRow["mark"] = mark;
            }
            else {
                services._facility_Update({ids: audit_ids, mark: mark}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg(res.message)
                    }
                    else {
                        layer.msg(res.message)
                    }
                    $scope.load();
                })
            }
        }
    }


    //设置省
    $scope.set_province = function (item) {
        $scope.selRow.province = item.text;
        $scope.selRow.province_id = item.value;
        $scope.selRow.city = null;
        $scope.selRow.city_id = null;
        $scope.selRow.area = null;
        $scope.selRow.area_id = null;
        $scope.cityList = [];
        $scope.areaList = []
        angular.forEach($scope.provinceList, function (ii, index) {
            if (ii.value == $scope.selRow.province_id) {
                $scope.cityList = ii.children
            }
        })
    }
    //设置市
    $scope.set_city = function (item) {
        $scope.selRow.city = item.text;
        $scope.selRow.city_id = item.value;
        $scope.selRow.area = null;
        $scope.selRow.area_id = null;
        $scope.areaList = []
        angular.forEach($scope.cityList, function (ii, index) {
            if (ii.value == $scope.selRow.city_id) {
                if (ii.children) {
                    $scope.areaList = ii.children
                } else {
                    $scope.selRow.area = '无'
                }
            }

        })
    }
    //设置区
    $scope.set_area = function (item) {
        $scope.selRow.area = item.text
        $scope.selRow.area_id = item.value
    }

    //信息补全保存
    $scope.selRow = {}
    $scope.provinceList = [];
    $scope.cityList = [];
    $scope.areaList = [];
    //提交
    $scope._form_submit = function () {
        if (!$scope.selRow.province) {
            layer.alert('请选择省')
            return false
        }
        if (!$scope.selRow.city) {
            layer.alert('请选择市')
            return false
        }
        if (!$scope.selRow.area) {
            layer.alert('请选择区')
            return false
        }
        if (!$scope.selRow.street) {
            layer.alert('街道不能为空')
            return false
        }
        if (!$scope.selRow.location) {
            layer.alert('位置不能为空')
            return false
        }
        services._facility_Update($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message);
                layer.close(layer_info);
            }
            else {
                layer.msg(res.message)
            }
            $scope.load();
        })
    }
});