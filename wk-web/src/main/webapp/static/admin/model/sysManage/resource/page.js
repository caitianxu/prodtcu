var startStop, is_forbid, isHide, order_top;
myApp.controller('resourceController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_add_res"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/sysResource/json/update_sysResource', param, "POST");
    }
    //删除资源
    services["_del_res"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/sysResource/json/delete_sysResource', param, "POST");
    }
    //更新状态
    services["_update_enabled"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/sysResource/json/update_enabled', param, "POST");
    }
    //一键更新
    services["_allot_sysResource"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/sysResource/json/allot_sysResource', param, "POST");
    }
    //一键推送
    services["_allot_toResource"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/sysResource/json/pushAllOrg', param, "POST");
    }
    //禁止推送
    services["_is_forbid"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/sysResource/json/is_forbid', param, "POST");
    }
    $scope.selRow = {}
    //机构
    $scope.orgTest = {
        org_name: $rootScope._USERINFO.org_name,
        org_id: $rootScope._USERINFO.org_id
    }
    $scope.tableControl = {
        config: {
            check: true,
            param: {
                pages: 1, //总页数
                pageNum: 1, //当前页
                pageSize: 1000, //每页条数
                total: 0, //总条数
                searchText: null
            },
            columns: [
                {field: 'res_name', title: "名称", align: 'left'},
                {field: 'res_key', title: "标识", align: 'left'},
                {field: 'res_icon', title: "图标", align: 'left'},
                {
                    field: 'res_type', title: "类型",
                    formatter: function (value, row, index) {
                        if (value == 1) {
                            return "菜单"
                        } else if (value == 2)
                            return "按钮"
                    }
                },
                {field: 'res_url', title: "地址", align: 'left', ngHide: isHide},
                {
                    field: 'is_forbid', title: "允许推送",
                    formatter: function (value, row, index) {
                        var bool = row.is_forbid == 2 ? 'layui-form-onswitch' : '';
                        return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="is_forbid(' + row.res_id + ', event)"><i></i></div>';
                    }
                },
                {
                    field: 'enabled', title: "启用状态",
                    formatter: function (value, row, index) {

                        var bool = row.enabled == 1 ? 'layui-form-onswitch' : '';
                        return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.res_id + ', event)"><i></i></div>';
                    }
                },
                {
                    field: 'other', title: "操作", formatter: function (value, row, index, data) {
                    var action = "";
                    if (index > 0 && data.length > 1) {
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序置顶" onclick="order_top(' + row.res_id + ',' + index + ',\'top\', event)"><i class="iconfont icon-dingbu"></i></a>';
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序上升" onclick="order_top(' + row.res_id + ',' + index + ',\'up\', event)"><i class="iconfont icon-shangsheng1"></i></a>';
                    }
                    if (index < data.length - 1) {
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序下降" onclick="order_top(' + row.res_id + ',' + index + ',\'down\', event)"><i class="iconfont icon-xiajiang"></i></a>';
                    }
                    return '<span>'+ action +'</span>';
                }
                }
            ]
        },
        reload: function (param) {
            services._menu_All({"org_id": $scope.orgTest.org_id}).success(function (res) {
                //手动加根目录
                var allData = [{
                    res_name: "所有资源",
                    res_id: 0,
                    children: res.data ? res.data : []
                }]
                $("#comTree").tree("loadData", allData);
            })
        },
        callback: function () {
            if ($scope.orgTest.org_id == 1) {
                $(".table_control").removeClass("hide_7").addClass("hide_8");
            }
            else {
                $(".table_control").addClass("hide_7").removeClass("hide_8");
            }
        }
    };
    $scope.reload = function () {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.tableControl.reload($scope.tableControl.config.param);
    }
    //页面操作内容
    $scope.param = {
        searchText: null,
        org_searchText: null
    }
    var selNode = null;
    $("#comTree").tree({
        lines: true,
        //数据过滤
        "loadFilter": function (data, parent) {
            for (var i = 0; i < data.length; i++) {
                changeTreeStyle1(data[i]);
            }
            return data;
        },
        "onSelect": function (node) {
            selNode = node;
            $scope.tableControl.loadData(node);
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
            if (selNode)
                openNode(selNode);
        }
    });
    function openNode(node) {
        if (node.pid) {
            $("#comTree").tree("expand", node.target);
            var mm = $("#comTree").tree("find", node.pid);
            if (node) {
                openNode(mm);
            }
        }
    }

    $("#resTree").tree({
        lines: true,
        //数据过滤
        "loadFilter": function (data, parent) {
            for (var i = 0; i < data.length; i++) {
                changeTreeStyle1(data[i]);
            }
            return data;
        },
        "onSelect": function (node) {
            $scope.selRow.pid = node.res_id
            $scope.selRow.parent_name = node.res_name
            $(".layui-form-selected").removeClass("layui-form-selected")
        },
        onLoadSuccess: function (node, data) {
            if (!selNode) {
                selNode = $("#resTree").tree("getRoot");
            }
            else {
                //匹配节点是否存在
                var boolNode = $("#resTree").tree("find", selNode.id);
                selNode = boolNode ? boolNode : $("#resTree").tree("getRoot");
            }
            $("#resTree").tree("select", selNode.target);
        }
    });

    function changeTreeStyle1(treeNode) {
        if (treeNode['children'] && treeNode['children'].length > 0) {
            for (var j = 0; j < treeNode['children'].length; j++) {
                changeTreeStyle1(treeNode['children'][j]);
            }
            if (treeNode.pid)
                treeNode["state"] = "closed";
        }
        //设置属性
        treeNode["text"] = treeNode.res_name;
        treeNode["id"] = treeNode.res_id;
    }

    //新增
    $scope.addRow = function () {
        $scope.selRow = {
            pid: selNode.id,
            parent_name: selNode.text
        }
        $scope.status = true
        $rootScope.formOpen();
        services._menu_All({"org_id": $scope.orgTest.org_id}).success(function (res) {
            var allData = [{
                res_name: "所有资源",
                res_id: 0,
                children: res.data
            }]
            selNode = $("#comTree").tree("getSelected");
            $("#resTree").tree("loadData", allData);
        })
    }
    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.res_name) {
            layer.alert("请填写资源名称")
            return false;
        }
        if (!$scope.selRow.res_type) {
            layer.alert("请选择类型")
            return false;
        }
        if ($scope.selRow.pid == $scope.selRow.res_id) {
            layer.alert('请选择正确的上级')
            return false;
        }

        if (!$scope.selRow.order_weight) {
            $scope.selRow.order_weight = (new Date()).getTime();
        }

        var res_id = -1;
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                res_id = $scope.tableControl.data[item.index].res_id
            }
        });

        $scope.selRow.org_id = $scope.orgTest.org_id
        services._add_res($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {
                        pid: $scope.selRow.pid,
                        parent_name: $scope.selRow.parent_name
                    };
                }
                $scope.reload();
                layer.msg('信息保存成功');
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
                $scope.orgTest = {
                    org_name: array[0].org_name,
                    org_id: array[0].org_id
                }
                $scope.reload();
            }
        });
    }
    //修改
    $scope.row_update = function () {
        var res_id = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                res_id.push($scope.tableControl.data[item.index].res_id)
            }
        });
        if (res_id.length == 0) {
            layer.alert("请选择你将要修改的数据");
            return false
        }
        if (res_id.length > 1) {
            layer.alert("同时只能修改一条数据");
            return false
        }
        $scope.status = false
        $rootScope.formOpen();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                $scope.selRow = $scope.tableControl.data[index]
                $scope.selRow['res_type_name'] = $scope.selRow.res_type == 1 ? '菜单' : '按钮';
            }
        })
        services._menu_All({"org_id": $scope.orgTest.org_id}).success(function (res) {
            var allData = [{
                res_name: "所有资源",
                res_id: 0,
                children: res.data
            }]
            selNode = $("#comTree").tree("getSelected");
            $("#resTree").tree("loadData", allData);
            //隐藏当前节点
            var nowNN = $("#resTree").tree("find", $scope.selRow.res_id);
            $("#resTree").tree("remove", nowNN.target);
        })

    }

    //一键更新
    $scope.allot_sysResource = function () {
        if ($scope.orgTest.org_id == null || $scope.orgTest.org_id == 1) {
            layer.alert("请选择机构！");
            return;
        }
        services._allot_sysResource({"org_id": $scope.orgTest.org_id}).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message);
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    //一键推送
    $scope.allot_toResource = function () {
        services._allot_toResource().success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message);
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    /**
     * 改变排序
     * @param res_id
     * @param index
     * @param type
     */
    order_top = function (res_id, index, type, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.res_id == res_id) {
                $scope.selRow = $scope.tableControl.data[index]
                $scope.selRow['res_type_name'] = $scope.selRow.res_type == 1 ? '菜单' : '按钮';
            }
        })
        if( type == 'up'){
            var row_last = $scope.tableControl.data[index - 1];
            $scope.selRow.order_weight = parseInt(row_last.order_weight) + 1;
        }
        else if(type == 'down'){
            var row_last = $scope.tableControl.data[index + 1];
            $scope.selRow.order_weight = parseInt(row_last.order_weight) - 1;
        }
        else{
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        services._add_res($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //是否显示
    startStop = function (res_id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.res_id == res_id) {
                $scope.selRow = item;
            }
        });
        services._update_enabled($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message);
                selNode = $("#comTree").tree("getSelected");
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    //禁止推送
    is_forbid = function (res_id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.res_id == res_id) {
                $scope.selRow = item;
            }
        });
        services._is_forbid($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message);
                selNode = $("#comTree").tree("getSelected");
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //删除
    $scope.delRow = function () {
        var res_ids = new Array();
        var mark = null;
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                res_ids.push($scope.tableControl.data[item.index].res_id)
            }
        });
        if (res_ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._del_res({res_ids: res_ids, mark: mark}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功")
                        $scope.reload();

                    }
                    else if (res.code == 1) {
                        layer.confirm(res.message, {
                            btn: ['确定', '取消']
                        }, function () {
                            mark = 'del'
                            services._del_res({res_ids: res_ids, mark: mark}).success(function (res) {
                                if (res.code == 0) {
                                    layer.msg("删除成功")
                                    layer.closeAll();
                                    $scope.reload();
                                }
                                else {
                                    layer.alert(res.message)
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