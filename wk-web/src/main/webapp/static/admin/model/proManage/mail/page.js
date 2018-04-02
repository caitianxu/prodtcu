myApp.controller('mailController', function ($rootScope, $scope, services, $sce, $stateParams) {
        $scope.services = services;
        //数据列表
        services["_data_mail"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/mailSetting/pageQuery', param, "POST");
        };
        //新增/修改
        services["_mail_save"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/mailSetting/update', param, "POST");
        };
        //删除
        services["_mail_del"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/mailSetting/deleteByIdStr', param, "POST");
        };
        //测试邮箱
        services["_mail_test"] = function (param) {
            return $rootScope.serverAction(ctxPath + '/admin/mailSetting/sendMail', param, "POST");
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
                    {field: 'mail_setting_id', title: "编号", align: 'left'},
                    {field: 'smtp_url', title: "SMTP服务器地址"},
                    {field: 'account', title: "账号"},
                    {field: 'available_times', title: "可用次数"},
                    {field: 'used_times', title: "已用次数"},
                    {
                        field: 'auto_zero', title: "是否清零", formatter: function (value, row, index) {
                        return value == 1 ? "是" : "否"
                    }
                    },
                    {
                        field: 'status', title: "状态", formatter: function (value, row, index) {
                        return value == 1 ? "有效" : "无效"
                    }
                    }
                ]
            },
            reload: function (param) {
                services._data_mail(param).success(function (res) {
                    $scope.tableControl.loadData(res.data);
                })
            }
        };
        $scope.selRow = {};

        //重新查询
        $scope.reload = function () {
            $scope.tableControl.config.param["pageNum"] = 1;
            services._data_mail($scope.tableControl.config.param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
        //新增
        $scope.addRow = function () {
            $scope.selRow = {
                is_authenticate: 1,
                is_ssl: 1,
                used_times: 0,
                available_times: 600,
                auto_zero: 1
            }
            $scope.status = true
            $rootScope.formOpen();
        }
        //修改
        $scope.row_update = function () {
            var mail_code = new Array();
            angular.forEach($scope.tableControl.rows, function (item, index) {
                if (item.select) {
                    mail_code.push($scope.tableControl.data[item.index].code)
                    $scope.selRow = $scope.tableControl.data[index]
                }
            });
            if (mail_code.length == 0) {
                layer.alert("请选择你将要修改的数据");
            } else if (mail_code.length > 1) {
                layer.alert("请选择一条数据");
            } else {
                $scope.status = false
                $rootScope.formOpen();
            }
        }
        //提交
        $scope._form_submit = function (bool) {
            if (!$scope.selRow.smtp_url) {
                layer.alert("请填写SMTP服务器地址")
                return !1;
            }
            if (!$scope.selRow.account) {
                layer.alert("请填写账号")
                return !1;
            }
            if (!$scope.selRow.pwd) {
                layer.alert("请填写密码")
                return !1;
            }
            services._mail_save($scope.selRow).success(function (res) {
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
            var mail_codes = new Array();
            angular.forEach($scope.tableControl.rows, function (item, index) {
                if (item.select) {
                    mail_codes.push($scope.tableControl.data[item.index].mail_setting_id)
                }
            });
            if (mail_codes.length == 0) {
                layer.alert("请选择你将要删除的数据");
                return !1
            }
            services._mail_del({ids: mail_codes}).success(function (res) {
                if (res.code == 0) {
                    layer.alert("删除成功")
                    $scope.reload()
                } else {
                    layer.alert(res.message)
                }
            })

        };
        $scope._mail_test = function () {
            $rootScope.layerfunTree = layer.open({
                type: 1,
                title: "发送测试",
                area: ["400px", "180px"],
                content: $("#layerOpen")
            })
        }
        $scope._mail_test_submit = function () {
            if (!$scope.selRow.account) {
                layer.alert("请填写邮箱")
                return !1
            }
            var email_test = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
            if (!email_test.test($scope.selRow.account)) {
                layer.alert("邮箱格式不正确")
                return !1
            }
            services._mail_test({mail_setting_id:$scope.selRow.mail_setting_id,email:$scope.selRow.email}).success(function (res) {
                if (res.code == 0) {
                    layer.alert('测试通过');
                    $rootScope.closeLayer()
                }
                else {
                    $scope.selRow.account = null;
                    layer.alert(res.message)
                }
            })
        }
    }
);