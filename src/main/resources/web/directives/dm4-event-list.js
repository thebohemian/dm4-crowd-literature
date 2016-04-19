angular.module("crowd").directive("dm4EventList", function() {
    return {
        restrict: "E",
        scope: {
            events: "="
        },
        templateUrl: "views/event-list.html"
    }
})
