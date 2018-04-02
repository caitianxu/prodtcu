var toBookList, excelDown, upBookTag, upBookTag1;
myApp.controller('bookTabController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_book_tab"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookTag/json/getList', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_book_tab.json', param, "POST");
    }
    //标签列表树形结构-数量
    services["_book_tag_tree_count"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookTag/tagBookCount', param, "POST");
    }
    //新增标签
    services["_add_book_tab"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookTag/json/updateBookTag', param, "POST");
    }
    //删除标签
    services["_del_book_tab"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookTag/json/deleteBookTag', param, "POST");
    }
    //书加标签
    services["_data_book_tab_all"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookrepo/json/getTagBookList', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_book_tab_all.json', param, "POST");
    }
    //修改书标签
    services["_up_book_tab"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookTag/json/updateBookTagRel', param, "POST");
    }

    //标签列表树形结构
    services["_book_tag_tree"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookTag/tagTree', param, "POST");
    }
    //新增修改标签
    services["_add_book_tag"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookTag/saveUpdateTag', param, "POST");
    //return $rootScope.serverAction(ctxPath + '/static/admin/json/add_book_tab.json', param, "POST");

    }
    //获取所有没有标签的图书列表
    services["_data_NoTag_books"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookTag/noTagBook', param, "POST");
    }


    $scope.tableControl = {
        config: {
            check: true,
            param: {
                pages: 1,
                pageSize: 50,
                pageNum: 1,
                total: 0,
                searchText: null
            },
            columns: [
                {field: 'tag_code', title: "编码"},
                {field: 'tag_name', title: "名称", align: 'left'},
                {
                    field: 'attributes', title: "相关图书", formatter: function (value, row) {
                    return '<a href="javascript:void(0)" onclick="toBookList(' + row.tag_id + ',\'' + row.tag_name + '\', event)">' + row.attributes + '本</a>'
                }
                }, {
                    field: 'other', title: "操作", formatter: function (value, row) {
                        return '<a href="javascript:void(0)" onclick="excelDown(' + row.tag_id + ',event)">数据下载</a>'
                    }
                }
            ]
        },
        reload: function (param) {
            $scope.load();
        }
    };

    var tag_tree_data_count = {};
    /**
     * 数据加载
     */
    $scope.load = function () {
        services._book_tag_tree_count().success(function (rcc) {
            tag_tree_data_count = rcc.data;
            services._book_tag_tree().success(function (res) {
                var arrdata = [];
                if (res.data) {
                    arrdata = res.data;
                }
                var allData = [{
                    tag_name: "所有资源",
                    tag_id: 0,
                    children: arrdata
                }]
                $("#comTree").tree("loadData", allData);

            })
        })
    }
    /**
     * excel下载
     * @param e
     */
    excelDown = function (tag_id, event) {
        $rootScope.stopEvent(event);
        //创建下载对象
        var iframe = $('<iframe style="display: none" src="' + $rootScope.ctxPath + '/admin/bookrepo/json/exportTagBook?id=' + tag_id + '"></iframe>');
        $("#dataTab").append(iframe);
    }
    /**
     * 页面参数
     * @type {{searchText: null}}
     */
    $scope.param = {
        searchText: null
    }
    /**
     * 重新加载
     */
    $scope.reload = function () {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.load();
    }

    /**
     * 新增分类
     */
    $scope.addRow = function () {
        selNode.text = "";
        $scope.selRow = {
            tag_pid: selNode.id,
            parent_name: selNode.text
        }
        $scope.status = true;
        $rootScope.formOpen();
        services._book_tag_tree().success(function (res) {

            var arrdata = [];
            if (res.data) {
                arrdata = res.data;
            }
            var allData = [{
                tag_name: "所有资源",
                tag_id: 0,
                children: arrdata
            }]
            selNode = $("#comTree").tree("getSelected");

            $("#resTree").tree("loadData", allData);
            var nodes = $('#resTree').tree("getChildren", $("#resTree").tree("getRoot").target)
            for (var i = 0; i < nodes.length; i++) {
                //循环所有二级节点然后关闭
                $("#resTree").tree('collapse', nodes[i].target);
            }
        })


    }
    /**
     * 保存
     * @param b
     * @private
     */
    $scope._form_submit = function (b) {
        if (!$scope.selRow || !$scope.selRow.tag_name) {
            layer.alert("请填写标签名称");
            return false;
        }
        if (!$scope.selRow.order_weight) {
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        services._add_book_tag($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg("标签更新成功!")
                $scope.load();
                if (b) {
                    $scope.formClose();
                }
                else {
                    $scope.selRow = null;
                }
            }
            else {
                layer.alert(res.message)
            }
        })
    }

    /**
     * 修改
     */
    $scope.editRow = function () {

        $scope.selRow = null;
        var ids = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push(item);
                $scope.selRow = $scope.tableControl.data[index]
                $scope.selRow['type'] = 'update';
            }
        });
        if (ids.length > 1) {
            layer.alert("同时只能修改一条数据")
            return false;
        }
        if (!$scope.selRow) {
            layer.alert("请选择要修改的数据行")
            return false;
        }
        $scope.status = false;
        services._book_tag_tree().success(function (res) {
            var arrdata = [];
            if (res.data) {
                arrdata = res.data;
            }
            var allData = [{
                tag_name: "所有资源",
                tag_id: 0,
                children: arrdata
            }]
            selNode = $("#comTree").tree("getSelected");
            $("#resTree").tree("loadData", allData);
            var nodes = $('#resTree').tree("getChildren", $("#resTree").tree("getRoot").target)
            for (var i = 0; i < nodes.length; i++) {
                //循环所有二级节点然后关闭
                $("#resTree").tree('collapse', nodes[i].target);
            }

            //隐藏当前节点
            var nowNN = $("#resTree").tree("find", $scope.selRow.tag_id);
            $("#resTree").tree("remove", nowNN.target);
        })
        $rootScope.formOpen();
    }

    /**
     * 删除
     */
    $scope.delRow = function () {
        var ids = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[index].tag_id)
            }
        });
        if (ids.length == 0) {
            layer.alert("请选择你要删除的数据")
            return false;
        }
        services._del_book_tab({
            ids: ids
        }).success(function (res) {
            if (res.code == 0) {
                layer.msg('数据已删除成功!')
                $scope.reload();
            }
            else {
                layer.alert(res.message)
            }
        })
    }

    /**
     * 标签加图书
     */
    $scope.tab_add_book = function () {
        var ids = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[index]);
            }
        });
        if (ids.length != 1) {
            layer.alert("请选择一条标签记录，并且只能选择一条");
            return false;
        }
        $scope.tagTableControl.config.param["tag_id"] = ids[0].tag_id;
        control_tag.reload_tag();
        $scope.layer_export = layer.open({
            type: 1,
            title: ids[0].tag_name,
            area: ["1000px", "600px"],
            content: $("#tab_add_book")
        });
    }


    ////////////////////*******标签加书********////////////////////////
    //无标签图书列表
    $scope.noTagBook = {
        searchText: null,
        pageNum: 1,
        pageSize: 10
    }
    $scope.bookRepoArray = {};
    $scope.no_tag_books = function () {
        $rootScope.layerfunTree = layer.open({
            type: 1,
            title: "无标签图书列表",
            area: ["800px", "600px"],
            content: $("#no_tag_books")

        });

        services._data_NoTag_books($scope.noTagBook).success(function (res) {
            $scope.bookRepoArray = res.data.rows
        })
    }


    var control_tag = null;
    //机构列表
    $scope.tagTableControl = {
        config: {
            param: {
                pageNum: 1, //当前页
                pageSize: 10, //每页条数
                searchText: null,
                bool: false
            },
            columns: [
                {
                    field: 'book_cover_small', title: "封面", formatter: function (value) {
                    if (value) {
                        return '<img src="' + $rootScope.ctxPath + value + '" style="width: 80px;display: block;margin: 0 auto;">'
                    }
                    return ''

                }
                },
                {field: 'book_name', title: "名称", align: 'left'},
                {field: 'book_isbn', title: "ISBN", align: 'left'},
                {field: 'book_author', title: "作者"},
                {field: 'tag_names', title: "所属标签"},
                {
                    field: 'tag_whether', title: "操作", formatter: function (value, row) {
                    return '<a href="javascript:void(0)" onclick="upBookTag(true,' + row.book_id + ', event)">添加当前标签</a>'
                }
                }
            ]
        },
        reload: function (param) {
            $scope.services._data_book_tab_all(param).success(function (res) {
                control_tag.loadData(res.data);
            })
        }
    }
    control_tag = $scope.tagTableControl;
    //数据加载
    control_tag.loadData = function (data) {
        control_tag.data = data.rows ? data.rows : data.children;
        control_tag.config.param["total"] = parseInt(data.total);
        control_tag.config.param["pages"] = parseInt(data.pages);
        control_tag.config.param["pageNum"] = parseInt(data.pageNum);
        control_tag.config.pagerNumCol = parseInt(data.pageNum);
        control_tag.rows = [];
        control_tag.config.selectAll = false;
        angular.forEach(control_tag.data, function (rowdata, rowindex) {
            var row = [];
            angular.forEach(control_tag.config.columns, function (item, index) {
                var filed = rowdata[item.field];
                if (filed == null || filed == undefined) {
                    filed = "";
                }
                var col = {};
                for (var i in item) {
                    col[i] = item[i];
                }
                if (item.formatter) {
                    filed = item.formatter(filed, rowdata, rowindex)
                }
                col.value = $scope._trustAsHtml('<span>' + filed + '</span>');
                row.push(col);
            })
            control_tag.rows.push({
                index: rowindex,
                data: row,
                select: false,
                source: rowdata
            });
        })
        if (control_tag.config.param.pages > 1 || control_tag.rows.length > 1) {
            layui.laypage({
                cont: "tab_add_book_pager",
                pages: control_tag.config.param.pages,
                curr: control_tag.config.param.pageNum,
                skip: true,
                jump: function (resPager) {
                    if (resPager.curr != control_tag.config.param.pageNum) {
                        control_tag.config.param.pageNum = parseInt(resPager.curr);
                        if (control_tag.reload)
                            control_tag.reload(control_tag.config.param);
                    }
                }
            });
        }
        else {
            $("#tab_add_book_pager").empty();
        }
    }

    //自动初始化
    control_tag.init = function () {
        angular.forEach(control_tag.config.columns, function (item, index) {
            item.align = !item.align ? 'center' : item.align;
            item.width = !item.width ? 'auto' : item.width;
            item.orderType = !item.orderType ? 'desc' : item.orderType;
        })
        if (control_tag.data) {
            control_tag.loadData(control_tag.data);
        }
        else {
            //control_tag.reload(control_tag.config.param);
        }
    }
    //重新查询
    control_tag.reload_tag = function () {
        control_tag.config.param["searchText"] = control_tag.tag_searchText;
        control_tag.config.param["pageNum"] = 1;
        control_tag.reload(control_tag.config.param);
    }
    control_tag.init();
    ////////////////////*******标签加书********////////////////////////

    /**
     * 添加删除标签
     * @param b
     * @param id
     */
    upBookTag = function (b, id, event) {
        $rootScope.stopEvent(event);
        services._up_book_tab({
            tag_id: control_tag.config.param.tag_id,
            book_id: id,
            bool: b
        }).success(function (res) {
            if (res.code == 0) {
                control_tag.reload(control_tag.config.param);
                //$scope.load();
            }
            else {
                layer.alert(res.message)
            }
        })
    }
    /**
     * 标签书列表
     * @param tagid
     * @param tagname
     */
    toBookList = function (tagid, tagname, event) {
        $rootScope.stopEvent(event);
        $scope.tagBookTableControl.config.param["tag_id"] = tagid;
        $scope.tagBookTableControl.config.param["tagname"] = tagname;
        control_tag_book.reload($scope.tagBookTableControl.config.param);
        $scope.layer_export = layer.open({
            type: 1,
            title: tagname,
            area: ["1000px", "600px"],
            content: $("#tab_book_list")
        });
    }
    ////////////////////*******标签书列表********////////////////////////
    var control_tag_book = null;
    //机构列表
    $scope.tagBookTableControl = {
        config: {
            param: {
                pageNum: 1, //当前页
                pageSize: 10, //每页条数
                searchText: null,
                bool: true
            },
            columns: [
                {
                    field: 'book_cover', title: "封面", formatter: function (value) {
                    if (value) {
                        return '<img src="' + $rootScope.ctxPath + value + '" style="display: block;margin: 0 auto;" class="df_book">'
                    }
                    return ''
                }
                },
                {field: 'book_name', title: "名称", align: 'left'},
                {field: 'book_isbn', title: "ISBN", align: 'left'},
                {field: 'book_author', title: "作者"},
                {field: 'tag_names', title: "所属标签"},
                {
                    field: 'tag_whether', title: "操作", formatter: function (value, row) {
                        return '<a href="javascript:void(0)" style="color: red" onclick="upBookTag1(false,' + row.book_id + ', event)">移除</a>'
                    }
                }
            ]
        },
        reload: function (param) {
            $scope.services._data_book_tab_all(param).success(function (res) {
                control_tag_book.loadData(res.data);
            })
        }
    }
    control_tag_book = $scope.tagBookTableControl;
    //数据加载
    control_tag_book.loadData = function (data) {
        control_tag_book.data = data.rows ? data.rows : data.children;
        control_tag_book.config.param["total"] = parseInt(data.total);
        control_tag_book.config.param["pages"] = parseInt(data.pages);
        control_tag_book.config.param["pageNum"] = parseInt(data.pageNum);
        control_tag_book.config.pagerNumCol = parseInt(data.pageNum);
        control_tag_book.rows = [];
        control_tag_book.config.selectAll = false;
        angular.forEach(control_tag_book.data, function (rowdata, rowindex) {
            var row = [];
            angular.forEach(control_tag_book.config.columns, function (item, index) {
                var filed = rowdata[item.field];
                if (filed == null || filed == undefined) {
                    filed = "";
                }
                var col = {};
                for (var i in item) {
                    col[i] = item[i];
                }
                if (item.formatter) {
                    filed = item.formatter(filed, rowdata, rowindex)
                }
                col.value = $scope._trustAsHtml('<span>' + filed + '</span>');
                row.push(col);
            })
            control_tag_book.rows.push({
                index: rowindex,
                data: row,
                select: false,
                source: rowdata
            });
        })
        if (control_tag_book.config.param.pages > 1 || control_tag_book.rows.length > 1) {
            layui.laypage({
                cont: "tab_book_list_pager",
                pages: control_tag_book.config.param.pages,
                curr: control_tag_book.config.param.pageNum,
                skip: true,
                jump: function (resPager) {
                    if (resPager.curr != control_tag_book.config.param.pageNum) {
                        control_tag_book.config.param.pageNum = parseInt(resPager.curr);
                        if (control_tag_book.reload)
                            control_tag_book.reload(control_tag_book.config.param);
                    }
                }
            });
        }
        else {
            $("#tab_book_list_pager").empty();
        }
    }

    //自动初始化
    control_tag_book.init = function () {
        angular.forEach(control_tag_book.config.columns, function (item, index) {
            item.align = !item.align ? 'center' : item.align;
            item.width = !item.width ? 'auto' : item.width;
            item.orderType = !item.orderType ? 'desc' : item.orderType;
        })
        if (control_tag_book.data) {
            control_tag_book.loadData(control_tag_book.data);
        }
        else {
            //control_tag_book.reload(control_tag_book.config.param);
        }
    }
    //重新查询
    control_tag_book.reload_tag = function () {
        control_tag_book.config.param["searchText"] = control_tag_book.tag_searchText;
        control_tag_book.config.param["pageNum"] = 1;
        control_tag_book.reload(control_tag_book.config.param);
    }
    control_tag_book.init();
    /**
     * 添加删除标签
     * @param b
     * @param id
     */
    upBookTag1 = function (b, id, event) {
        $rootScope.stopEvent(event);
        services._up_book_tab({
            tag_id: control_tag_book.config.param.tag_id,
            book_id: id,
            bool: b
        }).success(function (res) {
            if (res.code == 0) {
                control_tag_book.reload(control_tag_book.config.param);
                //$scope.load();
            }
            else {
                layer.alert(res.message)
            }
        })
    }

    ////////////////////*******标签书列表********////////////////////////


    $scope.selRow = {}
    var selNode = null;
    $("#comTree").tree({
        //数据过滤
        "lines": true,
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
            var nodes = $('#comTree').tree("getChildren", $("#comTree").tree("getRoot").target)
            for (var i = 0; i < nodes.length; i++) {
                //循环所有二级节点然后关闭
                $("#comTree").tree('collapse', nodes[i].target);
            }
            if (!selNode) {
                selNode = $("#comTree").tree("getRoot");
            }
            else {
                //匹配节点是否存在
                var boolNode = $("#comTree").tree("find", selNode.id);
                selNode = boolNode ? boolNode : $("#comTree").tree("getRoot");
            }
            //展开所有父节点
            expandParent(selNode);
            //展开自己
            $("#comTree").tree('expand', selNode.target);
            //更新所有节点数量
            countTreeNum($("#comTree").tree("getRoot"));
            //选中节点
            $("#comTree").tree("select", selNode.target);
        }
    });
    //更新所有节点数量
    function countTreeNum(node) {
        var num = 0;
        if (node) {
            var childNum = parseInt(node.attributes);
            if (node.children && node.children.length > 0) {
                for (var i = 0; i < node.children.length; i++) {
                    childNum = countTreeNum(node.children[i]);
                }
            }
            if( childNum != 0 && node.children.length==0){
                $('#comTree').tree('update', {
                    target: node.target,
                    text: node.text + "<s>(" + childNum + ")</s>",
                    attributes: childNum
                });
            }
            num = childNum;
        }
        return num;
    }

    function expandParent(node) {
        var parent = $("#comTree").tree("getParent", node.target);
        if (parent) {
            $("#comTree").tree('expand', parent.target);
            expandParent(parent);
        }
    }

    $("#resTree").tree({
        //数据过滤
        "loadFilter": function (data, parent) {
            for (var i = 0; i < data.length; i++) {
                changeTreeStyle1(data[i]);
            }
            return data;
        },
        "onSelect": function (node) {
            $scope.selRow.tag_pid = node.id;
            $scope.selRow.parent_name = node.text;
            $(".layui-form-selected").removeClass("layui-form-selected")
        },
        onLoadSuccess: function (node, data) {
            if (!selNode) {
                selNode = $("#resTree").tree("getRoot");
            } else {
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

    //tree节点改变
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
        treeNode["text"] = treeNode.tag_name;
        treeNode["id"] = treeNode.tag_id;
        //获取值
        var count = tag_tree_data_count[treeNode.tag_id];
        treeNode["attributes"] = count ? count : 0;
    }


});