angular.module("crowd", ["ngRoute", "leaflet-directive"])
.config(function($routeProvider, $logProvider, $httpProvider) {
    $routeProvider
        .when("/welcome",             {templateUrl: "views/welcome.html",      controller: "WelcomeController"})
        .when("/person/:personId",    {templateUrl: "views/person.html",       controller: "PersonController"})
        .when("/institution/:instId", {templateUrl: "views/institution.html",  controller: "InstitutionController"})
        .when("/work/:workId",        {templateUrl: "views/work.html",         controller: "WorkController"})
        .when("/event_series/:eventSeriesId",
                                      {templateUrl: "views/event-series.html", controller: "EventSeriesController"})
        .when("/event/:eventId",      {templateUrl: "views/event.html",        controller: "EventController", resolve: {
            eventsModel: function($rootScope) {
                return $rootScope.eventsModel;
            }
        }})
        .otherwise({redirectTo: "/welcome"})
    $logProvider.debugEnabled(false);
    $httpProvider.useLegacyPromiseExtensions(false);
})
