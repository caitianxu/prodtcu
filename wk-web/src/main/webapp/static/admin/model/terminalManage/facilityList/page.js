myApp.controller('facilityListController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //设备列表
    services["_facility_data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/device/listAll', param, "POST");
    }
    //修改
    services["_update_facility"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/device/update', param, "POST");
    }
    //启用停用
    services["_facility_stauts"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/device/audit', param, "POST");
    }
    $scope.tableControl = {
        config: {
            check: true,
            param: {
                orderField: "res_name",
                orderType: "desc",
                pages: 1, //总页数
                pageNum: 1, //当前页
                pageSize: 10, //每页条数
                total: 0, //总条数
                searchText: null
            },
            columns: [
                {field: 'device_code', title: "编号", align: 'left'},
                {field: 'org_name', title: "所属机构", align: 'left'},
                {field: 'user_name', title: "登录用户", align: 'left'},
                {
                    field: 'address', title: "区域(省市区)", align: 'left',
                    formatter: function (value, row, index) {
                        var str = row.address
                        return str;
                    }
                },
                {field: 'street', title: "街道", align: 'left'},
                {field: 'location', title: "位置", align: 'left'},
                {
                    field: 'enabled', title: "状态",
                    formatter: function (value, row, index) {
                        var i = "";
                        var c = "";//颜色
                        if (row.enabled == 1) {
                            i = "已启用"
                            c = "#6fd96f"
                        }
                        else {
                            i = "已停用"
                            c = "red"
                        }
                        return '<i style="color:' + c + '">' + i + '</i>';
                    }
                },
                {field: 'create_time', title: "创建时间"}
            ]
        },
        reload: function (param) {
            services._facility_data(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };

    $scope.selRow = {}
    $scope.provinceList = [];
    $scope.cityList = [];
    $scope.areaList = [];
    //页面操作内容
    $scope.param = {
        "pageNum": 1,
        "pageSize": 10,
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
        $scope.status = false
        var device_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                device_ids.push($scope.tableControl.data[item.index].device_id)
                $scope.selRow = $scope.tableControl.data[index]
                $scope.selRow['type'] = 'update'
                if(!$scope.selRow['area']){
                    $scope.selRow['area'] ='无';
                }
            }
        });
        if(device_ids.length > 1){
            layer.alert("同时只能修改一条记录");
        }
        else if (device_ids.length == 0) {
            layer.alert("请选择你将要修改的数据");
        } else {
            $scope.provinceList = []
            angular.forEach(cityData3, function (item, index) {
                $scope.provinceList.push(item)
            })
            if ($scope.selRow.province_id) {
                angular.forEach($scope.provinceList, function (item, index) {
                    if ($scope.selRow.province_id == item.value) {
                        $scope.cityList = item.children
                    }
                })
            }
            if ($scope.selRow.city_id) {
                angular.forEach($scope.cityList, function (item, index) {
                    if ($scope.selRow.city_id == item.value) {
                        $scope.areaList = item.children
                    }
                })
            }


            $rootScope.formOpen();
        }
    }

    //提交
    $scope._form_submit = function (bool) {
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
        services._update_facility($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {};
                }
                layer.msg('信息保存成功');
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })

    }

    //启用停用
    $scope.startStop = function (bool) {
        var mark = null;
        var device_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                device_ids.push($scope.tableControl.data[item.index].device_id)
                $scope.selRow = $scope.tableControl.data[index]
                $scope.selRow['type'] = 'update'
            }
        });
        if (device_ids.length == 0) {
            layer.alert("请选择你将要启用停用的设备");
            return false;
        }
        if (bool) {
            mark = 'enabled';
        } else {
            mark = 'no';
        }
        services._facility_stauts({ids: device_ids, mark: mark}).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message)
            }
            else {
                layer.msg(res.message)
            }
            $scope.tableControl.reload($scope.tableControl.config.param);
        })

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


});