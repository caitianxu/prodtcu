var order_top;
myApp.controller('bookClassifyController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_book_class"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/BookCat/getTree', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_book_class.json', param, "POST");
    }
    //更新分类
    services["_add_book_class"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookCat/save_book_cat', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/add_book_class.json', param, "POST");
    }
    //删除
    services["_book_class_del"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookCat/delete_book_cat', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/book_class_del.json', param, "POST");
    }
    //全部同步
    services["_book_class_syn"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/static/admin/json/book_class_syn.json', param, "POST");
    }

    $scope.tableControl = {
        config: {
            check: true,
            param: {
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0,
                searchText: null
            },
            columns: [
                {field: 'book_cat_name', title: "名称", align: 'left'},
                {field: 'create_time', title: "创建时间"},
                {field: 'order_weight', title: "排序"},
                {
                    field: 'other', title: "操作", formatter: function (value, row, index, data) {
                    var action = "";
                    if (index > 0 && data.length > 1) {
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序置顶" onclick="order_top(' + row.book_cat_id + ',' + index + ',\'top\', event)"><i class="iconfont icon-dingbu"></i></a>';
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序上升" onclick="order_top(' + row.book_cat_id + ',' + index + ',\'up\', event)"><i class="iconfont icon-shangsheng1"></i></a>';
                    }
                    if (index < data.length - 1) {
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序下降" onclick="order_top(' + row.book_cat_id + ',' + index + ',\'down\', event)"><i class="iconfont icon-xiajiang"></i></a>';
                    }
                    return '<span>'+ action +'</span>';
                }
                }
            ]
        },
        reload: function (param) {
            $scope.load();
        }
    };
    /**
     * 改变排序
     * @param res_id
     * @param index
     * @param type
     */
    order_top = function (book_cat_id, index, type, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.book_cat_id == book_cat_id) {
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
        services._add_book_class($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.load();
            }
            else {
                layer.msg(res.message);
            }
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
        }
    });
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
            $scope.selRow.pid = node.id;
            $scope.selRow.parent_name = node.text;
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
            //查找未分配的分类
            var unNode = $("#resTree").tree("find", -1);
            if (unNode) {
                unNode.target.parentNode.className = "unNode";
            }
        }
    });
    //tree组装
    function changeTreeStyle1(treeNode) {
        if (!treeNode['children'] || treeNode['children'].length == 0) {
            treeNode['res_icon'] = 'tree-folder';
        }
        if (treeNode['children'] && treeNode['children'].length > 0) {
            for (var j = 0; j < treeNode['children'].length; j++) {
                changeTreeStyle1(treeNode['children'][j]);
            }
        }
        //设置属性
        treeNode["text"] = treeNode.book_cat_name;
        treeNode["id"] = treeNode.book_cat_id;
    }

    //数据加载
    $scope.load = function () {
        services._data_book_class().success(function (res) {
            var allData = [{
                book_cat_name: "所有资源",
                book_cat_id: 0,
                children: res.data
            }]
            $("#comTree").tree("loadData", allData);
        })
    }
    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
        searchText: null
    }
    //新增
    $scope.addRow = function () {
        selNode = $("#comTree").tree("getSelected");
        $scope.selRow = {
            pid: selNode.id,
            parent_name: selNode.text
        }
        $scope.status = true
        $rootScope.formOpen();
        services._data_book_class().success(function (res) {
            var allData = [{
                book_cat_name: "所有资源",
                book_cat_id: 0,
                children: res.data
            }]
            $("#resTree").tree("loadData", allData);
        })
    }
    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.book_cat_name) {
            layer.alert("请填写分类名称")
            return false;
        }
        if ($scope.selRow.pid == $scope.selRow.book_cat_id) {
            layer.alert('请选择正确的父级')
            return false;
        }
        if (!$scope.selRow.order_weight) {
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        $scope.selRow.book_cat_pid = $scope.selRow.pid;
        services._add_book_class($scope.selRow).success(function (res) {
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
                $scope.load();
                layer.msg('信息保存成功');
            }
            else {
                layer.msg(res.message);
            }
        })
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
        services._data_book_class().success(function (res) {
            var allData = [{
                book_cat_name: "所有资源",
                book_cat_id: 0,
                children: res.data
            }]
            selNode = $("#comTree").tree("getSelected");
            $("#resTree").tree("loadData", allData);

            //隐藏当前节点
            var nowNN = $("#resTree").tree("find", $scope.selRow.book_cat_id);
            $("#resTree").tree("remove", nowNN.target);
        })
    }

    //删除
    $scope.delRow = function () {
        var ids = new Array();
        var mark = null;
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].id)
            }
        });
        if (ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._book_class_del({book_cat_ids: ids, mark: mark}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功")
                        $scope.load();

                    }
                    else if (res.code == 1) {
                        layer.confirm(res.message, {
                            btn: ['确定', '取消']
                        }, function () {
                            mark = 'del'
                            services._book_class_del({book_cat_ids: ids, mark: mark}).success(function (res) {
                                if (res.code == 0) {
                                    layer.msg("删除成功")
                                    layer.closeAll();
                                    $scope.load();
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