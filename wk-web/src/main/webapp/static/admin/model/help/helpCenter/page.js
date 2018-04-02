var startStop;
myApp.controller('helpCenterController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_helpCenter"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/helpCenter/getList', param, "POST");
    }
    //创建问题
    services["_add_helpCenter"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/helpCenter/newHelp', param, "POST");
    }
    //回复问题
    services["_reply_helpCenter"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/helpCenter/replyHelp', param, "POST");
    }
    //删除
    services["_del_helpCenter"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/helpCenter/deleteHelp', param, "POST");
    }
    $scope.tableControl = {
        config: {
            lines: true,
            check: true,
            param: {
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0,
                searchText: null
            },
            columns: [
                {field: 'id',title: "序号", align:'center', width: '50px'},
                {field: 'title', title: "标题", align: 'left'},
                {field: 'create_name', title: "创建人", align: 'center', width: '50px'},
                {field: 'reply_name', title: "回复人", align: 'center', width: '50px'},
                {field: 'create_time', title: "创建时间", align: 'center',width: '250px'},
                {field: 'reply_time', title: "回复时间", align: 'center',width: '250px'},
                {field: 'action',title:'查看',align: 'center',width :'50px',formatter: function (value, row, index) {
                    return '<a href="'+ ctxPath +'/admin/help/'+ row.id +'" target="_blank">查看</a>'
                }}
            ]
        },
        reload: function (param) {
            services._data_helpCenter(param).success(function (res) {
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
    $scope.reload = function (key, value) {
            $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
            $scope.tableControl.config.param["pageNum"] = 1;
            services._data_helpCenter($scope.tableControl.config.param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
    }


    //回复
    $scope.row_update = function (bool) {

        var ids = new Array();
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].id)
                $scope.selRow = $scope.tableControl.data[item.index]
            }
        });
        if (ids.length == 0) {
            layer.alert("请选择回复的问题");
            return false;
        }
        if (ids.length > 1) {
            layer.alert("请一条一条回复");
            return false;
        }
        $scope.status=false;
        $rootScope.formOpen();
            angular.forEach($scope.tableControl.rows, function (item, index) {
                if (item.select) {
                    $scope.selRow = $scope.tableControl.data[index]
                }
            })
        var a = document.getElementById('myIframeReply').contentWindow
                a.ue.setContent($scope.selRow.reply_content);
    }





    //新增
    $scope.addRow = function () {
        $scope.selRow = {}
        $scope.status = true
        $rootScope.formOpen();
    }

    //保存
    $scope._form_submit = function (bool) {
        //创建问题保存
        if (!$scope.selRow.title) {
            layer.alert("请填写标题")
            return false;
        }
        if($scope.status){
            var a = document.getElementById('myIframe').contentWindow
            $scope.selRow.content = a.ue.getContent()
            if (!$scope.selRow.content || $scope.selRow.content == "") {
                layer.alert("请填写内容")
                return false;
            }
            services._add_helpCenter($scope.selRow).success(function (res) {
                if (res.code == 0) {
                    if (bool) {
                        $rootScope.formClose();
                    }
                    else {
                        var a = document.getElementById('myIframe').contentWindow
                        $scope.selRow = {
                            title: ""
                        };
                    }
                    $scope.reload();
                    layer.msg('信息保存成功');
                }
                else {
                    layer.msg(res.message);
                }
            })
        }else{
            //回复问题保存
            var a = document.getElementById('myIframeReply').contentWindow
            $scope.selRow.reply_content = a.ue.getContent()
            if (!$scope.selRow.reply_content || $scope.selRow.reply_content == "") {
                layer.alert("请填写内容")
                return false;
            }
            services._reply_helpCenter($scope.selRow).success(function (res) {
                if (res.code == 0) {
                    if (bool) {
                        $rootScope.formClose();
                    }
                    else {
                        var a = document.getElementById('myIframeReply').contentWindow
                        $scope.selRow = {
                            title: ""
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
    }

    //删除
    $scope.delRow = function () {
        var ids = new Array();
        var mark = null;
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].id)
            }
        });
        if (ids.length == 0) {
            layer.alert("请选择你将要删除的数据");
        }
        else {
            layer.confirm('删除后数据无法找回,确认删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                services._del_helpCenter({ids: ids, mark: mark}).success(function (res) {
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

});