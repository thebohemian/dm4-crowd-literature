angular.module("crowd").controller("MainController", function($scope, $rootScope, $location, $timeout, crowdService,
                                                              leafletData) {
    $scope.hires = matchMedia("(min-resolution: 144dpi)").matches;  // put in scope solely for debugging
    $scope.devicePixelRatio = devicePixelRatio;                     // put in scope solely for debugging

    // --- Leaflet config (marker + cluster) ---

    var icons = {
      person: {
        normal:   createMarkerIcon('person', false),
        selected: createMarkerIcon('person', true)
      },
      institution: {
        normal:   createMarkerIcon('institution', false),
        selected: createMarkerIcon('institution', true)
      }

    };

    if (!$scope.hires) {
        var clusterSize = 40;
        var maxClusterRadius = 40;
        var spiderfyDistanceMultiplier = 1.5;
    } else {
        var clusterSize = 52;
        var maxClusterRadius = 52;
        var spiderfyDistanceMultiplier = 2;
    }

    // --- Leaflet config (map) ---

    $scope.center = {
        lat: 56.5,
        lng: 20,
        zoom: 4
    }

    $scope.defaults = {
        scrollWheelZoom: true
    }

    $scope.tiles = {
        url: "https://api.mapbox.com/v4/{mapId}/{z}/{x}/{y}.png?access_token={accessToken}",
        options: {
            mapId: 'jri.2eeeaa1d',
            accessToken: 'pk.eyJ1IjoianJpIiwiYSI6ImNpaG5ubmtsdDAwaHB1bG00aGk1c3BhamcifQ.2XkYFs4hGOel8DYCy4qKKw',
            attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a>, ' +
                'Imagery &copy; <a href="http://mapbox.com">Mapbox</a>'
        }
    }

    $scope.layers = {
        overlays: {
            person: {
                name: "Persons",
                type: "markercluster",
                visible: true,
                layerParams: {
                    showOnSelector: false,
                    maxClusterRadius: maxClusterRadius,
                    spiderfyDistanceMultiplier: spiderfyDistanceMultiplier,
                    iconCreateFunction: createClusterIcon
                }
            },
            institution: {
                name: "Institutions",
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
    }

    $scope.search = {
      text: ""
    }

    $scope.filter = null;

    $scope.markers = {};

    $scope.visibleMarkers = {};

    // --- Event Listeners ---

    // recalculate the map size each time when the map reappears on screen
    $scope.$watch("mapVisibility", function(mapVisibility) {
        if (mapVisibility) {
            calculateMapSize();
        }
    });

    $scope.$watch("selectedMarkerId", function(markerId, oldMarkerId) {
        if (markerId) {
            var marker = $scope.markers[markerId];
            $scope.markers[markerId].icon = icons[marker.layer].selected;
        }
        if (oldMarkerId) {
          var marker = $scope.markers[oldMarkerId];
          $scope.markers[markerId].icon = icons[marker.layer].normal;
        }
    });

    $scope.$on("leafletDirectiveMarker.map.click", function(event, args) {
        var id = args.modelName;
        var typeFromLayer = args.layerName;
        $location.path("/" + typeFromLayer + "/" + id);
    });

    // --- Controller Methods ---
    $scope.lastSearchTerm = "";

    $scope.onChangeSearch = function() {
      if ($scope.search.text.length >= 3) {
        var searchTerm = "*" + $scope.search.text + "*";
        $scope.lastSearchTerm = searchTerm;

        crowdService.search(searchTerm, function(response) {
            if (searchTerm != $scope.lastSearchTerm) {
              console.log("search response arrived late. Discarding.");
              return;
            }

            try {
              $scope.filter = response.data.filter;
              applyFilter();
            } catch (err) {
              console.log(err);
            }
        });
      } else {
        console.log("resetting filter");
        $scope.filter = null;
        applyFilter();
      }
    }

    $scope.resetMap = function() {
      $scope.center = {
        lat: 56.5,
        lng: 20,
        zoom: 4
      };
    }

    $scope.setMapVisibility = function(mapVisibility) {
        $scope.mapVisibility = mapVisibility;
    }

    $scope.setSelectedMarker = function(markerId) {
        $scope.selectedMarkerId = markerId;

        // Center around newly selected
        var marker = $scope.markers[markerId];
        if (marker) {
          $scope.center.lat = marker.lat;
          $scope.center.lng = marker.lng;
        }
    }

    // --- Startup ---

    var mql = matchMedia("(orientation: landscape)");
    mql.addListener(updateOrientation);
    updateOrientation(mql);

    // initial calculation of the map size once the flex layout is done
    calculateMapSize();

    $rootScope.allPersons = crowdService.getAllPersons(function(response) {
        response.data.forEach(function(person) {
            addMarker("person", person);
        })
    })

    $rootScope.allInstitutions = crowdService.getAllInstitutions(function(response) {
        response.data.forEach(function(inst) {
            addMarker("institution", inst);
        })
    })

    // ------------------------------------------------------------------------------------------------- Private Methods

    // --- Marker ---

    function createMarkerIcon(type, selected) {
        // TODO: Take type into account
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
        var iconFile = "event-marker" +
            (selected ? "-selected" : "") +
            ($scope.hires ? "-1.3x" : "") + ".png";
        return "lib/leaflet/images/" + iconFile;
    }

    function applyFilter() {
      if ($scope.filter) {
        // clear the visible markers
        $scope.visibleMarkers = [];

        // Then move the filtered elements into the other visible set
        for (var index in $scope.filter) {
          var filteredId = $scope.filter[index];
          $scope.visibleMarkers[filteredId] = $scope.markers[filteredId];
        }
      } else {
        // no filter active, place everything into the visible set
        for (var markerId in $scope.markers) {
          $scope.visibleMarkers[markerId] = $scope.markers[markerId];
        }
      }

    }

    function addMarker(layer, objectOfMap) {
        if (objectOfMap.lat != undefined && objectOfMap.lng != undefined) {
            var newMarker = {
                lat: objectOfMap.lat,
                lng: objectOfMap.lng,
                layer: layer,
                icon: icons[layer].normal
            }
            $scope.markers[objectOfMap.id] = newMarker;

            if (!$scope.filter || $scope.filter.indexOf(objectOfMap.id) >= 0) {
              // marker is currently visible
              $scope.visibleMarkers[objectOfMap.id] = newMarker;
            }
        } else {
            console.log("WARNING: element \"" + objectOfMap.name + "\" (" + objectOfMap.id +
                ") can't appear on map -- its geo coordinate is unknown")
        }
    }

    // --- Cluster ---

    function createClusterIcon(cluster) {
        return new L.DivIcon({
            html: "<div><span>" + cluster.getChildCount() + "</span></div>",
            className: "marker-cluster",
            iconSize: new L.Point(clusterSize, clusterSize)
        });
    }

    // --- Map ---

    function calculateMapSize() {
        leafletData.getMap("map").then(function(map) {
            $timeout(function() {
                map.invalidateSize();
            }, 1200);
        });
    }

    // --- Viewport ---

    function updateOrientation(mql) {
        // Note: this media query listener is called directly from the browser, that is outside the angular context.
        // So, we must explicitly $apply the scope manipulation.
        $scope.$applyAsync(function() {
            $scope.landscape = mql.matches;
            $scope.portrait = !$scope.landscape
        })
    }

})
