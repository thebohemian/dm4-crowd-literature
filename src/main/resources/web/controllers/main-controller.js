angular.module("crowd").controller("MainController", function($scope, $location, $timeout, crowdService, leafletData) {

    var SHOW_BUSTOUR = true;
    var BUSTOUR_ZOOM_THRESHOLD = 11;    // bustour is shown only below this zoom level

    var bustour;                        // GeoJSON cache
    
    $scope.hires = matchMedia("(min-resolution: 144dpi)").matches;  // put in scope solely for debugging
    $scope.devicePixelRatio = devicePixelRatio;                     // put in scope solely for debugging

    // leaflet config (marker + cluster)

    function createMarkerIcon(selected) {
        return !$scope.hires ? {
            iconUrl: markerIconUrl(selected),
            iconSize: [28, 41],
            iconAnchor: [14, 41],
            shadowUrl: "lib/leaflet/images/marker-shadow.png",
            shadowSize: [41, 41],
            shadowAnchor: [12, 41]
        } : {
            iconUrl: markerIconUrl(selected),
            iconSize: [36, 53],
            iconAnchor: [18, 53],
            shadowUrl: "lib/leaflet/images/marker-shadow.png",
            shadowSize: [41, 41],
            shadowAnchor: [12, 41]
        }
    }

    function markerIconUrl(selected) {
        var iconFile = "event-marker" + (selected ? "-selected" : "") + ($scope.hires ? "-1.3x" : "") + ".png";
        return "lib/leaflet/images/" + iconFile;
    }

    var markerIcon = createMarkerIcon();
    var markerIconSelected = createMarkerIcon(true);

    if (!$scope.hires) {
        var clusterSize = 40;
        var maxClusterRadius = 40;
        var spiderfyDistanceMultiplier = 1.5;
    } else {
        var clusterSize = 52;
        var maxClusterRadius = 52;
        var spiderfyDistanceMultiplier = 2;
    }

    // leaflet config (map)

    angular.extend($scope, {
        center: {
            lat: 56.5,
            lng: 20,
            zoom: 4
        },
        defaults: {
            scrollWheelZoom: true
        },
        tiles: {
            url: "https://api.mapbox.com/v4/{mapId}/{z}/{x}/{y}.png?access_token={accessToken}",
            options: {
                mapId: 'mapbox.emerald',
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

    // initial calculation of the map size once the flex layout is done
    calculateMapSize();

    // recalculate the map size each time when the map reappears on screen
    $scope.$watch("mapVisibility", function(mapVisibility) {
        if (mapVisibility) {
            calculateMapSize();
        }
    });

    $scope.$watch("selectedEventId", function(eventId, oldEventId) {
        if (eventId) {
            $scope.markers[eventId].icon = markerIconSelected;
        }
        if (oldEventId) {
            $scope.markers[oldEventId].icon = markerIcon;
        }
    });

    $scope.$on("leafletDirectiveMarker.map.click", function(event, args) {
        var eventId = args.modelName;
        $location.path("/event/" + eventId);
    })

    $scope.setMapVisibility = function(mapVisibility) {
        $scope.mapVisibility = mapVisibility;
    }

    $scope.setSelectedEvent = function(eventId) {
        $scope.selectedEventId = eventId;
    }

    $scope.sortEvents = function(events) {
        events.sort(function(e1, e2) {
            var d1 = e1.from.date;
            var d2 = e2.from.date;
            if (d1.year != d2.year) {
                return d1.year - d2.year;
            } else if (d1.month != d2.month) {
                return d1.month - d2.month;
            } else {
                return d1.day - d2.day;
            }
        });
    }

    if (SHOW_BUSTOUR) {
        crowdService.loadBustourGeojson(function(response) {
            bustour = {
                data: response.data,
                style: {
                    color: "rgb(218, 105, 6)",
                    weight: 5,
                    opacity: 1,
                    dashArray: "15, 10",
                    lineCap: "butt",
                    lineJoin: "miter"
                }
            }
            $scope.$watch("center.zoom", function(zoom) {
                $scope.bustour = $scope.center.zoom < BUSTOUR_ZOOM_THRESHOLD ? bustour : null;
            });
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

    function calculateMapSize() {
        leafletData.getMap("map").then(function(map) {
            $timeout(function() {
                map.invalidateSize();
            }, 3000);
        });
    }

    function createClusterIcon(cluster) {
        return new L.DivIcon({
            html: "<div><span>" + cluster.getChildCount() + "</span></div>",
            className: "marker-cluster",
            iconSize: new L.Point(clusterSize, clusterSize)
        });
    }

    function updateOrientation(mql) {
        // Note: this media query listener is called directly from the browser, that is outside the angular context.
        // So, we must explicitly $apply the scope manipulation.
        $scope.$applyAsync(function() {
            $scope.landscape = mql.matches;
            $scope.portrait = !$scope.landscape
        })
    }
})
