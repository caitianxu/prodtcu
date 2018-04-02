myApp.controller('bookEntrepotController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //数据列表
    services["_data_book"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookrepo/json/getList', param, "POST");
    }
    //数据过滤各标签数量
    services["_data_nums"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookrepo/json/exprotNums', param, "POST");
    }
    //图书上传解析
    services["_resolver_book"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookrepo/json/uploadBookRepo', param, "POST");
    }
    //图书数据更正
    services["_up_book_data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookrepo/json/importBook', param, "POST");
    }
    //图书数据更正(单条)
    services["_edit_book"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookrepo/json/updateBookRepo', param, "POST");
    }
    //图书上下架
    services["_state_book"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/bookrepo/json/updateBookStatus', param, "POST");
    }

    $scope.tableControl = {
        config: {
            check: true,
            param: {
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0,
                book_status: 1,
                searchText: null
            },
            columns: [
                {
                    field: 'book_cover_small', title: "封面", formatter: function (val) {
                    if(val) {
                        return '<img src="' + $rootScope.ctxPath + val + '" class="df_book" style="display: block;margin: 0px auto;"/>'
                    }
                    return "";
                }
                },
                {field: 'book_isbn', title: "ISBN", align: 'left'},
                {field: 'book_name', title: "书名", align: 'left'},
                {field: 'book_author', title: "作者", align: 'left'},
                {field: 'file_name', title: "文件名", align: 'left'},
                {field: 'price', title: "价格", align: 'left'},
                {
                    field: 'book_status', title: "状态", formatter: function (value) {
                    if (value == 0) {
                        return '<em style="color:gray">初始</em>';
                    } else if (value == 1) {
                        return '<em style="color:#009688">上架</em>';
                    } else {
                        return '<em style="color: gray">下架</em>';
                    }
                }
                }
            ]
        },
        reload: function (param) {
            $scope.load();
        }
    };

    /**
     * 数据加载
     */
    $scope.load = function () {
        services._data_book($scope.tableControl.config.param).success(function (res) {
            $scope.tableControl.loadData(res.data);
        })
    }
    /**
     * 页面参数
     * @type {{searchText: null}}
     */
    $scope.param = {
        searchText: null,
        book_status: 1
    }
    /**
     * 重新加载
     */
    $scope.reload = function (p) {
        if(p == "book_status"){
            $scope.param.book_status = $scope.param.book_status == 1? 2 : 1;
        }
        $scope.tableControl.config.param["searchText"] = $scope.param.searchText;
        $scope.tableControl.config.param["book_status"] = $scope.param.book_status;
        $scope.tableControl.config.param["pageNum"] = 1;
        $scope.load();
    }
//    //文件上传
//    $('#book_upload').prettyFileBook({
//        text: "上传图书",
//        change: function (res, obj) {
//            var mms = layer.msg("图书解析中请稍候...");
//            services._resolver_book(res).success(function (res) {
//                layer.close(mms);
//                if (res.code == 0) {
//                    layer.msg("图书解析完成");
//                    $scope.reload();
//                }
//                else {
//                    layer.alert(res.message);
//                }
//            })
//        },
//        init: function (obj) {
//            $(".file-btn-ku", obj).remove();
//            $(".file-btn-text", obj).addClass("layui-btn").removeClass("layui-btn-normal").removeClass("layui-btn-primary")
//        }
//    });
    //excel导入
    $('#excel_upload').prettyFile({
        text: "导入Excel-数据补全",
        change: function (res, obj) {
            services._up_book_data(res).success(function (res) {
                if (res.code == 0) {
                    layer.alert(res.message)
                    $scope.load();
                }
                else {
                    layer.alert(res.message);
                }
            })
        },
        init: function (obj) {
            $(".file-btn-ku", obj).remove();
            $(".file-btn-text", obj).addClass("btn-link").removeClass("layui-btn-primary")
        }
    });
    /*
     导出数据
     */
    $scope.export_data=function(){
        var array = [];
        angular.forEach($scope.tableControl.rows, function (item) {
            if (item.select)
                array.push($scope.tableControl.data[item.index].book_id)
        })
        if(array.length==0){
            layer.alert('请选择导出数据')
            return false
        }
        var iframe = $('<iframe style="display: none" src="' + $rootScope.ctxPath + '/admin/bookrepo/json/exportBooks?ids=' + array.toString() + '"></iframe>');
        layer.close($scope.layer_export);
        $("#dataEntrepot").append(iframe);
    }
    //图书封面
    $('#bookCover').prettyFile({
        text: "本地上传",
        change: function (res, obj) {
            if (res.data.length > 0) {
                $scope.$apply(function () {
                    $scope.selRow.book_cover = res.data[0].url;
                })
            }
        },
        init: function (par) {
            $(".input_group",par).removeClass("input_group");
        }
    });
    $("#up_book_info .bCover").click(function () {
        $("#bookCover").click();
    });

    $scope.data_export = function () {
        //过滤数据范围
        services._data_nums().success(function(res){
            $scope.exDataArray = [{
                text: "书名",
                value: 1,
                count: res.data.bookNames,
                select: false
            }, {
                text: "作者",
                value: 2,
                count: res.data.bookAuthors,
                select: false
            }, {
                text: "出版社",
                value: 3,
                count: res.data.publishers,
                select: false
            }, {
                text: "出版时间",
                value: 4,
                count: res.data.publisherTimes,
                select: false
            }, {
                text: "版权日期",
                value: 5,
                count: res.data.endTimes,
                select: false
            }, {
                text: "ISBN",
                value: 6,
                count: res.data.bookIsbns,
                select: false
            }, {
                text: "简介",
                value: 7,
                count: res.data.remarks,
                select: false
            }, {
                text: "标签",
                value: 8,
                count: res.data.tags,
                select: false
            }, {
                text: "封面",
                value: 9,
                count: res.data.covers,
                select: false
            },{
                text: "价格",
                value: 10,
                count: res.data.price,
                select: false
            }];

            $scope.layer_export = layer.open({
                type: 1,
                title: "导出数据过滤",
                area: ["650px", "270px"],
                content: $("#export_plan")
            });
        })
    }





    /**
     * 确认导出
     */
    $scope.centerExport = function () {
        var array = [];
        angular.forEach($scope.exDataArray, function (item) {
            if (item.select)
                array.push(item.value)
        })
        //创建下载对象
        var iframe = $('<iframe style="display: none" src="' + $rootScope.ctxPath + '/admin/bookrepo/json/exportBook?ids=' + array.toString() + '"></iframe>');
        $("#dataEntrepot").append(iframe);
    }

    /**
     * 修改图书信息
     */
    $scope.editRow = function () {
        $scope.selRow = null;
        var ids = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push(item.book_id);
                $scope.selRow = $scope.tableControl.data[index]
                $scope.selRow['type'] = 'update';
            }
        });
        if (ids.length > 1) {
            layer.alert("同时只能修改一条数据")
            return false;
        }
        if (!$scope.selRow) {
            layer.alert("请选择要修改的数据行")
            return false;
        }
        $rootScope.formOpen();
    }

    /**
     * 修改图书
     * @private
     */
    $scope._form_submit = function () {
        services._edit_book($scope.selRow).success(function (res) {
            if (res.code == 0) {
                layer.msg("图书信息更新成功")
                $rootScope.formClose();
                $scope.load();
            }
            else {
                layer.alert(res.message);
            }
        })
    }

    /**
     * 上下架
     */
    $scope.upState = function (b) {
        var ids = [];
        angular.forEach($scope.tableControl.rows, function (item, index) {
            if (item.select) {
                ids.push($scope.tableControl.data[item.index].book_id)
            }
        });
        if (ids.length == 0) {
            layer.msg("请选择你要变更的图书")
            return false;
        }
        services._state_book({
            ids: ids,
            bool: b
        }).success(function (res) {
            if (res.code == 0) {
                $scope.load();
            }
            else {
                layer.alert(res.message);
            }
        })
    }
});