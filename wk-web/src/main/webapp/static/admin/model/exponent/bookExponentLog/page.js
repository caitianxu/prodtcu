myApp.controller('bookExponentLogController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //记录列表
    services["_bookIndexRecord_list"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookIndexRecord/getList', param, "POST");
    }
    //项目机构树
    services["_pro_org_tree"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/project/getProOrgTree', param, "POST");
    }

    var date = new Date();
    date.setDate(date.getDate() - 1);
    var dd = date.getFullYear() + "-" +(date.getMonth() + 1) + "-" + date.getDate();
    $scope.tableControl = {
        config: {
            check: false,
            param: {
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0,
                create_time: dd,
                id: $rootScope._USERINFO.org_id == 1 ? null : $rootScope._USERINFO.org_id,
                type: 'org',
                code: null

            },
            columns: [
                {field: 'book_name', title: "图书名称", align: 'left', width: '150px', order: true},
                {
                    field: 'book_type', title: "图书类型",width: '150px', formatter: function (value) {
                    if (value == 1) {
                        return "网文"
                    } else {
                        return "出版"
                    }
                    }
                },
                {field: 'nick_name', title: "会员名称",width:"150px", align: 'left'},
                {field: 'org_name', title: "机构名称",width: "150px", align: 'left'},
                {
                    field: 'record_type', title: "类型",width: "150px",
                    formatter:function(val, row, index){
                        if(val == 1) {
                            return "点击详情"
                        }else if(val == 2) {
                            return "收藏"
                        }else if(val == 3) {
                            return "分享"
                        }else if(val == 4) {
                            return "评论"
                        }else if(val == 5) {
                            return "阅读"
                        }

                    }
                },
                {
                    field: 'device_type_code', title: "终端",width: "150px"
                },
                {field: 'create_time', title: "创建时间",width: "150px"}
            ]
        },
        reload: function (param) {
            services._bookIndexRecord_list(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };
    $scope.selRow = {
        create_time: dd,
        id: $rootScope._USERINFO.org_id == 1 ? null : $rootScope._USERINFO.org_id,
        type: 'org',
        code: null
    }

    $scope.rest = function(){
        $scope.selRow.create_time = null
    }
    //加载
    $scope.load = function () {
        if(!$scope.selRow.id) {
            services._pro_org_tree().success(function (res) {
                var arrdata = [];
                if (res.data) {
                    arrdata = res.data;
                }
                var allData = [{
                    text: "所有资源",
                    id: 0,
                    type: "",
                    code: "",
                    children: arrdata
                }]
                $("#comTree").tree("loadData", allData);
                $scope.tableControl.config.param.create_time = $scope.selRow.create_time;
            })
        }
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
            $scope.tableControl.config.param.id = node.id;
            $scope.tableControl.config.param.type = node.type;
            $scope.tableControl.config.param.code = node.code;
            $scope.tableControl.config.param["pageNum"] = 1;
            services._bookIndexRecord_list($scope.tableControl.config.param).success(function (res) {
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
    function changeTreeStyle1(treeNode) {
        if (treeNode['children'] && treeNode['children'].length > 0) {
            for (var j = 0; j < treeNode['children'].length; j++) {
                changeTreeStyle1(treeNode['children'][j]);
            }
        }
        //设置属性
        treeNode["text"] = treeNode.text;
        treeNode["id"] = treeNode.id;
    }

    //重新查询
    $scope.reload = function (key, value) {
        selNode = $("#comTree").tree("getSelected");
        $scope.tableControl.config.param.id = selNode.id;
        $scope.tableControl.config.param.type = selNode.type;
        $scope.tableControl.config.param.code = selNode.code;
        $scope.tableControl.config.param.create_time = $scope.selRow.create_time;

        $scope.tableControl.config.param["pageNum"] = 1;
        services._bookIndexRecord_list($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }

    $scope.load();
});