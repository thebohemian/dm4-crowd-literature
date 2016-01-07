angular.module("crowd").service("crowdService", function($http) {

    this.loadBustourGeojson = function(callback) {
        $http.get("bustour.geo.json").then(callback);
    }

    // --- Person ---

    this.getPerson = function(personId, callback) {
        $http.get("/crowd/person/" + personId).then(callback);
    }

    // --- Work ---

    this.getWork = function(workId, callback) {
        $http.get("/crowd/work/" + workId).then(callback);
    }

    // --- Event ---

    this.getEvent = function(eventId, callback) {
        $http.get("/crowd/event/" + eventId).then(callback);
    }

    this.getAllEvents = function() {
        return $http.get("/core/topic/by_type/dm4.events.event?include_childs=true");
    }

	// --- Event Series ---

    this.getEventSeries = function(eventSeriesId, callback) {
        $http.get("/crowd/event_series/" + eventSeriesId).then(callback);
    }

    // --- Institution ---

    this.getInstitution = function(instId, callback) {
        $http.get("/crowd/institution/" + instId).then(callback);
    }
})
