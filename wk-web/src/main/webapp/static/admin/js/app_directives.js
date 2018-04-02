var myDirectives = angular.module('myDirectives', [])
//列表插件
    .directive('gridTable', [function () {
        return {
            restrict: 'E',
            templateUrl: ctxPath + "/static/admin/plugin/gridtable/main.html",
            replace: true,
            link: function (scope, el, attr) {
                var control = scope[attr.control];
                control.unpager = attr.unpager;
                //数据加载
                control.loadData = function (data) {
                    control.data = data.rows ? data.rows : data.children;
                    control.config.param["total"] = parseInt(data.total);
                    control.config.param["pages"] = parseInt(data.pages);
                    control.config.param["pageNum"] = parseInt(data.pageNum)
                    if (!control.config.param["total"]) {
                        control.config.param["total"] = 0
                    }
                    if (!control.config.param["pages"]) {
                        control.config.param["pages"] = 0
                    }
                    if (!control.config.param["pageNum"]) {
                        control.config.param["pageNum"] = 1
                    }
                    control.config.pagerNumCol = parseInt(data.pageNum);
                    control.rows = [];
                    control.config.selectAll = false;
                    angular.forEach(control.data, function (rowdata, rowindex) {
                        var row = [];
                        angular.forEach(control.config.columns, function (item, index) {
                            var filed = rowdata[item.field];
                            if (filed == null || filed == undefined) {
                                filed = "";
                            }
                            var col = {};
                            for (var i in item) {
                                col[i] = item[i];
                            }
                            if (item.formatter) {
                                filed = item.formatter(filed, rowdata, rowindex, control.data)
                            }
                            if (filed && filed.length > 100)
                                col.value = scope._trustAsHtml('<span class="langText">' + filed + '</span>');
                            else
                                col.value = scope._trustAsHtml('<span class="sortFiled">' + filed + '</span>');
                            row.push(col);
                        })
                        control.rows.push({
                            index: rowindex,
                            data: row,
                            select: false
                        });
                    })
                    if (control.config.param.pages > 1 || control.config.param.rows > 1) {
                        $(".table_pager", el).show();
                        layui.laypage({
                            cont: "public_table_pager",
                            pages: control.config.param.pages,
                            curr: control.config.param.pageNum,
                            skip: true,
                            jump: function (resPager) {
                                if (resPager.curr != control.config.param.pageNum) {
                                    control.config.param.pageNum = parseInt(resPager.curr);
                                    if (control.reload)
                                        control.reload(control.config.param);
                                }
                            }
                        });
                    }
                    else if (control.rows.length >0) {
                        $("#public_table_pager", el).empty();
                        $(".table_pager", el).show();
                    }
                    else {
                        $("#public_table_pager", el).empty();
                        $(".table_pager", el).hide();
                    }
                    if (control.callback) {
                        setTimeout(function () {
                            control.callback();
                        }, 50);
                    }
                }
                //点击分页数量
                control.setPageSize = function (num) {
                    control.config.param.pageSize = num;
                    control.config.param.pageNum = 1;
                    if (control.reload)
                        control.reload(control.config.param);
                }
                //自动初始化
                control.init = function () {
                    angular.forEach(control.config.columns, function (item, index) {
                        item.align = !item.align ? 'center' : item.align;
                        item.width = !item.width ? 'auto' : item.width;
                        item.orderType = !item.orderType ? 'desc' : item.orderType;
                    })
                    if (control.data) {
                        control.loadData(control.data);
                    }
                    else {
                        if (control.reload)
                            control.reload(control.config.param);
                    }
                }
                //排序
                control.orderBy = function (t) {
                    if (t.order) {
                        t.orderType = t.orderType == "desc" ? "asc" : "desc";
                        control.config.param.orderField = t.field;
                        control.config.param.orderType = t.orderType;
                        if (control.reload)
                            control.reload(control.config.param);
                    }
                }
                //全选
                control.unSelectAll = function () {
                    control.config.selectAll = !control.config.selectAll;
                    angular.forEach(control.rows, function (row) {
                        row.select = control.config.selectAll;
                    })
                }
                //行选择
                control.unSelectRow = function (row) {
                    row.select = !row.select;
                    var bool = true;
                    angular.forEach(control.rows, function (line) {
                        if (!line.select)
                            bool = false;
                    })
                    control.config.selectAll = bool;
                }
                scope.gridTableControl = control;
                scope.gridTableControl.init();
            }
        };
    }])
    //日历控件
    .directive('dateTimePicker', function () {
        return {
            restrict: 'ECA',
            link: function (scope, element, attr) {
                var _conf = {
                    lang: 'zh'
                };
                if (attr.plugintype == 'date') {
                    _conf.format = attr.format || 'Y-m-d H:i';
                    $(element).datetimepicker(_conf);
                } else if (attr.plugintype == 'datetime') {
                    _conf.format = attr.format || 'Y-m-d';
                    _conf.timepicker = false;
                    $(element).datetimepicker(_conf);
                } else if (attr.plugintype == 'time') {
                    _conf.format = attr.format || 'H:i:s';
                    _conf.datepicker = false;
                    $(element).datetimepicker(_conf);
                }
            }
        };
    })
    //排序
    .directive('sortControl', function () {
        return {
            restrict: 'ECA',
            link: function (scope, element, attr) {
                $(".layui-btn", element).bind("click", function () {
                    var input = $("input", this.parentNode);
                    var d = new Date();
                    input.val(d.getTime()).change();
                })
            }
        };
    })
    //机构选择
    .directive('selOrg', function () {
        return {
            restrict: 'ECA',
            templateUrl: ctxPath + "/static/admin/plugin/selOrg/main.html",
            replace: true,
            link: function (scope, element, attr) {
                var layer_org = null;
                var control = null;
                //机构列表
                scope.orgTableControl = {
                    config: {
                        check: attr.check,
                        param: {
                            pageNum: 1, //当前页
                            pageSize: 10, //每页条数
                            searchText: null
                        },
                        columns: [
                            {field: 'org_name', title: "机构名称", align: 'left'},
                            {field: 'extend_code', title: "机构编码"},
                            {field: 'short_name', title: "机构简称"},
                            {field: 'project_name', title: "所属项目"}
                        ]
                    },
                    reload: function (param) {
                        scope.services._outfit_list(param).success(function (res) {
                            control.loadData(res.data);
                        })
                    }
                }
                control = scope.orgTableControl;
                scope.openOrg = function (callback) {
                    layer_org = layer.open({
                        type: 1,
                        title: "选择机构",
                        area: ["800px", "600px"],
                        content: $("#Select_Org")
                    });
                    control.reload(control.config.param);
                    control.callback = callback;
                }
                //确认
                control.config_btn = function () {
                    var array = [];
                    angular.forEach(control.rows, function (line) {
                        if (line.select)
                            array.push(line.source)
                    });
                    if (array.length > 0) {
                        layer.close(layer_org);
                        if (control.callback)
                            control.callback(array);
                    }
                    else {
                        layer.alert("你还未选择任何机构");
                    }
                }
                //取消
                control.df_btn = function () {
                    layer.close(layer_org);
                }
                //数据加载
                control.loadData = function (data) {
                    control.data = data.rows ? data.rows : data.children;
                    control.config.param["total"] = parseInt(data.total);
                    control.config.param["pages"] = parseInt(data.pages);
                    control.config.param["pageNum"] = parseInt(data.pageNum);
                    control.config.pagerNumCol = parseInt(data.pageNum);
                    control.rows = [];
                    control.config.selectAll = false;
                    angular.forEach(control.data, function (rowdata, rowindex) {
                        var row = [];
                        angular.forEach(control.config.columns, function (item, index) {
                            var filed = rowdata[item.field];
                            if (filed == null || filed == undefined) {
                                filed = "";
                            }
                            var col = {};
                            for (var i in item) {
                                col[i] = item[i];
                            }
                            if (item.formatter) {
                                filed = item.formatter(filed, rowdata, rowindex, control.data)
                            }
                            col.value = scope._trustAsHtml('<span>' + filed + '</span>');
                            row.push(col);
                        })
                        control.rows.push({
                            index: rowindex,
                            data: row,
                            select: false,
                            source: rowdata
                        });
                    })

                    if (control.config.param.pages > 1 || control.rows.length > 1) {
                        //处理分页
                        layui.laypage({
                            cont: "org_table_pager",
                            pages: control.config.param.pages,
                            curr: control.config.param.pageNum,
                            groups: 0,
                            first: false,
                            last: false,
                            jump: function (resPager) {
                                if (resPager.curr != control.config.param.pageNum) {
                                    control.config.param.pageNum = parseInt(resPager.curr);
                                    if (control.reload)
                                        control.reload(control.config.param);
                                }
                            }
                        });
                    }
                    else {
                        $("#org_table_pager").empty();
                    }
                }
                //自动初始化
                control.init = function () {
                    angular.forEach(control.config.columns, function (item, index) {
                        item.align = !item.align ? 'center' : item.align;
                        item.width = !item.width ? 'auto' : item.width;
                        item.orderType = !item.orderType ? 'desc' : item.orderType;
                    })
                }
                //重新查询
                control.reload_org = function () {
                    control.config.param["searchText"] = control.org_searchText;
                    control.config.param["pageNum"] = 1;
                    control.reload(control.config.param);
                }
                //排序
                control.orderBy = function (t) {
                    if (t.order) {
                        t.orderType = t.orderType == "desc" ? "asc" : "desc";
                        control.config.param.orderField = t.field;
                        control.config.param.orderType = t.orderType;
                        if (control.reload)
                            control.reload(control.config.param);
                    }
                }
                //全选
                control.unSelectAll = function () {
                    control.config.selectAll = !control.config.selectAll;
                    angular.forEach(control.rows, function (row) {
                        row.select = control.config.selectAll;
                    })
                }
                //行选择
                control.unSelectRow = function (row) {
                    row.select = !row.select;
                    if (control.config.check) {
                        var bool = true;
                        angular.forEach(control.rows, function (line) {
                            if (!line.select)
                                bool = false;
                        })
                        control.config.selectAll = bool;
                    }
                    else {
                        angular.forEach(control.rows, function (line) {
                            if (row != line) {
                                line.select = false;
                            }
                        })
                    }
                }
                control.init();
            }
        };
    })
    //设备列表
    .directive('selDevice', function () {
        return {
            restrict: 'ECA',
            templateUrl: ctxPath + "/static/admin/plugin/selDevice/main.html",
            replace: true,
            link: function (scope, element, attr) {
                var layer_org = null;
                var control = null;
                control = scope.DeviceControl;
                scope.openDev = function (callback) {
                    layer_org = layer.open({
                        type: 1,
                        title: "使用设备",
                        area: ["800px", "600px"],
                        content: $("#Select_Device")
                    });
                    control.reload(control.config.param);
                    control.callback = callback;
                }
                control.reload_dev = function () {
                    control.config.param["searchText"] = control.dev_searchText;
                    control.config.param["pageNum"] = 1;
                    control.reload(control.config.param);
                }
                //数据加载
                control.loadData = function (data) {
                    control.data = data.rows ? data.rows : data.children;
                    control.config.param["total"] = parseInt(data.total);
                    control.config.param["pages"] = parseInt(data.pages);
                    control.config.param["pageNum"] = parseInt(data.pageNum);
                    control.config.pagerNumCol = parseInt(data.pageNum);
                    control.rows = [];
                    control.config.selectAll = false;
                    angular.forEach(control.data, function (rowdata, rowindex) {
                        var row = [];
                        angular.forEach(control.config.columns, function (item, index) {
                            var filed = rowdata[item.field];
                            if (filed == null || filed == undefined) {
                                filed = "";
                            }
                            var col = {};
                            for (var i in item) {
                                col[i] = item[i];
                            }
                            if (item.formatter) {
                                filed = item.formatter(filed, rowdata, rowindex, control.data);
                            }
                            col.value = scope._trustAsHtml('<span>' + filed + '</span>');
                            row.push(col);
                        })
                        control.rows.push({
                            index: rowindex,
                            data: row,
                            select: false,
                            source: rowdata
                        });
                    })
                    //删除
                    control.config_btn = function () {
                        var array = [];
                        angular.forEach(control.rows, function (line) {
                            if (line.select)
                                array.push(line.source)
                        });
                        if (array.length > 0) {
                            if (control.callback)
                                control.callback(array);
                        }
                        else {
                            layer.alert("你还未选择设备");
                        }
                    }
                    //取消
                    control.df_btn = function () {
                        layer.close(layer_org);
                    }
                    //处理分页
                    if (control.config.param.pages > 1 || control.rows.length > 1) {
                        //处理分页
                        layui.laypage({
                            cont: "device_table_pager",
                            pages: control.config.param.pages,
                            curr: control.config.param.pageNum,
                            groups: 0,
                            first: false,
                            last: false,
                            jump: function (resPager) {
                                if (resPager.curr != control.config.param.pageNum) {
                                    control.config.param.pageNum = parseInt(resPager.curr);
                                    if (control.reload)
                                        control.reload(control.config.param);
                                }
                            }
                        });
                    }
                    else {
                        $("#device_table_pager").empty();
                    }
                }
                //自动初始化
                control.init = function () {
                    angular.forEach(control.config.columns, function (item, index) {
                        item.align = !item.align ? 'center' : item.align;
                        item.width = !item.width ? 'auto' : item.width;
                        item.orderType = !item.orderType ? 'desc' : item.orderType;
                    })
                }
                //排序
                control.orderBy = function (t) {
                    if (t.order) {
                        t.orderType = t.orderType == "desc" ? "asc" : "desc";
                        control.config.param.orderField = t.field;
                        control.config.param.orderType = t.orderType;
                        if (control.reload)
                            control.reload(control.config.param);
                    }
                }
                //全选
                control.unSelectAll = function () {
                    control.config.selectAll = !control.config.selectAll;
                    angular.forEach(control.rows, function (row) {
                        row.select = control.config.selectAll;
                    })
                }
                //行选择
                control.unSelectRow = function (row) {
                    row.select = !row.select;
                    if (control.config.check) {
                        var bool = true;
                        angular.forEach(control.rows, function (line) {
                            if (!line.select)
                                bool = false;
                        })
                        control.config.selectAll = bool;
                    }
                    else {
                        angular.forEach(control.rows, function (line) {
                            if (row != line) {
                                line.select = false;
                            }
                        })
                    }
                }
                control.init();
            }
        };
    })
