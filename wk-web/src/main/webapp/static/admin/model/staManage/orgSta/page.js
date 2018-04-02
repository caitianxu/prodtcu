myApp.controller('orgStaController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据统计
    services["_data_orgSta"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/resourceCount/getOrgCount', param, "POST");
    }
    var chart1 = echarts.init(document.getElementById('Chart_1'));
    var chart2 = echarts.init(document.getElementById('Chart_2'));
    var chart3 = echarts.init(document.getElementById('Chart_3'));

    $scope.load = function () {
        services._data_orgSta().success(function (res) {
            var x1=[];
            var y1=[];
            var x2=[];
            var y2=[];
            var x3=[];
            var y3=[];
            angular.forEach(res.data.orgCount,function(item,index){
                x1.push(item.org_name)
                y1.push(item.num)
            })
            $scope.setOption1(res.data.year,x1,y1)
            angular.forEach(res.data.bookCount,function(item,index){
                x2.push(item.org_name)
                y2.push(item.num)
            })
            $scope.setOption2(res.data.year,x2,y2)
            angular.forEach(res.data.newsCount,function(item,index){
                x3.push(item.org_name)
                y3.push(item.num)
            })
            $scope.setOption3(res.data.year,x3,y3)
        })
    }
    $scope.load()
    $scope.setOption1 = function (year,x,y) {
        var option1 = {
            backgroundColor: '#f4f4f4',
            title: {
                x: 'center',
                text: year+'年机构总体访问量统计TOP10',
                padding: [20, 0, 0, 0]
            },
            tooltip: {
                trigger: 'item'
            },
            toolbox: {
                show: true,
                feature: {
                    saveAsImage: {show: true}
                },
                padding: [10, 50, 0, 0]
            },
            calculable: true,
            grid: {
                borderWidth: 0,
                y: 100,
                y2: 10
            },
            xAxis: [
                {
                    type: 'category',
                    show: false,
                    data: x
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
                    name: '访问总数',
                    type: 'bar',
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                var colorList = [
                                    '#C1232B', '#B5C334', '#FCCE10', '#E87C25', '#27727B',
                                    '#FE8463', '#9BCA63', '#FAD860', '#F3A43B', '#60C0DD',
                                    '#D7504B', '#C6E579', '#F4E001', '#F0805A', '#26C0C0'
                                ];
                                return colorList[params.dataIndex]
                            },
                            label: {
                                show: true,
                                position: 'top',
                                formatter: '{b}\n{c}'
                            }
                        }
                    },
                    data: y
                }
            ]
        };
        chart1.setOption(option1)
    }
    
	$scope.setOption2 = function (year,x,y) {
	    var option2 = {
	        backgroundColor: '#f4f4f4',
	        title: {
	            x: 'center',
	            text: year+'年机构图书访问量统计TOP10',
	            padding: [20, 0, 0, 0]
	        },
	        tooltip: {
	            trigger: 'item'
	        },
	        toolbox: {
	            show: true,
	            feature: {
	                saveAsImage: {show: true}
	            },
	            padding: [10, 50, 0, 0]
	        },
	        calculable: true,
	        grid: {
	            borderWidth: 0,
	            y: 100,
	            y2: 10
	        },
	        xAxis: [
	            {
	                type: 'category',
	                show: false,
	                data: x
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
	                name: '访问总数',
	                type: 'bar',
	                itemStyle: {
	                    normal: {
	                        color: function (params) {
	                            var colorList = [
	                                '#C1232B', '#B5C334', '#FCCE10', '#E87C25', '#27727B',
	                                '#FE8463', '#9BCA63', '#FAD860', '#F3A43B', '#60C0DD',
	                                '#D7504B', '#C6E579', '#F4E001', '#F0805A', '#26C0C0'
	                            ];
	                            return colorList[params.dataIndex]
	                        },
	                        label: {
	                            show: true,
	                            position: 'top',
	                            formatter: '{b}\n{c}'
	                        }
	                    }
	                },
	                data: y
	            }
	        ]
	    };
	
	    chart2.setOption(option2)
	}
	
	$scope.setOption3 = function (year,x,y) {
	    var option3 = {
	        backgroundColor: '#f4f4f4',
	        title: {
	            x: 'center',
	            text: year+'年机构新闻访问量统计TOP10',
	            subtext: '数据访问总量',
	            padding: [20, 0, 0, 0]
	        },
	        tooltip: {
	            trigger: 'item'
	        },
	        toolbox: {
	            show: true,
	            feature: {
	                saveAsImage: {show: true}
	            },
	            padding: [10, 50, 0, 0]
	        },
	        calculable: true,
	        grid: {
	            borderWidth: 0,
	            y: 100,
	            y2: 10
	        },
	        xAxis: [
	            {
	                type: 'category',
	                show: false,
	                data: x
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
	                name: '访问总数',
	                type: 'bar',
	                itemStyle: {
	                    normal: {
	                        color: function (params) {
	                            var colorList = [
	                                '#C1232B', '#B5C334', '#FCCE10', '#E87C25', '#27727B',
	                                '#FE8463', '#9BCA63', '#FAD860', '#F3A43B', '#60C0DD',
	                                '#D7504B', '#C6E579', '#F4E001', '#F0805A', '#26C0C0'
	                            ];
	                            return colorList[params.dataIndex]
	                        },
	                        label: {
	                            show: true,
	                            position: 'top',
	                            formatter: '{b}\n{c}'
	                        }
	                    }
	                },
	                data: y
	            }
	        ]
	    };
	    chart3.setOption(option3)
    }
});