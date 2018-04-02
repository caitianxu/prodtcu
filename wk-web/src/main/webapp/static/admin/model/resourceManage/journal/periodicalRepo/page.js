var restructuring, updateEnabled;
myApp.controller('periodicalRepoController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //期刊管理列表
    services["_periodical_Manage_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/periodical/periodicalList', param, "POST");
        // return $rootScope.serverAction(ctxPath + '/admin/periodical/getList', param, "POST");
    }
    //期刊分类树
    services["_periodical_Manage_Tree"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/periodicalCat/getTree', param, "POST");
    }
    //期刊是否推荐
    services["_periodical_startStop"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/periodical/updateStatus', param, "POST");
    }
    //期刊上传
    services["_resolver_pdf"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/periodical/uploadPdf', param, "POST");
    }
    //期刊拆分重组
    services["_restructuring_img"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/periodical/restructuring', param, "POST");
    }
    //期刊系列发布
    services["_enabled_periodical_all"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/periodical/updateEnabled', param, "POST");
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
                org_id: $rootScope._USERINFO.org_id,
                searchText: null
            },
            columns: [
                {field: 'series_name', title: "系列名"},
                {
                    field: 'periodical_name', title: "最新一期", formatter: function (value, row, index) {
                    return value.replace(row.series_name, "");
                }
                },
                {field: 'periodical_cat_name', title: "分类"},
                {field: 'order_weight', title: "排序"},
                {field: 'update_time', title: "更新时间"},
                {
                    field: 'action', title: "发布", formatter: function (value, row, index) {
                    if ($rootScope._USERINFO.org_id == 1) {
                        var bool = row.enabled == 1 ? 'layui-form-onswitch' : '';
                        return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="updateEnabled(' + row.periodical_id + ', event)"><i></i></div>';
                    }
                    else {
                        return "<em style='color: #999'>无权限</em>";
                    }
                }
                },
                {
                    field: 'link', title: "操作", formatter: function (value, row, index) {
                    if ($rootScope._USERINFO.org_id == 1)
                        return "<a href='#/periodicalRepoList/" + row.series_name + "'>查看列表</a>";
                    else
                        return "<em style='color: #999'>无权限</em>";
                }
                }
            ]
        },
        reload: function (param) {
            $scope.load()
        }
    };


    //重新查询
    $scope.reload = function (key, value) {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        services._periodical_Manage_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }


    var selNode = null, selNode1 = null;
    $("#comTree").tree({
        //数据过滤
        "loadFilter": function (data, parent) {
            for (var i = 0; i < data.length; i++) {
                changeTreeStyle1(data[i]);
            }
            return data;
        },
        "onSelect": function (node) {
            $scope.tableControl.config.param.periodical_cat_id = node.id
            $scope.tableControl.config.param["pageNum"] = 1;
            services._periodical_Manage_Data($scope.tableControl.config.param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        },
        onLoadSuccess: function (node, data) {
            if (!selNode) {
                selNode = $("#comTree").tree("getRoot");
            }
            else {
                //匹配节点是否存在
                var boolNode = $("#comTree").tree("find", selNode.id);
                selNode = boolNode ? boolNode : $("#comTree").tree("getRoot");
            }
            $("#comTree").tree("select", selNode.target);
            //查找未分配的分类
            var unNode = $("#comTree").tree("find", -1);
            if (unNode) {
                unNode.target.parentNode.className = "unNode";
            }
        }
    });


    //启用、停用
    updateEnabled = function (periodical_id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.periodical_id == periodical_id) {
                $scope.selRow = item;
            }
        });
        if ($scope.selRow.enabled == 1) {
            $scope.selRow.enabled = 2;
        } else {
            $scope.selRow.enabled = 1;
        }
        services._enabled_periodical_all($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message);
                $scope.load();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    function changeTreeStyle1(treeNode) {
        if (treeNode['children'] && treeNode['children'].length > 0) {
            for (var j = 0; j < treeNode['children'].length; j++) {
                changeTreeStyle1(treeNode['children'][j]);
            }
        }
        //设置属性
        treeNode["text"] = treeNode.periodical_cat_name;
        treeNode["id"] = treeNode.periodical_cat_id;

    }

    var loadingData = null;
    //加载
    $scope.load = function () {
        if (!loadingData) {
            services._periodical_Manage_Tree().success(function (res) {
                loadingData = res.data
                var arrdata = [];
                if (res.data) {
                    arrdata = res.data;
                }
                if ($rootScope._USERINFO.org_id == 1) {
                    arrdata.push({
                        periodical_cat_name: "未分类",
                        periodical_cat_id: -1,
                        children: []
                    })
                }
                var allData = [{
                    periodical_cat_name: "所有资源",
                    periodical_cat_id: 0,
                    children: arrdata
                }]
                $("#comTree").tree("loadData", allData);
            })
        } else {
            selNode = $("#comTree").tree("getSelected");
            $scope.tableControl.config.param.periodical_cat_id = selNode.id
            services._periodical_Manage_Data($scope.tableControl.config.param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    }
    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
        searchText: null
    }

    //拆分重组
    restructuring = function (periodical_id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.periodical_id == periodical_id) {
                $scope.upRow = item;
            }
        })
        services._restructuring_img($scope.upRow).success(function (res) {
            layer.msg(res.message);
        })
    }
});