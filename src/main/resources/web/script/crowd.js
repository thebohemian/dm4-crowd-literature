angular.module("crowd", ["ngRoute", "ngSanitize", "leaflet-directive"])
.config(function($routeProvider, $logProvider, $httpProvider) {
    $routeProvider
        .when("/welcome",          {templateUrl: "partials/welcome.html", controller: "welcomeController"})
        .when("/event/:eventId",   {templateUrl: "partials/event.html",   controller: "eventController", resolve: {
            eventsModel: function($rootScope) {
                return $rootScope.eventsModel;
            } 
        }})
        .when("/person/:personId", {templateUrl: "partials/person.html",  controller: "personController"})
        .otherwise({redirectTo: "/welcome"})
    $logProvider.debugEnabled(false);
    $httpProvider.useLegacyPromiseExtensions(false);
})
.controller("welcomeController", function($scope) {
    console.log("CONSTRUCTING welcomeController");
    $scope.event = null;
})
.controller("eventController", function($scope, $routeParams) {
    var eventId = $routeParams.eventId;
    console.log("CONSTRUCTING eventController", eventId);
    $scope.showEvent(eventId);
})
.controller("personController", function($scope, $routeParams, crowdService) {
    var personId = $routeParams.personId;
    console.log("CONSTRUCTING personController", personId);
    showPerson(personId);

    function showPerson(personId) {
        crowdService.getPerson(personId, function(response) {
            $scope.person = response.data;
        })
        crowdService.getEventsByParticipant(personId, function(response) {
            $scope.events = response.data.items;
        })
    }
})
.controller("crowdController", function($scope, $location, $q, $rootScope, crowdService) {

    console.log("CONSTRUCTING crowdController");

    // application scope

    $scope.events = {};

    $scope.showEvent = function(eventId) {
        $scope.event = $scope.events[eventId];
        crowdService.getEventParticipants(eventId, function(response) {
            $scope.participants = response.data.items;
        })
    }

    $scope.backToMap = function() {
        $location.path("/welcome");
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
            url: "https://api.tiles.mapbox.com/v4/{mapId}/{z}/{x}/{y}.png?access_token={accessToken}",
            options: {
                mapId: 'jri.oaem7303',
                accessToken: 'pk.eyJ1IjoianJpIiwiYSI6ImNpaG5ubmtsdDAwaHB1bG00aGk1c3BhamcifQ.2XkYFs4hGOel8DYCy4qKKw',
                attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a>, ' +
                    'Imagery &copy; <a href="http://mapbox.com">Mapbox</a>'
            }
        },
        markers: {}
    });

    $scope.$on("leafletDirectiveMarker.map.click", function(event, args) {
        var eventId = args.modelName
        $location.path("/event/" + eventId);
        $scope.showEvent(eventId)
    })

    // startup code

    var mql = matchMedia("(orientation: landscape)");
    updateOrientation(mql);
    mql.addListener(updateOrientation);

    crowdService.loadBustourGeojson(function(response) {
        $scope.bustour = {
            data: response.data,
            style: {
                color: "#f86767"
            }
        }
    })

    $rootScope.eventsModel = crowdService.getAllEvents().then(function(response) {     // store promise in root scope
        response.data.items.forEach(function(event) {
            $scope.events[event.id] = event;    // put in model
            addMarker(event);                   // add to map
        });
        console.log("Events model complete");
    })

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
