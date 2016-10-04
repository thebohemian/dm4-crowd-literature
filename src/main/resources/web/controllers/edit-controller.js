persona = null;

angular.module("crowdedit")
.controller("EditPersonController", function($scope, $routeParams, $location, crowdService) {
    var loadPerson = function(personId) {
      crowdService.getEditablePerson(personId, function(response) {
        $scope.person = response.data;
        $scope.isUpdatedBlocked = false;

        persona = $scope.person;

        // Resets new data
        $scope.newData = {
          email: "",
          address: {
            street: "",
            postalCode: "",
            city: "",
            country: ""
          }
        };

      });
    };

    $scope.newData = {};

    $scope.isUpdatedBlocked = false;

    // Autoload
    loadPerson($routeParams.personId);

    $scope.addNewEmail = function(person) {
      person['childs']['dm4.contacts.email_address'].push({
        uri: "",
        type_uri: "dm4.contacts.email_address",
        value: $scope.newData.email
      });

      $scope.newData.email = "";
    };

    $scope.addNewAddress = function(person) {
      person['childs']['dm4.contacts.address#dm4.contacts.address_entry'].push({
        uri: "",
        type_uri: "dm4.contacts.address",
        childs: {
          "dm4.contacts.street": {
            type_uri: "dm4.contacts.street",
            value: $scope.newData.address.street
          },
          "dm4.contacts.postal_code": {
            type_uri: "dm4.contacts.postal_code",
            value: $scope.newData.address.postalCode
          },
          "dm4.contacts.city": {
            type_uri: "dm4.contacts.city",
            value: $scope.newData.address.city
          },
          "dm4.contacts.country": {
            type_uri: "dm4.contacts.country",
            value: $scope.newData.address.country
          }
        }
      });

      $scope.newData.address = {
        street: "",
        postalCode: "",
        city: "",
        country: ""
      };

    };

    $scope.updatePerson = function(person) {
      // Prevent further updates until the last write hasn't been followed by
      // a reload.
      if (!$scope.isUpdatedBlocked) {
        $scope.isUpdatedBlocked = true;

        // Reloads the person completely automatically
        crowdService.updatePerson(person, function(response) {
          loadPerson(person.id);
        });
      }
    };

})
