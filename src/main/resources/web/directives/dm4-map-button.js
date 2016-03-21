angular.module("crowd").directive("dm4MapButton", function() {
    return {
        restrict: "E",
        transclude: true,
        template: '<button ng-click="setMapVisibility(true)" ng-if="portrait" ng-transclude></button>'
    }
})
