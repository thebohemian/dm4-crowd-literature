angular.module("crowdedit", ["ngRoute"])
.config(function($routeProvider) {
    $routeProvider
        .when("/login",{templateUrl: "views/editlogin.html",   controller: "EditPersonController"})
        .when("/editperson/:username",{templateUrl: "views/editperson.html",   controller: "EditPersonController"})
        .otherwise({redirectTo: "/login"})
})
.config(function($logProvider) {
    $logProvider.debugEnabled(false);
})
.config(function($httpProvider) {
    // console.log("Configuring request/response interceptors")
    $httpProvider.useLegacyPromiseExtensions(false);
})
