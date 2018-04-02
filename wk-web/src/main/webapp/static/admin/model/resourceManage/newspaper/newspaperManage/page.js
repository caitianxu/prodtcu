var startStop;
myApp.controller('newspaperManageController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //报纸管理列表
    services["_newspaper_Manager_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/newspaper/list', param, "POST");
    }
    //报纸分类树
    services["_newspaper_Manager_Tree"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/newspaperCat/getTree', param, "POST");
    }
    //报纸管理新增/修改
    services["_newspaper_add"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/newspaper/save', param, "POST");
    }
    //报纸删除
    services["_newspaper_del"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/newspaper/del', param, "POST");
    }
    //报纸删除
    services["_newspaper_startStop"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/newspaper/updateStatus', param, "POST");
    }
    //报纸排序
    services["_newspaper_order"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/newspaper/updateOrder', param, "POST");
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
                {
                    field: 'paper_img_small', title: "封面", align: 'left',
                    formatter: function (value, row, index) {
                        if (value) {
                            return '<img style="display: block;margin: 0px auto;" class="df_book" src="' + $rootScope.ctxPath + value + '">'
                        }
                        return ''
                    }
                },
                {field: 'paper_name', title: "名称", align: 'left'},
                {
                    field: 'paper_url', title: "链接地址", align: 'left', formatter: function (value, row, index) {
                    return '<a href="' + value + '" target="_blank">' + value + '</a>';
                }
                },
                {
                    field: 'other', title: "操作", formatter: function (value, row, index, data) {
                    var action = "";
                    if (index > 0 && data.length > 1) {
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序置顶" onclick="order_top(' + row.newspaper_id + ',' + index + ',\'top\', event)"><i class="iconfont icon-dingbu"></i></a>';
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序上升" onclick="order_top(' + row.newspaper_id + ',' + index + ',\'up\', event)"><i class="iconfont icon-shangsheng1"></i></a>';
                    }
                    if (index < data.length - 1) {
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序下降" onclick="order_top(' + row.newspaper_id + ',' + index + ',\'down\', event)"><i class="iconfont icon-xiajiang"></i></a>';
                    }
                    return '<span>'+ action +'</span>';
                }
                }
            ]
        },
        reload: function (param) {
            if ($rootScope._USERINFO.org_id != 1 && spliceBool) {
                spliceBool = false;
                $scope.tableControl.config.columns.splice(3, 1)
            }
            $scope.load()
        }
    };
     var spliceBool = true;
    <!--文件上传-->
    $('#prj-log').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["paper_img"] = res.data[0].url;
            })
        },
        init: function (par) {
            $(".input_group", par).removeClass("input_group");
        }
    });
    $("#img_cc").click(function () {
        $('#prj-log').click();
    })

    //重新查询
    $scope.reload = function (key, value) {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        services._newspaper_Manager_Data($scope.tableControl.config.param).success(function (res) {
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
            if (node.id == -1) {
                return;
            }
            $scope.tableControl.config.param.newspaper_cat_id = node.id
            $scope.tableControl.config.param["pageNum"] = 1;
            services._newspaper_Manager_Data($scope.tableControl.config.param).success(function (res) {
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

    $("#resTree").tree({
        //数据过滤
        "loadFilter": function (data, parent) {
            for (var i = 0; i < data.length; i++) {
                changeTreeStyle1(data[i]);
            }
            return data;
        },
        "onSelect": function (node) {
            $scope.selRow.newspaper_cat_id = node.id;
            $scope.selRow.parent_name = node.text;
            $(".layui-form-selected").removeClass("layui-form-selected")
        },
        onLoadSuccess: function (node, data) {
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


    function changeTreeStyle1(treeNode) {
        if (treeNode['children'] && treeNode['children'].length > 0) {
            for (var j = 0; j < treeNode['children'].length; j++) {
                changeTreeStyle1(treeNode['children'][j]);
            }
        }
        //设置属性
        treeNode["text"] = treeNode.newspaper_cat_name;
        treeNode["id"] = treeNode.newspaper_cat_id;
    }

    var loadingData = null;
    //加载
    $scope.load = function () {
        if (!loadingData) {
            services._newspaper_Manager_Tree().success(function (res) {
                loadingData = res.data
                var arrdata = [];
                if (res.data) {
                    arrdata = res.data;
                }
                var allData = [{
                    newspaper_cat_name: "所有资源",
                    newspaper_cat_id: 0,
                    children: arrdata
                }]
                $("#comTree").tree("loadData", allData);
            })
        }
        else {
            selNode = $("#comTree").tree("getSelected");
            $scope.tableControl.config.param.newspaper_cat_id = selNode.id
            services._newspaper_Manager_Data($scope.tableControl.config.param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    }
    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
        searchText: null
    }

    //新增
    $scope.addRow = function () {
        $rootScope.formOpen();
        $scope.selRow = {
            newspaper_cat_id: selNode.id,
            parent_name: selNode.text,
            outlink_type: 0
        }
        $scope.status = true
        $rootScope.formOpen();
        services._newspaper_Manager_Tree().success(function (res) {
            var arrdata = [];
            if (res.data) {
                arrdata = res.data;
            }
            var allData = [{
                newspaper_cat_name: "所有资源",
                newspaper_cat_id: 0,
                children: arrdata
            }]
            selNode1 = $("#comTree").tree("getSelected");
            $("#resTree").tree("loadData", allData);
        })
    }

    //修改
    $scope.row_update = function () {
        var ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].newspaper_cat_id)
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
        services._newspaper_Manager_Tree().success(function (res) {
            var arrdata = [];
            if (res.data) {
                arrdata = res.data;
            }
            var allData = [{
                newspaper_cat_name: "所有资源",
                newspaper_cat_id: 0,
                children: arrdata
            }]
            selNode1 = $("#comTree").tree("find", $scope.selRow.newspaper_cat_id)
            $("#resTree").tree("loadData", allData);
        })
    }

    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.paper_name) {
            layer.alert("请填写标题")
            return false;
        }
        if (!$scope.selRow.paper_img) {
            layer.alert("请上传封面")
            return false;
        }
        if (!$scope.selRow.newspaper_cat_id) {
            layer.alert("请选择分类")
            return false;
        }
        if (!$scope.selRow.order_weight) {
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        if (!$scope.selRow.paper_url) {
            layer.alert("请填写链接地址")
            return false;
        }
        services._newspaper_add($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {
                        newspaper_cat_id: $scope.selRow.newspaper_cat_id,
                        parent_name: $scope.selRow.parent_name
                    };
                }
                $scope.load();
                layer.msg('信息保存成功');
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //删除
    $scope.delRow = function () {
        var newspaper_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                newspaper_ids.push($scope.tableControl.data[item.index].newspaper_id)
            }
        });
        if (newspaper_ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._newspaper_del({newspaper_ids: newspaper_ids}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功")
                        selNode = $("#comTree").tree("getSelected");
                        $scope.load();
                    }
                    else {
                        layer.msg(res.message)
                    }
                })
            })
        }
    }

    //是否推荐
    startStop = function (is_recommend, newspaper_id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.newspaper_id == newspaper_id) {
                $scope.selRow = item;
                $scope.selRow.is_recommend = is_recommend == 1 ? 2 : 1;
            }
        })
        services._newspaper_startStop($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.load()
            } else {
                layer.msg(res.message)
            }
        })
    }

    //改变排序
        order_top = function (newspaper_id, index, type, event) {
            $rootScope.stopEvent(event);
            angular.forEach($scope.tableControl.data, function (item, index) {
                if (item.newspaper_id == newspaper_id) {
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
            services._newspaper_order($scope.selRow).success(function (res) {
                if (res.code == 0) {
                    $scope.load();
                }
                else {
                    layer.msg(res.message);
                }
            })
        }

});