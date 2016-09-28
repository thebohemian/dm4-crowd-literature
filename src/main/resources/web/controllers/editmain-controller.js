angular.module("crowdedit")
.controller("EditMainController", function($scope, $location, crowdService) {
    $scope.person = {
    };

    $scope.credentials = {
      username:"",
      password:""
    };

    $scope.goToEditPerson = function(username) {
      crowdService.getPersonIdByUsername(username, function(response) {
          var personId = response.data;
          if (personId == -1) {
            $location.path("/error-noperson");
          } else {
            $location.path("/editperson/" + personId);
          }
      })

    }

})
