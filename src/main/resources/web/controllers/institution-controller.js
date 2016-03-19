angular.module("crowd").controller("InstitutionController", function($scope, $routeParams, crowdService) {
    $scope.setMapVisibility(false);
    crowdService.getInstitution($routeParams.instId, function(response) {
        $scope.institution = response.data;
    })
})
