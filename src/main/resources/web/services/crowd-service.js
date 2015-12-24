angular.module("crowd").service("crowdService", function($http) {

    this.loadBustourGeojson = function(callback) {
        $http.get("bustour.geo.json").then(callback);
    }

    // Person

    this.getPerson = function(personId, callback) {
        getTopic(personId, callback);
    }

    // Events

    this.getAllEvents = function() {
        return $http.get("/core/topic/by_type/dm4.events.event?include_childs=true");
    }

    this.getEventParticipants = function(eventId, callback) {
        $http.get("/event/" + eventId + "/participants").then(callback);
    }

    this.getEventsByParticipant = function(personId, callback) {
        $http.get("/event/participant/" + personId).then(callback);
    }

    // Work

    this.getWork = function(workId, callback) {
        getTopic(workId, callback);
    }

    this.getWorks = function(personId, callback) {
        $http.get("/crowd/person/" + personId + "/works").then(callback);
    }

    /**
     * @param   workId      ID of a work or a translation
     */
    this.getWorkPersons = function(workId, callback) {
        $http.get("/crowd/work/" + workId + "/persons").then(callback);
    }

    // Institution

    this.getInstitution = function(instId, callback) {
        getTopic(instId, callback);
    }

    this.getInstitutions = function(personId, callback) {
        $http.get("/contact/" + personId + "/institutions").then(callback);
    }

    this.getInstitutionPersons = function(instId, callback) {
        $http.get("/contact/" + instId + "/persons").then(callback);
    }

    // ------------------------------------------------------------------------------------------------- Private Methods

    function getTopic(topicId, callback) {
        $http.get("/core/topic/" + topicId + "?include_childs=true").then(callback);
    }
})
