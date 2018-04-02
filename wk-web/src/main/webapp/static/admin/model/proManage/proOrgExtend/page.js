var startStop;
myApp.controller('proOrgExtendController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_proOrg"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/proOrgExtend/json/proOrgExtendList', param, "POST");
    }
    //新增、修改
    services["_add_proOrg"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/proOrgExtend/json/updateProOrgExtend', param, "POST");
    }
    //删除
    services["_del_proOrg"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/proOrgExtend/json/delProOrgExtends', param, "POST");
    }
    //农家书屋机构
    services["_pro_orgs"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/proOrgExtend/json/_pro_orgs', param, "POST");
    }
    //农家书屋机构
    services["_update_enabled"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/proOrgExtend/json/enabled', param, "POST");
    }

    <!--文件上传-->
    $('#org_logo').prettyFile({
        text: "上传LOGO",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["org_logo"] = res.data[0].url;
            })
        }
    });
    $('#org_weixin').prettyFile({
        text: "上传二维码",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["org_weixin"] = res.data[0].url;
            })
        }
    });
    $scope.accParam = null;
    $('#table_file_img').prettyFile({
        text: "上传图片",
        change: function (res, obj) {
            $scope.$apply(function () {
                if ($scope.accParam) {
                    $scope.accParam["url"] = res.data[0].url;
                }
            })
        }
    });

    $scope.tableControl = {
        config: {
            check: true,
            param: {
                orderField: "res_name",
                orderType: "desc",
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0,
                searchText: null
            },
            columns: [
                {
                    field: 'org_logo', title: "机构LOGO", align: "left",
                    formatter: function (value, row, index) {
                        if (value) {
                            return '<img src="' + $rootScope.ctxPath + value + '" style="height: 50px;"/>';
                        }
                        else {
                            return '';
                        }
                    }
                },
                {
                    field: 'org_weixin', title: "微信二维码",
                    formatter: function (value, row, index) {
                        if (value) {
                            return '<img src="' + $rootScope.ctxPath + value + '" style="height: 50px;"/>';
                        }
                        else {
                            return '';
                        }
                    }
                },
                {field: 'pro_name', title: "所属项目", align: 'left'},
                {field: 'org_name', title: "所属机构", align: 'left'},
                {field: 'server_name', title: "二级域名", align: 'left'},
                {
                    field: 'enabled', title: "状态",
                    formatter: function (value, row, index) {
                        if ($rootScope._USERINFO.role_id == 1) {
                            var bool = row.enabled == 1 ? 'layui-form-onswitch' : '';
                            return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="startStop(' + index + ', event)"><i></i></div>';
                        }
                        else {
                            var txt = row.enabled == 1 ? '<label style="color: green">启用</label>' : '<label style="color: #999">停用</label>';
                            return txt;
                        }
                    }

                },
                {field: 'create_time', title: "创建时间", align: 'left'}

            ]
        },
        reload: function (param) {
            services._data_proOrg(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };

    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
        searchText: null
    }
    //重新查询
    $scope.reload = function () {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.tableControl.reload($scope.tableControl.config.param);
    }

    //机构
    $scope.orgData = null;
    //菜单tree申明
    $("#authTree").tree({
        lines: true,
        //数据过滤
        "loadFilter": function (data, parent) {
            for (var i = 0; i < data.length; i++) {
                changeTreeStyle1(data[i]);
            }
            return data;
        },
        "onSelect": function (node) {
            $scope.$apply(function () {
                $scope.selRow.org_id = node.org_id
                $scope.selRow.org_name = node.org_name
                $(".layui-form-selected").removeClass("layui-form-selected")
            })
        }
    })
    function changeTreeStyle1(treeNode) {
        if (!treeNode['children'] || treeNode['children'].length == 0) {
            treeNode['org_icon'] = 'tree-folder';
        }
        if (treeNode['children'] && treeNode['children'].length > 0) {
            for (var j = 0; j < treeNode['children'].length; j++) {
                changeTreeStyle1(treeNode['children'][j]);
            }
        }
        //设置属性
        treeNode["text"] = treeNode.org_name;
    }

    //修改
    $scope.row_update = function () {
        var pro_org_extend_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                pro_org_extend_ids.push($scope.tableControl.data[item.index].pro_org_extend_id)
            }
        });
        if (pro_org_extend_ids.length == 0) {
            layer.alert("请选择你将要修改的数据");
            return false
        }
        if (pro_org_extend_ids.length > 1) {
            layer.alert("同时只能修改一条数据");
            return false
        }
        $scope.status = false
        $rootScope.formOpen();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                $scope.selRow = $scope.tableControl.data[index];
                $("#table_file .file-btn-input").val($scope.selRow.org_logo);
                $("#weixin_file .file-btn-input").val($scope.selRow.org_weixin);
                document.getElementById("is_registe").checked = $scope.selRow.is_registe == 2 ? true : false;
            }
        });
    }

    //新增
    $scope.addRow = function () {
        $scope.selRow = {}
        $scope.status = true
        $("#table_file .file-btn-input").val("");
        $rootScope.formOpen();
    }
    //提交
    $scope._form_submit = function (bool) {
        if (!$scope.selRow.org_name) {
            layer.alert("请选择机构")
            return false;
        }
        if (!$scope.selRow.org_logo) {
            layer.alert("请上传机构LOGO")
            return false;
        }
//        if (!$scope.selRow.webConfigList || $scope.selRow.webConfigList.length < 6) {
//            layer.alert("请上传推荐模块配置，并且配置不能少于6项!")
//            return false;
//        }
//        var rt = false;
//        angular.forEach($scope.selRow.webConfigList, function (mm) {
//            if (!mm.name || !mm.content_url || !mm.config_cover || !mm.config_cover_small || !mm.config_remark) {
//                rt = true;
//            }
//        })
//        if (rt) {
//            layer.alert("请完整填写推荐模块配置，配置每一项都不能为空!")
//            return false;
//        }
        $scope.selRow.is_registe = document.getElementById("is_registe").checked ? 2 : 1;
        services._add_proOrg($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {};
                }
                $scope.reload();
                layer.msg('信息保存成功');
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //启用停用
    startStop = function (index, event) {
        $rootScope.stopEvent(event);
        $scope.selRow = $scope.tableControl.data[index]
        $scope.selRow.enabled = $scope.selRow.enabled == 1 ? 2 : 1
        services._update_enabled($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg(res.message);
                $scope.tableControl.reload($scope.tableControl.config.param);
            }
            else {
                layer.msg(res.message);
            }
        })
    }

    //删除
    $scope.delRow = function () {
        var ids = new Array();
        var mark = null;
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].pro_org_extend_id)
            }
        });
        if (ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._del_proOrg({ids: ids, mark: mark}).success(function (res) {
                    if (res.code == 0) {
                        layer.msg("删除成功")
                        $scope.reload();
                    } else {
                        layer.msg(res.message)
                    }
                })
            })
        }
    }

    //机构选择列表
    $scope.org_list = function () {
        //清空搜索input
        $scope.orgTableControl.org_searchText = null
        $scope.openOrg(function (array) {
            if (array && array.length > 0) {
                $scope.selRow["org_name"] = array[0].org_name;
                $scope.selRow["org_id"] = array[0].org_id;
                $scope.selRow["pro_name"] = array[0].project_name;
                $scope.selRow["pro_code"] = array[0].project_code;
            }
        });
    }

//    //添加配置项
//    $scope.addSelRowItem = function () {
//        if (!$scope.selRow) {
//            return false;
//        }
//        if (!$scope.selRow.webConfigList) {
//            $scope.selRow.webConfigList = [];
//        }
//        $scope.selRow.webConfigList.push({
//            name: null,
//            content_url: null,
//            config_cover: null,
//            config_cover_small: null,
//            config_remark: null
//        })
//    }
//    //删除项
//    $scope.del_sel_row_item = function ($index) {
//        $scope.selRow.webConfigList.splice($index, 1)
//    }
//    //上传图片
//    $scope.selConfigImg = function (item, type, $index) {
//        $scope.accParam = {
//            type: type,
//            index: $index
//        }
//        $("#table_file_img").parent().find('.file-btn-text').click();
//    }
//    $scope.$watch("accParam", function (newV) {
//        if (newV && newV.url) {
//            if (newV.type == 1) {
//                $scope.selRow.webConfigList[newV.index]["config_cover_small"] = newV.url;
//            }
//            else {
//                $scope.selRow.webConfigList[newV.index]["config_cover"] = newV.url;
//            }
//        }
//    }, true)
})
