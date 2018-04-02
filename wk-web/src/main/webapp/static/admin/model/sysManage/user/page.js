var startStop;
myApp.controller('userController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_user"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/user/listAll', param, "POST");
    }
    //新增用户
    services["_add_user"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/user/save', param, "POST");
    }
    //删除用户
    services["_del_user"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/user/delete', param, "POST");
    }

    <!--文件上传-->
    $('#prj-log').prettyFile({
        text: "上传头像",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["icon"] = res.data[0].url;
            })
        }
    });
    $("#img_cc").click(function () {
        $("#prj-log").click();
    })
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
                searchText: null,
                org_id: null,
                dept_id: null
            },
            columns: [{
                field: 'icon', title: "头像",
                formatter: function (value, row, index) {
                    if (value) {
                        return '<img src="' + $rootScope.ctxPath + value + '" style="width: 50px;height: 50px;"/>'
                    }
                    else {
                        return '<img src="' + $rootScope.ctxPath + '/static/admin/img/df_touxiang.png" style="width: 50px;height: 50px;"/>'
                    }
                }
            },
                {field: 'user_name', title: "用户名", align: 'left'},
                {field: 'user_real_name', title: "真实姓名", align: 'left'},
                {field: 'org_name', title: "所属机构", align: 'left'},
                {field: 'dept_name', title: "所属组织", align: 'left'},
                {field: 'role_name', title: "角色", align: 'left'},
                {field: 'phone', title: "联系方式", align: 'left'},
                {
                    field: 'role_id', title: "是否授权", align: 'center',
                    formatter: function (value, row, index) {
                        var bool = row.enabled == 1 ? 'layui-form-onswitch' : '';
                        return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.user_id + ',' + row.enabled + ', event)"><i></i></div>';
                    }
                }
            ]
        },
        reload: function (param) {
            if ($rootScope._USERINFO.role_id != 1 && spliceBool) {
                spliceBool = false;
                $scope.tableControl.config.columns.splice(7, 1)
                $scope.tableControl.config.columns.splice(7, 1)
            }
            services._data_user(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };
    $scope.load = function () {
        services._org({"org_id": $scope.orgTest.org_id}).success(function (res) {
            $("#comTree").tree("loadData", res.data);
        })
        $scope.tableControl.config.param.org_id = $scope.orgTest.org_id;
    }
    var spliceBool = true;
    $scope.status = false;
    $scope.selRow = {};
    //机构
    $scope.orgTest = {
        org_name: $rootScope._USERINFO.org_name,
        org_id: $rootScope._USERINFO.org_id
    }
    //页面操作内容
    $scope.param = {
        "pageNum": 1,
        "pageSize": 1000,
        searchText: null,
        "org_id": null,
        "dept_id": null
    }
    //角色
    $scope.roleData = null;
    services._role($scope.param).success(function (res) {
        $scope.roleData = res.data.rows
    })
    var selNode = null;
    //左边组织树
    $("#comTree").tree({
        lines: true,
        //数据过滤
        "loadFilter": function (data, parent) {
            for (var i = 0; i < data.length; i++) {
                changeTreeStyle(data[i]);
            }
            return data;
        },
        "onSelect": function (node) {
            if (node.id == -1) {
                return;
            }
            $scope.tableControl.config.param.dept_id = node.id;
            $scope.tableControl.config.param["pageNum"] = 1;
            if (node.id && node.id != -1) {
                services._data_user($scope.tableControl.config.param).success(function (res) {
                    $scope.tableControl.loadData(res.data);
                })
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
            var mm = $("#comTree").tree("find", node.pid);
            if (node) {
                $("#comTree").tree("expand", mm.target);
                openNode(mm);
            }
        }
    }

    function changeTreeStyle(treeNode) {
        if (!treeNode['children'] || treeNode['children'].length == 0) {
            treeNode['res_icon'] = 'tree-folder';
        }
        if (treeNode['children'] && treeNode['children'].length > 0) {
            for (var j = 0; j < treeNode['children'].length; j++) {
                changeTreeStyle(treeNode['children'][j]);
            }
            if (treeNode.pid)
                treeNode["state"] = "closed";
        }
        //设置属性
        treeNode["text"] = treeNode.org_name;
        treeNode["id"] = treeNode.org_id;
    }

    //组织
    $scope.orgData = null;
    services._org($scope.param).success(function (res) {
        $("#authTree").tree("loadData", res.data);
        $scope.orgData = res.data
    })
    //菜单tree申明
    $("#authTree").tree({
        lines: true,
        //数据过滤
        "loadFilter": function (data, parent) {
            for (var i = 0; i < data.length; i++) {
                changeTreeStyle1(data[i]);
            }
            return data;
        },
        "onSelect": function (node) {
            $scope.$apply(function () {
                $scope.selRow.dept_id = node.org_id
                $scope.selRow.dept_name = node.org_name
                $(".layui-form-selected").removeClass("layui-form-selected")
            })
        }
    })
    function changeTreeStyle1(treeNode) {
        if (!treeNode['children'] || treeNode['children'].length == 0) {
            treeNode['org_icon'] = 'tree-folder';
        }
        if (treeNode['children'] && treeNode['children'].length > 0) {
            for (var j = 0; j < treeNode['children'].length; j++) {
                changeTreeStyle1(treeNode['children'][j]);
            }
        }
        //设置属性
        treeNode["text"] = treeNode.org_name;
    }


    //新增
    $scope.addRow = function () {
        $scope.selRow = {};
        $scope.status = true;
        $rootScope.formOpen();
    }
    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.user_name) {
            layer.alert("请填写用户名")
            return false;
        }
        if (!$scope.selRow.user_pwd) {
            layer.alert("请填写密码")
            return false;
        }
        if (!$scope.selRow.dept_name) {
            layer.alert("请选择机构/部门")
            return false;
        }
        if (!$scope.selRow.role_name) {
            layer.alert("请填写角色")
            return false;
        }
        if (!$scope.selRow.user_real_name) {
            layer.alert("请填用户真实姓名")
            return false;
        }
        var filter = /\w@\w*\.\w/;
        if ($scope.selRow.email != "" && $scope.selRow.email != null) {
            if (!filter.test($scope.selRow.email)) {
                layer.alert("请填写正确的邮箱")
                return false;
            }
        }
        if ($scope.selRow.phone) {
            if (!(/^1[34578]\d{9}$/.test($scope.selRow.phone))) {
                layer.alert("手机号码有误，请重填");
                return false;
            }
        }
        services._add_user($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {};
                }
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //重新查询
    $scope.reload = function (key, value) {
        selNode = $("#comTree").tree("getSelected");
        $scope.tableControl.config.param.dept_id = selNode.id;
        $scope.tableControl.config.param.org_id = $scope.orgTest.org_id;
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.tableControl.reload($scope.tableControl.config.param);
    }
    //修改
    $scope.row_update = function () {
        var ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].user_id)
            }
        });
        if (ids.length != 1) {
            layer.alert("请选择你将要修改的数据，同时只能修改一条数据");
            return false;
        }

        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                $scope.selRow = $scope.tableControl.data[index]
                if ($scope.selRow.birth != "" && $scope.selRow.birth != null) {
                    var startTime = new XDate($scope.selRow.birth).toString('yyyy-MM-dd')
                    $scope.selRow.birth = startTime
                }
            }
        });
        if ($scope.selRow.org_id != $rootScope._USERINFO.org_id) {
            layer.alert("无法修改其他机构用户信息")
            return false;
        }
        //绑定组织
        $scope.status = false;
        $rootScope.formOpen();
    }

    //用户名验证
    $scope.short_Change = function () {
        //是否包含汉字
        var reg = /[\u4E00-\u9FA5]/g;
        if (reg.test($scope.selRow.user_name)) {
            //删除汉字
            $scope.selRow.user_name = $scope.selRow.user_name.replace(reg, '')
        }
    }
    //用户启用停用
    startStop = function (userId, bool, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.user_id == userId) {
                $scope.selRow = item;
                $scope.selRow.enabled = bool == 1 ? 2 : 1;
                services._add_user($scope.selRow).success(function (res) {
                    if (res.code == 0) {
                        $scope.tableControl.reload($scope.tableControl.config.param);
                    }
                    else {
                        layer.msg(res.message);
                    }
                })
            }
        });
    }

    //删除
    $scope.delRow = function () {
        var ids = new Array();
        var kk = false;
        var mm = false;
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                if ($rootScope._USERINFO.user_id == $scope.tableControl.data[item.index].user_id) {
                    kk = true;
                }
                else {
                    ids.push($scope.tableControl.data[item.index].user_id)
                }
                if ($rootScope._USERINFO.org_id != $scope.tableControl.data[item.index].org_id) {
                    mm = true;
                }
            }
        });
        if (mm) {
            layer.alert("无法删除其他机构用户");
        }
        else if (kk) {
            layer.alert("无法删除自己");
        }
        else if (ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._del_user({ids: ids}).success(function (res) {
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

    //机构选择列表
    $scope.org_list = function () {
        //清空搜索input
        $scope.orgTableControl.org_searchText = null
        $scope.openOrg(function (array) {
            angular.forEach($scope.orgTableControl.rows, function (item, index) {
                if (item.select) {
                    $scope.orgTest = {
                        org_name: $scope.orgTableControl.data[item.index].org_name,
                        org_id: $scope.orgTableControl.data[item.index].org_id
                    }
                }
            });
            $scope.load();
        });
    }
    $scope.load();
})