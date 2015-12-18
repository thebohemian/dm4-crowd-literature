CROWD = {};

angular.module("crowd", [/*"ngRoute"*/ "leaflet-directive"])
/*.config(function($routeProvider) {
    $routeProvider
        .when("/event/:eventId", {
            templateUrl: "partials/event.html", controller: "eventController"
        })
        .otherwise({redirectTo: "/event"})
})*/
.controller("crowdController", function($scope, $location, crowdService) {

    $scope.center = {
        lat: 55,
        lng: 20,
        zoom: 4
    }
    $scope.tiles = {
        url: "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}",
        options: {
            attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
                '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
                'Imagery © <a href="http://mapbox.com">Mapbox</a>',
            id: 'jri.oaem7303',
            accessToken: 'pk.eyJ1IjoianJpIiwiYSI6ImNpaG5ubmtsdDAwaHB1bG00aGk1c3BhamcifQ.2XkYFs4hGOel8DYCy4qKKw'
        }
    }
    crowdService.getEvents(function(events) {
        console.log("Events", events)
        events.items.forEach(function(evt) {
            createMarker(evt);
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

    function createMarker(evt) {
        var geoCoordinate = evt.childs["dm4.contacts.address"].childs["dm4.geomaps.geo_coordinate"].childs;
        // geoCoordinate["dm4.geomaps.latitude"].value
        // geoCoordinate["dm4.geomaps.longitude"].value
        // $scope.event = evt;
    }
})    
.service("crowdService", function($http) {

    this.getEvents = function(callback) {
        $http.get("/core/topic/by_type/dm4.events.event?include_childs=true").success(callback)
    }
})
