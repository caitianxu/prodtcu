var updateEnabled, order_top;
myApp.controller('discountBooksController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //图书折扣列表
    services["_discount_book_data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/discount/getList', param, "POST");
    }
    //查询网文列表
    services["_recommend_ww_list"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/cjzww/getBookList', param, "POST");
    }
    //查询出版列表
    services["_recommend_cb_list"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookOrgRel/json/getList', param, "POST");
    }
    //添加图书
    services["_discount_addBook"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/discount/addBook', param, "POST");
    }
    //修改图书
    services["_discount_editBook"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/discount/update', param, "POST");
    }
    //修改图书
    services["_discount_updateOrder"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/discount/order', param, "POST");
    }
    //删除图书
    services["_discount_delBook"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/discount/delete', param, "POST");
    }
    //启用停用
    services["_discount_enabledBook"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/discount/enabled', param, "POST");
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
                channel_type: 1,
                searchText: null
            },
            columns: [
                {
                    field: 'book_cover', title: "封面",
                    formatter: function (value, row, index) {
                        if (value) {
                            return '<img style="display: block;margin: 0 auto" class="df_book" src="' + value + '">'
                        }
                        return ''
                    }
                },
                {field: 'book_name', title: "书名", align: 'left'},
                {field: 'book_author', title: "作者", align: 'left'},
                {field: 'price', title: "原价", align: 'right',formatter: function (value, row, index) {
                    if(value){
                        return '<s style="color: #999">￥'+ value +'</s>'
                    }
                    else{
                        return '';
                    }
                }},
                {field: 'discount_price', title: "折扣价", align: 'right',formatter: function (value, row, index) {
                    if((value != undefined && value != null && value != "") || value == "0")
                        return '<em style="color: #f63;font-weight: bold;font-size: 16px;">￥'+ value +'</em>'
                    else
                        return '';
                }},
                {field: 'enabled', title: "状态",formatter: function (value, row, index) {
                    var bool = row.enabled == 1 ? 'layui-form-onswitch' : '';
                    return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="updateEnabled(' + row.id + ', event)"><i></i></div>';
                }},
                {field: 'start_time', title: "开始时间"},
                {field: 'end_time', title: "截至时间"},
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
            services._discount_book_data(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };
    $scope.param = {
        channel_type: 1,
        searchText: null
    }
    $scope.load = function () {
        // scope.tableControl.config.param["pageNum"] = 1;
        $scope.tableControl.reload($scope.tableControl.config.param);
    }
    $scope.reload = function (type, txt) {
        if (type) {
            $scope.param.channel_type = type;
            $scope.tableControl.config.param.channel_type = type;
        }
        if (txt) {
            $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        }
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.tableControl.reload($scope.tableControl.config.param);
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
        $scope.param1.channel_type = 1;
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
        $scope.param1.channel_type = 2;
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
    $scope.searchBook3 = function () {
        $scope.param1.channel_type = 3;
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
        book.channel_type = $scope.param1.channel_type;
        book.recommend_cat_id = $scope.tableControl.config.param.recommend_cat_id;
        services._discount_addBook(book).success(function (res) {
            if (res.code == 0) {
                layer.msg("图书《" + res.data.book_name + "》添加成功！");
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    //修改
    $scope.editBookRow = function () {
        var ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].id)
                $scope.selRow = $scope.tableControl.data[item.index];
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

        $scope.layer_export = layer.open({
            type: 1,
            title: "编辑图书",
            area: ["400px", "370px"],
            content: $("#editBookForm")
        });
    }
    //修改图书
    $scope.saveBook = function () {
        if($scope.selRow.channel_type == 3){
            if(($scope.selRow.discount_price == null || $scope.selRow.discount_price =="" || isNaN($scope.selRow.discount_price)) && $scope.selRow.discount_price != 0){
                layer.msg("请填写折扣价格!")
                return false;
            }
        }else{
            $scope.selRow.discount_price =0;
        }
        if(!$scope.selRow.start_time){
            layer.msg('请填写开始时间');
            return false;
        }
        if(!$scope.selRow.end_time){
            layer.msg('请填写截至时间')
            return false;
        }
        $scope.selRow.start_time = $scope.selRow.start_time + ':00'
        $scope.selRow.end_time = $scope.selRow.end_time + ':00'
        services._discount_editBook($scope.selRow).success(function (res) {
            if(res.code == 0){
                layer.msg("折扣变更成功!");
                layer.close($scope.layer_export);
                $scope.reload();
            }
            else{
                layer.msg(res.message);
            }
        })
    }
    //删除图书
    $scope.delBookRow = function () {
        var ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].id);
            }
        });
        layer.confirm("你确定要删除这些数据吗?","提示",function () {
            services._discount_delBook({
                ids: ids
            }).success(function (res) {
                if(res.code == 0){
                    layer.msg("数据删除成功!");
                    layer.close($scope.layer_export);
                    $scope.reload();
                }
                else{
                    layer.msg(res.message);
                }
            })
        })
    }
    updateEnabled = function (id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.id == id) {
                $scope.selRow = item;
            }
        });
        if($scope.selRow.enabled == 2){
            if(isNaN($scope.selRow.discount_price) || $scope.selRow.discount_price == null || $scope.selRow.discount_price == "" ){
                if($scope.selRow.discount_price != 0){
                    layer.msg("未设置折扣价格无法启用");
                    return false;
                }
            }
            if(!$scope.selRow.start_time){
                layer.msg("未设置开始时间无法启用");
                return false;
            }
            if(!$scope.selRow.end_time){
                layer.msg("未设置截至时间无法启用");
                return false;
            }
            $scope.selRow.start_time = $scope.selRow.start_time + ':00'
            $scope.selRow.end_time = $scope.selRow.end_time + ':00'
        }
        if ($scope.selRow.enabled == 1) {

            $scope.selRow.enabled = 2;
        } else {
            $scope.selRow.enabled = 1;
        }
        services._discount_enabledBook($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message);
                $scope.load();
            }
            else {
                layer.msg(res.message);
            }
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
        services._discount_updateOrder($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.load();
            }
            else {
                layer.msg(res.message);
            }
        })
    }
});