var startStop,memberMonthList;
myApp.controller('tbMemberSignController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_signList"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/memberSign/getList', param, "POST");
    }
    //会员月签到记录
    services["_date_monthList"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/memberSign/monthList', param, "POST");
        //return $rootScope.serverAction(ctxPath + '/static/admin/json/data_cmsTemplate.json', param, "POST");
    }


    var date = new XDate().toString('yyyy-MM');

    $scope.tableControl = {
        config: {
            check: true,
            param: {
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0,
                date_time: date ,
                searchText: null
            },
            columns: [
                {field: 'nick_name', title: "昵称", align: 'center'},
                {field: 'account' , title: "账号" , align: 'center'},
                {field: 'sign_month_count', title: "月累计签到天数",align: 'center'},
                {
                    field: 'link', title: "操作", formatter: function (value, row, index) {
                       return '<a href="javascript:void(0)" onclick="memberMonthList(' + row.member_id + ')">查看</em>';
                }
                }
            ]
        },
        reload: function (param) {
            services._data_signList(param).success(function (res) {
                $scope.tableControl.loadData(res.data);
            })
        }
    };

    $scope.selRow = {}
    //页面操作内容
    $scope.param = {
        searchText: null,
    }


    $scope.paramCol = {
        searchText: null,
        monthText: new XDate().toString('yyyy-MM')
    }
    //重新查询
     $scope.reload = function () {
     $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
     $scope.tableControl.config.param["pageNum"] = 1;
     $scope.tableControl.config.param["date_time"] = $scope.paramCol.monthText
     $scope.tableControl.reload($scope.tableControl.config.param);
     }

    $scope.setTime=function(value){
        $scope.paramCol.monthText=new XDate().toString('yyyy')+'-'+value;
        $scope.reload();
    }


    $scope.memberSignArray = {};
    memberMonthList = function (member_id){
        $rootScope.layerfunTree = layer.open({
            type: 1,
            title: "会员月签到记录",
            area: ["600px", "700px"],
            content: $("#memberSign_month")
        });
        services._date_monthList({member_id:member_id,date_time:$scope.paramCol.monthText}).success(function(res){
            $scope.memberSignArray = res.data;
        })
    }

});