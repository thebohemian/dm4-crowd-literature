angular.module("crowd").directive("dm4Address", function() {
    return {
        restrict: "A",
        scope: {
            address: "=dm4Address"
        },
        templateUrl: "views/dm4-address.html"
    }
})
.directive("address", function() {
    return {
        restrict: "A",
        scope: {
            address: "=address"
        },
        templateUrl: "views/address.html"
    }
})
