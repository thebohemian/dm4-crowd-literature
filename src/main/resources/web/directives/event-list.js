angular.module("crowd").directive("eventList", function() {
    return {
        restrict: "E",
        scope: {
            events: "="
        },
        templateUrl: "views/event-list.html"
    }
})
