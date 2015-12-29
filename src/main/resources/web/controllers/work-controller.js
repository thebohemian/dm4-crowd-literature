angular.module("crowd").controller("WorkController", function($scope, $routeParams, crowdService) {
    var workId = $routeParams.workId;
    crowdService.getWork(workId, function(response) {
        $scope.work = response.data;
        //
        $scope.translators = {};
        $scope.work.childs["crowd.work.translation"].forEach(function(translation) {
            crowdService.getWorkPersons(translation.id, function(response) {
                $scope.translators[translation.id] = response.data.items;
            })
        })
    })
    crowdService.getWorkPersons(workId, function(response) {
        $scope.persons = response.data.items;
    })
})
