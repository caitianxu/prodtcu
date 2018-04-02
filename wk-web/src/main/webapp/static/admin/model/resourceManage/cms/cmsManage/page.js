var startStop;
myApp.controller('cmsManageController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //资讯管理列表
    services["_cms_Manage_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/article/json/list', param, "POST");
    }
    //资讯管理分类树
    services["_cms_Manage_Tree"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/articleCat/getOrgTree', param, "POST");
    }
    //资讯模版列表
    services["_cms_Temp_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/articleTemp/json/listAll', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }
    //资讯管理新增/修改
    services["_cms_add"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/article/json/updateArticle', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }
    //资讯管理删除
    services["_cms_del"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/article/json/deleteArticles', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }
    //资讯发布
    services["_cms_publish"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/article/json/updateStatus', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }
    //资讯管理分配同步
    services["_cms_allot"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/article/json/allotArticle', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }
    //资讯管理发布
    services["_cms_status"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/article/json/updateStatus', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
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
                article_cat_id: 0,
                searchText: null
            },
            columns: [
                {
                    field: 'cover_url_small', title: "封面", align: 'left',
                    formatter: function (value, row, index) {
                        if (value) {
                            return '<img style="display: block;width: auto;min-width: 50px;" class="df_book" src="' + $rootScope.ctxPath + value + '">'
                        }
                        return ''
                    }
                },
                {field: 'order_weight', title: "排序"},
                {field: 'article_title', title: "标题", align: 'left'},
                {
                    field: 'article_status', title: "是否发布",
                    formatter: function (value, row, index) {
                        if ($rootScope._USERINFO.org_id == row.org_id) {
                            var bool = row.article_status == 1 ? 'layui-form-onswitch' : '';
                            return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.article_id + ',' + '\'article_status\'' + ', event)"><i></i></div>';
                        }
                        else {
                            var txt = '<label>无权限</label>';
                            return txt;
                        }
                    }
                },
                {
                    field: 'recommend', title: "新闻推荐",
                    formatter: function (value, row, index) {
                        if ($rootScope._USERINFO.org_id == row.org_id) {
                            var bool = row.recommend == 1 ? 'layui-form-onswitch' : '';
                            return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.article_id + ',' + '\'recommend\'' + ', event)"><i></i></div>';
                        }else {
                            var txt = '<label>无权限</label>';
                            return txt;
                        }
                    }
                },
            ]
        },
        reload: function (param) {
            $scope.load()
        }
    };
    <!--文件上传-->
    $('#prj-log').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["cover_url"] = res.data[0].url;
            })
        },
        init: function (par) {
            $(".input_group", par).removeClass("input_group");
        }
    });
    $("#img_cc").click(function () {
        $("#prj-log").click();
    })

    //新增图文textarea内容
    $scope.textarea_inner = null;
    <!--文件上传-->
    $('#prj-log2').prettyFile({
        text: "",
        change: function (res, obj) {
            $scope.imageTextData.push({
                url: res.data[0].url,
                text: $scope.textarea_inner
            })
            $scope.textarea_inner = ""
        }
    });
    //重新查询
    $scope.reload = function (key, value) {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        services._cms_Manage_Data($scope.tableControl.config.param).success(function (res) {
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
            $scope.tableControl.config.param.article_cat_id = node.id;
            $scope.tableControl.config.param["pageNum"] = 1;
            services._cms_Manage_Data($scope.tableControl.config.param).success(function (res) {
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
            $scope.selRow.article_cat_id = node.id;
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
        if (!treeNode['children'] || treeNode['children'].length == 0) {
            treeNode['res_icon'] = 'tree-folder';
        }
        if (treeNode['children'] && treeNode['children'].length > 0) {
            for (var j = 0; j < treeNode['children'].length; j++) {
                changeTreeStyle1(treeNode['children'][j]);
            }
        }
        //设置属性
        treeNode["text"] = treeNode.article_cat_name;
        if (treeNode.temp_name) {
            treeNode["text"] = treeNode.temp_name;
        }
        treeNode["id"] = treeNode.article_cat_id;
    }

    var loadingData = null;
    //加载
    $scope.load = function () {
        if (!loadingData) {
            services._cms_Manage_Tree().success(function (res) {
                loadingData = res.data;
                var arrdata = [];
                if (res.data) {
                    arrdata = res.data;
                }
                var allData = [{
                    article_cat_name: "所有资源",
                    article_cat_id: 0,
                    children: arrdata
                }]
                $("#comTree").tree("loadData", allData);
            })
        }
        else {
            selNode = $("#comTree").tree("getSelected");
            $scope.tableControl.config.param.article_cat_id = selNode.id;
            services._cms_Manage_Data($scope.tableControl.config.param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    }
    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
        searchText: null
    }

    $scope.cmsTempData = null;
    //新增
    $scope.addRow = function () {
        $rootScope.formOpen();
        $scope.selRow = {
            article_cat_id: selNode.id,
            parent_name: selNode.text,
            article_type: 0
        }
        //先清空
        $scope.imageTextData = [];
        $scope.outer_link = "";
        var a = document.getElementById('myIframe').contentWindow
        a.ue.setContent("")

        $scope.status = true
        $rootScope.formOpen();
        services._cms_Manage_Tree().success(function (res) {
            var arrdata = [];
            if (res.data) {
                arrdata = res.data;
            }
            var allData = [{
                article_cat_name: "所有资源",
                article_cat_id: 0,
                children: arrdata
            }]
            selNode1 = $("#comTree").tree("getSelected");
            $("#resTree").tree("loadData", allData);
        })
        //资讯模版
        services._cms_Temp_Data().success(function (res) {
            $scope.cmsTempData = res.data
        })
    }

    //修改
    $scope.row_update = function () {
        //先清空
        $scope.imageTextData = [];
        $scope.outer_link = "";
        var a = document.getElementById('myIframe').contentWindow
        a.ue.setContent('');

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
        if ($scope.selRow.article_type == 0) {
            var a = document.getElementById('myIframe').contentWindow
            a.ue.setContent($scope.selRow.article_content);
        }
        if ($scope.selRow.article_type == 1) {
            $scope.imageTextData = $.parseJSON($scope.selRow.article_content)
        }
        if ($scope.selRow.article_type == 2) {
            $scope.outer_link = $scope.selRow.article_content
        }
        services._cms_Manage_Tree().success(function (res) {
            var arrdata = [];
            if (res.data) {
                arrdata = res.data;
            }
            var allData = [{
                article_cat_name: "所有资源",
                article_cat_id: 0,
                children: arrdata
            }]
            selNode1 = $("#comTree").tree("find", $scope.selRow.article_cat_id);
            $("#resTree").tree("loadData", allData);
        })
    }

    //分配
    $scope.allot = function () {
        var article_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                article_ids.push($scope.tableControl.data[item.index].article_id)
            }
        });
        if (article_ids.length == 0) {
            layer.alert("请选择分配的资讯");
        }
        else {
            //清空搜索input
            $scope.orgTableControl.org_searchText = null
            $scope.openOrg(function (array) {
                services._cms_allot({
                    orgs: array,
                    article_ids: article_ids
                }).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("分配成功")
                    }
                    else {
                        layer.msg("分配失败");
                    }
                })
            });
        }
    }
    $scope.imageTextData = []

    //图文顺序切换/删除
    $scope.imageTextIndex = function (index, idxType) {
        var dd = $scope.imageTextData[index]
        //删除
        if (idxType == 0) {
            $scope.imageTextData.splice(index, 1)
            return
        }
        //上移
        if (idxType == 1) {
            $scope.imageTextData[index] = $scope.imageTextData[index - 1]
            $scope.imageTextData[index - 1] = dd
            return
        }
        //下移
        if (idxType == 2) {
            $scope.imageTextData[index] = $scope.imageTextData[index + 1]
            $scope.imageTextData[index + 1] = dd
            return
        }
    }
    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.article_title) {
            layer.alert("请填写标题")
            return false;
        }
        if (!$scope.selRow.cover_url) {
            layer.alert("请上传封面")
            return false;
        }
        if (!$scope.selRow.article_cat_id) {
            layer.alert("请选择分类")
            return false;
        }
        if (!$scope.selRow.article_remark) {
            layer.alert("请填写概要")
            return false;
        }
        if (!$scope.selRow.order_weight) {
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        var a = document.getElementById('myIframe').contentWindow
        if ($scope.selRow.article_type == 0) {
            $scope.selRow.article_content = a.ue.getContent()
        }
        if ($scope.selRow.article_type == 1) {
            $scope.selRow.article_content = $scope.imageTextData
        }
        if ($scope.selRow.article_type == 2) {
            $scope.selRow.article_content = $scope.outer_link
        }
        if (!$scope.selRow.article_content || $scope.selRow.article_content == "") {
            layer.alert("请上传内容")
            return false;
        }
        services._cms_add($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.imageTextData = [];
                    $scope.outer_link = "";
                    var a = document.getElementById('myIframe').contentWindow
                    a.ue.setContent("")
                    $scope.selRow = {
                        article_cat_id: $scope.selRow.article_cat_id,
                        parent_name: $scope.selRow.parent_name,
                        article_type: 0
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
        var article_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                article_ids.push($scope.tableControl.data[item.index].article_id)
            }
        });
        if (article_ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._cms_del({article_ids: article_ids}).success(function (res) {
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
    //是否显示
    startStop = function (car_id, type, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.article_id == car_id) {
                $scope.selRow = item;
                if (type == 'article_status') {
                    $scope.selRow.article_status = item.article_status == 1 ? 2 : 1
                } else if (type == 'recommend') {
                    $scope.selRow.recommend = item.recommend == 1 ? 2 : 1
                } else if (type == 'headline') {
                    $scope.selRow.headline = item.headline == 1 ? 2 : 1
                }
            }
        });
        services._cms_publish($scope.selRow).success(function (res) {
            if (res.code == 0) {
                selNode = $("#comTree").tree("getSelected");
                $scope.load();
            }
            else {
                layer.msg(res.message);
            }
        })
    }

})
