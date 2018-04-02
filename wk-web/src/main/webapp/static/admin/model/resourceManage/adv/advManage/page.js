var order_top,fenpei,updateShow,startStop;
myApp.controller('advManageController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //广告管理列表
    services["_adv_Manager_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/adv/json/getAdvList', param, "POST");
    }
    //广告分类
    services["_adv_cat_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/advCat/json/getAllAdvCat', param, "POST");
    }
    //广告管理新增/修改
    services["_adv_add"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/adv/json/updateAdv', param, "POST");
    }
    //广告删除
    services["_adv_del"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/adv/json/deleteAdvs', param, "POST");
    }
    //广告启用停用
    services["_adv_startStop"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/adv/json/updateEnable', param, "POST");
    }
    //广告是否显示
    services["_adv_isShow"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/adv/json/updateShow', param, "POST");
    }
    //广告分配机构列表
    services["_adv_org_data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/adv/json/orgList', param, "POST");
    }
    //广告添加机构
    services["_adv_addOrg"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/adv/json/addOrg', param, "POST");
    }
    //广告移除机构
    services["_adv_removeOrg"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/adv/json/removeOrg', param, "POST");
    }
    //广告更改排序
    services["_adv_updateOrder"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/adv/json/updateOrder', param, "POST");
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
                    field: 'adv_img_small', title: "封面", align: 'left',
                    formatter: function (value, row, index) {
                        if (value) {
                            return '<img style="display: block;width: auto;min-width: 50px;" class="df_book" src="' + $rootScope.ctxPath + value + '">'
                        }
                        return ''
                    }
                },
                {field: 'adv_name', title: "名称", align: 'left'},
                {
                    field: 'adv_url', title: "链接地址", align: 'left', formatter: function (value, row, index) {
                    return '<a href="' + value + '" target="_blank">' + value + '</a>';
                }
                },
                {
                    field: 'org_count', title: "使用机构", formatter: function (value, row, index, data) {
                    return '<a href="javascript:void(0)" onclick="fenpei(' + row.adv_id + ',event)">' + value + '</a>';
                }
                },
                {
                    field: 'enabled', title: "是否启用",
                    formatter: function (value, row, index) {
                        if($rootScope._USERINFO.org_id == row.org_id ||$rootScope._USERINFO.org_id == 1) {
                            var bool = row.enabled == 1 ? 'layui-form-onswitch' : '';
                            return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + row.enabled + ',' + row.adv_id + ', event)"><i></i></div>';
                        }
                        else{
                            return "<em style='color: #999'>无权限</em>";
                        }
                    }
                },
                {
                    field: 'is_show', title: "是否显示",
                    formatter: function (value, row, index) {
                        var bool = row.is_show == 1 ? 'layui-form-onswitch' : '';
                        return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="updateShow(' + row.adv_id + ', event)"><i></i></div>';
                    }
                },
                {
                    field: 'other', title: "操作", formatter: function (value, row, index, data) {
                        var action = "";
                        if (index > 0 && data.length > 1) {
                            action += '<a href="javascript:void(0)" class="btn-icon" title="排序置顶" onclick="order_top(' + row.adv_id + ',' + index + ',\'top\',event)"><i class="iconfont icon-dingbu"></i></a>';
                            action += '<a href="javascript:void(0)" class="btn-icon" title="排序上升" onclick="order_top(' + row.adv_id + ',' + index + ',\'up\', event)"><i class="iconfont icon-shangsheng1"></i></a>';
                        }
                        if (index < data.length - 1) {
                            action += '<a href="javascript:void(0)" class="btn-icon" title="排序下降" onclick="order_top(' + row.adv_id + ',' + index + ',\'down\', event)"><i class="iconfont icon-xiajiang"></i></a>';
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
                $scope.tableControl.config.columns.splice(3, 1)
            }
            services._adv_Manager_Data(param).success(function (res) {
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
                $scope.selRow["adv_img"] = res.data[0].url;
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
        row.adv_id = $scope.fenpeiParam.adv_id;
        services._adv_addOrg(row).success(function (res) {
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
        services._adv_removeOrg(row).success(function (res) {
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
        services._adv_org_data($scope.fenpeiParam).success(function (res) {
            $scope.fenpeiDataArray = res.data.rows;
            //更新总页数
            $scope.fenpeiParam.pages = res.data.pages;
        })
    }
    //分配机构
    fenpei = function (adv_id, event) {
        $rootScope.stopEvent(event);
        //打开层
        $scope.layer_export = layer.open({
            type: 1,
            title: "分类使用机构",
            area: ["750px", "600px"],
            content: $("#adv_org")
        });
        $scope.fenpeiParam = {
            adv_id: adv_id,
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
            $scope.tableControl.config.param.adv_cat_id = node.id
            $scope.tableControl.config.param["pageNum"] = 1;
            if(node.id != -1) {
                services._adv_Manager_Data($scope.tableControl.config.param).success(function (res) {
                    $scope.tableControl.loadData(res.data);
                })
            }
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
            $scope.selRow.adv_cat_id = node.id;
            $scope.selRow.parent_name = node.text;
            $(".layui-form-selected").removeClass("layui-form-selected");

            //项目信息变更
            $("#img_tt").html(node.adv_cat_remark);
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
        treeNode["text"] = treeNode.adv_cat_name;
        treeNode["id"] = treeNode.adv_cat_id;
    }

    var loadingData = null;
    //加载
    $scope.load = function () {
        services._adv_cat_Data().success(function (res) {
            loadingData = res.data
            var arrdata = [];
            if (res.data) {
                arrdata = res.data;
            }
            var allData = [{
                adv_cat_name: "所有资源",
                adv_cat_id: 0,
                children: arrdata
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
        $rootScope.formOpen();
        $scope.selRow = {
            adv_classify_id: selNode.id,
            parent_name: selNode.text,
            outlink_type: 0
        }
        $scope.status = true
        $rootScope.formOpen();
        services._adv_cat_Data().success(function (res) {
            var arrdata = [];
            if (res.data) {
                arrdata = res.data;
            }
            var allData = [{
                adv_cat_name: "所有资源",
                adv_cat_id: 0,
                children: arrdata
            }]
            selNode1 = $("#comTree").tree("getSelected");
            $("#resTree").tree("loadData", allData);
        })
    }

    /**
     * 改吧排序
     * @param res_id
     * @param index
     * @param type
     */
    order_top = function (adv_id, index, type, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.adv_id == adv_id) {
                $scope.selRow = $scope.tableControl.data[index]
            }
        })
        if (type == 'up') {
            var row_last = $scope.tableControl.data[index - 1];
            $scope.selRow.org_order_weight = parseInt(row_last.org_order_weight) + 1;
        }
        else if (type == 'down') {
            var row_last = $scope.tableControl.data[index + 1];
            $scope.selRow.org_order_weight = parseInt(row_last.org_order_weight) - 1;
        }
        else {
            $scope.selRow.org_order_weight = (new Date()).getTime();
        }
        services._adv_updateOrder($scope.selRow).success(function (res) {
            if (res.code == 0) {
                $scope.tableControl.reload($scope.tableControl.config.param);
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
                ids.push($scope.tableControl.data[item.index].adv_cat_id)
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
        if ($rootScope._USERINFO.org_id !=1 && $scope.selRow.org_id != $rootScope._USERINFO.org_id  ) {
            layer.alert("您不具有操作该数据的权限");
            return false
        }
        $scope.status = false
        $rootScope.formOpen();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                $scope.selRow = $scope.tableControl.data[index]
            }
        })
        services._adv_cat_Data().success(function (res) {
            var arrdata = [];
            if (res.data) {
                arrdata = res.data;
            }
            var allData = [{
                adv_cat_name: "所有资源",
                adv_cat_id: 0,
                children: arrdata
            }]
            selNode1 = $("#comTree").tree("find", $scope.selRow.adv_cat_id)
            $("#resTree").tree("loadData", allData);
        })
    }

    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.adv_name) {
            layer.alert("请填写广告名称")
            return false;
        }
        if (!$scope.selRow.adv_img) {
            layer.alert("请上传广告图片")
            return false;
        }
        if (!$scope.selRow.adv_cat_id) {
            layer.alert("请选择分类")
            return false;
        }
        if (!$scope.selRow.adv_url) {
            layer.alert("请输入广告链接")
            return false;
        }
        if (!$scope.selRow.order_weight) {
            $scope.selRow.order_weight = (new Date()).getTime();
        }
        services._adv_add($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {
                        adv_cat_id: $scope.selRow.adv_cat_id,
                        parent_name: $scope.selRow.parent_name
                    };
                }
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
        var adv_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                adv_ids.push($scope.tableControl.data[item.index].adv_id)
            }
        });
        if (adv_ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._adv_del({adv_ids: adv_ids}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功")
                        selNode = $("#comTree").tree("getSelected");
                        $scope.tableControl.reload($scope.tableControl.config.param);
                    }
                    else {
                        layer.msg(res.message)
                    }
                })
            })
        }
    }

    //是否启用
    startStop = function (enabled, adv_id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.adv_id == adv_id) {
                $scope.selRow = item;
                $scope.selRow.enabled = enabled
            }
        })
        services._adv_startStop($scope.selRow).success(function (res) {
            if (res.code == 0) {
                selNode = $("#comTree").tree("getSelected");
                $scope.tableControl.reload($scope.tableControl.config.param);
            } else {
                layer.msg(res.message)
            }
        })
    }
    //是否显示
    updateShow = function (adv_id, event) {
        $rootScope.stopEvent(event);
        angular.forEach($scope.tableControl.data, function (item, index) {
            if (item.adv_id == adv_id) {
                $scope.selRow = item;
            }
        });
        if ($scope.selRow.is_show == 1) {
            $scope.selRow.is_show = 2;
        } else {
            $scope.selRow.is_show = 1;
        }
        services._adv_isShow($scope.selRow).success(function (res) {
            if (res.code == 0) {
                selNode = $("#comTree").tree("getSelected");
                $scope.tableControl.reload($scope.tableControl.config.param);
            }
            else {
                layer.msg(res.message);
            }
        })
    }
    $scope.load();

});