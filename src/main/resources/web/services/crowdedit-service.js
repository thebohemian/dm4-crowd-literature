angular.module("crowdedit").service("crowdService", function($http) {

    // --- Person ---

    this.getEditablePersonByUsername = function(username, callback) {
        $http.get("/crowd/editable_person/by_username/" + username).then(callback);
    }

    this.updatePerson = function(person, callback) {
        $http.put("/crowd/editable_person/" + person.id, person).then(callback);
    }

})
