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

    $scope.updatePerson = function(person) {
      // TODO: Implement in server
      crowdService.updatePerson(person);
    }

    $scope.enlargeEmail = function(person) {
      person.emails.push("");
    }
})
