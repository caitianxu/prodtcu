myApp.controller('bookOffLineController', function ($rootScope, $scope, services, $sce, $stateParams) {
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

    //设备列表
    services["_facility_data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/device/getDeviceInfoBean', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/facility_data_list.json', param, "POST");
    }
    //发送、删除离线
    services["_send_book_offline"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookOrgRel/json/updateBookOffLine', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/send_book_offline.json', param, "POST");
    }

    $scope.tableControl = {
        config: {
            check: true,
            param: {
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0,
                searchText: null
            },
            columns: [
                {
                    field: 'book_cover_small', title: "封面", formatter: function (value, row) {
                    if (value) {
                        return '<img style="display: block;margin: 0px auto;" class="df_book" src="' + $rootScope.ctxPath  + value + '">'
                    }
                    return ''
                }
                },
                {field: 'book_name', title: "名称", align: 'left'},
                {field: 'book_isbn', title: "ISBN", align: 'left'},
                {
                    field: 'status', title: "状态", formatter: function (value) {
                    if (value == 1) {
                        return '<em style="color: #f90">离线中</em>'
                    }
                    else if (value == 2) {
                        return '<em style="color: #5be540">已离线</em>'
                    }
                    else {
                        return '<em style="color: #999">未离线</em>'
                    }
                }
                }
            ]
        },
        reload: function (param) {
            $scope.load();
        }
    };
    $scope.paramCol = {
        searchText: null,
        book_cat_id: null,
        book_cat_name: null,
        status: -1,
        status_text: "全部"
    }
    $scope.param = {
        book_cat_id: null,
        book_cat_name: null,
        pages: 1,
        pageSize: 10,
        pageNum: 1,
        total: 0,
        status: -1,
        searchText: null
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
        if (key == 'status') {
            $scope.paramCol.status = code;
            $scope.paramCol.status_text = value;
        }
        $scope.param["pageNum"] = 1;
        $scope.param["searchText"] = $scope.paramCol["searchText"];
        $scope.param["status"] = $scope.paramCol.status;
        for (i in $scope.param) {
            $scope.tableControl.config.param[i] = $scope.param[i];
        }
        $scope.load();
    }
    $scope.tableBook = null;
    var treeData = null;
    $scope.facilityArray = null;
    //数据加载
    $scope.load = function () {
        if (treeData) {
            services._data_book($scope.tableControl.config.param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
        else {
            services._facility_data().success(function (ss) {
                $scope.facilityArray = ss.data;
                if (ss.data.length > 0) {
                    $scope.param["device_id"] = ss.data[0].device_id;
                    $scope.param["device_code"] = ss.data[0].device_code;
                    $scope.param["status"] = ss.data[0].status;
                    if($scope.param["status"] == 1){
                        $scope.param.facility_state = "离线";
                    }else{
                        $scope.param.facility_state = "在线";
                    }
                    $scope.param["book_count"] = ss.data[0].book_count;
                    $scope.param["memory"] = ss.data[0].memory;
                }
                services._data_book_class().success(function (res) {
                    var allData = [{
                        book_cat_name: "所有资源",
                        book_cat_id: 0,
                        children: res.data
                    }]
                    treeData = allData;
                    $("#comTree").tree("loadData", allData);
                })
            })
        }
    }
    //选择设备
    $scope.setfacility = function (item) {
        $scope.param["device_id"] = item.device_id;             //设备ID
        $scope.param["device_code"] = item.device_code;         //设备名称
        $scope.param["status"] = item.status;       //网络状态
        $scope.param["book_count"] = item.book_count;         //图书离线数量
        $scope.param["memory"] = item.memory;   //剩余内存
        $scope.reload();
    }
    /**
     * 添加删除离线
     */
    $scope.updataState = function (bool) {
        var ids = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].book_id)
            }
        });
        if (ids.length == 0) {
            layer.msg("请选择你要设置的图书")
        }
        else {
            services._send_book_offline({
                device_id: $scope.param.device_id,
                ids: ids,
                bool: bool
            }).success(function (res) {
                if (res.code == 0) {
                    $scope.load();
                }
                else {
                    layer.alert(res.message);
                }
            })
        }
    }
});