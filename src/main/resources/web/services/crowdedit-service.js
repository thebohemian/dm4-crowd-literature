angular.module("crowdedit").service("crowdService", function($http) {

    // --- Person ---

    this.getPersonIdByLoggedInUser = function(callback, errorCallback) {
        $http.get("/crowd/person/id/by_loggedinuser")
          .then(callback, errorCallback);
    }

    this.validateSetup = function(successCallback, errorCallback) {
        $http.post("/crowd/person/validate_setup")
          .then(successCallback, errorCallback);
    }

    this.getEditablePerson = function(personId, callback) {
      $http.get("/core/topic/" + personId + "?include_childs=true")
        .then(callback);
    }

    this.updatePerson = function(person, callback) {
        $http.put("/core/topic/" + person.id, person).then(callback);
    }

    this.logout = function(callback) {
      $http.post("/accesscontrol/logout").then(callback);
    }

})
