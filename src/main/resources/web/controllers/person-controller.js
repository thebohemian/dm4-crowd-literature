angular.module("crowd").controller("PersonController", function($scope, $routeParams, crowdService) {
    $scope.setMapVisibility(false);
    crowdService.getPerson($routeParams.personId, function(response) {
        $scope.person = response.data;
    })
})
