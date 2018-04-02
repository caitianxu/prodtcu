var rowRemove, order_top, startStop;
myApp.controller('dataPackageInfoController', function ($rootScope, $scope, services, $sce, $stateParams, $state) {
    if (!$stateParams || !$stateParams.id || !$stateParams.name) {
        $state.go("dataPackage");
    }
    $scope.services = services;
    //标签列表树形结构
    services["_book_tag_tree"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookTag/tagTree', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/add_book_tab.json', param, "POST");
    }
    //标签列表树形结构-数量
    services["_book_tag_tree_count"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookTag/tagBookCount', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/add_book_tab.json', param, "POST");
    }
    //数据分类书列表
    services["_data_package_book"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/pkgBookRel/json/getList', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_package_book.json', param, "POST");
    }
    //图书分类
    services["_pkg_class"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/pkgBookCat/getTree', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/pkg_class.json', param, "POST");
    }
    //图书分类保存
    services["_save_pkg_class"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/pkgBookCat/save_book_cat', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/save_pkg_class.json', param, "POST");
    }
    //图书分类删除
    services["_del_pkg_class"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/pkgBookCat/delete_book_cat', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/save_pkg_class.json', param, "POST");
    }
    //所有标签
    services["_get_all_tag"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookTag/json/getAllList', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/get_all_tag.json', param, "POST");
    }
    //添加图书
    services["_add_book_to_tag"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/pkgBookRel/json/savePkgBookRel', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/get_all_tag.json', param, "POST");
    }
    //删除图书
    services["_del_book_to_tag"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/pkgBookRel/json/removePkgBookRel', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/get_all_tag.json', param, "POST");
    }
    //批量删除图书
    services["_del_books_to_tag"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/pkgBookRel/json/removePkgBookRels', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/get_all_tag.json', param, "POST");
    }
    //热门、推荐、离线、排序
    services["_up_book_status"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/pkgBookRel/json/updatePkgBookRel', param, "POST");
    }
    //图书批量置顶
    services["_top_books"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/dataPackage/json/topBooks', param, "POST");
    }
    //图书搜索查询
    services["_book_repo_search"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookrepo/json/getList', param, "POST");
    }
    //数据包单本加书
    services["_add_book_pkg"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/pkgBookRel/json/pkgInsertBook', param, "POST");
    }


    $scope.param = {
        pkg_id: $stateParams.id,
        pkg_name: $stateParams.name,
        searchText: null,
        is_hot: -1,
        is_recommend: -1,
        offline_status: -1,
        is_hot_text: "全部",
        is_recommend_text: "全部",
        offline_status_text: "全部"
    }
    var selNode = null, selNode1 = null;
    //图书分类
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

    //数据分类
    $scope.getBookClass = function () {
        services._pkg_class($scope.param).success(function (res) {
            var allData = [{
                book_cat_name: "所有资源",
                book_cat_id: 0,
                children: res.data ? res.data : []
            }]
            $("#comTree").tree("loadData", allData);
        })
    }
    $scope.getBookClass();


    $("#dataPackageInfo .content_page_left").height(document.documentElement.clientHeight - 145);
    $(window).resize(function () {
        $("#dataPackageInfo .content_page_left").height(document.documentElement.clientHeight - 145);
    });

    /**
     * 返回数据包
     */
    $scope.backPage = function () {
        $state.go("dataPackage");
    }
    $scope.tableControl = {
        config: {
            check: true,
            param: {
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0,
                searchText: null,
                pkg_id: $stateParams.id,
                pkg_name: $stateParams.name,
                book_cat_id: 0,
                is_hot: -1,
                is_recommend: -1,
                offline_status: -1
            },
            columns: [
                {
                    field: 'book_cover_small', title: "封面", formatter: function (val) {
                    if (val) {
                        return '<img src="' + $rootScope.ctxPath + val + '" class="df_book" style="display: block;margin: 0px auto"/>'
                    }
                    return ''
                }
                },
                {field: 'book_isbn', title: "ISBN", align: 'left'},
                {field: 'book_name', title: "书名", align: 'left'},
                {field: 'book_author', title: "作者", align: 'left'},
                {field: 'tag_names', title: "所属标签", align: 'left'},
                {
                    field: 'is_hot', title: "是否热门", formatter: function (value, row, index) {
                    var bool = row.is_hot == 1 ? 'layui-form-onswitch' : '';
                    return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.book_cat_id + ',' + row.book_id + ',' + '\'is_hot\'' + ', event)"><i></i></div>';
                }
                },
                {
                    field: 'is_recommend', title: "是否推荐", formatter: function (value, row, index) {
                    var bool = row.is_recommend == 1 ? 'layui-form-onswitch' : '';
                    return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.book_cat_id + ',' + row.book_id + ',' + '\'is_recommend\'' + ', event)"><i></i></div>';
                }
                },
                {
                    field: 'offline_status', title: "是否离线", formatter: function (value, row, index) {
                    var bool = row.offline_status == 1 ? 'layui-form-onswitch' : '';
                    return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.book_cat_id + ',' + row.book_id + ',' + '\'offline_status\'' + ', event)"><i></i></div>';
                }
                },
                {
                    field: 'other', title: "操作", width: '200', align: 'left',
                    formatter: function (value, row, index, data) {
                        var action = '<a href="javascript:void(0)"  class="btn-icon" onclick="rowRemove(' + row.book_id + "," + row.book_cat_id + "," + row.org_id + ', event)"><i class="iconfont icon-shanchu"></i></a>';
                        if (index == 0 && $scope.tableControl.config.param.pageNum > 1) {
                            action += '<a href="javascript:void(0)" class="btn-icon" title="排序置顶" onclick="order_top(' + row.org_id + ',' + row.book_cat_id + ',' + row.book_id + ',' + index + ',\'top\', event)"><i class="iconfont icon-dingbu"></i></a>';
                        }
                        if (index > 0 && data.length > 1) {
                            action += '<a href="javascript:void(0)" class="btn-icon" title="排序置顶" onclick="order_top(' + row.org_id + ',' + row.book_cat_id + ',' + row.book_id + ',' + index + ',\'top\', event)"><i class="iconfont icon-dingbu"></i></a>';
                            action += '<a href="javascript:void(0)" class="btn-icon" title="排序上升" onclick="order_top(' + row.org_id + ',' + row.book_cat_id + ',' + row.book_id + ',' + index + ',\'up\', event)"><i class="iconfont icon-shangsheng1"></i></a>';
                        }
                        if (index < data.length - 1) {
                            action += '<a href="javascript:void(0)" class="btn-icon" title="排序下降" onclick="order_top(' + row.org_id + ',' + row.book_cat_id + ',' + row.book_id + ',' + index + ',\'down\', event)"><i class="iconfont icon-xiajiang"></i></a>';
                        }
                        return '<span style="width: 150px;float:right;">'+ action +'</span>';
                    }
                }
            ]
        },
        reload: function (param) {
            $scope.load();
        }
    };

    /**
     * 数据加载
     */
    $scope.load = function () {
        var node = $("#comTree").tree("getSelected");
        if (node) {
            $scope.tableControl.config.param["book_cat_id"] = node.id;
            services._data_package_book($scope.tableControl.config.param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    }
    /**
     * 重新加载
     */
    $scope.reload = function (key, code, value) {
        if (key == 'is_hot') {
            $scope.param.is_hot = code;
            $scope.param.is_hot_text = value;
        }
        else if (key == 'is_recommend') {
            $scope.param.is_recommend = code;
            $scope.param.is_recommend_text = value;
        }
        else if (key == 'offline_status') {
            $scope.param.offline_status = code;
            $scope.param.offline_status_text = value;
        }
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["is_hot"] = $scope.param.is_hot;
        $scope.tableControl.config.param["is_recommend"] = $scope.param.is_recommend;
        $scope.tableControl.config.param["offline_status"] = $scope.param.offline_status;
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.load();
    }
    /**
     * 新增数据行
     */
    $scope.addRow = function () {
        $scope.selRow = {
            pid: $("#comTree").tree("getSelected").id,
            pname: null
        }
        $rootScope.layerfunTree = layer.open({
            type: 1,
            title: "新增分类",
            area: ["400px", "270px"],
            content: $("#classForm")
        });
        services._pkg_class($scope.param).success(function (res) {
            var allData = [{
                book_cat_name: "所有资源",
                book_cat_id: 0,
                children: res.data ? res.data : []
            }]
            $("#resTree").tree("loadData", allData);
        })
    }

    //表单分类树
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
            $(".layui-form-selected").removeClass("layui-form-selected");
            if (!$scope.$$phase) {
                $scope.$apply(function () {
                    $scope.selRow["pname"] = node.text;
                    $scope.selRow["pid"] = node.id;
                })
            }
            else {
                $scope.selRow["pname"] = node.text;
                $scope.selRow["pid"] = node.id;
            }
        },
        onLoadSuccess: function (node, data) {
            var boolNode = $("#resTree").tree("find", $scope.selRow.book_cat_pid);
            selNode1 = boolNode ? boolNode : $("#resTree").tree("getRoot");
            $("#resTree").tree("select", selNode1.target);
        }
    });
    //保存
    $scope.saveForm = function () {
        if (!$scope.selRow.book_cat_name) {
            layer.alert("请填写分类名称")
            return false;
        }
        $scope.selRow.book_cat_pid = $scope.selRow.pid;
        $scope.selRow.pkg_id = $scope.param.pkg_id;
        if ($scope.selRow.book_cat_id == $scope.selRow.book_cat_pid) {
            layer.alert('请选择正确的父级')
            return false;
        }
        if (!$scope.selRow.order_weight) {
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        services._save_pkg_class($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg("新增分类成功")
                $scope.getBookClass();
                layer.closeAll();
            }
            else {
                layer.alert(res.message)
            }
        })
    }
    /**
     * 修改分类
     */
    $scope.row_update = function () {
        $scope.selRow = $("#comTree").tree("getSelected");
        if ($scope.selRow.id != 0) {
            $rootScope.layerfunTree = layer.open({
                type: 1,
                title: "修改分类",
                area: ["400px", "270px"],
                content: $("#classForm")
            });

            services._pkg_class($scope.param).success(function (res) {
                var allData = [{
                    book_cat_name: "所有资源",
                    book_cat_id: 0,
                    children: res.data ? res.data : []
                }]
                $("#resTree").tree("loadData", allData);


                //隐藏当前节点
                var nowNN = $("#resTree").tree("find", $scope.selRow.book_cat_id);
                $("#resTree").tree("remove", nowNN.target);
            })
        }
        else {
            layer.alert("根节点不允许修改")
        }
    }

    /**
     * 删除数据
     */
    $scope.delRow = function () {
        var catids = new Array();
        $scope.selRow = $("#comTree").tree("getSelected");
        if ($scope.selRow.id != 0) {
            layer.confirm("删除分类将分类下的图书划分一起删除，确定继续操作吗？", "提示", function () {
                catids.push($scope.selRow.id)
                layer.closeAll();
                services._del_pkg_class({
                    book_cat_ids: catids,
                    pkg_id: $stateParams.id,
                    pkg_name: $stateParams.name
                }).success(function (res) {
                    if (res.code == 0) {
                        $scope.getBookClass();
                    }
                    else {
                        layer.alert(res.message);
                    }
                })
            })
        }
        else {
            layer.alert("根节点不允许删除")
        }
    }
    /**
     * 批量删除图书
     */
    $scope.delBook = function () {
        var books = new Array();
        var bookids = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                var book = {
                    "book_cat_id": $scope.tableControl.data[index].book_cat_id,
                    "book_id": $scope.tableControl.data[index].book_id
                }
                books.push(book)
            }
        });
        if (books.length > 0) {
            layer.confirm("你确定要将这些图书移除数据包吗?", "提示", function () {
                services._del_books_to_tag({
                    pkg_id: $stateParams.id,
                    pkg_name: $stateParams.name,
                    books: books,
                    book_id: bookids
                }).success(function (res) {
                    if (res.code == 0) {
                        $scope.load();
                        layer.closeAll();
                    }
                    else {
                        layer.alert(res.message);
                    }
                })
            })
        }
        else {
            layer.alert("请选择需要删除的图书")
        }
    }

    $scope.bookTagArray = null;

    /**
     * 添加图书
     */
    $scope.searchTags = {
        searchText: null
    }
    var tag_tree_data = null;
    var tag_tree_data_count = {};
    $scope.addBook = function () {
        // $scope.reload_tag();
        // $rootScope.layerfunTree = layer.open({
        //     type: 1,
        //     title: "添加图书",
        //     area: ["600px", "420px"],
        //     content: $("#tag_add_book")
        // });
        $scope.books.cat_id = $("#comTree").tree("getSelected").id;
        //判断是否有选择分类
        if (!$scope.books.cat_id) {
            layer.alert("请选择需要添加图书的分类!");
            return false;
        }
        if (tag_tree_data == null) {
            services._book_tag_tree_count().success(function (rcc) {
                tag_tree_data_count = rcc.data;
                services._book_tag_tree().success(function (res) {
                    var arrdata = [];
                    if (res.data) {
                        arrdata = res.data;
                        tag_tree_data = res.data;
                    }
                    var allData = [{
                        tag_name: "所有资源",
                        tag_id: 0,
                        children: arrdata
                    }]
                    $("#tagTree").tree("loadData", allData);
                })
            })
        }
        $rootScope.formOpen();
    }
    //标签结构树
    $("#tagTree").tree({
        //数据过滤
        "checkbox": true,
        "lines": true,
        "loadFilter": function (data, parent) {
            for (var i = 0; i < data.length; i++) {
                changeTreeStyle2(data[i]);
            }
            return data;
        },
        "onSelect": function (node) {
            selNode = node;
        },
        onLoadSuccess: function (node, data) {
            var nodes = $('#tagTree').tree("getChildren", $("#tagTree").tree("getRoot").target)
            for (var i = 0; i < nodes.length; i++) {
                //循环所有二级节点然后关闭
                $("#tagTree").tree('collapse', nodes[i].target);
            }
            //展开所有父节点
            expandParent(selNode);
            //更新所有节点数量
            countTreeNum($("#tagTree").tree("getRoot"));
        }
    });
    //更新所有节点数量
    function countTreeNum(node) {
        var num = 0;
        if (node) {
            var childNum = parseInt(node.attributes);
            if (node.children && node.children.length > 0) {
                for (var i = 0; i < node.children.length; i++) {
                    childNum += countTreeNum(node.children[i]);
                }
            }
            $('#tagTree').tree('update', {
                target: node.target,
                text: node.text + "<s>(" + childNum + ")</s>",
                attributes: childNum
            });
            num = childNum;
        }
        return num;
    }

    function expandParent(node) {
        var parent = $("#tagTree").tree("getParent", node.target);
        if (parent) {
            $("#tagTree").tree('expand', parent.target);
            expandParent(parent);
        }
    }

    //tree节点改变
    function changeTreeStyle2(treeNode) {
        if (!treeNode['children'] || treeNode['children'].length == 0) {
            treeNode['res_icon'] = 'tree-folder';
        }
        if (treeNode['children'] && treeNode['children'].length > 0) {
            for (var j = 0; j < treeNode['children'].length; j++) {
                changeTreeStyle2(treeNode['children'][j]);
            }
        }
        //设置属性
        treeNode["text"] = treeNode.tag_name;
        treeNode["id"] = treeNode.tag_id;
        //获取值
        var count = tag_tree_data_count[treeNode.tag_id];
        treeNode["attributes"] = count ? count : 0;
    }

    //搜索加书
    $scope.searchBooks = {
        searchText: null,
        pageNum: 1,
        pageSize: 20,
    }
    $scope.searchBook_add = function () {
        $scope.bookRepoArray = {};
        $scope.searchBooks.searchText = null;
        $rootScope.layerfunTree = layer.open({
            type: 1,
            title: "图书搜索",
            area: ["700px", "700px"],
            content: $("#searchBook_add")
        });
    }
    $scope.bookRepoArray = {};
    //搜索图书
    $scope.searchData = function () {
        if ($scope.searchBooks.searchText == "" || $scope.searchBooks.searchText == null) {
            layer.alert("搜索内容不可为空")
            return false
        }
        services._book_repo_search($scope.searchBooks).success(function (res) {
            $scope.bookRepoArray = res.data.rows
        })

    }
    $scope.add_book_to_pkg = {
        book_id: null,
        book_cat_id: null,
        pkg_id: null
    }

    //添加图书到数据包
    $scope.add_book = function (row) {
        $scope.add_book_to_pkg.book_id = row.book_id;
        $scope.add_book_to_pkg.book_cat_id = $("#comTree").tree("getSelected").id;
        $scope.add_book_to_pkg.pkg_id = $scope.param.pkg_id;

        services._add_book_pkg($scope.add_book_to_pkg).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message)
            }
            else {
                layer.msg(res.message)
            }
        })
    }

    $scope.books = {
        book_ids: "",
        cat_id: 0
    };
    //图书批量置顶
    $scope.topBook = function () {
        $scope.books.book_ids = null;
        $rootScope.layerfunTree = layer.open({
            type: 1,
            title: "请填写图书id，用逗号隔开",
            area: ["600px", "600px"],
            content: $("#top_book")
        });
    }
    $scope.book_ids = function () {
        //批量置顶提交
        $scope.books.cat_id = $("#comTree").tree("getSelected").id;
        if (!$scope.books.cat_id) {
            layer.alert("请选择分类，该功能只能同时置顶一个分类下面的图书!");
            return false;
        }
        if (!$scope.books.book_ids) {
            layer.alert("图书id不可为空")
            return false
        }
        services._top_books($scope.books).success(function (res) {
            if (res.code == 0) {
                layer.alert(res.message + "失败id：" + res.data)
            }
            else {
                layer.msg(res.message)
            }
        })
    }

    //重新查询
    $scope.reload_tag = function () {
        services._get_all_tag($scope.searchTags).success(function (res) {
            $scope.bookTagArray = res.data;
        })
    }


    /**
     * 加书提交
     */
    $scope.checkBook = function () {
        var tag_ids = [];
        var nodes = $("#tagTree").tree("getChecked");
        if(nodes && nodes.length > 0){
            for(var i=0;i<nodes.length;i++){
                if(!nodes[i].children || nodes[i].children.length == 0){
                    tag_ids.push(nodes[i].tag_id)
                }
            }
        }
        if (tag_ids.length == 0) {
            layer.alert("请选择标签")
            return false;
        }
        else {
            services._add_book_to_tag({
                book_cat_id: $("#comTree").tree("getSelected").id,
                pkg_id: $stateParams.id,
                pkg_name: $stateParams.name,
                tag_ids: tag_ids
            }).success(function (res) {
                if (res.code == 0) {
                    layer.msg("图书添加成功");
                    layer.closeAll();
                    $scope.reload();
                }
                else {
                    layer.alert(res.message);
                }
            })
        }
    }

    /**
     * 移除
     */
    rowRemove = function (id, cat_id, org_id, event) {
        $rootScope.stopEvent(event);
        services._del_book_to_tag({
            book_cat_id: cat_id,
            pkg_id: $stateParams.id,
            pkg_name: $stateParams.name,
            book_id: id,
            org_id: org_id
        }).success(function (res) {
            if (res.code == 0) {
                $scope.load();
            }
            else {
                layer.alert(res.message);
            }
        })
    }
    /**
     * 排序置顶
     */
    order_top = function (org_id, cat_id, id, index, type, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.book_cat_id == cat_id && item.book_id == id) {
                $scope.selRow = item;
            }
        });
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
        services._up_book_status($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.load();
            }
            else {
                layer.alert(res.message);
            }
        })
    }

    startStop = function (book_cat_id, book_id, type, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.book_cat_id == book_cat_id && item.book_id == book_id) {
                $scope.selRow = $scope.tableControl.data[index];
                if (type == 'is_hot') {
                    $scope.selRow.is_hot = $scope.selRow.is_hot == 1 ? 2 : 1
                } else if (type == 'is_recommend') {
                    $scope.selRow.is_recommend = $scope.selRow.is_recommend == 1 ? 2 : 1
                } else if (type == 'offline_status') {
                    $scope.selRow.offline_status = $scope.selRow.offline_status == 1 ? 0 : 1
                }
            }
        });
        services._up_book_status($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.load();
            }
            else {
                layer.msg(res.message);
            }
        })
    }
});
