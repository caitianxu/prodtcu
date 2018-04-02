var order_top, remove_book;
myApp.controller('recommendBooksController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //图书推荐管理列表
    services["_recommend_Manager_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/recommend/getBookList', param, "POST");
    }
    //图书推荐分类列表
    services["_recommend_cat_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/recommend/getList', param, "POST");
    }
    //图书推荐分类新增/修改
    services["_recommend_add"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/recommend/addUpdate', param, "POST");
    }
    //图书推荐分类删除
    services["_recommend_del"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/recommend/deleteCat', param, "POST");
    }
    //查询网文列表
    services["_recommend_ww_list"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/cjzww/getBookList', param, "POST");
    }
    //查询出版列表
    services["_recommend_cb_list"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookOrgRel/json/getList', param, "POST");
    }
    //分类添加图书
    services["_recommend_addBook"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/recommend/addBook', param, "POST");
    }
    //分类删除图书
    services["_recommend_removeBook"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/recommend/removeBook', param, "POST");
    }
    //排序
    services["_order_weight"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/recommend/orderWeight', param, "POST");
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
                recommend_cat_id: null,
                searchText: null
            },
            columns: [
                {
                    field: 'book_cover', title: "封面", align: 'left',
                    formatter: function (value, row, index) {
                        if (value) {
                            return '<img style="display: block;margin: 0 auto" class="df_book" src="' + value + '">'
                        }
                        return ''
                    }
                },
                {field: 'book_name', title: "名称", align: 'left'},
                {field: 'book_author', title: "作者", align: 'left'},
                {
                    field: 'book_type', title: "图书类型", formatter: function (value) {
                    if (value == 1) {
                        return "网文"
                    } else {
                        return "出版"
                    }
                }
                },
                {
                    field: 'other', title: "操作",
                    formatter: function (value, row, index) {
                        return '<div class="layui-btn layui-btn-link" style="color: #ff5722" onclick = "remove_book(' + row.id + ',' + row.recommend_cat_id + ',' + row.book_type + ',event)"><i>移除</i></div>';
                    }
                },
                {
                    field: 'other', title: "排序", formatter: function (value, row, index, data) {
                    var action = "";
                    if (index > 0 && data.length > 1) {
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序置顶" onclick="order_top(' + row.id + ',' + index + ',\'top\',event)"><i class="iconfont icon-dingbu"></i></a>';
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序上升" onclick="order_top(' + row.id + ',' + index + ',\'up\', event)"><i class="iconfont icon-shangsheng1"></i></a>';
                    }
                    if (index < data.length - 1) {
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序下降" onclick="order_top(' + row.id + ',' + index + ',\'down\', event)"><i class="iconfont icon-xiajiang"></i></a>';
                    }
                    return '<span>'+ action +'</span>';
                }
                }
            ]
        },
        reload: function (param) {
            services._recommend_Manager_Data(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };
    $scope.param = {
        searchText: null
    }
    $scope.arrdata = null;
    //加载
    $scope.load = function () {
        services._recommend_cat_Data().success(function (res) {
            if (res.data) {
                $scope.arrdata = res.data;
                if (selNode) {
                    $scope.updateClass(selNode);
                }
                else {
                    $scope.updateClass($scope.arrdata[0]);
                }
            }
        })
    }
    $scope.reload = function (txt) {
        if(txt){
            $scope.tableControl.config.param["searchText"] = txt;
        }
        else{
            $scope.tableControl.config.param["searchText"] = null;
            $scope.param.searchText = null;
        }
        $scope.tableControl.config.param["pageNum"] = 1;
        services._recommend_Manager_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }
    $scope.load();
    $scope.updateClass = function (item) {
        selNode = item;
        angular.forEach($scope.arrdata, function (item_m, index) {
            if (item.recommend_cat_id == item_m.recommend_cat_id) {
                item_m.select = true;
            }
            else {
                item_m.select = false;
            }
        })
        $scope.tableControl.config.param.recommend_cat_id = item.recommend_cat_id
        $scope.tableControl.config.param["pageNum"] = 1;
        services._recommend_Manager_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }

    //添加分类
    $scope.addRowClass = function () {
        $scope.selRow = {};
        $scope.layer_export = layer.open({
            type: 1,
            title: "新建分类",
            area: ["400px", "230px"],
            content: $("#addClassForm")
        });
    }
    var selNode
    //保存分类
    $scope.saveClass = function () {
        if (!$scope.selRow.recommend_type_name) {
            layer.msg("请填写分类名称");
            return false;
        }
        if (!$scope.selRow.recommend_code) {
            layer.msg("请填写分类编码");
            return false;
        }
        services._recommend_add($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg("分类新增成功!");
                layer.close($scope.layer_export);
                selNode = res.data;
                $scope.load();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //修改分类
    $scope.updateRowClass = function () {
        if (!selNode) {
            layer.msg("请选择你需要修改的分类！")
            return false;
        }
        $scope.selRow = selNode;
        $scope.layer_export = layer.open({
            type: 1,
            title: "修改分类",
            area: ["400px", "230px"],
            content: $("#addClassForm")
        });
    }
    //删除分类
    $scope.delRowClass = function () {
        if (!selNode) {
            layer.msg("请选择你需要删除的分类！")
            return false;
        }
        layer.confirm('你确定要删除此分类吗？', '提示', function (rrr) {
            if (rrr) {
                services._recommend_del({
                    ids: [selNode.recommend_cat_id]
                }).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功!");
                        layer.close($scope.layer_export);
                        selNode = null;
                        $scope.load();
                    }
                    else {
                        layer.msg(res.message);
                    }
                })
            }
        })
    }

    //添加图书
    $scope.addBookRow = function () {
        $scope.layer_export = layer.open({
            type: 1,
            title: "添加图书",
            area: ["700px", "500px"],
            content: $("#addBookRow")
        });
        $scope.data_book_array = [];
        $scope.param1 = {
            searchText: null,
            pageNum: 1,
            pageSize: 10
        };
    }
    $scope.searchBook1 = function () {
        $scope.param1.book_type = 1;
        services._recommend_ww_list($scope.param1).success(function (res) {
            if (res.data.total == 0) {
                layer.msg("没有找到相关图书");
                $scope.data_book_array = [];
            }
            else {
                $scope.data_book_array = res.data.rows;
            }
        })
    }
    $scope.searchBook2 = function () {
        $scope.param1.book_type = 2;
        services._recommend_cb_list($scope.param1).success(function (res) {
            if (res.data.total == 0) {
                layer.msg("没有找到相关图书");
                $scope.data_book_array = [];
            }
            else {
                $scope.data_book_array = res.data.rows;
            }
        })
    }
    //图书添加
    $scope.addBookToClass = function (book) {
        book.book_type = $scope.param1.book_type;
        book.recommend_cat_id = $scope.tableControl.config.param.recommend_cat_id;
        services._recommend_addBook(book).success(function (res) {
            if (res.code == 0) {
                layer.msg("图书《" + res.data.book_name + "》添加成功！");
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    //移除
    remove_book = function (id, cat_id, book_type) {
        var cm = layer.confirm('删除后数据无法找回,确认删除吗？', {
            btn: ['确定', '取消'] //按钮
        },function () {
            services._recommend_removeBook({
                id: id
            }).success(function (res) {
                if (res.code == 0) {
                    $scope.reload();
                }
                else {
                    layer.msg(res.message);
                }
                layer.close(cm);
            })
        })
    }
    //排序
    order_top = function (id, index, type, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.id == id) {
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
        services._order_weight($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

});