myApp.controller('homeController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    services["_load_Data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/resourceCount/indexCount', param, "POST");
    }
    var option = {
        title: {
            x: 'center',
            text: '图书热门综合指数排行',
            padding: [20, 0, 0, 0]
        },
        tooltip: {
            trigger: 'item'
        },
        calculable: true,
        grid: {
            borderWidth: 0,
            x: 50,
            y: 50,
            y2: 30,
            x2: 10
        },
        xAxis: [
            {
                type: 'category',
                show: false,
                data: []
            }
        ],
        yAxis: [
            {
                type: 'value',
                show: false
            }
        ],
        series: [
            {
                name: '图书热门综合指数排行',
                type: 'bar',
                itemStyle: {
                    normal: {
                        color: function (params) {
                            var colorList = [
                                "#3b8cff", "#3b8cff", "#3b8cff", "#3b8cff", "#3b8cff",
                                "#6fd96f", "#6fd96f", "#6fd96f", "#6fd96f", "#6fd96f",
                                "#eb7070", "#eb7070", "#eb7070", "#eb7070", "#eb7070",
                                "#f1dc2e", "#f1dc2e", "#f1dc2e", "#f1dc2e", "#f1dc2e",
                                "#F3A43B", "#F3A43B", "#F3A43B", "#F3A43B", "#F3A43B",
                                "#60C0DD", "#60C0DD", "#60C0DD", "#60C0DD", "#60C0DD"
                            ];
                            return colorList[params.dataIndex]
                        },
                        label: {
                            show: true,
                            position: 'top',
                            formatter: '{c}'
                        }
                    }
                },
                data: []
            }
        ]
    };

    var option_1 = {
        title: {
            x: 'center',
            text: '用户阅读指数排行',
            padding: [20, 0, 0, 0]
        },
        tooltip: {
            trigger: 'item'
        },
        calculable: true,
        grid: {
            borderWidth: 0,
            x: 50,
            y: 50,
            y2: 30,
            x2: 10
        },
        xAxis: [
            {
                type: 'category',
                show: false,
                data: []
            }
        ],
        yAxis: [
            {
                type: 'value',
                show: false
            }
        ],
        series: [
            {
                name: '用户阅读指数排行',
                type: 'bar',
                itemStyle: {
                    normal: {
                        color: function (params) {
                            var colorList = [
                                "#3b8cff", "#3b8cff", "#3b8cff", "#3b8cff", "#3b8cff",
                                "#6fd96f", "#6fd96f", "#6fd96f", "#6fd96f", "#6fd96f",
                                "#eb7070", "#eb7070", "#eb7070", "#eb7070", "#eb7070",
                                "#f1dc2e", "#f1dc2e", "#f1dc2e", "#f1dc2e", "#f1dc2e",
                                "#F3A43B", "#F3A43B", "#F3A43B", "#F3A43B", "#F3A43B",
                                "#60C0DD", "#60C0DD", "#60C0DD", "#60C0DD", "#60C0DD"
                            ];
                            return colorList[params.dataIndex]
                        },
                        label: {
                            show: true,
                            position: 'top',
                            formatter: '{c}'
                        }
                    }
                },
                data: []
            }
        ]
    };

    //设置报表
    $scope.setOption = function (bl) {
        var chart = echarts.init(document.getElementById('chart_dianji'));
        chart.setOption(option);

        var chart = echarts.init(document.getElementById('chart_member'));
        chart.setOption(option_1);
        if (bl == 0) {
            $("#chart_dianji").append("<p>还没有任何统计数据</p>")
            $("#chart_member").append("<p>还没有任何统计数据</p>")
        }
    }
    $scope.selRow = {}
    //加载
    $scope.load = function () {
        var data = $rootScope._MAINDATA;
        if(data == null || data == ""){
            services._load_Data().success(function (res) {
                $scope.selRow = res.data;
                $rootScope._MAINDATA = res.data;
                angular.forEach($scope.selRow.books, function (item, index) {
                    option.xAxis[0].data.push(item.book_name);
                    option.series[0].data.push(item.count);
                })
                angular.forEach($scope.selRow.members, function (item, index) {
                    option_1.xAxis[0].data.push(item.nick_name);
                    option_1.series[0].data.push(item.count);
                })
                $scope.setOption();
            })
        }
        else{
            $scope.selRow = $rootScope._MAINDATA;
            angular.forEach($scope.selRow.books, function (item, index) {
                option.xAxis[0].data.push(item.book_name);
                option.series[0].data.push(item.count);
            })
            angular.forEach($scope.selRow.members, function (item, index) {
                option_1.xAxis[0].data.push(item.nick_name);
                option_1.series[0].data.push(item.count);
            })
            $scope.setOption();
        }

    }
    $scope.load()
});
