angular.module("crowd").service("crowdService", function($http) {

    this.loadBustourGeojson = function(callback) {
        $http.get("json/bustour.geo.json").then(callback);
    }

    // Core

    this.getPerson = function(personId, callback) {
        $http.get("/core/topic/" + personId + "?include_childs=true").then(callback);
    }

    this.getAllEvents = function() {
        return $http.get("/core/topic/by_type/dm4.events.event?include_childs=true");
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

    this.getPersons = function(institutionId, callback) {
        $http.get("/contact/" + institutionId + "/persons").then(callback);
    }
})
