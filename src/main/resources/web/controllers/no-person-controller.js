angular.module("crowdedit")
.controller("NoPersonController", function($scope, $location, crowdService) {

    $scope.logout = function() {
      crowdService.logout(function() {
        $location.path("/start");
      });
    };

})
