angular.module("crowd").controller("WorkController", function($scope, $routeParams, crowdService) {
    $scope.setMapVisibility(false);
    crowdService.getWork($routeParams.workId, function(response) {
        $scope.work = response.data;
    })
})
