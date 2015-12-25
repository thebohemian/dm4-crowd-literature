angular.module("crowd").controller("personController", function($scope, $routeParams, crowdService) {
    var personId = $routeParams.personId;
    crowdService.getPerson(personId, function(response) {
        $scope.person = response.data;
    })
    crowdService.getInstitutions(personId, function(response) {
        $scope.institutions = response.data.items;
    })
    crowdService.getWorks(personId, function(response) {
        $scope.works = response.data;
    })
    crowdService.getEventsOfParticipant(personId, function(response) {
        $scope.events = response.data.items;
    })
})
