angular.module("crowdedit").service("crowdService", function($http) {

    // --- Person ---

    this.getPersonIdByUsername = function(username, callback) {
        $http.get("/crowd/person/id/by_username/" + username)
          .then(callback);
    }

    this.getEditablePerson = function(personId, callback) {
      $http.get("/core/topic/" + personId + "?include_childs=true")
        .then(callback);
    }

    this.updatePerson = function(person, callback) {
        $http.put("/core/topic/" + person.id, person).then(callback);
    }

})
