myApp.controller('advClassifyController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_advCat"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/advCat/json/advCatList', param, "POST");
    }
    //分类新增/修改
    services["_advCat_add"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/advCat/json/updateAdvCat', param, "POST");
    }
    //分类删除
    services["_advCat_del"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/advCat/json/deleteAdvCats', param, "POST");
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
                {field: 'adv_cat_code', title: "编号"},
                {field: 'adv_cat_name', title: "名称", align: 'left'},
                {field: 'order_weight', title: "排序"},
                {field: 'create_time', title: "创建时间"},
                {field: 'adv_cat_remark', title: "描述", align: 'left'}
            ]
        },
        reload: function (param) {
            services._data_advCat(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };

    $scope.selRow = {}
    //重新查询
    $scope.reload = function () {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.tableControl.reload($scope.tableControl.config.param);
    }
    //页面操作内容
    $scope.param = {
        searchText: null
    }
    //新增
    $scope.addRow = function () {
        $scope.selRow = {}
        $scope.status = true
        $rootScope.formOpen();
    }

    //修改
    $scope.row_update = function () {
        var ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].id)
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
        $scope.status = false
        $rootScope.formOpen();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                $scope.selRow = $scope.tableControl.data[index]
            }
        })
    }
    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.adv_cat_code) {
            layer.alert("请填写分类编号")
            return false;
        }
        if (!$scope.selRow.adv_cat_name) {
            layer.alert("请填写分类名称")
            return false;
        }
        if (!$scope.selRow.order_weight) {
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        services._advCat_add($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {};
                }
                $scope.reload();
                layer.msg('信息保存成功');
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    //删除
    $scope.delRow = function () {
        var adv_cat_ids = new Array();
        var mark = null;
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                adv_cat_ids.push($scope.tableControl.data[item.index].adv_cat_id)
            }
        });
        if (adv_cat_ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._advCat_del({ids: adv_cat_ids, mark: mark}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功")
                        $scope.reload();
                    }
                    else if (res.code == 1) {
                        layer.confirm(res.message, {
                            btn: ['确定', '取消']
                        }, function () {
                            mark='del'
                            services._advCat_del({ids: adv_cat_ids, mark: mark}).success(function (res) {
                                if (res.code == 0) {
                                    layer.msg("删除成功")
                                    $scope.reload();
                                }else{
                                    layer.msg(res.message)
                                }
                            })
                        })
                    } else {
                        layer.msg(res.message)
                    }
                })
            })
        }
    }
});