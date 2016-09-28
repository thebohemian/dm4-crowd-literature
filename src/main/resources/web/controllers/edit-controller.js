persona=null;

angular.module("crowdedit")
.controller("EditPersonController", function($scope, $routeParams, $location, crowdService) {
    crowdService.getEditablePerson($routeParams.personId, function(response) {
      persona= response.data;
//      alert("response: " + JSON.stringify(response.data));
      $scope.person = response.data;
    });
})
