angular.module("crowd").service("crowdService", function($http) {

    this.loadBustourGeojson = function(callback) {
        $http.get("json/bustour.geo.json").then(callback);
    }

    this.getAllEvents = function() {
        return $http.get("/core/topic/by_type/dm4.events.event?include_childs=true");
    }

    this.getEventParticipants = function(eventId, callback) {
        $http.get("/event/" + eventId + "/participants").then(callback);
    }

    this.getEventsByParticipant = function(personId, callback) {
        $http.get("/event/participant/" + personId).then(callback);
    }

    this.getPerson = function(personId, callback) {
        $http.get("/core/topic/" + personId + "?include_childs=true").then(callback);
    }
})
