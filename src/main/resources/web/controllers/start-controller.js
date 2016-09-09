angular.module("crowd")
.controller("StartController", function($scope, crowdService) {
    $scope.setMapVisibility(false);
    $scope.resetMap();
    crowdService.getStartPageContent(function(response) {
        $scope.startPageContent = response.data;
    })
})
.controller("MapController", function($scope) {
    $scope.setMapVisibility(true);
})
