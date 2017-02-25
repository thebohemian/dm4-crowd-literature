angular.module("crowdedit", ["ngRoute"])
.config(function($routeProvider) {
    var start = {
      controller: function() {
          window.location.replace('/filerepo/dm4-crowd-meet/src/main/resources/web/index.html');
      },
      template : "<div></div>"
    };

    $routeProvider
        .when("/start", start)
        .when("/error-noperson",{templateUrl: "views/edit-error-noperson.html"})
        .when("/editperson",{templateUrl: "views/editperson.html",   controller: "EditPersonController"})
        .otherwise({redirectTo: "/editperson"})
})
.config(function($logProvider) {
    $logProvider.debugEnabled(false);
})
.config(function($httpProvider) {
    // console.log("Configuring request/response interceptors")
    $httpProvider.useLegacyPromiseExtensions(false);
})
