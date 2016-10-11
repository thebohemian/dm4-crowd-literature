angular.module("crowdedit", ["ngRoute"])
.config(function($routeProvider) {
    $routeProvider
        .when("/login",{templateUrl: "views/editlogin.html", controller: "EditMainController"})
        .when("/error-noperson",{templateUrl: "views/edit-error-noperson.html"})
        .when("/editperson/:personId",{templateUrl: "views/editperson.html",   controller: "EditPersonController"})
        .otherwise({redirectTo: "/login"})
})
.config(function($logProvider) {
    $logProvider.debugEnabled(false);
})
.config(function($httpProvider) {
    // console.log("Configuring request/response interceptors")
    $httpProvider.useLegacyPromiseExtensions(false);
})
