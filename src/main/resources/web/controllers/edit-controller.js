persona = null;

angular.module("crowdedit")
.controller("EditPersonController", function($scope, $routeParams, $location, crowdService) {
    var loadPerson = function(personId) {
      crowdService.getEditablePerson(personId, function(response) {
        $scope.person = response.data;
        $scope.isUpdateBlocked = false;

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

    var updatePerson = function() {
      var person = $scope.person;
      // Prevent further updates until the last write hasn't been followed by
      // a reload.
      if (!$scope.isUpdateBlocked) {
        $scope.isUpdateBlocked = true;

        // Reloads the person completely automatically
        crowdService.updatePerson(person, function(response) {
          loadPerson(person.id);
        });

      }
    };

    $scope.newData = {};

    $scope.isUpdateBlocked = false;

    // Autoload
    loadPerson($routeParams.personId);

    $scope.addNewEmail = function() {
      var person = $scope.person;

      if (!person['childs']['dm4.contacts.email_address']) {
        person['childs']['dm4.contacts.email_address'] = [];
      }

      person['childs']['dm4.contacts.email_address'].push({
        uri: "",
        type_uri: "dm4.contacts.email_address",
        value: $scope.newData.email
      });

      $scope.newData.email = "";

      updatePerson();
    };

    var moveToTrash = function(array, index) {
      var old = array[index];

      if (old.id) {
        // Replace with a trash object
        array[index] = "del_id:" + old.id;
      } else {
        // Has not been saved yet. Just throw away the array entry
        array.splice(index, 1);
      }
    };

    $scope.removeEmail = function(index) {
      moveToTrash($scope.person['childs']['dm4.contacts.email_address'], index);

      updatePerson();
    };

    $scope.addNewAddress = function() {
      var person = $scope.person;

      if (!person['childs']['dm4.contacts.address#dm4.contacts.address_entry']) {
        person['childs']['dm4.contacts.address#dm4.contacts.address_entry'] = [];
      }

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

      updatePerson();
    };

    $scope.removeAddress = function(index) {
      moveToTrash($scope.person['childs']['dm4.contacts.address#dm4.contacts.address_entry'], index);

      updatePerson();
    };

    $scope.updatePerson = updatePerson;

})
