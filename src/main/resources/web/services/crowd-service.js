angular.module("crowd").service("crowdService", function($http) {

    this.loadBustourGeojson = function(callback) {
        $http.get("bustour.geo.json").then(callback);
    }

    // Person

    this.getPerson = function(personId, callback) {
        $http.get("/crowd/person/" + personId).then(callback);
    }

    // Events

    this.getAllEvents = function() {
        return $http.get("/core/topic/by_type/dm4.events.event?include_childs=true");
    }

    this.getParticipantsOfEvent = function(eventId, callback) {
        $http.get("/event/" + eventId + "/participants").then(callback);
    }

	// Event Series

    this.getEventSeries = function(eventSeriesId, callback) {
        getTopic(eventSeriesId, callback);
    }

    this.getEventSeriesOfEvent = function(eventId, callback) {
        $http.get("/crowd/event/" + eventId + "/event_series").then(callback);
    }

    this.getEventsOfEventSeries = function(eventSeriesId, callback) {
        $http.get("/crowd/event_series/" + eventSeriesId + "/events").then(callback);
    }

    // Work

    this.getWork = function(workId, callback) {
        $http.get("/crowd/work/" + workId).then(callback);
    }

    // Institution

    this.getInstitution = function(instId, callback) {
        getTopic(instId, callback);
    }

    this.getInstitutionPersons = function(instId, callback) {
        $http.get("/contact/" + instId + "/persons").then(callback);
    }

    // ------------------------------------------------------------------------------------------------- Private Methods

    function getTopic(topicId, callback) {
        $http.get("/core/topic/" + topicId + "?include_childs=true").then(callback);
    }
})
