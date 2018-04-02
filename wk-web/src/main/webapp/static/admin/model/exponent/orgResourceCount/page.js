myApp.controller('orgResourceCountController', function ($rootScope, $scope, services, $sce, $stateParams) {
    $scope.services = services;
    //会员列表
    services["_resCount_data"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/resCount/getCountData', param, "POST");
    }
    //项目机构树
    services["_pro_org_tree"] = function (param) {
        return $rootScope.serverAction(ctxPath + '/admin/project/getProOrgTree', param, "POST");
    }

    $scope.tableControl = {
        config: {
            check: false,
            param: {
                pages: 1,
                pageSize: 10,
                pageNum: 1,
                total: 0
            },
            columns: [
                {field: 'org_name', title: "机构名称", align: 'center', width: '150px', order: true},
                {field: 'member_count', title: "会员", width: '150px', order: true},
                {field: 'device_count', title: "终端", width: "150px"},
                {field: 'book_count', title: "图书", width: "150px"},
                {field: 'article_count', title: "资讯", width: "150px"},
                {field: 'newspaper_count', title: "报纸", width: "150px"},
                {field: 'periodical_count', title: "杂志", width: "150px"},
                {field: 'video_count', title: "视频", width: "150px"},
                {field: 'audio_count', title: "音频", width: "150px"}
            ]
        },
        reload: function () {
            $scope.load()
        }
    };

    $scope.selRow = {
        id: $rootScope._USERINFO.org_id == 1 ? null : $rootScope._USERINFO.org_id,
        type: 'org',
        code: null
    }
    var countData = {
        "org_name":"",
        "member_count": 0,
        "device_count": 0,
        "book_count": 0,
        "article_count": 0,
        "newspaper_count": 0,
        "periodical_count": 0,
        "video_count": 0,
        "audio_count": 0
    };

    //加载
    $scope.load = function () {
        if($rootScope._USERINFO.org_id == 1){
            services._pro_org_tree().success(function (res) {
                var arrdata = [];
                if (res.data) {
                    arrdata = res.data;
                }
                var allData = [{
                    text: "所有资源",
                    id: 0,
                    type: "",
                    code: "",
                    children: arrdata
                }]
                $("#comTree").tree("loadData", allData);
            })
        }
        services._resCount_data().success(function (res) {
            countData = {
                "org_name": res.data.org_name,
                "member_count": res.data.member_count,
                "device_count": res.data.device_count,
                "book_count": res.data.book_count,
                "article_count": res.data.article_count,
                "newspaper_count": res.data.newspaper_count,
                "periodical_count": res.data.periodical_count,
                "video_count": res.data.video_count,
                "audio_count": res.data.audio_count
            }
            var data = {
                "pageNum": 1,
                "pageSize": 10,
                "pages": 1,
                "rows": [countData],
                "size": 1,
                "total": 1
            }
            $scope.tableControl.loadData(data);
        })
    }
    var selNode = null;
    $("#comTree").tree({
        //数据过滤
        "loadFilter": function (data, parent) {
            for (var i = 0; i < data.length; i++) {
                changeTreeStyle1(data[i]);
            }
            return data;
        },
        "onSelect": function (node) {
            if (node.id == 0 || node.type != "org") {
                return;
            }
            $scope.tableControl.config.param.org_id = node.id;
            services._resCount_data($scope.tableControl.config.param).success(function (res) {
                countData = {
                    "org_name": res.data.org_name,
                    "member_count": res.data.member_count,
                    "device_count": res.data.device_count,
                    "book_count": res.data.book_count,
                    "article_count": res.data.article_count,
                    "newspaper_count": res.data.newspaper_count,
                    "periodical_count": res.data.periodical_count,
                    "video_count": res.data.video_count,
                    "audio_count": res.data.audio_count
                }
                var data = {
                    "pageNum": 1,
                    "pageSize": 10,
                    "pages": 1,
                    "rows": [countData],
                    "size": 1,
                    "total": 1
                }
                $scope.tableControl.loadData(data);
            })
        },
        onLoadSuccess: function (node, data) {
            if (!selNode) {
                selNode = $("#comTree").tree("getRoot");
            }
            else {
                //匹配节点是否存在
                var boolNode = $("#comTree").tree("find", selNode.id);
                selNode = boolNode ? boolNode : $("#comTree").tree("getRoot");
            }
            $("#comTree").tree("select", selNode.target);

            //查找未分配的分类
            var unNode = $("#comTree").tree("find", -1);
            if (unNode) {
                unNode.target.parentNode.className = "unNode";
            }
        }
    });
    function changeTreeStyle1(treeNode) {
        if (treeNode['children'] && treeNode['children'].length > 0) {
            for (var j = 0; j < treeNode['children'].length; j++) {
                changeTreeStyle1(treeNode['children'][j]);
            }
        }
        //设置属性
        treeNode["text"] = treeNode.text;
        treeNode["id"] = treeNode.id;
    }
});