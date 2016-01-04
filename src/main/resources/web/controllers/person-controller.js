angular.module("crowd").controller("PersonController", function($scope, $routeParams, crowdService) {
    crowdService.getPerson($routeParams.personId, function(response) {
        $scope.person = response.data;
    })
})
