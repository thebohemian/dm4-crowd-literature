angular.module("crowd").controller("InstitutionController", function($scope, $routeParams, crowdService) {
    crowdService.getInstitution($routeParams.instId, function(response) {
        $scope.institution = response.data;
    })
})
