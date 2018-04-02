myApp.controller('newStaController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_newSta"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/resourceCount/getNewsCountList', param, "POST");
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
                {field: 'org_name', title: "机构", width: '50px', align: 'left', order: true},
                {field: 'resource_name', title: "资讯", align: 'left'},
                {field: 'num', title: "统计次数"}
            ]
        },
        reload: function (param) {
            services._data_newSta(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };
});