angular.module("crowd").controller("EventController", function($scope, $routeParams, crowdService) {
    var eventId = $routeParams.eventId;
    $scope.event = $scope.events[eventId];
    crowdService.getParticipantsOfEvent(eventId, function(response) {
        $scope.participants = response.data.items;
    })
    crowdService.getEventSeriesOfEvent(eventId, function(response) {
        $scope.eventSeries = response.data.items;
    })
})
