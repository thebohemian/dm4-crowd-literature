angular.module("crowd").controller("eventSeriesController", function($scope, $routeParams, crowdService) {
    var eventSeriesId = $routeParams.eventSeriesId;
    crowdService.getEventSeries(eventSeriesId, function(response) {
        $scope.eventSeries = response.data;
    })
    crowdService.getEventsOfEventSeries(eventSeriesId, function(response) {
        $scope.events = response.data.items;
    })
})
