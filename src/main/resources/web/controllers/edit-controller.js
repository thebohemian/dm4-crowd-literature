angular.module("crowdedit")
.controller("EditPersonController", function($scope, $routeParams, $location, crowdService) {
    var loadPerson = function(personId) {
      crowdService.getEditablePerson(personId, function(response) {
        $scope.person = response.data;
        $scope.isUpdatedBlocked = false;
      });
    };

    $scope.isUpdatedBlocked = false;

    // Autoload
    loadPerson($routeParams.personId);

    $scope.enlargeEmail = function(person) {
      person['childs']['dm4.contacts.email_address'].push({
        uri: "",
        type_uri: "dm4.contacts.email_address",
        value: ""
      });
    };

    $scope.updatePerson = function(person) {
      if (!$scope.isUpdatedBlocked) {
        $scope.isUpdatedBlocked = true;

        crowdService.updatePerson(person, function(response) {
          loadPerson(person.id);
        });
      }
    };

})
