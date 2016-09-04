angular.module("crowd")
.controller("PersonController", function($scope, $routeParams, crowdService) {
    crowdService.getPerson($routeParams.personId, function(response) {
        $scope.person = response.data;
    })
})
.controller("InstitutionController", function($scope, $routeParams, crowdService) {
    crowdService.getInstitution($routeParams.instId, function(response) {
        $scope.institution = response.data;
    })
})
.controller("WorkController", function($scope, $routeParams, crowdService) {
    crowdService.getWork($routeParams.workId, function(response) {
        $scope.work = response.data;
    })
})
