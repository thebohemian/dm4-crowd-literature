angular.module("crowd")
.controller("PersonController", function($scope, $routeParams, crowdService) {
    $scope.setMapVisibility(false);
    crowdService.getPerson($routeParams.personId, function(response) {
        $scope.person = response.data;
    })
})
.controller("InstitutionController", function($scope, $routeParams, crowdService) {
    $scope.setMapVisibility(false);
    crowdService.getInstitution($routeParams.instId, function(response) {
        $scope.institution = response.data;
    })
})
.controller("WorkController", function($scope, $routeParams, crowdService) {
    $scope.setMapVisibility(false);
    crowdService.getWork($routeParams.workId, function(response) {
        $scope.work = response.data;
    })
})
.controller("EventController", function($scope, $routeParams, crowdService) {
    $scope.setMapVisibility(false);
    crowdService.getEvent($routeParams.eventId, function(response) {
        $scope.event = response.data;
    });
})
.controller("EventSeriesController", function($scope, $routeParams, crowdService) {
    $scope.setMapVisibility(false);
    crowdService.getEventSeries($routeParams.eventSeriesId, function(response) {
        $scope.eventSeries = response.data;
    })
})
