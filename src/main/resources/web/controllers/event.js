angular.module("crowd").controller("eventController", function($scope, $routeParams) {
    $scope.showEvent($routeParams.eventId);
})
