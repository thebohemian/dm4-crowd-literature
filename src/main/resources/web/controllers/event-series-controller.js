angular.module("crowd").controller("EventSeriesController", function($scope, $routeParams, crowdService) {
    $scope.setMapVisibility(false);
    crowdService.getEventSeries($routeParams.eventSeriesId, function(response) {
        $scope.eventSeries = response.data;
    })
})
