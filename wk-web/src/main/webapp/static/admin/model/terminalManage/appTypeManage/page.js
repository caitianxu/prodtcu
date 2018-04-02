var appTypeOrgList;
myApp.controller('appTypeManageController', function ($rootScope, $scope, services, $sce, $stateParams, $state) {
    $scope.services = services;
    //数据列表
    services["_data_appType"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/appType/getList', param, "POST");
    }
    //添加、修改app类型
    services["_add_appType"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/appType/addUpdate', param, "POST");
    }
    //删除app类型
    services["_del_appType"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/appType/delete', param, "POST");
    }
    //分配
    services["addOrg"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/appType/addOrg', param, "POST");
    }
    //app类型机构列表
    services["_data_appType_org"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/appType/orgList', param, "POST");
    }

    $scope.tableControl = {
        config: {
            check: true,
            param: {
            },
            columns: [
                {field: 'app_type_id', title: "app类型编号", align: 'left'},
                {field: 'app_type_name', title: "app类型名称", align: 'left'},
                {field: 'app_name', title: "app名称"},
                {field: 'package_name', title: "包名"},
                {
                    field: 'org_count', title: "分配机构",
                    formatter: function (value, row) {
                        return '<a href="javascript:void(0)" onclick="appTypeOrgList(' + row.app_type_id + ', event)" >' + value + '</a>'
                    }
                }
            ]
        },
        reload: function (param) {
            $scope.load();
        }
    };

    /**
     * 数据加载
     */
    $scope.load = function (param) {
        services._data_appType(param).success(function (res) {
            var data={
                rows:res.data
            }
            $scope.tableControl.loadData(data);
        })
    }

     //修改
    $scope.row_update = function () {
        var app_type_id = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                app_type_id.push($scope.tableControl.data[item.index].app_type_id)
            }
        });
        if (app_type_id.length == 0) {
            layer.alert("请选择你将要修改的数据");
            return false
        }
        if (app_type_id.length > 1) {
            layer.alert("同时只能修改一条数据");
            return false
        }
        $scope.status = false
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
        $scope.status = true
        $rootScope.formOpen();
    }
    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.app_type_name) {
            layer.alert("请填写app类型名称")
            return false;
        }
        if (!$scope.selRow.app_name) {
            layer.alert("请填写app名称")
            return false;
        }
        if (!$scope.selRow.package_name) {
            layer.alert("请填写包名")
            return false;
        }

        services._add_appType($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {};
                }
                layer.msg('信息保存成功');
                $scope.load();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    /**
     * 删除app类型
     */
    $scope.delRow = function () {
        $scope.selRow = null;
        var ids = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[index].app_type_id)
            }
        });
        if (ids.length > 0) {
            layer.confirm("你确定要删除吗?", "提示", function () {
                services._del_appType({ids:ids}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功")
                        $scope.load();
                    }
                    else {
                        layer.msg(res.message);
                    }
                })
            })
        }
        else {
            layer.alert("请选择需要删除的数据")
        }
    }

    $scope._addOrg = {
        app_type_id: null,
        org_id: null,
        org_name: null
    }

    //查询分类参数
    $scope.fenpeiParam = {
        app_type_id: null,
        searchText: null,
        pageNum: 1,
        pageSize: 10,
        pages: 1
    }
    $scope.fenpeiParamCol = {
        searchText: null
    }



    //分配
    $scope.addOrg = function (row) {
        $scope._addOrg.app_type_id = $scope.fenpeiParam.app_type_id;
        $scope._addOrg.org_id = row.org_id;
        $scope._addOrg.org_name = row.org_name;
        services.addOrg($scope._addOrg).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message)
                $scope._fenpeiData($scope.fenpeiParam.pageNum);
            }
            else {
                layer.msg(res.message)
            }
        })
    }

    $scope.data_appType_org_array = null;
    //机构列表数据
    $scope._fenpeiData = function(num){
        if(num){
            $scope.fenpeiParam.pageNum = num;
        }else{
            $scope.fenpeiParam.pageNum = 1;
            $scope.fenpeiParam.searchText = $scope.fenpeiParamCol.searchText;

        }
         services._data_appType_org($scope.fenpeiParam).success(function (res) {
             $scope.data_appType_org_array = res.data.rows;
             $scope.fenpeiParam.pages = res.data.pages;
         })
    }

    //打开层
    appTypeOrgList = function (app_type_id, event) {
        $rootScope.stopEvent(event);
        $scope.layer_export = layer.open({
            type: 1,
            title: "app类型使用机构",
            area: ["700px", "620px"],
            content: $("#allocationAppType")
        });
        $scope.fenpeiParam = {
            app_type_id: app_type_id,
            searchText: null,
            pageNum: 1,
            pageSize: 10,
            pages: 1
        }
        $scope._fenpeiData();
    }

});