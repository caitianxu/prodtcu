var startStop, sel_Click;
myApp.controller('authorityController', function ($rootScope, $scope, services, $sce, $stateParams, $state) {
    $scope.services = services;
    //角色资源权限
    services["_data_authority"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/sysRoleResRel/json/list', param, "POST");
    }
    //授权
    services["_start_stop"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/sysRoleResRel/json/update_sysRoleResRel', param, "POST");
    }
    //修改数据权限类型
    services["_update_type"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/sysRoleResRel/json/update_data_type', param, "POST");
    }

    $scope.tableControl = {
        config: {
            check: false,
            param: {
                orderField: "res_name",
                orderType: "desc",
                pages: 1, //总页数
                pageNum: 1,//当前页
                pageSize: 1000,//每页条数
                total: 0, //总条数
                searchText: null
            },
            columns: [
                {field: 'res_name', title: "名称", align: 'left'},
                {
                    field: 'res_type', title: "资源类型",
                    formatter: function (value, row, index) {
                        if (value == 1) {
                            return "菜单"
                        } else if (value == 2) {
                            return "按钮"
                        }
                    }
                },
                {
                    field: 'data_type_id', title: "数据权限",
                    formatter: function (value, row, index) {
                        var ii = row.data_type_id != null && row.data_type_id != "" ? row.data_type_id : 0;
                        var sel = "", bool1 = 'layui-btn-primary', bool2 = 'layui-btn-primary', bool3 = 'layui-btn-primary';
                        //资源类型是(1/菜单)
                        if (ii == 0) {
                            bool1 = "layui-btn-disabled"
                            bool2 = "layui-btn-disabled"
                            bool3 = "layui-btn-disabled"
                        }
                        else {
                            if (ii == 1) bool1 = '';
                            if (ii == 2) bool2 = '';
                            if (ii == 3) bool3 = '';
                        }

                        sel = '<div class=""><button class="layui-btn layui-btn-small ' + bool1 + '" onclick="sel_Click(' + index + ',1, event)">全部</button>' +
                            '<button class="layui-btn layui-btn-small ' + bool2 + '" onclick="sel_Click(' + index + ',2, event)">部门</button>' +
                            '<button class="layui-btn layui-btn-small ' + bool3 + '" onclick="sel_Click(' + index + ',3, event)">个人</button></div>';
                        return sel;
                    }
                },
                {field: 'create_time', title: "授权时间"},
                {
                    field: 'role_id', title: "是否授权", align: 'center',
                    formatter: function (value, row, index) {
                        var bool = row.role_id != null ? 'layui-form-onswitch' : '';
                        return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.role_res_rel_id + ',' + row.res_id + ', event)"><i></i></div>';
                    }
                }
            ]
        },
        reload: function (param) {
            services._role($scope.param).success(function (res) {
                //默认给第一个角色
                $scope.selRow['role_name'] = res.data.rows[0].role_name;
                $scope.selRow['role_id'] = res.data.rows[0].role_id;
                $scope.roleData = res.data.rows;
                if (res.data.rows.length <= 1) {
                    layer.msg("请先创建用户角色，才能进行权限设置!");
                    $state.go("role");
                }
            })
            setTimeout(function(){
                services._menu().success(function (res) {
                    //手动加根目录
                    var allData = [{
                        res_name: "菜单",
                        res_id: 0,
                        children: res.data
                    }]
                    selNode = $("#comTree").tree("getSelected");
                    $("#comTree").tree("loadData", allData);
                })
            },50)
        }
    };

    $scope.selRow = {
        pageNum: 1,
        pageSize: 1000,
        pid: null,
        role_id: null,
        role_name: null,
    }
    //页面操作内容
    $scope.param = {
        pageNum: 1,
        pageSize: 1000,
        searchText: null
    }

    //角色
    $scope.roleData = null;

    //角色改变
    $scope.roleChange = function (item) {
        $scope.selRow['role_name'] = item.role_name;
        $scope.selRow['role_id'] = item.role_id;
        if ($scope.selRow.pid != null) {
            services._data_authority($scope.selRow).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    }
    //加载列表
    $scope.load_data = function () {
        services._data_authority($scope.selRow).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
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
            $scope.selRow['pid'] = node.res_id;
            if ($scope.selRow.role_id != null) {
                $scope.load_data()
            }
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

    function changeTreeStyle1(treeNode) {
        if (!treeNode['children'] || treeNode['children'].length == 0) {
            treeNode['res_icon'] = 'tree-folder';
        }
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

    //数据权限
    sel_Click = function (value, c, event) {
        $rootScope.stopEvent(event);
        var obj = null;
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (index == value) {
                obj = $scope.tableControl.data[item.index]
                obj.data_type_id = c
            }
        });
        if (obj.role_id == null) {
            layer.alert('请先授权')
            $scope.load_data()
            return false
        }
        services._update_type(obj).success(function (res) {
            if (res.code == 0) {
                $scope.load_data()
            } else {
                layer.msg(res.message)
            }
        })

    }
    //授权
    startStop = function (role_res_rel_id, res_id, event) {
        $rootScope.stopEvent(event);
        var obj = {
            role_res_rel_id: role_res_rel_id,
            res_id: res_id,
            role_id: $scope.selRow.role_id
        };
        services._start_stop(obj).success(function (res) {
            if (res.code == 0) {
                $scope.load_data()
            } else {
                layer.msg(res.message)
            }
        })
    }
});