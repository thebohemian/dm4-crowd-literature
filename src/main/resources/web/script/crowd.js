CROWD = {};

angular.module("crowd", ["ngRoute"])
.config(function($routeProvider) {
    $routeProvider
        .when("/event/:eventId", {
            templateUrl: "partials/event.html", controller: "eventController"
        })
})
.controller("crowdController", function($scope, crowdService) {

    var map = createMap();
    crowdService.getEvents(function(events) {
        console.log("Events", events)
        events.items.forEach(function(event) {
            var geoCoordinate = event.childs["dm4.contacts.address"].childs["dm4.geomaps.geo_coordinate"].childs;
            L.marker([
                geoCoordinate["dm4.geomaps.latitude"].value,
                geoCoordinate["dm4.geomaps.longitude"].value
            ]).addTo(map);
        });
    })

    function createMap() {
        var map = L.map("map").setView([55, 20], 4);
        L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
            attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
                '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
                'Imagery © <a href="http://mapbox.com">Mapbox</a>',
            maxZoom: 18,
            id: 'jri.oaem7303',
            accessToken: 'pk.eyJ1IjoianJpIiwiYSI6ImNpaG5ubmtsdDAwaHB1bG00aGk1c3BhamcifQ.2XkYFs4hGOel8DYCy4qKKw'
        }).addTo(map);
        L.geoJson(CROWD.tourGeoJson, {
            style: function(feature) {
                return {
                    color: feature.properties.stroke
                }
            }
        }).addTo(map)
        return map;
    }
})    
.service("crowdService", function($http) {

    this.getEvents = function(callback) {
        $http.get("/core/topic/by_type/dm4.events.event?include_childs=true").success(callback)
    }
})
