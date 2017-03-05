angular.module("crowd").directive("dm4EventList", function() {
    return {
        restrict: "E",
        scope: {
            events: "="
        },
        link: function (scope, element, attrs) {
          scope.title = attrs['title'] || "Events";
        },
        templateUrl: "views/event-list.html"
    }
})
