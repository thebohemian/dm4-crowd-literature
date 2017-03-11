angular.module("crowd")
.controller("PersonController", function($scope, $routeParams, crowdService) {
    crowdService.getPerson($routeParams.personId, function(response) {
        var person = response.data;

        // If email is available unescape it on the fly using the browser.
        if (person.email) {
          person.email = angular.element('<textarea />').html(person.email).text();
        }

        $scope.person = person;

        $scope.setSelectedMarker($routeParams.personId);
    })
})
.controller("InstitutionController", function($scope, $routeParams, crowdService) {
    crowdService.getInstitution($routeParams.instId, function(response) {
        $scope.institution = response.data;
        $scope.setSelectedMarker($routeParams.instId);
    })
})
.controller("WorkController", function($scope, $routeParams, crowdService) {
    crowdService.getWork($routeParams.workId, function(response) {
        $scope.work = response.data;
    })
})
.controller("EventController", function($scope, $routeParams, crowdService) {
    var eventId = $routeParams.eventId;

    crowdService.getEvent(eventId, function(response) {
        $scope.event = response.data;
    });
})
