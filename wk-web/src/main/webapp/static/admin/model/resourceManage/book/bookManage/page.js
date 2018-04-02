var upClass;
myApp.controller('bookManageController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_book"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookOrgRel/json/getList', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_book.json', param, "POST");
    }
    //图书分类
    services["_data_book_class"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/BookCat/getTree', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_book_class.json', param, "POST");
    }
    //启用停用
    services["_up_book_state"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookOrgRel/json/updateStatus', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_book_class.json', param, "POST");
    }
    //排序置顶、热门、推荐
    //热门
    services["_order_by_book"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookOrgRel/json/updateBookOrgRel', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_book_class.json', param, "POST");
    }
    $scope.paramCol = {
        searchText: null,
        book_cat_id: null,
        book_cat_name: null,
        is_hot: -1,
        is_hot_text: "全部",
        is_recommend: -1,
        is_recommend_text: "全部",
        enabled: -1,
        enabled_text: "全部",
    }
    $scope.param = {
        book_cat_id: null,
        book_cat_name: null,
        pages: 1,
        pageSize: 10,
        pageNum: 1,
        total: 0,
        searchText: null,
        is_hot: -1,
        is_recommend: -1,
        enabled: -1
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
            $scope.param["book_cat_id"] = node.id;
            $scope.param["book_cat_name"] = node.text;
            $scope.reload();
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

    //重新查询
    $scope.reload = function (key, code, value) {
        if (key == 'is_hot') {
            $scope.paramCol.is_hot = code;
            $scope.paramCol.is_hot_text = value;
        }
        else if (key == 'is_recommend') {
            $scope.paramCol.is_recommend = code;
            $scope.paramCol.is_recommend_text = value;
        }
        else if (key == 'enabled') {
            $scope.paramCol.enabled = code;
            $scope.paramCol.enabled_text = value;
        }
        $scope.param["pageNum"] = 1;
        $scope.param["searchText"] = $scope.paramCol["searchText"];
        $scope.param["is_hot"] = $scope.paramCol["is_hot"];
        $scope.param["is_recommend"] = $scope.paramCol["is_recommend"];
        $scope.param["enabled"] = $scope.paramCol["enabled"];
        $scope.load();
    }
    $scope.tableBook = null;
    //数据加载
    $scope.load = function () {
        services._data_book($scope.param).success(function (res) {
            $scope.tableBook = res.data.rows;
            $scope.param["total"] = parseInt(res.data.total);
            $scope.param["pages"] = parseInt(res.data.pages);
            $scope.param["pageNum"] = parseInt(res.data.pageNum);
            if ($scope.param.pages > 1 || res.data.rows.length > 1) {
                //处理分页
                layui.laypage({
                    cont: "bookManagePager",
                    pages: $scope.param.pages,
                    curr: $scope.param.pageNum,
                    skip: true,
                    jump: function (resPager) {
                        if (resPager.curr != $scope.param.pageNum) {
                            $scope.param.pageNum = parseInt(resPager.curr);
                            $scope.load();
                        }
                    }
                });
            }
            else {
                $("#bookManagePager").empty();
            }
            $("#bookManagePager").append('<div class="pager_total">' +
            '<span>共' + $scope.param.pages + '页，</span>' +
            '<span>合计' + $scope.param.total + '条</span>' +
            '</div>')

        })
    }
    services._data_book_class().success(function (res) {
        var allData = [{
            book_cat_name: "所有资源",
            book_cat_id: 0,
            children: res.data
        }]
        $("#comTree").tree("loadData", allData);
    })

    /**
     * 排序置顶
     * @param book
     */
    $scope.order_top = function (book, index, type) {
        if (type == 'up') {
            var row_last = $scope.tableBook[index - 1];
            book.order_weight = parseInt(row_last.order_weight) + 1;
        }
        else if (type == 'down') {
            var row_last = $scope.tableBook[index + 1];
            book.order_weight = parseInt(row_last.order_weight) - 1;
        }
        else {
            book.order_weight = (new Date()).getTime();
        }
        services._order_by_book(book).success(function (res) {
            $scope.load();
        })
    }
    /**
     * 启用停用
     * @param book
     * @param bool
     */
    $scope.up_book_state = function (book, bool) {
        book.enabled = bool;
        var books = new Array();
        books.push(book);
        services._up_book_state({beanList: books}).success(function (res) {
            //$scope.load();
        })
    }
    /**
     * 是否热门
     * @param book
     */
    $scope.upBookHot = function (book) {
        if (book.is_hot == 1) {
            book.is_hot = 2
        } else {
            book.is_hot = 1
        }
        services._order_by_book(book).success(function () {
            //$scope.load();
        })
    }
    /**
     * 是否推荐
     * @param book
     */
    $scope.upBookRecommend = function (book) {
        if (book.is_recommend == 1) {
            book.is_recommend = 2
        } else {
            book.is_recommend = 1
        }
        services._order_by_book(book).success(function () {
            //$scope.load();
        })
    }
});