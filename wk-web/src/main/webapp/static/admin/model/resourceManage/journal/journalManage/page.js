var startStop;
myApp.controller('journalManageController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //期刊管理列表
    services["_journal_Manage_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/journal/json/list', param, "POST");
    }
    //期刊分类树
    services["_journal_Manage_Tree"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/journalCat/getTree', param, "POST");
    }
    //期刊是否推荐
    services["_journal_startStop"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/journal/json/updateStatus', param, "POST");
    }
    //期刊管理新增/修改
    services["_journal_add"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/journal/json/updateJournal', param, "POST");
    }
    //期刊删除
    services["_journal_del"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/journal/json/deleteJournals', param, "POST");
    }
    //期刊上传
    services["_resolver_pdf"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/periodical/uploadPdf', param, "POST");
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
                    field: 'journal_cover_small', title: "封面", align: 'left',
                    formatter: function (value, row, index) {
                        if (value) {
                            return '<img style="display: block;margin: 0px auto;" class="df_book"  src="' + $rootScope.ctxPath + value + '">'
                        }
                        return ''
                    }
                },
                {field: 'journal_name', title: "名称", align: 'left'},
                {field: 'order_weight', title: "排序"}
            ]
        },
        reload: function (param) {
            $scope.load()
        }
    };
    <!--文件上传-->
    //文件上传
    $('#pdf_upload').prettyFilePDF({
        text: "上传期刊",
        change: function (res, obj) {
            var mms = layer.msg("文件转换中请稍候...");
            services._resolver_pdf(res).success(function (res) {
                layer.close(mms);
                if (res.code == 0) {
                    layer.msg("转换完成");
                    $scope.reload();
                }
                else {
                    layer.alert(res.message);
                }
            })
        },
        init: function (obj) {
            $(".file-btn-ku", obj).remove();
            $(".file-btn-text", obj).addClass("layui-btn").removeClass("layui-btn-normal").removeClass("layui-btn-primary")
        }
    });

    $('#prj-log').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["journal_cover"] = res.data[0].url;
            })
        },
        init: function (par) {
            $(".input_group", par).removeClass("input_group");
        }
    });
    $("#img_cc").click(function () {
        $('#prj-log').click();
    })

    $('#android_code').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["android_qr_code"] = res.data[0].url;
            })
        },
        init: function (par) {
            $(".input_group", par).removeClass("input_group");
        }
    });
    $("#img_cc_2").click(function () {
        $('#android_code').click();
    })
    $('#ios_code').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["ios_qr_code"] = res.data[0].url;
            })
        },
        init: function (par) {
            $(".input_group", par).removeClass("input_group");
        }
    });
    $("#img_cc_1").click(function () {
        $('#ios_code').click();
    })

    //重新查询
    $scope.reload = function (key, value) {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        services._journal_Manage_Data($scope.tableControl.config.param).success(function (res) {
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
            $scope.tableControl.config.param.journal_cat_id = node.id
            $scope.tableControl.config.param["pageNum"] = 1;
            services._journal_Manage_Data($scope.tableControl.config.param).success(function (res) {
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
            $scope.selRow.journal_cat_id = node.id;
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
        treeNode["text"] = treeNode.journal_cat_name;
        treeNode["id"] = treeNode.journal_cat_id;
    }

    var loadingData = null;
    //加载
    $scope.load = function () {
        if (!loadingData) {
            services._journal_Manage_Tree().success(function (res) {
                loadingData = res.data
                var arrdata = [];
                if (res.data) {
                    arrdata = res.data;
                }
                var allData = [{
                    journal_cat_name: "所有资源",
                    journal_cat_id: 0,
                    children: arrdata
                }]
                $("#comTree").tree("loadData", allData);
            })
        } else {
            selNode = $("#comTree").tree("getSelected");
            $scope.tableControl.config.param.journal_cat_id = selNode.id
            services._journal_Manage_Data($scope.tableControl.config.param).success(function (res) {
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
        $scope.selRow = {
            journal_cat_id: selNode.id,
            parent_name: selNode.text,
            journal_type: 0
        }
        $scope.status = true
        $rootScope.formOpen();
        services._journal_Manage_Tree().success(function (res) {
            var arrdata = [];
            if (res.data) {
                arrdata = res.data;
            }
            var allData = [{
                journal_cat_name: "所有资源",
                journal_cat_id: 0,
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
        services._journal_Manage_Tree().success(function (res) {
            var arrdata = [];
            if (res.data) {
                arrdata = res.data;
            }
            var allData = [{
                journal_cat_name: "所有资源",
                journal_cat_id: 0,
                children: arrdata
            }]
            selNode1 = $("#comTree").tree("find", $scope.selRow.journal_cat_id)
            $("#resTree").tree("loadData", allData);
        })
    }

    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.journal_name) {
            layer.alert("请填写标题")
            return false;
        }
        if (!$scope.selRow.journal_cover) {
            layer.alert("请上传封面")
            return false;
        }
        if (!$scope.selRow.journal_cat_id) {
            layer.alert("请选择分类")
            return false;
        }
        if (!$scope.selRow.order_weight) {
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        services._journal_add($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {
                        journal_cat_id: $scope.selRow.journal_cat_id,
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
        var journal_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                journal_ids.push($scope.tableControl.data[item.index].journal_id)
            }
        });
        if (journal_ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._journal_del({journal_ids: journal_ids}).success(function (res) {
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
    //是否发布、推荐、头条
    startStop = function (car_id, type, event) {
        $rootScope.stopEvent(event);
        if ($rootScope._USERINFO.org_id == 1) {
            angular.forEach($scope.tableControl.data, function (item, index) {
                if (item.journal_id == car_id) {
                    $scope.selRow = item;
                    if (type == 'journal_status') {
                        $scope.selRow.journal_status = item.journal_status == 1 ? 2 : 1
                    } else if (type == 'recommend') {
                        $scope.selRow.recommend = item.recommend == 1 ? 2 : 1
                    } else if (type == 'headline') {
                        $scope.selRow.headline = item.headline == 1 ? 2 : 1
                    }
                }
            });
            services._journal_startStop($scope.selRow).success(function (res) {
                if (res.code == 0) {
                    selNode = $("#comTree").tree("getSelected");
                    $scope.load();
                }
                else {
                    layer.msg(res.message);
                }
            })
        }
    }
});