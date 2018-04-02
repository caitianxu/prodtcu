var order_top, fenpei, updateShow, startStop;
myApp.controller('subjectManageController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //专题管理列表
    services["_subject_Manager_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/getList', param, "POST");
    }
    //专题管理新增/修改
    services["_subject_add"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/addUpdate', param, "POST");
    }
    //专题删除
    services["_subject_del"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/delete', param, "POST");
    }
    //专题启用停用
    services["_subject_startStop"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/enabled', param, "POST");
    }
    //专题是否显示
    services["_subject_isShow"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/isShow', param, "POST");
    }
    //专题分配机构列表
    services["_subject_org_data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/orgList', param, "POST");
    }
    //专题添加机构
    services["_subject_addOrg"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/addOrg', param, "POST");
    }
    //专题移除机构
    services["_subject_removeOrg"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/removeOrg', param, "POST");
    }
    //专题更改排序
    services["_subject_updateOrder"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/orderSubject', param, "POST");
    }

    //专题添加资讯
    services["_subject_addArticle"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/addArticle', param, "POST");
    }
    //专题获取资讯
    services["_subject_getArticle"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/getArticle', param, "POST");
    }
    //专题删除资讯
    services["_subject_deleteArticle"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/deleteArticle', param, "POST");
    }
    //专题添加图书
    services["_subject_addBook"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/addBook', param, "POST");
    }
    //专题获取图书列表
    services["_subject_getBookList"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/getBookList', param, "POST");
    }
    //专题移除图书
    services["_subject_removeBook"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/removeBook', param, "POST");
    }
    //专题图书列表更改排序
    services["_subject_orderBooks"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/orderBooks', param, "POST");
    }
    //专题资讯列表更改排序
    services["_subject_orderNews"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/subject/orderArticle', param, "POST");
    }

    //资讯分页列表（机构）
    services["_data_article"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/article/json/list', param, "POST");
    }
    //查询出版列表
    services["_data_books"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookOrgRel/json/getList', param, "POST");
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
                    field: 'subject_cover', title: "封面", align: 'left',
                    formatter: function (value, row, index) {
                        if (value) {
                            return '<img style="display: block;width: auto;min-width: 50px;" class="df_book" src="' + $rootScope.ctxPath + value + '">'
                        }
                        return ''
                    }
                },
                {field: 'subject_name', title: "专题名称", align: 'left'},
                {
                    field: 'org_count', title: "使用机构", formatter: function (value, row, index, data) {
                    return '<a href="javascript:void(0)" onclick="fenpei(' + row.subject_id + ',event)">' + value + '</a>';
                }
                },
                {
                    field: 'enabled', title: "是否启用",
                    formatter: function (value, row, index) {
                        if ($rootScope._USERINFO.org_id == row.org_id || $rootScope._USERINFO.org_id == 1) {
                            var bool = row.enabled == 1 ? 'layui-form-onswitch' : '';
                            return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.enabled + ',' + row.subject_id + ', event)"><i></i></div>';
                        }
                        else {
                            return "<em style='color: #999'>无权限</em>";
                        }
                    }
                },
                {
                    field: 'is_show', title: "是否显示",
                    formatter: function (value, row, index) {
                        var bool = row.is_show == 1 ? 'layui-form-onswitch' : '';
                        return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="updateShow(' + row.subject_id + ', event)"><i></i></div>';
                    }
                },
                {
                    field: 'other', title: "操作", formatter: function (value, row, index, data) {
                    var action = "";
                    if (index > 0 && data.length > 1) {
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序置顶" onclick="order_top(' + row.subject_id + ',' + index + ',\'top\',event)"><i class="iconfont icon-dingbu"></i></a>';
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序上升" onclick="order_top(' + row.subject_id + ',' + index + ',\'up\', event)"><i class="iconfont icon-shangsheng1"></i></a>';
                    }
                    if (index < data.length - 1) {
                        action += '<a href="javascript:void(0)" class="btn-icon" title="排序下降" onclick="order_top(' + row.subject_id + ',' + index + ',\'down\', event)"><i class="iconfont icon-xiajiang"></i></a>';
                    }
                    return '<span>' + action + '</span>';
                }
                }
            ]
        },
        reload: function (param) {
            if ($rootScope._USERINFO.org_id != 1 && spliceBool) {
                spliceBool = false;
                $scope.tableControl.config.columns.splice(2, 1)
                $scope.tableControl.config.columns.splice(2, 1)
            }
            services._subject_Manager_Data(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };
    var spliceBool = true;
    <!--文件上传-->
    $('#prj-log').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["subject_cover"] = res.data[0].url;
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
        $scope.tableControl.reload($scope.tableControl.config.param);
    }


    //所有项目
    $scope.allProject = null;
    //数据列表
    $scope.fenpeiDataArray = null;
    //查询分配参数
    $scope.fenpeiParam = {
        audio_cat_id: null,
        project_id: null,
        searchtext: null,
        pageNum: 1,
        pageSize: 10,
        pages: 1
    }
    $scope.fenpeiParamCol = {
        project_id: null,
        project_name: '所有项目',
        searchtext: null
    }


    //添加分配
    $scope.addOrg = function (row) {
        row.subject_id = $scope.fenpeiParam.subject_id;
        services._subject_addOrg(row).success(function (res) {
            if (res.code == 0) {
                $scope._fenpeiData($scope.fenpeiParam.pageNum);
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    //移除分配
    $scope.removeOrg = function (row) {
        services._subject_removeOrg(row).success(function (res) {
            if (res.code == 0) {
                $scope._fenpeiData($scope.fenpeiParam.pageNum);
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    //获取分配结构数据列表
    $scope._fenpeiData = function (num) {
        if (num) {
            $scope.fenpeiParam.pageNum = num;
        }
        else {
            $scope.fenpeiParam.pageNum = 1;
            $scope.fenpeiParam.project_id = $scope.fenpeiParamCol.project_id;
            $scope.fenpeiParam.searchtext = $scope.fenpeiParamCol.searchtext;
        }
        services._subject_org_data($scope.fenpeiParam).success(function (res) {
            $scope.fenpeiDataArray = res.data.rows;
            //更新总页数
            $scope.fenpeiParam.pages = res.data.pages;
        })
    }
    //分配机构
    fenpei = function (subject_id, event) {
        $rootScope.stopEvent(event);
        //打开层
        $scope.layer_export = layer.open({
            type: 1,
            title: "分类使用机构",
            area: ["700px", "600px"],
            content: $("#subject_org")
        });
        $scope.fenpeiParam = {
            subject_id: subject_id,
            project_id: null,
            searchtext: null,
            pageNum: 1,
            pageSize: 10,
            pages: 1
        }
        $scope.fenpeiParamCol = {
            project_id: null,
            project_name: '所有项目',
            searchtext: null
        }
        //查询所有项目
        if (!$scope.allProject) {
            services._project().success(function (res) {
                $scope.allProject = res.data;
            })
        }
        //执行查询
        $scope._fenpeiData();
    }


    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
        searchText: null
    }

    //新增
    $scope.addRow = function () {
        $scope.selRow = {};
        $scope.status = true;
        $scope.selRow_news_title = null;
        $scope.ZtBookListArray = [];
        var a = document.getElementById('myIframe').contentWindow;
        a.ue.setContent("");
        $rootScope.formOpen();
    }

    /**
     * 改吧排序
     * @param res_id
     * @param index
     * @param type
     */
    order_top = function (subject_id, index, type, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.subject_id == subject_id) {
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
        services._subject_updateOrder($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.reload();
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
                ids.push($scope.tableControl.data[item.index].subject_cat_id)
                $scope.selRow = $scope.tableControl.data[index]
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
        if ($rootScope._USERINFO.org_id != 1 && $scope.selRow.org_id != $rootScope._USERINFO.org_id) {
            layer.alert("您不具有操作该数据的权限");
            return false
        }
        var a = document.getElementById('myIframe').contentWindow
        a.ue.setContent('');
        $scope.status = false
        $rootScope.formOpen();
        $scope.selRow_news_title = null;
        $scope.ZtBookListArray = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                $scope.selRow = $scope.tableControl.data[index];
                //获取图书
                $scope.getZtBookList();
                //获取新闻
                $scope.get_new();
            }
        })
        var a = document.getElementById('myIframe').contentWindow
        a.ue.setContent($scope.selRow.subject_remark);
    }

    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.subject_name) {
            layer.alert("请填写专题名称")
            return false;
        }
        if (!$scope.selRow.subject_cover) {
            layer.alert("请上传专题图片")
            return false;
        }
        if (!$scope.selRow.order_weight) {
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        var a = document.getElementById('myIframe').contentWindow
        $scope.selRow.subject_remark = a.ue.getContent()
        if(!$scope.selRow.subject_remark){
            layer.alert("请填写专题描述")
            return false;
        }
        services._subject_add($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.selRow = res.data;
                $scope.reload();
                layer.msg('信息保存成功');
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //删除
    $scope.delRow = function () {
        var subject_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                subject_ids.push($scope.tableControl.data[item.index].subject_id)
            }
        });
        if (subject_ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._subject_del({ids: subject_ids}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功")
                        $scope.reload();
                    }
                    else {
                        layer.msg(res.message)
                    }
                })
            })
        }
    }

    //是否启用
    startStop = function (enabled, subject_id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.subject_id == subject_id) {
                $scope.selRow = item;
                $scope.selRow.enabled = enabled
            }
        })
        services._subject_startStop($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.reload();
            } else {
                layer.msg(res.message)
            }
        })
    }
    //是否显示
    updateShow = function (subject_id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.subject_id == subject_id) {
                $scope.selRow = item;
            }
        });
        if ($scope.selRow.is_show == 1) {
            $scope.selRow.is_show = 2;
        } else {
            $scope.selRow.is_show = 1;
        }
        services._subject_isShow($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.reload();
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    $scope.reload();

    //搜索加书
    $scope.searchBooks = {
        searchText: null,
        pageNum: 1,
        pageSize: 20,
    }
    $scope.bookRepoArray = {};
    $scope.searchBook_add = function () {
        if (!$scope.selRow.subject_id) {
            layer.msg("请选将专题的封面、标题、排序、描述信息保存后再添加图书！")
            return false;
        }
        $scope.bookRepoArray = {};
        $scope.searchBooks.searchText = null;
        $rootScope.layerfunTree = layer.open({
            type: 1,
            title: "图书搜索",
            area: ["700px", "700px"],
            content: $("#searchBook_add")
        });
    }
    //搜索图书
    $scope.searchData = function () {
        services._data_books($scope.searchBooks).success(function (res) {
            $scope.bookRepoArray = res.data.rows
        })
    }
    $scope.add_book_to_pkg = {
        book_id: null,
        subject_id: null
    }
    //添加图书到数据包
    $scope.add_book = function (row) {
        $scope.add_book_to_pkg.book_id = row.book_id;
        $scope.add_book_to_pkg.subject_id = $scope.selRow.subject_id;

        services._subject_addBook($scope.add_book_to_pkg).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message);
                $scope.getZtBookList();
            }
            else {
                layer.msg(res.message)
            }
        })
    }
    //图书列表
    $scope.ZtBookListArray = [];
    $scope.getZtBookList = function () {
        services._subject_getBookList({
            pageNum: 1,
            pageSize: 1000000,
            subject_id: $scope.selRow.subject_id
        }).success(function (res) {
            $scope.ZtBookListArray = res.data.rows;
        })
    }
    //图书排序
    $scope.order_top1 = function (rel_id, index, type, event) {
        $rootScope.stopEvent(event);
        var pp = {
            rel_id: rel_id,
            subject_id: $scope.selRow.subject_id,
            order_weight: null
        }
        if (type == 'up') {
            var row_last = $scope.ZtBookListArray[index - 1];
            pp.order_weight = parseInt(row_last.order_weight) + 1;
        }
        else if (type == 'down') {
            var row_last = $scope.ZtBookListArray[index + 1];
            pp.order_weight = parseInt(row_last.order_weight) - 1;
        }
        else {
            pp.order_weight = (new Date()).getTime();
        }
        services._subject_orderBooks(pp).success(function (res) {
            if (res.code == 0) {
                $scope.getZtBookList();
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    //删除图书
    $scope.book_list_remove = function (book) {
        services._subject_removeBook({
            book_id: book.book_id,
            subject_id: book.subject_id
        }).success(function (res) {
            if (res.code == 0) {
                $scope.getZtBookList();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    // 资讯==============================================
    //添加资讯
    $scope.newsRepoArray = {};

    //搜索新闻
    $scope.searchNews = {
        searchText: null,
        pageNum: 1,
        pageSize: 20
    }

    $scope.searchNews_add = function () {
        if (!$scope.selRow.subject_id) {
            layer.msg("请选将专题的封面、标题、排序、描述信息保存后再添加资讯！")
            return false;
        }
        $scope.newsRepoArray = {};
        $scope.searchNews.searchText = null;
        $rootScope.layerfunTree = layer.open({
            type: 1,
            title: "资讯搜索",
            area: ["700px", "700px"],
            content: $("#searchNews_add")
        });
    }

    $scope.searchNewsData = function () {
        services._data_article($scope.searchNews).success(function (res) {
            $scope.newsRepoArray = res.data.rows;
        })
    }

    $scope.set_new = function (news) {
        services._subject_addArticle({
            subject_id: $scope.selRow.subject_id,
            article_id: news.article_id
        }).success(function (res) {
            if (res.code == 0) {
                layer.msg('新闻设置成功!')
                $scope.get_new();
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    $scope.ZtNewsListArray = [];
    $scope.get_new = function () {
        services._subject_getArticle({
            pageNum: 1,
            pageSize: 1000000,
            subject_id: $scope.selRow.subject_id
        }).success(function (res) {
            $scope.ZtNewsListArray = res.data.rows;
        })
    }

    $scope.order_top2 = function (rel_id, index, type, event) {
        $rootScope.stopEvent(event);
        var pp = {
            rel_id: rel_id,
            subject_id: $scope.selRow.subject_id,
            order_weight: null
        }
        if (type == 'up') {
            var row_last = $scope.ZtNewsListArray[index - 1];
            pp.order_weight = parseInt(row_last.order_weight) + 1;
        }
        else if (type == 'down') {
            var row_last = $scope.ZtNewsListArray[index + 1];
            pp.order_weight = parseInt(row_last.order_weight) - 1;
        }
        else {
            pp.order_weight = (new Date()).getTime();
        }
        services._subject_orderNews(pp).success(function (res) {
            if (res.code == 0) {
                $scope.get_new();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    $scope.news_list_remove = function (news) {
        services._subject_deleteArticle({
            article_id: news.article_id,
            subject_id: news.subject_id
        }).success(function (res) {
            if (res.code == 0) {
                $scope.get_new();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

});