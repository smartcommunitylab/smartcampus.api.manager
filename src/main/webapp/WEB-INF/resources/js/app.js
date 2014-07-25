'use strict';
var app = angular.module('apiManager', ['ngRoute', 'ngCookies', 'apiManager.directives', 'apiManager.services', 'hljs', 'ui.bootstrap']);

app.filter('fromNow', function () {
    return function (dateString) {
        return moment(dateString).fromNow();
    };
});

app.config(['$routeProvider', '$locationProvider', '$httpProvider', 'hljsServiceProvider',
    function ($routeProvider, $locationProvider, $httpProvider, hljsServiceProvider) {
        var access = routingConfig.accessLevels;
        $locationProvider.html5Mode(true);

        $httpProvider.interceptors.push(function ($q, $location) {
        	return {
                'responseError': function (response) {
                    if (response.status === 401) {
                    	$location.path('/');
                        return $q.reject(response);
                    } else {
                    	$location.path('/');
                    	return $q.reject(response);
                    }
                }
            };
        });

        $routeProvider.
        when('/', {
            controller: 'homeCtrl',
            templateUrl: 'partials/home.html',
            access: access.public
        }).
        when('/apis',{
        	controller: 'apisCtrl',
        	templateUrl: 'partials/apis/list.html',
        	access: access.public
        }).
        when('/api/:apiId',{
        	controller: 'showApiCtrl',
        	templateUrl: 'partials/apis/show.html',
        	access: access.public
        }).
        when('/create/api',{
        	controller: 'addApiCtrl',
        	templateUrl: 'partials/apis/edit.html',
        	access: access.public
        }).
        when('/create/resource/:apiId',{
        	controller: 'addResourceCtrl',
        	templateUrl: 'partials/resource/edit.html',
        	access: access.public
        }).
        when('/create/policy/:apiId',{
        	controller: 'addPolicyCtrl',
        	templateUrl: 'partials/policy/edit.html',
        	access: access.public
        }).
        when('/create/app/:apiId',{
        	controller: 'addAppCtrl',
        	templateUrl: 'partials/app/edit.html',
        	access: access.public
        }).
        when('/edit/api/:apiId',{
        	controller: 'editApiCtrl',
        	templateUrl: 'partials/apis/edit.html',
        	access: access.public
        }).
        when('/edit/:apiId/resource/:resourceId',{
        	controller: 'editResourceCtrl',
        	templateUrl: 'partials/resource/edit.html',
        	access: access.public
        }).
        when('/edit/:apiId/policy/:policyId',{
        	controller: 'editPolicyCtrl',
        	templateUrl: 'partials/policy/edit.html',
        	access: access.public
        }).
        when('/edit/:apiId/app/:appId',{
        	controller: 'editAppCtrl',
        	templateUrl: 'partials/app/edit.html',
        	access: access.public
        }).
        otherwise({
            redirectTo: '/'
        });

    }
]).run(function ($rootScope, $location, $routeParams, $cookieStore) {
    var history = [];
    var value = $cookieStore.get('value');
    if (value === false) {
        $cookieStore.remove('user');
    }

    $rootScope.$on('$routeChangeStart', function (event, next) {
        history.push($location.$$path);

        $rootScope.loc = $location.path().split('/');
        $rootScope.loc.splice(0, 1);

    });
    $rootScope.back = function () {
        var prevUrl = history.length > 1 ? history.splice(-2)[0] : '/';
        $location.path(prevUrl);
    };
});
