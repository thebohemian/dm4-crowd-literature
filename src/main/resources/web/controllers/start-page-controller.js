angular.module("crowd")
.controller("StartPageController", function($scope, crowdService) {
    crowdService.getStartPageContent(function(response) {
        $scope.startPageContent = response.data;
    })
})
