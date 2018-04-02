var startStop;
myApp.controller('searchCountController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_njswSearchCount"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/searchCount/json/searchList', param, "POST");
    }
    //新增、修改
    services["_add_njswSearchCount"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/searchCount/json/updateSearchCount', param, "POST");
    }
    //删除
    services["_del_njswSearchCount"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/searchCount/json/delSearchCounts', param, "POST");
    }
    //启用停用
    services["_njswSearchCount_updateStatus"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/searchCount/json/updateStatus', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }
    $scope.tableControl = {
        config: {
            check: true,
            param: {
                orderField: "res_name",
                orderType: "desc",
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0,
                org_id: $rootScope._USERINFO.org_id == 1 ? null : $rootScope._USERINFO.org_id,
                searchText: null
            },
            columns: [
                {field: 'name', title: "搜索内容", align: 'left'},
                {field: 'search_count', title: "搜索次数"},
                {field: 'org_name', title: "所属机构", align: 'left'},
                {
                    field: 'status', title: "是否启用",
                    formatter: function (value, row, index) {
                        var bool = row.status == 1 ? 'layui-form-onswitch' : '';
                        return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.search_id + ',event)"><i></i></div>';
                    }
                },
                {
                    field: 'other', title: "操作", formatter: function (value, row, index, data) {
                    var action = "";
                    if (index > 0 && data.length > 1) {
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序置顶" onclick="order_top(' + row.search_id + ',' + index + ',\'top\', event)"><i class="iconfont icon-dingbu"></i></a>';
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序上升" onclick="order_top(' + row.search_id + ',' + index + ',\'up\', event)"><i class="iconfont icon-shangsheng1"></i></a>';
                    }
                    if (index < data.length - 1) {
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序下降" onclick="order_top(' + row.search_id + ',' + index + ',\'down\', event)"><i class="iconfont icon-xiajiang"></i></a>';
                    }
                    return '<span>'+ action +'</span>';
                }
                }
            ]
        },
        reload: function (param) {
            services._data_njswSearchCount(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };

    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
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
        var search_id = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                search_id.push($scope.tableControl.data[item.index].search_id)
            }
        });
        if (search_id.length == 0) {
            layer.alert("请选择你将要修改的数据");
            return false
        }
        if (search_id.length > 1) {
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
        if (!$scope.selRow.name) {
            layer.alert("请填写搜索内容")
            return false;
        }
        if($rootScope._USERINFO.org_id != 1){
            $scope.selRow["org_name"] = $rootScope._USERINFO.org_name;
            $scope.selRow["org_id"] = $rootScope._USERINFO.org_id;
        }
        services._add_njswSearchCount($scope.selRow).success(function (res) {
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
        var ids = new Array();
        var mark = null;
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].search_id)
            }
        });
        if (ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._del_njswSearchCount({ids: ids, mark: mark}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功")
                        $scope.reload();
                    } else {
                        layer.msg(res.message)
                    }
                })
            })
        }
    }

    //改变排序
    order_top = function (search_id, index, type, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.search_id == search_id) {
                $scope.selRow = $scope.tableControl.data[index]
            }
        })
        if (type == 'up') {
            var row_last = $scope.tableControl.data[index - 1];
            $scope.selRow.order_weight = parseInt(row_last.order_weight) + 1;
        }
        else if (type == 'down') {
            var row_last = $scope.tableControl.data[index + 1];
            $scope.selRow.order_weight = parseInt(row_last.order_weight) - 1;
        }
        else {
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        services._add_njswSearchCount($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    //是否显示
    startStop = function (car_id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.search_id == car_id) {
                $scope.selRow = item;
                $scope.selRow.status = item.status == 1 ? 2 : 1
            }
        });
        services._njswSearchCount_updateStatus($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    //机构选择列表
    $scope.org_list = function () {
        //清空搜索input
        $scope.orgTableControl.org_searchText = null
        $scope.openOrg(function (array) {
            if (array && array.length > 0) {
                $scope.selRow["org_name"] = array[0].org_name;
                $scope.selRow["org_id"] = array[0].org_id;
            }
        });
    }
});