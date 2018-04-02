myApp.controller('cmsTemplateController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //资讯模版列表
    services["_cms_data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/articleTemp/json/list', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }
    //资讯新增/修改
    services["_cms_add"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/articleTemp/json/updateArticleTemp', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }
    //机构树
    services["_org_data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/organization/getRootOrgList', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }
    <!--文件上传-->
    $('#prj-log').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            $scope.$apply(function () {
                $scope.selRow["temp_url"] = res.data[0].url;
            })
        }
    });
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
                {field: 'temp_name', title: "模版名称", align: 'left'},
                {field: 'user_name', title: "上传人"},
                {field: 'upload_time', title: "上传时间"},
                {field: 'temp_remark', title: "备注"}
            ]
        },
        reload: function (param) {
            services._cms_data(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
            services._org_data().success(function (res) {
                $scope.orgData=res.data
            })
        }
    };

    $scope.orgData={}
    $scope.selRow = {}
    //新增
    $scope.addRow = function () {
        $scope.selRow = {}
        $scope.status=true
        $rootScope.formOpen();

    }
    //修改
    $scope.row_update = function () {
        var org_id = new Array();
        $scope.status=false
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                org_id.push($scope.tableControl.data[item.index].org_id)
                $scope.selRow = $scope.tableControl.data[index]
                $scope.formType = 'update'
            }
        });
        if (org_id.length == 0) {
            layer.alert("请选择你将要修改的数据");
            return false
        }
        $rootScope.formOpen();
        if($scope.selRow.temp_url)
            $('#prj-log + div > div > input').val($scope.selRow.temp_url)
    }

    //提交
    $scope._form_submit = function (bool) {
        if(!$scope.selRow.temp_url){
            layer.alert('请上传模版')
            return false
        }
        services._cms_add($scope.selRow).success(function (res) {
            if (res.code == 0) {
                if (bool) {
                    $rootScope.formClose();
                }
                else {
                    $scope.selRow = {
                        org_name: $scope.selRow.org_name,
                        org_id: $scope.selRow.org_id
                    };
                }
                layer.msg('上传成功');
                $scope.tableControl.reload($scope.tableControl.config.param);
            }
            else {
                layer.msg(res.message);
            }
        })
    }
});