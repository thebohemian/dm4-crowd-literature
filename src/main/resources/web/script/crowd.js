angular.module("crowd", ["ngRoute", "ngSanitize", "leaflet-directive"])
.config(function($routeProvider, $logProvider, $httpProvider) {
    $routeProvider
        .when("/welcome",          {templateUrl: "views/welcome.html", controller: "welcomeController"})
        .when("/event/:eventId",   {templateUrl: "views/event.html",   controller: "eventController", resolve: {
            eventsModel: function($rootScope) {
                return $rootScope.eventsModel;
            } 
        }})
        .when("/person/:personId",    {templateUrl: "views/person.html",       controller: "personController"})
        .when("/institution/:instId", {templateUrl: "views/institution.html",  controller: "institutionController"})
        .otherwise({redirectTo: "/welcome"})
    $logProvider.debugEnabled(false);
    $httpProvider.useLegacyPromiseExtensions(false);
})
