angular.module("crowdedit")
.controller("EditPersonController", function($scope, $routeParams, $location, crowdService) {
    crowdService.getEditablePersonByUsername($routeParams.username, function(response) {
        $scope.person = response.data;
        $scope.setMapVisibility(false);
    })

    $scope.goToEditPerson = function(username) {
      $location.path("/editperson/" + username);
    }

    $scope.updatePerson = function(person) {
      // TODO: Implement in server
      crowdService.updatePerson(person);
    }

    $scope.enlargeEmail = function(person) {
      person.emails.push("");
    }
})
