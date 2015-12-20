angular.module("crowd", [/*"ngRoute"*/ "ngSanitize", "leaflet-directive"])
/*.config(function($routeProvider) {
    $routeProvider
        .when("/event/:eventId", {
            templateUrl: "partials/event.html", controller: "eventController"
        })
        .otherwise({redirectTo: "/event"})
})*/
.config(function($logProvider) {
    $logProvider.debugEnabled(false);
})
.controller("crowdController", function($scope, $location, crowdService) {

    // application scope

    $scope.events = {};

    $scope.showEvent = function(eventId) {
        $scope.event = $scope.events[eventId];
        crowdService.getEventParticipants(eventId, function(participants) {
            $scope.participants = participants.items;
        })
    }

    $scope.backToMap = function() {
        $scope.event = null;
    }

    // map scope

    angular.extend($scope, {
        center: {
            lat: 55,
            lng: 20,
            zoom: 4
        },
        defaults: {
            scrollWheelZoom: false
        },
        tiles: {
            url: "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}",
            options: {
                attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
                    '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
                    'Imagery © <a href="http://mapbox.com">Mapbox</a>',
                id: 'jri.oaem7303',
                accessToken: 'pk.eyJ1IjoianJpIiwiYSI6ImNpaG5ubmtsdDAwaHB1bG00aGk1c3BhamcifQ.2XkYFs4hGOel8DYCy4qKKw'
            }
        },
        markers: {}
    });

    $scope.$on("leafletDirectiveMarker.map.click", function(event, args) {
        var eventId = args.modelName
        $scope.showEvent(eventId)
    })

    // startup code

    crowdService.loadBustourGeojson(function(data) {
        $scope.bustour = {
            data: data,
            style: {
                color: "#f86767"
            }
        }
    })

    crowdService.getEvents(function(events) {
        events.items.forEach(function(event) {
            $scope.events[event.id] = event;    // put in model
            addMarker(event);                   // add to map
        });
    })

    var mql = matchMedia("(orientation: landscape)");
    updateOrientation(mql);
    mql.addListener(updateOrientation);

    // private

    function addMarker(event) {
        var geoCoordinate = event.childs["dm4.contacts.address"].childs["dm4.geomaps.geo_coordinate"].childs;
        $scope.markers[event.id] = {
            lat: geoCoordinate["dm4.geomaps.latitude"].value,
            lng: geoCoordinate["dm4.geomaps.longitude"].value
        }
    }

    function updateOrientation(mql) {
        $scope.landscape = mql.matches;
        $scope.portrait = !$scope.landscape;
    }
})    
.service("crowdService", function($http) {

    this.loadBustourGeojson = function(callback) {
        $http.get("json/bustour.geo.json").success(callback);
    }

    this.getEvents = function(callback) {
        $http.get("/core/topic/by_type/dm4.events.event?include_childs=true").success(callback);
    }

    this.getEventParticipants = function(eventId, callback) {
        $http.get("/event/" + eventId + "/participants").success(callback);
    }
})
