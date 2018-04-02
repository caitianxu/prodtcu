myApp.controller('facilityStaController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据统计
    services["_data_deviceSta"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/resourceCount/getDeviceCount', param, "POST");
    }
    
    var chart = echarts.init(document.getElementById('chart_dianji'));

	$scope.load = function () {
        services._data_deviceSta().success(function (res) {
            var x=[];
            var y=[];
            angular.forEach(res.data.orgCount,function(item,index){
                x.push(item.org_name)
                y.push(item.num)
            })
            $scope.setOption1(res.data.year,x,y)
        })
    }
    $scope.load()

	$scope.setOption1 = function (year,x,y) {
	    var option = {
	        title: {
	            x: 'center',
	            text: year+'年设备访问统计TOP10',
	            subtext: '数据访问总量'
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
	            y: 80,
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
	
	    chart.setOption(option)
    }
});