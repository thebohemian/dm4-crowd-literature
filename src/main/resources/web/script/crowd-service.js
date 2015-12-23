angular.module("crowd").service("crowdService", function($http) {

    this.loadBustourGeojson = function(callback) {
        $http.get("json/bustour.geo.json").then(callback);
    }

    // Core

    this.getAllEvents = function() {
        return $http.get("/core/topic/by_type/dm4.events.event?include_childs=true");
    }

    this.getPerson = function(personId, callback) {
        getTopic(personId, callback);
    }

    this.getInstitution = function(instId, callback) {
        getTopic(instId, callback);
    }

    // Events

    this.getEventParticipants = function(eventId, callback) {
        $http.get("/event/" + eventId + "/participants").then(callback);
    }

    this.getEventsByParticipant = function(personId, callback) {
        $http.get("/event/participant/" + personId).then(callback);
    }

    // Contacts

    this.getInstitutions = function(personId, callback) {
        $http.get("/contact/" + personId + "/institutions").then(callback);
    }

    this.getPersons = function(instId, callback) {
        $http.get("/contact/" + instId + "/persons").then(callback);
    }

    // ------------------------------------------------------------------------------------------------- Private Methods

    function getTopic(topicId, callback) {
        $http.get("/core/topic/" + topicId + "?include_childs=true").then(callback);
    }
})
