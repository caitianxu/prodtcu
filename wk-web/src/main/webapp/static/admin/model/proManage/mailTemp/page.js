myApp.controller('mailTempController', function ($rootScope, $scope, services, $sce, $stateParams) {
        $scope.services = services;
        //数据列表
        services["_data_mailTemp"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/mailTemplate/pageQuery', param, "POST");
        };
        //修改
        services["_mailTemp_save"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/mailTemplate/update', param, "POST");
        };
        $scope.tableControl = {
            config: {
                check: true,
                param: {
                    pages: 1, //总页数
                    pageNum: 1, //当前页
                    pageSize: 10, //每页条数
                    total: 0, //总条数
                },
                columns: [
                    {field: 'template_code', title: "编号", align: 'left'},
                    {field: 'mail_template_name', title: "模板名称"},
                    {field: 'user_name', title: "修改人"},
                    {field: 'update_time', title: "修改时间"}
                ]
            },
            reload: function (param) {
                services._data_mailTemp(param).success(function (res) {
                    $scope.tableControl.loadData(res.data);
                })
            }
        };
         var myIframe = document.getElementById('myIframe').contentWindow;
        $scope.selRow = {};
        //重新查询
        $scope.reload = function () {
            $scope.tableControl.config.param["pageNum"] = 1;
            services._data_mailTemp($scope.tableControl.config.param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
        //修改
        $scope.row_update = function () {
            var mailTemp_code = new Array();
            angular.forEach($scope.tableControl.rows, function (item, index) {
                if (item.select) {
                    mailTemp_code.push($scope.tableControl.data[item.index].code)
                    $scope.selRow = $scope.tableControl.data[index]
                    myIframe.ue.setContent($scope.selRow.content)

                }
            });
            if (mailTemp_code.length == 0) {
                layer.alert("请选择你将要修改的数据");
            } else if (mailTemp_code.length > 1) {
                layer.alert("请选择一条数据");
            } else {
                $rootScope.formOpen();
            }
        }
        //提交
        $scope._form_submit = function () {
            $scope.selRow.content = document.getElementById('myIframe').contentWindow.ue.getContent()
            services._mailTemp_save($scope.selRow).success(function (res) {
                if (res.code == 0) {
                    $rootScope.formClose();
                    layer.msg('信息保存成功');
                    $scope.reload();
                }
                else {
                    layer.msg(res.message);
                }
            })
        }
    }
);