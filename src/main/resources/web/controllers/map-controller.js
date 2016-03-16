angular.module("crowd").controller("MapController", function($scope, $location, crowdService) {

    var SHOW_BUSTOUR = false;

    // leaflet config (marker + cluster)

    $scope.retina = L.Browser.retina;
    if (!$scope.retina) {
        var markerIcon = {
            iconUrl: "lib/leaflet/images/event-marker.png",
            iconSize: [28, 41],
            iconAnchor: [14, 41],
            shadowUrl: "lib/leaflet/images/marker-shadow.png",
            shadowSize: [41, 41],
            shadowAnchor: [12, 41]
        }
        var clusterSize = 40;
        var maxClusterRadius = 40;
        var spiderfyDistanceMultiplier = 1.5;
    } else {
        var markerIcon = {
            iconUrl: "lib/leaflet/images/event-marker-1.3x.png",
            iconSize: [36, 53],
            iconAnchor: [18, 53],
            shadowUrl: "lib/leaflet/images/marker-shadow.png",
            shadowSize: [41, 41],
            shadowAnchor: [12, 41]
        }
        var clusterSize = 52;
        var maxClusterRadius = 52;
        var spiderfyDistanceMultiplier = 2;
    }

    // leaflet config (map)

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
                        showOnSelector: false,
                        maxClusterRadius: maxClusterRadius,
                        spiderfyDistanceMultiplier: spiderfyDistanceMultiplier,
                        iconCreateFunction: createClusterIcon
                    }
                }
            }
        },
        markers: {}
    });

    // startup code

    var mql = matchMedia("(orientation: landscape)");
    mql.addListener(updateOrientation);
    updateOrientation(mql);

    $scope.$on("leafletDirectiveMarker.map.click", function(event, args) {
        var eventId = args.modelName
        $location.path("/event/" + eventId);
    })

    $scope.backToMap = function() {
        $location.path("/welcome");
    }

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

    crowdService.getAllEvents(function(response) {
        response.data.forEach(function(event) {
            addMarker(event);
        })
    })

    // ------------------------------------------------------------------------------------------------- Private Methods

    function addMarker(event) {
        if (event.lat != undefined && event.lng != undefined) {
            $scope.markers[event.id] = {
                lat: event.lat,
                lng: event.lng,
                layer: "currentEvents",
                icon: markerIcon
            }
        } else {
            console.log("WARNING: event \"" + event.title + "\" (" + event.id +
                ") can't appear on map -- its geo coordinate is unknown")
        }
    }

    function createClusterIcon(cluster) {
        return new L.DivIcon({
            html: "<div><span>" + cluster.getChildCount() + "</span></div>",
            className: "marker-cluster",
            iconSize: new L.Point(clusterSize, clusterSize)
        });
    }

    function updateOrientation(mql) {
        $scope.landscape = mql.matches;
        $scope.portrait = !$scope.landscape;
    }
})
