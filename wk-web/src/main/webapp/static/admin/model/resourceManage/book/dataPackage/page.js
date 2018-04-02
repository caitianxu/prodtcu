var pkgOrgList;
myApp.controller('dataPackageController', function ($rootScope, $scope, services, $sce, $stateParams, $state) {
    $scope.services = services;
    //数据列表
    services["_data_package"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/dataPackage/json/getList', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_package.json', param, "POST");
    }
    //添加数据包
    services["_add_package"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/dataPackage/json/updatePkg', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/add_package.json', param, "POST");
    }
    //删除数据包
    services["_del_package"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/dataPackage/json/deletePkgs', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/add_package.json', param, "POST");
    }
    //复制数据包
    services["_copy_package"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/dataPackage/json/copyPkg', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/add_package.json', param, "POST");
    }
    //分配
    services["_allocation_pkg"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/dataPackage/json/allotPkg', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/allocation_pkg.json', param, "POST");
    }
    //数据包机构列表
    services["_data_pkg_org"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/dataPackage/json/getAllortOrgs', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_pkg_org.json', param, "POST");
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
                {field: 'pkg_name', title: "名称", align: 'left'},
                {field: 'create_user_name', title: "创建人", align: 'left'},
                {field: 'create_time', title: "创建时间"},
                {field: 'update_time', title: "最后更新时间"},
                {field: 'book_count', title: "图书数量(包括重复)"},
                {field: 'book_real_count', title: "图书数量"},
                {
                    field: 'org_count', title: "使用机构",
                    formatter: function (value, row) {
                        return '<a href="javascript:void(0)" onclick="pkgOrgList(' + row.pkg_id + ', event)" >' + value + '家</a>'
                    }
                }
            ]
        },
        reload: function (param) {
            $scope.load();
        }
    };
    /**
     * 页面参数
     * @type {{searchText: null}}
     */
    $scope.param = {
        searchText: null
    }
    /**
     * 数据加载
     */
    $scope.load = function () {
        services._data_package($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }
    /**
     * 重新加载
     */
    $scope.reload = function () {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.load();
    }

    /**
     * 新增数据行
     */
    $scope.addRow = function () {
        layer.prompt({title: '数据包名称', formType: 0}, function (pass, index) {
            services._add_package({pkg_name: pass}).success(function (res) {
                if (res.code == 0) {
                    layer.close(index);
                    //直接进入分类
                    $state.go("dataPackageDetail", {id: res.data.pkg_id, name: res.data.pkg_name})
                }
                else {
                    layer.alert(res.message)
                }
            })
        });
    }

    /**
     * 修改数据包
     */
    $scope.editRow = function () {
        $scope.selRow = null;
        var ids = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push(item);
                $scope.selRow = $scope.tableControl.data[index]
            }
        });
        if (ids.length != 1) {
            layer.msg("请选择修改数据包，并且同时只能修改一条数据")
        }
        else {
            //直接进入分类
            $state.go("dataPackageDetail", {id: $scope.selRow.pkg_id, name: $scope.selRow.pkg_name})
        }
    }
    /**
     * 删除数据包
     */
    $scope.delRow = function () {
        $scope.selRow = null;
        var dataPackages = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                dataPackages.push($scope.tableControl.data[index])
            }
        });
        if (dataPackages.length > 0) {
            layer.confirm("你确定要删除数据包吗?", "提示", function () {
                services._del_package({dataPackages:dataPackages}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("数据包删除成功")
                        $scope.reload();
                    }
                    else {
                        layer.alert(res.message);
                    }
                })
            })
        }
        else {
            layer.alert("请选择需要删除的数据包")
        }
    }

    /**
     * 分配数据包
     */
    $scope.allocationPkg = function () {
        $scope.selRow = null;
        var ids = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push(item);
                $scope.selRow = $scope.tableControl.data[index]
            }
        });
        if (ids.length != 1) {
            layer.msg("请选择复制数据包，并且同时只能复制一个数据包")
        }
        else {
            $scope.openOrg(function (array) {
                var obj = {
                    pkg_id:$scope.selRow.pkg_id,
                    orgs:array
                }
                //$scope.selRow["org"] = array;
                services._allocation_pkg(obj).success(function (res) {
                //services._allocation_pkg($scope.selRow).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("数据包已分配成功");
                        $scope.load();
                    }
                    else {
                        layer.alert(res.message);
                    }
                })
            });
        }
    }

    /**
     * 复制数据包
     */
    $scope.copyPkg = function () {
        $scope.selRow = null;
        var ids = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push(item);
                $scope.selRow = $scope.tableControl.data[index]
            }
        });
        if (ids.length != 1) {
            layer.msg("请选择复制数据包，并且同时只能复制一个数据包")
        }
        else {
            layer.prompt({title: '数据包名称', formType: 0}, function (pass, index) {
                var obj = {
                    pkg_name:pass,
                    dataPackage:$scope.selRow
                }
                services._copy_package(obj).success(function (res) {
                    if (res.code == 0) {
                        layer.close(index);
                        //直接进入分类
                        $state.go("dataPackageDetail", {id: res.data.pkg_id, name: res.data.pkg_name})
                    }
                    else {
                        layer.alert(res.message)
                    }
                })
            });
        }
    }
    $scope.data_pkg_org_array = null;
    /**
     * 数据包机构列表
     * @param pid
     */
    pkgOrgList = function (pid, event) {
        $rootScope.stopEvent(event);
        services._data_pkg_org({
            pkg_id: pid
        }).success(function (res) {
            $scope.data_pkg_org_array = res.data;
        })
        $scope.layer_export = layer.open({
            type: 1,
            title: "数据包使用机构",
            area: ["700px", "400px"],
            content: $("#allocationPkg")
        });
    }

    /*
     全部导出
     */
    $scope.export_all_data=function(){
        $scope.selRow = null;
        var ids = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push(item);
                $scope.selRow = $scope.tableControl.data[index]
            }
        });
        if (ids.length != 1) {
            layer.msg("请选择数据包，并且同时只能导出一个数据包的图书")
        }
        else {
            var iframe = $('<iframe style="display: none" src="' + $rootScope.ctxPath + '/admin/pkgBookRel/json/allExport?pkg_id=' + $scope.selRow.pkg_id + '"></iframe>');
            layer.close($scope.layer_export);
            $("#dataPackage").append(iframe);
        }
    }
});