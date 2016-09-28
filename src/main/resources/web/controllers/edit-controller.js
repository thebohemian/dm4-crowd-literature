persona=null;

angular.module("crowdedit")
.controller("EditPersonController", function($scope, $routeParams, $location, crowdService) {
    crowdService.getEditablePerson($routeParams.personId, function(response) {
//      persona= response.data;
      $scope.person = response.data;
    });

    $scope.enlargeEmail = function(person) {
      person['childs']['dm4.contacts.email_address'].push({
        uri: "",
        type_uri: "dm4.contacts.email_address",
        value: ""
      });
    }

    $scope.updatePerson = function(person) {
      crowdService.updatePerson(person);
    }

})
