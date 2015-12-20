angular.module("crowd", ["ngRoute", "ngSanitize", "leaflet-directive"])
.config(function($routeProvider, $logProvider, $httpProvider) {
    $routeProvider
        .when("/welcome",        {templateUrl: "partials/welcome.html"})
        .when("/event/:eventId", {templateUrl: "partials/event.html", controller: "eventController", resolve: {
            eventsModel: function($rootScope) {
                return $rootScope.eventsModel;
            } 
        }})
        .otherwise({redirectTo: "/welcome"})
    $logProvider.debugEnabled(false);
    $httpProvider.useLegacyPromiseExtensions(false);
})
.controller("eventController", function($scope, $routeParams) {
    var eventId = $routeParams.eventId;
    console.log("CONSTRUCTING eventController", eventId);
    $scope.showEvent(eventId);
})
.controller("crowdController", function($scope, $location, $q, $rootScope, crowdService) {

    console.log("CONSTRUCTING crowdController");

    // application scope

    $scope.events = {};

    $scope.showEvent = function(eventId) {
        $location.path("/event/" + eventId);
        $scope.event = $scope.events[eventId];
        crowdService.getEventParticipants(eventId, function(response) {
            $scope.participants = response.data.items;
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

    crowdService.loadBustourGeojson(function(response) {
        $scope.bustour = {
            data: response.data,
            style: {
                color: "#f86767"
            }
        }
    })

    $rootScope.eventsModel = crowdService.getEvents().then(function(response) {     // put promise in root scope
        response.data.items.forEach(function(event) {
            $scope.events[event.id] = event;    // put in model
            addMarker(event);                   // add to map
        });
        console.log("Events model complete");
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
})
