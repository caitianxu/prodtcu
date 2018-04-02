var updateRow;
myApp.controller('periodicalRepoListController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //期刊管理列表
    services["_periodical_Manage_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/periodical/sameList', param, "POST");
    }

    //期刊分类树
    services["_periodical_Manage_Tree"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/periodicalCat/getTree', param, "POST");
    }

    //期刊管理新增/修改
    services["_periodical_add"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/periodical/updatePeriodical', param, "POST");
    }
    //期刊管理新增/修改
    services["_periodical_add_file"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/periodical/updatePeriodicalFile', param, "POST");
    }
    $scope.series_name = $stateParams.sn;
    $scope.tableControl = {
        config: {
            lines: true,
            check: true,
            param: {
                org_id: $rootScope._USERINFO.org_id,
                series_name: $stateParams.sn
            },
            columns: [
                {
                    field: 'periodical_cover_small', title: "封面", align: 'left',
                    formatter: function (value, row, index) {
                        if (value) {
                            return '<img style="display: block;margin: 0px auto;" class="df_book"  src="' + $rootScope.ctxPath + value + '">'
                        }
                        return ''
                    }
                },
                {field: 'periodical_cat_name', title: "分类"},
                {
                    field: 'periodical_name', title: "期数名", formatter: function (value, row, index) {
                    return value.replace(row.series_name, "");
                }
                },
                {field: 'child_file_name', title: "解压目录"},
                {field: 'total_page', title: "总页数"},
                {field: 'update_time', title: "更新时间"},
                {field: 'periodical_remark', title: "简介", align: 'left'},
                {
                    field: 'action', title: "操作", formatter: function (value, row, index) {
                    if ($rootScope._USERINFO.org_id == 1)
                        return '<a href="javascript:void(0)" onclick="updateRow(' + row.periodical_id + ', event)">修改</a>'
                    else
                        return ''
                }
                }
            ]
        },
        reload: function (param) {
            services._periodical_Manage_Data($scope.tableControl.config.param).success(function (res) {
                $scope.tableControl.loadData({
                    rows: res.data
                });
            })
        }
    };
    $scope._form_submit_file = function () {
        services._periodical_add_file($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $rootScope.formClose();
                $scope.tableControl.reload();
                layer.msg('信息保存成功');
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    $('#prj-log').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["periodical_cover"] = res.data[0].url;
            })
        },
        init: function (par) {
            $(".input_group", par).removeClass("input_group");
        }
    });
    $("#img_cc").click(function () {
        $('#prj-log').click();
    })

    $("#resTree").tree({
        //数据过滤
        "loadFilter": function (data, parent) {
            for (var i = 0; i < data.length; i++) {
                changeTreeStyle1(data[i]);
            }
            return data;
        },
        "onSelect": function (node) {
            $scope.selRow.periodical_cat_id = node.id;
            $scope.selRow.parent_name = node.text;
            $(".layui-form-selected").removeClass("layui-form-selected")
        },
        onLoadSuccess: function (node, data) {
            selNode1 = $("#resTree").tree("find", $scope.selRow.periodical_cat_id)
            selNode1 = selNode1 ? selNode1 : $("#resTree").tree("getRoot");
            selNode1 = $("#resTree").tree("find", selNode1.id);
            $("#resTree").tree("select", selNode1.target);
            //查找未分配的分类
            var unNode = $("#resTree").tree("find", -1);
            if (unNode) {
                unNode.target.parentNode.className = "unNode";
            }
        }
    });

    var selNode = null, selNode1 = null;
    updateRow = function (id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.periodical_id == id) {
                $scope.selRow = item;
            }
        });
        $scope.status = false
        $rootScope.formOpen();
        services._periodical_Manage_Tree().success(function (res) {
            var arrdata = [];
            if (res.data) {
                arrdata = res.data;
            }
            var allData = [{
                periodical_cat_name: "所有资源",
                periodical_cat_id: 0,
                children: arrdata
            }]
            $("#resTree").tree("loadData", allData);

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

    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.series_name) {
            layer.alert("请填写期刊系列")
            return false;
        }
        if (!$scope.selRow.periodical_name) {
            layer.alert("请填写期刊名称")
            return false;
        }
        if (!$scope.selRow.periodical_cover) {
            layer.alert("请上传封面")
            return false;
        }
        if (!$scope.selRow.periodical_cat_id) {
            layer.alert("请选择分类")
            return false;
        }
        if (!$scope.selRow.order_weight) {
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        services._periodical_add($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {
                        periodical_cat_id: $scope.selRow.periodical_cat_id,
                        parent_name: $scope.selRow.parent_name
                    };
                }
                $scope.tableControl.reload();
                layer.msg('信息保存成功');
            }
            else {
                layer.msg(res.message);
            }
        })
    }
});