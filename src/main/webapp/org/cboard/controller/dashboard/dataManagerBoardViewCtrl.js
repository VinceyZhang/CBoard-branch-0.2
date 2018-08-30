/**
 * Created by yfyuan on 2016/8/2.
 */

cBoard.controller('dataManagerBoardViewCtrl', function ($rootScope, $scope, $state, $stateParams, $http, ModalUtils, chartService, $interval, $uibModal, dataService) {

    $scope.categoryList = [];
    $scope.boardList = [];
    $scope.board = {};
    $scope.datasourceId="";

    $scope.getWidgetById = function (val) {
        var widgetId=val.layout.rows[0].widgets[0].widgetId;
        $http.post("dashboard/getWidgetById.do", {json: angular.toJson({"id": widgetId})}).success(function (response) {
            var data=response.data;
            data=JSON.parse(data);
            $scope.datasourceId = data.datasourceId;

        }).then($http.post("dataManager/getDataManagerById.do", {json: angular.toJson({"id":  $scope.datasourceId})}).success(function (response) {
            $scope.categoryList = response;
        }));
    };

    var getCategoryListByType = function () {
        var cateGoryId = null;
        $http.post("dashboard/getCategoryListByType.do", {json: angular.toJson({"type": 1})}).success(function (response) {
            $scope.categoryList = response;
        });
    };


    var getBoardListByType = function () {
        var cateGoryId = null;
        $http.post("dashboard/getBoardListByType.do", {json: angular.toJson({"type": 1})}).success(function (response) {
            $scope.boardList = response;
        });
    };
    getBoardListByType();




    $scope.loading = true;


});