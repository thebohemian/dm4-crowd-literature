angular.module("crowd").service("crowdService", function($http) {

    this.getStartPageContent = function(callback) {
        $http.get("/crowd/start_page").then(callback);
    }

    this.loadBustourGeojson = function(callback) {
        $http.get("crowd-bustour.geo.json").then(callback);
    }

    // --- Person ---

    this.getPerson = function(personId, callback) {
        $http.get("/crowd/person/" + personId).then(callback);
    }

    this.getAllPersons = function(callback) {
        return $http.get("/crowd/persons").then(callback);
    }

    // --- Work ---

    this.getWork = function(workId, callback) {
        $http.get("/crowd/work/" + workId).then(callback);
    }

    // --- Institution ---

    this.getInstitution = function(instId, callback) {
        $http.get("/crowd/institution/" + instId).then(callback);
    }

    this.getAllInstitutions = function(callback) {
        return $http.get("/crowd/institutions").then(callback);
    }

})
