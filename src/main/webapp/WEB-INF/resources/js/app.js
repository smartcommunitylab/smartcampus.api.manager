'use strict';
var app = angular.module('apiManager', ['ngRoute', 'ngCookies', 'apiManager.directives', 
                                        'apiManager.services', 'hljs', 'ui.bootstrap',
                                        'googlechart']);

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
                    if (response.status === 401 || response.status === 404) {
                    	console.log('401 or 404');
                    	$location.path('/');
                        return $q.reject(response);
                    } else {
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
        when('/api/:apiId/:elem',{
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
        when('/create/status/:apiId',{
        	controller: 'addStatusCtrl',
        	templateUrl: 'partials/status/edit.html',
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
        when('/edit/:apiId/status/:statusName',{
        	controller: 'editStatusCtrl',
        	templateUrl: 'partials/status/edit.html',
        	access: access.public
        }).
        when('/show/:apiId/resource/:resourceId',{
        	controller: 'showResourceCtrl',
        	templateUrl: 'partials/resource/show.html',
        	access: access.public
        }).
        when('/create/:apiId/resource/:resourceId/add/policy',{
        	controller: 'addResourcePolicyCtrl',
        	templateUrl: 'partials/policy/edit.html',
        	access: access.public
        }).
        when('/edit/:apiId/resource/:resourceId/policy/:policyId',{
        	controller: 'editResourcePolicyCtrl',
        	templateUrl: 'partials/policy/edit.html',
        	access: access.public
        }).
        when('/apps',{
        	controller: 'appsCtrl',
        	templateUrl: 'partials/app/list.html',
        	access: access.public
        }).
        when('/app/:appId',{
        	controller: 'showAppCtrl',
        	templateUrl: 'partials/app/show.html',
        	access: access.public
        }).
        when('/app/:appId/:elem',{
        	controller: 'showAppCtrl',
        	templateUrl: 'partials/app/show.html',
        	access: access.public
        }).
        when('/create/app/',{
        	controller: 'addAppCtrl',
        	templateUrl: 'partials/app/edit.html',
        	access: access.public
        }).
        when('/edit/app/:appId',{
        	controller: 'editAppCtrl',
        	templateUrl: 'partials/app/edit.html',
        	access: access.public
        }).
        when('/dashboard',{
        	controller: 'startCtrl',
        	templateUrl: 'partials/analysis/start.html',
        	access: access.public
        }).
        when('/dashboard/enable',{
        	controller: 'tridCtrl',
        	templateUrl: 'partials/analysis/enable.html',
        	access: access.public
        }).
        when('/dashboard/login',{
        	controller: 'dashLoginCtrl',
        	templateUrl: 'partials/analysis/login.html',
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
