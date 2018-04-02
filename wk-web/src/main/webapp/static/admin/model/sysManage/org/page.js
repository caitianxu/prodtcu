var row_update;
myApp.controller('orgController', function ($rootScope, $scope, services, $sce, $stateParams) {
        $scope.services = services;
        //新增组织
        services["_add_org"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/organization/save', param, "POST");
        }
        //当前机构
        services["_before_org"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/user/getOrgTree', param, "POST");
        }
        //删除组织
        services["_del_org"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/organization/delete', param, "POST");
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
                    {field: 'org_name', title: "组织名称", align: 'left', width: '200px'},
                    {field: 'org_code', title: "组织编号", order: true, width: '200px'},
                    {field: 'create_time', title: "创建时间", width: '200px'},
                    {field: 'remark', title: "备注"}
                ]
            },
            reload: function (param) {
                services._org({"org_id": $scope.orgTest.org_id}).success(function (res) {
                    $("#comTree").tree("loadData", res.data);
                })
            }
        };

        $scope.selRow = {}
        //机构
        $scope.orgTest = {
            org_name: $rootScope._USERINFO.org_name,
            org_id: $rootScope._USERINFO.org_id
        }
        //页面操作内容
        $scope.param = {
            pageNum: 1,
            pageSize: 10,
            searchText: null
        }
        //重新查询
        $scope.reload = function () {
            $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
            $scope.tableControl.config.param["pageNum"] = 1;
            $scope.tableControl.reload($scope.tableControl.config.param);
        }

        //表单组织树
        $("#orgTree").tree({
            lines: true,
            //数据过滤
            "loadFilter": function (data, parent) {
                for (var i = 0; i < data.length; i++) {
                    changeTreeStyle1(data[i]);
                }
                return data;
            },
            "onSelect": function (node) {
                $scope.selRow.pid = node.org_id
                $scope.selRow.parent_orgName = node.org_name
                $(".layui-form-selected").removeClass("layui-form-selected")
            },
            onLoadSuccess: function (node, data) {
                if (!selNode) {
                    selNode = $("#orgTree").tree("getRoot");
                }
                else {
                    //匹配节点是否存在
                    var boolNode = $("#orgTree").tree("find", selNode.id);
                    selNode = boolNode ? boolNode : $("#orgTree").tree("getRoot");
                }
                $("#orgTree").tree("select", selNode.target);
            }
        });
        var selNode = null;
        //左边组织树
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
                var mm = $("#comTree").tree("find", node.pid);
                if (node) {
                    $("#comTree").tree("expand", mm.target);
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
            treeNode["text"] = treeNode.org_name;
            treeNode["id"] = treeNode.org_id;
        }
        //新增
        $scope.addRow = function () {
            formType = "add"
            $scope.selRow = {}
            $scope.status = true
            $('#parent_org').show()
            $rootScope.formOpen();
            services._org({"org_id": $scope.orgTest.org_id}).success(function (res) {
                selNode = $("#comTree").tree("getSelected");
                $("#orgTree").tree("loadData", res.data);
            })

        }
        var formType = null;
        //修改
        $scope.row_update = function () {
            var org_id = new Array();
            angular.forEach($scope.tableControl.rows, function (item, index) {
                if (item.select) {
                    org_id.push($scope.tableControl.data[item.index].org_id)
                    $scope.selRow = $scope.tableControl.data[index]
                    $scope.formType = 'update'
                }
            });
            if (org_id.length == 0) {
                layer.alert("请选择你将要修改的数据");
            } else {
                if ($scope.selRow.pid == null) {
                    $('#parent_org').hide()
                } else {
                    $('#parent_org').show()
                }
                $scope.status = false
                $rootScope.formOpen();
                services._org({"org_id": $scope.orgTest.org_id}).success(function (res) {
                    selNode = $("#comTree").tree("getSelected");
                    $("#orgTree").tree("loadData", res.data);

                    //隐藏当前节点
                    var nowNN = $("#orgTree").tree("find", $scope.selRow.org_id);
                    $("#orgTree").tree("remove", nowNN.target);
                })
            }
        }
        //提交
        $scope._form_submit = function (bool) {
            if (!$scope.selRow.org_name) {
                layer.alert("请填写组织名称")
                return false;
            }
            if (!$scope.selRow.parent_orgName) {
                layer.alert("请选择上级名称")
                return false;
            }
            var org_id = 0;
            angular.forEach($scope.tableControl.rows, function (item, index) {
                if (item.select) {
                    org_id = $scope.tableControl.data[item.index].org_id
                }
            });
            if ($scope.selRow.pid == $scope.selRow.org_id) {
                layer.alert('请选择正确的上级')
                return false;
            }
            services._add_org($scope.selRow).success(function (res) {
                if (res.code == 0) {
                    if (bool) {
                        $rootScope.formClose();
                    }
                    else {
                        $scope.selRow = {
                            pid: $scope.selRow.pid,
                            parent_orgName: $scope.selRow.parent_orgName
                        };

                    }
                    layer.msg('信息保存成功');
                    $scope.reload();
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
                    ids.push($scope.tableControl.data[item.index].org_id)
                }
            });
            if (ids.length == 0) {
                layer.alert("请选择你将要删除的数据");
            }

            else {
                layer.confirm('删除后数据无法找回,确认删除吗？', {
                    btn: ['确定', '取消'] //按钮
                }, function () {
                    services._del_org({ids: ids, mark: mark}).success(function (res) {
                        if (res.code == 0) {
                            layer.msg("删除成功")
                            $scope.reload();
                        }
                        else if (res.code == 1) {
                            layer.confirm(res.message, {
                                btn: ['确定', '取消']
                            }, function () {
                                mark = 'del'
                                services._del_org({ids: ids, mark: mark}).success(function (res) {
                                    if (res.code == 0) {
                                        layer.msg(res.message)
                                        $scope.reload();
                                    }
                                    else {
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
                $scope.reload();
            });
        }

    }
);