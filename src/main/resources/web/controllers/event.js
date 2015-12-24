angular.module("crowd").controller("eventController", function($scope, $routeParams, crowdService) {
    var eventId = $routeParams.eventId;
    $scope.event = $scope.events[eventId];
    crowdService.getEventParticipants(eventId, function(response) {
        $scope.participants = response.data.items;
    })
})
