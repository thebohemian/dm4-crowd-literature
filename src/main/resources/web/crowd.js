angular.module("crowd", ["ngRoute", "ngSanitize", "leaflet-directive"])
.config(function($routeProvider, $logProvider, $httpProvider) {
    $routeProvider
        .when("/welcome",             {templateUrl: "views/welcome.html",      controller: "welcomeController"})
        .when("/person/:personId",    {templateUrl: "views/person.html",       controller: "personController"})
        .when("/institution/:instId", {templateUrl: "views/institution.html",  controller: "institutionController"})
        .when("/work/:workId",        {templateUrl: "views/work.html",         controller: "workController"})
        .when("/event_series/:eventSeriesId",
                                      {templateUrl: "views/event-series.html", controller: "eventSeriesController"})
        .when("/event/:eventId",      {templateUrl: "views/event.html",        controller: "eventController", resolve: {
            eventsModel: function($rootScope) {
                return $rootScope.eventsModel;
            }
        }})
        .otherwise({redirectTo: "/welcome"})
    $logProvider.debugEnabled(false);
    $httpProvider.useLegacyPromiseExtensions(false);
})
