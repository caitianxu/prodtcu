var startStop;
myApp.controller('outfitController', function ($rootScope, $scope, services, $sce, $stateParams) {
        $scope.services = services;
        //新增机构 修改 启用停用
        services["_add_outfit"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/organization/init', param, "POST");
        }
        //删除
        services["_del_outfit"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/organization/deleteExtend', param, "POST");
        }
        //更改机构锁定密码状态
        services["_pwd_lock"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/organization/isLock', param, "POST");
        }

        $scope.tableControl = {
            config: {
                check: true,
                param: {
                    orderField: "res_name",
                    orderType: "desc",
                    pages: 1, //总页数
                    pageNum: 1, //当前页
                    pageSize: 10, //每页条数
                    total: 0, //总条数
                    searchText: null
                },
                columns: [
                    {field: 'extend_code', title: "机构编码", align: 'left'},
                    {field: 'short_name', title: "机构简称", align: 'left'},
                    {field: 'org_name', title: "机构名称", align: 'left'},
                    {field: 'address', title: "区域", align: 'left'},
                    {field: 'street', title: "街道", align: 'left'},
                    {field: 'project_name', title: "所属项目", align: 'left'},
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
                    {field: 'create_time', title: "创建时间"},
                    {
                        field: 'pwd_lock', title: "锁定密码",
                        formatter: function (value, row, index) {
                            var bool = row.pwd_lock == 1 ? 'layui-form-onswitch' : '';
                            return '<div class="layui-unselect layui-form-switch ' + bool + '" onclick="isLock(' + index + ', event)"><i></i></div>';
                        }
                    }
                ]
            },
            reload: function (param) {
                services._outfit(param).success(function (res) {
                    $scope.tableControl.loadData(res.data);
                })
            }
        };

        $scope.selRow = {}
        $scope.provinceList = [];
        $scope.cityList = [];
        $scope.areaList = [];
        //页面操作内容
        $scope.param = {
            "pageNum": 1,
            "pageSize": 10,
            searchText: null
        }
        //重新查询
        $scope.reload = function () {
            $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
            $scope.tableControl.config.param["pageNum"] = 1;
            $scope.tableControl.reload($scope.tableControl.config.param);
        }
        var selNode = null;
        $("#comTree").tree({
            //数据过滤
            "loadFilter": function (data, parent) {
                for (var i = 0; i < data.length; i++) {
                    changeTreeStyle1(data[i]);
                }
                return data;
            },
            "onSelect": function (node) {
                $scope.tableControl.config.param.project_code = node.id
                services._outfit($scope.tableControl.config.param).success(function (res) {
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
                if (selNode)
                    openNode(selNode);
            }
        });
        function openNode(node) {
            if (node.pid) {
                $("#comTree").tree("expand", node.target);
                var mm = $("#comTree").tree("find", node.pid);
                if (node) {
                    openNode(mm);
                }
            }
        }

        function changeTreeStyle1(treeNode) {
            if (treeNode['children'] && treeNode['children'].length > 0) {
                for (var j = 0; j < treeNode['children'].length; j++) {
                    changeTreeStyle1(treeNode['children'][j]);
                }
            }
            //设置属性
            treeNode["text"] = treeNode.project_name;
            treeNode["id"] = treeNode.project_code;
        }

        //新增
        $scope.addRow = function () {
            $scope.provinceList = []
            $scope.status = true
            $scope.selRow = {
                type: 'add'
            }
            //省赋值 cityData3:省市区js对象
            angular.forEach(cityData3, function (item, index) {
                $scope.provinceList.push(item)
            })

            $rootScope.formOpen();

        }
        //修改
        $scope.row_update = function () {
            $scope.status = false
            var org_id = new Array();
            angular.forEach($scope.tableControl.rows, function (item, index) {
                if (item.select) {
                    org_id.push($scope.tableControl.data[item.index].org_id)
                    $scope.selRow = $scope.tableControl.data[index]
                    $scope.selRow['type'] = 'update'
                    if(!$scope.selRow['area']){
                        $scope.selRow['area'] ='无';
                     }
                }
            });
            if (org_id.length > 1) {
                layer.alert("同时只能修改一条数据");
                return false
            }
            if (org_id.length == 0) {
                layer.alert("请选择你将要修改的数据");
            } else {
                $scope.provinceList = []
                angular.forEach(cityData3, function (item, index) {
                    $scope.provinceList.push(item)
                })
                if ($scope.selRow.province_id) {
                    angular.forEach($scope.provinceList, function (item, index) {
                        if ($scope.selRow.province_id == item.value) {
                            $scope.cityList = item.children
                        }
                    })
                }
                if ($scope.selRow.city_id) {
                    angular.forEach($scope.cityList, function (item, index) {
                        if ($scope.selRow.city_id == item.value) {
                            $scope.areaList = item.children
                        }
                    })
                }
                $rootScope.formOpen();
            }
        }
        //机构简称验证
        $scope.short_Change = function () {
            //是否包含汉字
            var reg = /[\u4E00-\u9FA5]/g;
            //是否包含特殊字符 /g是只所有
            var symbol = /['"!@#$%&^.,~()_+-={}|>?。，；’‘［］／、＝－｀` \^*]/g;
            if (reg.test($scope.selRow.short_name)) {
                //删除汉字
                $scope.selRow.short_name = $scope.selRow.short_name.replace(reg, '')
            }
            if (symbol.test($scope.selRow.short_name)) {
                //删除字符
                $scope.selRow.short_name = $scope.selRow.short_name.replace(symbol, '')
            }
        }

        //提交
        $scope._form_submit = function (bool) {
            if (!$scope.selRow.short_name) {
                layer.alert('机构简称不能为空')
                return false
            }
            if (!$scope.selRow.org_name) {
                layer.alert('机构名不能为空')
                return false
            }
            if (!$scope.selRow.project_code) {
                layer.alert('项目名称不能为空')
                return false
            }
            if (!$scope.selRow.province) {
                layer.alert('请选择省')
                return false
            }
            if (!$scope.selRow.city) {
                layer.alert('请选择市')
                return false
            }
            if (!$scope.selRow.area) {
                layer.alert('请选择区')
                return false
            }

            $scope.selRow.area = $scope.selRow.area == '无' ? "" : $scope.selRow.area
            services._add_outfit($scope.selRow).success(function (res) {
                if (res.code == 0) {
                    if (bool) {
                        $rootScope.formClose();
                    }
                    else {
                        $scope.selRow = {};
                    }
                    layer.msg('信息保存成功');
                    $scope.reload();
                }
                else {
                    layer.msg(res.message);
                }
            })
        }

        //删除
        $scope.delRow = function () {
            var org_id = new Array();
            var mark = null;
            angular.forEach($scope.tableControl.rows, function (item, index) {
                if (item.select) {
                    org_id.push($scope.tableControl.data[item.index].org_id)
                }
            });
            if (org_id.length == 0) {
                layer.alert("请选择你将要删除的数据");
            }
            else if (org_id.length > 1) {
                layer.alert("机构不支持批量删除");
            }
            else {
                layer.confirm('删除后数据无法找回,并且删除关联组织,确认删除吗？', {
                    btn: ['确定', '取消'] //按钮
                }, function () {
                    services._del_outfit({org_id: org_id[0], mark: mark}).success(function (res) {
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


        //设置省
        $scope.set_province = function (item) {
            $scope.selRow.province = item.text;
            $scope.selRow.province_id = item.value;
            $scope.selRow.city = null;
            $scope.selRow.city_id = null;
            $scope.selRow.area = null;
            $scope.selRow.area_id = null;
            $scope.cityList = [];
            $scope.areaList = []
            angular.forEach($scope.provinceList, function (ii, index) {
                if (ii.value == $scope.selRow.province_id) {
                    $scope.cityList = ii.children
                }
            })
        }

        //项目
        $scope.projectData = null;
        services._project($scope.param).success(function (res) {
            $scope.projectData = res.data
            var allData = [{
                project_name: "所有项目",
                project_id: null,
                project_code: null,
                children: res.data ? res.data : []
            }]
            selNode = $("#comTree").tree("getSelected");
            $("#comTree").tree("loadData", allData);
        })
        //设置市
        $scope.set_city = function (item) {
            $scope.selRow.city = item.text;
            $scope.selRow.city_id = item.value;
            $scope.selRow.area = null;
            $scope.selRow.area_id = null;
            $scope.areaList = []
            angular.forEach($scope.cityList, function (ii, index) {
                if (ii.value == $scope.selRow.city_id) {
                    if (ii.children) {
                        $scope.areaList = ii.children
                    } else {
                        $scope.selRow.area = '无'
                    }
                }

            })
        }
        //设置区
        $scope.set_area = function (item) {
            $scope.selRow.area = item.text
            $scope.selRow.area_id = item.value
        }
        //启用停用
        startStop = function (index, event) {
            $rootScope.stopEvent(event);
            $scope.selRow = $scope.tableControl.data[index]
            $scope.selRow.enabled = $scope.selRow.enabled == 1 ? 2 : 1
            services._add_outfit($scope.selRow).success(function (res) {
                if (res.code == 0) {
                    layer.msg(res.message);
                    $scope.tableControl.reload($scope.tableControl.config.param);
                }
                else {
                    layer.msg(res.message);
                }
            })
        }

        //锁定密码
        isLock = function (index, event) {
            $rootScope.stopEvent(event);
            $scope.selRow = $scope.tableControl.data[index]
            $scope.selRow.pwd_lock = $scope.selRow.pwd_lock == 1 ? 2 : 1
            services._pwd_lock($scope.selRow).success(function (res) {
                if (res.code == 0) {
                    layer.msg(res.message);
                    $scope.tableControl.reload($scope.tableControl.config.param);
                }
                else {
                    layer.msg(res.message);
                }
            })
        }
    }
);

