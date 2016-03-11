angular.module("crowd").controller("MapController", function($scope, $location, $rootScope, crowdService) {

    var SHOW_BUSTOUR = false;

    // application scope

    $scope.events = {};

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
                mapId: 'jri.ogoig93d',
                accessToken: 'pk.eyJ1IjoianJpIiwiYSI6ImNpaG5ubmtsdDAwaHB1bG00aGk1c3BhamcifQ.2XkYFs4hGOel8DYCy4qKKw',
                attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a>, ' +
                    'Imagery &copy; <a href="http://mapbox.com">Mapbox</a>'
            }
        },
        layers: {
            overlays: {
                currentEvents: {
                    name: "Current Events",
                    type: "markercluster",
                    visible: true,
                    layerParams: {
                        maxClusterRadius: 40,
                        spiderfyDistanceMultiplier: !L.Browser.retina ? 1.5 : 2,
                        showOnSelector: false
                    }
                }
            }
        },
        markers: {}
    });

    $scope.$on("leafletDirectiveMarker.map.click", function(event, args) {
        var eventId = args.modelName
        $location.path("/event/" + eventId);
    })

    // startup code

    $scope.retina = L.Browser.retina;

    var mql = matchMedia("(orientation: landscape)");
    mql.addListener(updateOrientation);
    updateOrientation(mql);

    if (SHOW_BUSTOUR) {
        crowdService.loadBustourGeojson(function(response) {
            $scope.bustour = {
                data: response.data,
                style: {
                    color: "#f55"
                }
            }
        })
    }

    $rootScope.eventsModel = crowdService.getAllEvents().then(function(response) {     // store promise in root scope
        response.data.items.forEach(function(event) {
            $scope.events[event.id] = event;    // put in model
            addMarker(event);                   // add to map
        });
    })

    // private

    var markerIcon = !L.Browser.retina ? {
        iconUrl: "lib/leaflet/images/marker-icon.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        shadowUrl: "lib/leaflet/images/marker-shadow.png",
        shadowSize: [41, 41],
        shadowAnchor: [12, 41]
    } : {
        iconUrl: "lib/leaflet/images/marker-icon-1.3x.png",
        iconSize: [32, 53],
        iconAnchor: [16, 53],
        shadowUrl: "lib/leaflet/images/marker-shadow.png",
        shadowSize: [41, 41],
        shadowAnchor: [12, 41]
    }

    function addMarker(event) {
        try {
            var geoCoordinate = event.childs["dm4.contacts.address"].childs["dm4.geomaps.geo_coordinate"].childs;
            $scope.markers[event.id] = {
                lat: geoCoordinate["dm4.geomaps.latitude"].value,
                lng: geoCoordinate["dm4.geomaps.longitude"].value,
                layer: "currentEvents",
                icon: markerIcon
            }
        } catch (e) {
            console.log("Event \"" + event.value + "\" (" + event.id + ") has a problem", e, event)
        }
    }

    function updateOrientation(mql) {
        $scope.landscape = mql.matches;
        $scope.portrait = !$scope.landscape;
    }
})
