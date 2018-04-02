var edition_list, updateConfig;
myApp.controller('largeConfigController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //大屏配置列表
    services["_largeConfig_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/deviceset/configList', param, "POST");
    }
    //版本列表
    services["_edition_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/appVersion/allList', param, "POST");
    }
    //大屏配置新增-修改
    services["_largeConfig_Update"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/deviceset/updateConfig', param, "POST");
    }
    //大屏配置删除
    services["_largeConfig_Del"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/deviceset/delectConfig', param, "POST");
    }
    //大屏配置--设备下拉
    services["_largeConfig_Tree"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/device/getOrgDevices', param, "POST");
    }
    //删除使用设备
    services["_edition_del"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/deviceset/deleteDevices', param, "POST");
    }

    //更新配置
    services["_update_config"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/deviceset/updateDeviceConfig', param, "POST");
    }

    /*
     LOGO
     */
    <!--文件上传-->
    $('#prj-log1').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["logo_url"] = res.data[0].url;
            })
        }
    });
    /*
     首页背景
     */
    <!--文件上传-->
    $('#prj-log2').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["home_url"] = res.data[0].url;
            })
        }
    });
    /*
     列表背景
     */
    <!--文件上传-->
    $('#prj-log3').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["list_url"] = res.data[0].url;
            })
        }
    });
    /*
     详细背景
     */
    <!--文件上传-->
    $('#prj-log4').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["detail_url"] = res.data[0].url;
            })
        }
    });
    /*
     大屏配置图标
     */
    <!--文件上传-->
    $('#prj-log5').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.titleTextData['content_icon'] = res.data[0].url;
            })
        }
    });

    $scope.ScreenData = [];

    //配置内容下拉
    $scope.configTypeSelect = [{
        name: "自定义",
        id: "custom"
    }, {
        name: "图书",
        id: "book"
    }, {
        name: "报纸",
        id: "newspaper"
    }, {
        name: "期刊杂志",
        id: "magazine"
    }, {
        name: "视频",
        id: "video"
    }, {
        name: "音频",
        id: "audio"
    }, {
        name: "新闻",
        id: "news"
    }, {
        name: "用户反馈",
        id: "feedback"
    }]

    $scope.tableControl = {
        config: {
            lines: true,
            check: true,
            param: {
                pages: 1,
                pageNum: 1,
                pageSize: 50,
                total: 0,
                searchText: null
            },
            columns: [
                {field: 'conf_name', title: "名称", align: 'left'},
                {field: 'content_num', title: "导航数量"},
                {field: 'update_time', title: "更新时间"},
                {
                    field: 'is_default', title: "是否默认", formatter: function (value, row, index) {
                    return value == 1 ? '是' : '否'
                }
                },
                {
                    field: 'device_num', title: "使用设备", formatter: function (value, row, index) {
                    return '<a style="color:#4f66ff" onclick="edition_list(' + row.device_num + ',' + row.conf_id + ', event)">' + value + '</a>'
                }
                }
            ]
        },
        reload: function (param) {
            services._largeConfig_Data(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };
    $scope.selRow = {
        tabList: []
    }
    //页面操作内容
    $scope.param = {
        searchText: null
    }
    //重新查询
    $scope.reload = function (key, value) {
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["pageNum"] = 1;
        services._largeConfig_Data($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }
    var upIndex = 0;
    //配置顺序切换/删除
    $scope.configTabindex = function (index, idxType) {
        var dd = $scope.selRow.tabList[index]
        //删除
        if (idxType == 0) {
            $scope.selRow.tabList.splice(index, 1)
            return
        }
        //上移
        if (idxType == 1 && index != 0) {
            $scope.selRow.tabList[index] = $scope.selRow.tabList[index - 1]
            $scope.selRow.tabList[index - 1] = dd
            return
        }
        //下移
        if (idxType == 2 && index < $scope.selRow.tabList.length - 1) {
            $scope.selRow.tabList[index] = $scope.selRow.tabList[index + 1]
            $scope.selRow.tabList[index + 1] = dd
            return
        }
        //修改
        if (idxType == 3) {
            $scope.configTabUpdate = true
            $scope.titleTextData = {
                content_name: $scope.selRow.tabList[index].content_name,
                content_icon: $scope.selRow.tabList[index].content_icon,
                content_type_name: $scope.selRow.tabList[index].content_type_name,
                content_type: $scope.selRow.tabList[index].content_type,
                content_url: $scope.selRow.tabList[index].content_url
            }
            $("#pub_file .file-btn-input").val($scope.titleTextData.content_icon);
            //$scope.selRow.tabList.splice(index, 1)
            upIndex = index;
            layer_form = layer.open({
                type: 1,
                title: "修改菜单",
                area: ["700px", "300px"],
                content: $("#upFx_row")
            });
        }
    }
    //表单配置新增
    $scope.configAdd = function (bool) {
        if (!$scope.titleTextData.content_name) {
            layer.alert('请输入名称')
            return false
        }
        if (!$scope.titleTextData.content_icon) {
            layer.alert('请上传图标')
            return false
        }
        if ($scope.titleTextData.content_type == "custom" && !$scope.titleTextData.content_url) {
            layer.alert('请输入链接地址')
            return false
        }
        $('#prj-log5').parent().find('div > div > input').val("");
        //这样赋值不会绑定.
        if ($scope.configTabUpdate) {
            $scope.selRow.tabList[upIndex] = {
                content_name: $scope.titleTextData.content_name,
                content_icon: $scope.titleTextData.content_icon,
                content_type_name: $scope.titleTextData.content_type_name,
                content_type: $scope.titleTextData.content_type,
                content_url: $scope.titleTextData.content_url
            }
        }
        else {
            $scope.selRow['tabList'].push({
                content_name: $scope.titleTextData.content_name,
                content_icon: $scope.titleTextData.content_icon,
                content_type_name: $scope.titleTextData.content_type_name,
                content_type: $scope.titleTextData.content_type,
                content_url: $scope.titleTextData.content_url
            })
        }
        //确定修改按钮显示隐藏
        $scope.configTabUpdate = false
        $scope.titleTextData = {
            content_name: "",
            content_icon: "",
            content_type_name: "自定义",
            content_type: "custom",
            content_url: ""
        }
        layer.close(layer_form);
    }
    $scope.titleTextData = {
        content_name: "",//内容名称
        content_icon: "",// 图标
        content_type_name: "自定义",
        content_type: "custom",
        content_url: "",//链接地址
    }
    //新增
    $scope.addRow = function () {
        $scope.selRow = {
            tabList: []
        }
        $('#prj-log1').parent().find('>div input').val('')
        //图片路径
        $('#prj-log1 + div > div > input').val("")
        $('#prj-log2 + div > div > input').val("")
        $('#prj-log3 + div > div > input').val("")
        $('#prj-log4 + div > div > input').val("")
        $scope.status = true
        $rootScope.formOpen();
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
                $scope.selRow = $scope.tableControl.data[index];
                $scope.selRow["device_ids"] = null;
                $scope.selRow['version_code'] = $scope.selRow.version
                $scope.ScreenData =[];
                var urlList = new Array();
                if($scope.selRow.screensaver_url != ''){
                    urlList = $scope.selRow.screensaver_url
                    $scope.ScreenData = urlList.split(",")
                    console.log($scope.ScreenData)
                }
                angular.forEach($scope.ScreenData,function(item,index){
                    console.log(item);
                })
            }
        })
        //匹配类型
        angular.forEach($scope.selRow.tabList, function (item, index) {
            angular.forEach($scope.configTypeSelect, function (i, k) {
                if (item.content_type == i.id) {
                    $scope.selRow.tabList[index].content_type_name = i.name
                }
            })
        })

        //图片路径
        if ($scope.selRow.logo_url)
            $('#prj-log1 + div > div > input').val($scope.selRow.logo_url)
        if ($scope.selRow.home_url)
            $('#prj-log2 + div > div > input').val($scope.selRow.home_url)
        if ($scope.selRow.list_url)
            $('#prj-log3 + div > div > input').val($scope.selRow.list_url)
        if ($scope.selRow.detail_url)
            $('#prj-log4 + div > div > input').val($scope.selRow.detail_url)
    }
    //删除
    $scope.delRow = function () {
        var conf_ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                conf_ids.push($scope.tableControl.data[index].conf_id)
            }
        });
        if (conf_ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._largeConfig_Del({confs: conf_ids}).success(function (res) {
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
    //提交
    $scope._form_submit = function (bool) {
        var order_weight = new Date().getTime();
        if ($scope.selRow.tabList && $scope.selRow.tabList.length > 0) {
            $.each($scope.selRow.tabList, function (index, item) {
                item.order_weight = order_weight - index;
            });
        }
        services._largeConfig_Update($scope.selRow).success(function (res) {
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
    $scope.DeviceControl = {
        config: {
            check: true,
            param: {
                pageNum: 1, //当前页
                pageSize: 10, //每页条数
            },
            columns: [
                {field: 'org_name', title: "机构", align: 'left'},
                {field: 'device_code', title: "设备编号", align: 'left'},
                {field: 'address', title: "地址", align: 'left'},
                {
                    field: 'action', title: "操作", formatter: function (value, row, index) {
                    if (!row.conf_id) {
                        return '<a href="javascript:void(0)" style="background:#ef4f4f;color: #fff;font-size: 12px;padding: 4px 10px;text-decoration: none" onclick="updateConfig(' + row.device_id + ',' + row.org_id + ')">更新配置</a>'
                    } else {
                        return '已配置'
                    }
                }
                },
            ]
        },
        reload: function (param) {
            //初始化
            $rootScope.servers._Version_list(param).success(function (res) {
                $scope.DeviceControl.loadData(res.data);
            })
        }
    }
    //使用设备列表
    edition_list = function (device_num, conf_id, event) {
        $rootScope.stopEvent(event);
        //使用设备列表
        $scope.DeviceControl.config.param.conf_id = conf_id
        $scope.DeviceControl.config.param.searchText = $scope.DeviceControl.dev_searchText || '';
        $scope.DeviceControl.config.param.pageNum = 1;
        //$scope.DeviceControl.reload($scope.DeviceControl.config.param)
        $scope.openDev();
    }
    var layer_form = null;
    $scope.openForm = function () {
        layer_form = layer.open({
            type: 1,
            title: "新增菜单",
            area: ["700px", "300px"],
            content: $("#upFx_row")
        });
    }
    /**
     *更新配置
     */
    updateConfig = function (device_id, org_id) {
        var layerConfirm = layer.confirm('请确认是否更新', {btn: ['确定', '取消'], closeBtn: 0}, function () {
            services._update_config({
                device_id: device_id,
                org_id: org_id,
                conf_id: $scope.DeviceControl.config.param.conf_id
            }).success(function (res) {
                if (res.code == 0) {
                    layer.msg(res.message)
                    $scope.DeviceControl.reload($scope.DeviceControl.config.param)
                } else {
                    layer.msg(res.message)
                }
            })
            layer.close(layerConfirm)
        })
    }
    //屏保设置
    $scope.setScreen = function(){
        // $('#img_cc').html('')
        $('#prj-log6').parent().find('>div input').val('')
        $('#prj-log6 + div > div > input').val("")
        layer.open({
            type: 1,
            title: "",
            area: ["800px", "600px"],
            content: $("#setScreen")
        });
    }
    $scope.ScreenList = [
        {id:5,},{
        id:10,
        },{
        id:15,
        },{
        id:20,
        },{
        id:30,
        },{
        id:60,
        },
    ]

        // <!--图片上传-->
        $('#prj-log6').prettyFile({
            text: "上传图片",
            change: function (res, obj) {
                $scope.$apply(function () {
                    $scope.ScreenData.push(res.data[0].url);
                })
            }
        });

    $scope.submitScreen = function (){
        // $scope.selRow.screensaver_url ='';
        if($scope.ScreenData.length > 0 && $scope.ScreenData.length <=5){
            $scope.selRow.screensaver_url =$scope.ScreenData.join(',');
            console.log($scope.selRow.screensaver_url);
            layer.msg('保存成功！')
        }
         if($scope.ScreenData.length > 5 ){
            layer.msg('最多只能传5张图片！')
            return false;
        }
        if($scope.ScreenData.length == 0){
            return false;
        }
    }
    //关闭屏保设置弹窗
    $scope.CloseScreen = function () {
        layer.closeAll()
    }
    //删除屏保图片
    $scope.del_Screen = function (i) {
        angular.forEach($scope.ScreenData,function(item,index){
            if(index == i){
                $scope.ScreenData.splice(index, 1);
            }
        })

    }

});