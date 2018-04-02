myApp.controller('bookStaController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_bookSta"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/resourceCount/getBookCountList', param, "POST");
    }
    $scope.tableControl = {
        config: {
            check: false,
            param: {
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0,
                searchText: null
            },
            columns: [
                {field: 'org_name', title: "机构", align: 'left', width: '50px', order: true},
                {field: 'resource_name', title: "图书", align: 'left'},
                {field: 'operation_type', title: "类型", align: 'left',
                    formatter: function (value, row, index) {
                        if (value == 1) {
                            return "点击"
                        }else if(value == 2){
                            return "收藏"
                        }else if(value == 3){
                        	return "分享"
                        }else {
                        	return "评论"
                        }
                    }
                },
                {field: 'num', title: "统计次数"}
            ]
        },
        reload: function (param) {
            services._data_bookSta(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };
});