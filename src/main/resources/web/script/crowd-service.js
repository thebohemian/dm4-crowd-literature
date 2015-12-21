angular.module("crowd").service("crowdService", function($http) {

    this.loadBustourGeojson = function(callback) {
        $http.get("json/bustour.geo.json").then(callback);
    }

    this.getEvents = function(callback) {
        var promise = $http.get("/core/topic/by_type/dm4.events.event?include_childs=true");
        if (callback) {
            promise.then(callback);
        } else {
            return promise;
        }
    }

    this.getEventParticipants = function(eventId, callback) {
        $http.get("/event/" + eventId + "/participants").then(callback);
    }

    this.getPerson = function(personId, callback) {
        $http.get("/core/topic/" + personId + "?include_childs=true").then(callback);
    }
})
