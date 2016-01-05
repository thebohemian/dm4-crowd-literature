angular.module("crowd").controller("EventController", function($scope, $routeParams, crowdService) {
    crowdService.getEvent($routeParams.eventId, function(response) {
        $scope.event = response.data;
    });
})
