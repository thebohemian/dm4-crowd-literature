angular.module("crowd").controller("EventSeriesController", function($scope, $routeParams, crowdService) {
    crowdService.getEventSeries($routeParams.eventSeriesId, function(response) {
        $scope.eventSeries = response.data;
    })
})
