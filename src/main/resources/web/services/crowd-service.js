angular.module("crowd").service("crowdService", function($http) {

    this.getStartPageContent = function(callback) {
        $http.get("/crowdmeet/start_page").then(callback);
    }

    // --- Person ---

    this.getPerson = function(personId, callback) {
        $http.get("/crowdmeet/person/" + personId).then(callback);
    }

    this.getAllPersons = function(callback) {
        return $http.get("/crowdmeet/persons").then(callback);
    }

    // --- Work ---

    this.getWork = function(workId, callback) {
        $http.get("/crowdmeet/work/" + workId).then(callback);
    }

    // --- Institution ---

    this.getInstitution = function(instId, callback) {
        $http.get("/crowdmeet/institution/" + instId).then(callback);
    }

    this.getAllInstitutions = function(callback) {
        return $http.get("/crowdmeet/institutions").then(callback);
    }

})
