angular.module("crowd").directive("dm4EventList", function() {
    return {
        restrict: "E",
        scope: {
            events: "="
        },
        templateUrl: "/eu.crowd-literature/views/event-list.html"
    }
})
