/**
 * Created by yfyuan on 2016/10/11.
 */
cBoard.controller('datamanagerViewCtrl', function ($scope, $http, dataService, $uibModal, ModalUtils, $filter, chartService, $timeout) {

    var translate = $filter('translate');
    $scope.optFlag = 'none';
    $scope.curDataManager = {data: {expressions: []}};
    $scope.curWidget = {};
    $scope.alerts = [];
    $scope.verify = {dsName: true};
    $scope.colParams = [];
    $scope.params = "";
    $scope.pages = [];
    $scope.pagesParams = null;
    $scope.curPage = 0;

    var treeID = 'dataManagerTreeID'; // Set to a same value with treeDom
    var originalData = [];
    var updateUrl = "dataManager/updateDataManager.do";

    $scope.goPage = function (p) {
        $scope.curPage = p;
        $scope.pagesParams = {"curPage": $scope.curPage, "pageSize": 10};
        $scope.searchByParams();
    };

    $scope.initPages = function (widgetData) {
        var curPage = widgetData.curPage == null ? 1 : widgetData.curPage;
        var totalPage = widgetData.totalPage;
        var begin = 1;
        var end = 0;
        $scope.pages = [];


        if (curPage < 10) {
            begin = 1;
            end = curPage + 5 >= totalPage ? totalPage : 10;
        } else {
            begin = curPage - 4;
            end = curPage + 5 >= totalPage ? totalPage : curPage + 5;
        }


        for (var i = begin; i <= end; i++) {
            $scope.pages.push(i);
        }
    }


    $scope.clickSearch=function(){
        $scope.curPage = 1;
        $scope.pagesParams = {"curPage": $scope.curPage, "pageSize": 10};
        $scope.searchByParams();
    }

    $scope.searchByParams = function () {

        var cps = $scope.colParams;
        var cols = $scope.curDataManager.data.params;
        var ps = {};
        for (var i in cps) {
            if (cps[i] != null && cps[i] != "") {
                ps[cols[i]] = cps[i];
            }
        }
        $scope.params = angular.toJson(ps);

        dataService.getDataByParams($scope.datasource.id, $scope.curWidget.query, $scope.params, $scope.pagesParams, null, function (widgetData) {
            $scope.loading = false;
            $scope.toChartDisabled = false;
            if (widgetData.msg == '1') {
                $scope.alerts = [];
                $scope.widgetData = widgetData.data;
                $scope.initPages(widgetData);
                $scope.selects = angular.copy($scope.widgetData[0]);
            } else {
                if (widgetData.msg != null) {
                    $scope.alerts = [{msg: widgetData.msg, type: 'danger'}];
                }
            }

        });

    };

    $http.get("dashboard/getDatasourceList.do").success(function (response) {
        $scope.datasourceList = response;
    });

    /**
     * 切换数据源以加载表
     */
    $scope.changeTableBySource = function (ds) {
        alert(ds.id);
        $http.post("datamanager/getTablesBySource.do", {id: ds.id}).success(function (response) {
            if (response.status == '1') {
                doEditDs(ds);
            } else {
                ModalUtils.alert(translate("ADMIN.CONTACT_ADMIN") + "：Datasource/" + response.msg, "modal-danger", "lg");
            }
        });

    }

    var getDataManagerList = function () {
        $http.get("dataManager/getDataManagerList.do").success(function (response) {
            $scope.dataManagerList = response;
            $scope.searchNode();
        });

    };

    var getCategoryList = function () {
        $http.get("dashboard/getDatasetCategoryList.do").success(function (response) {
            $scope.categoryList = response;
            $("#DatasetName").autocomplete({
                source: $scope.categoryList
            });
        });
    };

    getCategoryList();
    getDataManagerList();

    $scope.newDs = function () {
        $scope.optFlag = 'new';
        $scope.curDataManager = {data: {expressions: []}};
        $scope.curWidget = {};
        cleanPreview();
    };

    $scope.editDs = function (ds) {
        $http.post("dashboard/checkDatasource.do", {id: ds.data.datasource}).success(function (response) {
            if (response.status == '1') {
                doEditDs(ds);
            } else {
                ModalUtils.alert(translate("ADMIN.CONTACT_ADMIN") + "：Datasource/" + response.msg, "modal-danger", "lg");
            }
        });
    };

    var doEditDs = function (ds) {
        $scope.optFlag = 'edit';
        $scope.curDataManager = angular.copy(ds);
        $scope.curDataManager.name = $scope.curDataManager.categoryName + '/' + $scope.curDataManager.name;
        if ($scope.curDataManager.data.params != null) {
            $scope.showParams = true;
        } else {
            $scope.showParams = false;
        }
        if (!$scope.curDataManager.data.expressions) {
            $scope.curDataManager.data.expressions = [];
        }
        $scope.datasource = _.find($scope.datasourceList, function (ds) {
            return ds.id == $scope.curDataManager.data.datasource;
        });
        $scope.curWidget.query = $scope.curDataManager.data.query;
        $scope.loadData();
    };

    $scope.deleteDs = function (ds) {
        ModalUtils.confirm(translate("COMMON.CONFIRM_DELETE"), "modal-warning", "lg", function () {
            $http.post("dashboard/deleteDataset.do", {id: ds.id}).success(function () {
                $scope.optFlag = 'none';
                getDataManagerList();
            });
        });
    };

    $scope.copyDs = function (ds) {
        var data = angular.copy(ds);
        data.name = data.name + "_copy";
        $http.post("dataManager/saveNewDataManager.do", {json: angular.toJson(data)}).success(function (serviceStatus) {
            if (serviceStatus.status == '1') {
                $scope.optFlag = 'none';
                getDataManagerList();
                ModalUtils.alert(translate("COMMON.SUCCESS"), "modal-success", "sm");
            } else {
                ModalUtils.alert(serviceStatus.msg, "modal-warning", "lg");
            }
        });
    };

    var validate = function () {
        $scope.alerts = [];
        if (!$scope.curDataManager.name) {
            1
            $scope.alerts = [{msg: translate('CONFIG.DATASET.NAME') + translate('COMMON.NOT_EMPTY'), type: 'danger'}];
            $scope.verify = {dsName: false};
            $("#DatasetName").focus();
            return false;
        }
        return true;
    };

    $scope.save = function () {
        $scope.datasource ? $scope.curDataManager.data.datasource = $scope.datasource.id : null;
        $scope.curDataManager.data.query = $scope.curWidget.query;
        $scope.curDataManager.data.params = $scope.curDataManager.params;
        if (!validate()) {
            return;
        }
        var ds = angular.copy($scope.curDataManager);
        var index = ds.name.lastIndexOf('/');
        ds.categoryName = $scope.curDataManager.name.substring(0, index).trim();
        ds.name = $scope.curDataManager.name.slice(index + 1).trim();
        if (ds.categoryName == '') {
            ds.categoryName = translate("COMMON.DEFAULT_CATEGORY");
        }

        if ($scope.optFlag == 'new') {
            $http.post("dataManager/saveDataManager.do", {json: angular.toJson(ds)}).success(function (serviceStatus) {
                if (serviceStatus.status == '1') {
                    $scope.optFlag = 'edit';
                    getCategoryList();
                    getDataManagerList();
                    $scope.verify = {dsName: true};
                    ModalUtils.alert(translate("COMMON.SUCCESS"), "modal-success", "sm");
                } else {
                    $scope.alerts = [{msg: serviceStatus.msg, type: 'danger'}];
                }
            });
        } else {
            $http.post(updateUrl, {json: angular.toJson(ds)}).success(function (serviceStatus) {
                if (serviceStatus.status == '1') {
                    $scope.optFlag = 'edit';
                    getCategoryList();
                    getDataManagerList();
                    $scope.verify = {dsName: true};
                    ModalUtils.alert(translate("COMMON.SUCCESS"), "modal-success", "sm");
                } else {
                    $scope.alerts = [{msg: serviceStatus.msg, type: 'danger'}];
                }
            });
        }

    };

    $scope.editExp = function (col) {
        var selects = angular.copy($scope.widgetData[0]);
        var aggregate = [
            {name: 'sum', value: 'sum'},
            {name: 'count', value: 'count'},
            {name: 'avg', value: 'avg'},
            {name: 'max', value: 'max'},
            {name: 'min', value: 'min'}
        ];
        var ok;
        var data = {expression: ''};
        if (!col) {
            ok = function (exp, alias) {
                $scope.curDataManager.data.expressions.push({
                    type: 'exp',
                    exp: data.expression,
                    alias: data.alias
                });
            }
        } else {
            data.expression = col.exp;
            data.alias = col.alias;
            ok = function (data) {
                col.exp = data.expression;
                col.alias = data.alias;
            }
        }

        $uibModal.open({
            templateUrl: 'org/cboard/view/config/modal/exp.html',
            windowTemplateUrl: 'org/cboard/view/util/modal/window.html',
            backdrop: false,
            controller: function ($scope, $uibModalInstance) {
                $scope.data = data;
                $scope.selects = selects;
                $scope.aggregate = aggregate;
                $scope.alerts = [];
                $scope.close = function () {
                    $uibModalInstance.close();
                };
                $scope.addToken = function (str, agg) {
                    var tc = document.getElementById("expression_area");
                    var tclen = $scope.data.expression.length;
                    tc.focus();
                    var selectionIdx = 0;
                    if (typeof document.selection != "undefined") {
                        document.selection.createRange().text = str;
                        selectionIdx = str.length - 1;
                    }
                    else {
                        var a = $scope.data.expression.substr(0, tc.selectionStart);
                        var b = $scope.data.expression.substring(tc.selectionStart, tclen);
                        $scope.data.expression = a + str;
                        selectionIdx = $scope.data.expression.length - 1;
                        $scope.data.expression += b;
                    }
                    if (!agg) {
                        selectionIdx++;
                    }
                    tc.selectionStart = selectionIdx;
                    tc.selectionEnd = selectionIdx;
                };
                $scope.verify = function () {
                    $scope.alerts = [];
                    var v = verifyAggExpRegx($scope.data.expression);
                    $scope.alerts = [{
                        msg: v.isValid ? translate("COMMON.SUCCESS") : v.msg,
                        type: v.isValid ? 'success' : 'danger'
                    }];
                };
                $scope.ok = function () {
                    if (!$scope.data.alias) {
                        ModalUtils.alert(translate('CONFIG.WIDGET.ALIAS') + translate('COMMON.NOT_EMPTY'), "modal-warning", "lg");
                        return;
                    }
                    ok($scope.data);
                    $uibModalInstance.close();
                };
            }
        });
    };

    $scope.loadData = function (params) {
        cleanPreview();
        $scope.loading = true;


        dataService.getDataByParams($scope.datasource.id, $scope.curWidget.query, null, null, null, function (widgetData) {
            $scope.loading = false;
            $scope.toChartDisabled = false;
            if (widgetData.msg == '1') {
                $scope.alerts = [];
                $scope.widgetData = widgetData.data;
                $scope.initPages(widgetData);

                $scope.selects = angular.copy($scope.widgetData[0]);
            } else {
                if (widgetData.msg != null) {
                    $scope.alerts = [{msg: widgetData.msg, type: 'danger'}];
                }
            }

            var widget = {
                chart_type: "table",
                filters: [],
                groups: [],
                keys: [],
                selects: [],
                values: [{
                    cols: []
                }
                ]
            };
            _.each($scope.widgetData[0], function (c) {
                widget.keys.push({
                    col: c,
                    type: "eq",
                    values: []
                });
            });

            chartService.render($('#dataset_preview'), $scope.widgetData, widget, null, {myheight: 300});
        });

    };

    var cleanPreview = function () {
        $('#dataset_preview').html("");
    };


    /**  js tree related start **/
    $scope.treeConfig = jsTreeConfig1;

    $("#" + treeID).keyup(function (e) {
        if (e.keyCode == 46) {
            $scope.deleteNode();
        }
    });

    var getSelectedDataManager = function () {
        var selectedNode = jstree_GetSelectedNodes(treeID)[0];
        return _.find($scope.dataManagerList, function (ds) {
            return ds.id == selectedNode.id;
        });
    };

    var checkTreeNode = function (actionType) {
        return jstree_CheckTreeNode(actionType, treeID, ModalUtils.alert);
    };

    var switchNode = function (id) {
        $scope.ignoreChanges = false;
        var dataManagerTreeID = jstree_GetWholeTree(treeID);
        dataManagerTreeID.deselect_all();
        dataManagerTreeID.select_node(id);
    };

    $scope.applyModelChanges = function () {
        return !$scope.ignoreChanges;
    };

    $scope.copyNode = function () {
        if (!checkTreeNode("copy")) return;
        $scope.copyDs(getSelectedDataManager());
    };

    $scope.editNode = function () {
        if (!checkTreeNode("edit")) return;
        $scope.editDs(getSelectedDataManager());
    };

    $scope.deleteNode = function () {
        if (!checkTreeNode("delete")) return;
        $scope.deleteDs(getSelectedDataManager());
    };
    $scope.searchNode = function () {
        var para = {dsName: '', dsrName: ''};
        //map dataManagerList to list (add datasourceName)
        var list = $scope.dataManagerList.map(function (ds) {
            var dsr = _.find($scope.datasourceList, function (obj) {
                return obj.id == ds.data.datasource
            });
            return {
                "id": ds.id,
                "name": ds.name,
                "categoryName": ds.categoryName,
                "datasourceName": dsr ? dsr.name : ''
            };
        });
        //split search keywords
        if ($scope.keywords) {
            if ($scope.keywords.indexOf(' ') == -1 && $scope.keywords.indexOf(':') == -1) {
                para.dsName = $scope.keywords;
            } else {
                var keys = $scope.keywords.split(' ');
                for (var i = 0; i < keys.length; i++) {
                    var w = keys[i].trim();
                    if (w.split(':')[0] == 'ds') {
                        para["dsName"] = w.split(':')[1];
                    }
                    if (w.split(':')[0] == 'dsr') {
                        para["dsrName"] = w.split(':')[1];
                    }
                }
            }
        }
        //filter data by keywords
        originalData = jstree_CvtVPath2TreeData(
            $filter('filter')(list, {name: para.dsName, datasourceName: para.dsrName})
        );

        jstree_ReloadTree(treeID, originalData);
    };

    $scope.treeEventsObj = function () {
        var baseEventObj = jstree_baseTreeEventsObj({
            ngScope: $scope, ngHttp: $http, ngTimeout: $timeout,
            treeID: treeID, listName: "dataManagerList", updateUrl: updateUrl
        });
        return baseEventObj;
    }();

    /**  js tree related end **/
});