'use strict';
app.controller('homeCtrl', ['$http', '$scope', '$rootScope', '$location',
    function ($http, $scope, $rootScope, $location) {
        
    }
]);

app.controller('navCtrl', ['$scope', '$location', '$rootScope',
    function ($scope, $location, $rootScope) {
	
	}
]);

app.controller('breadCtrl', [
    function () {}
]);

app.controller('apisCtrl', ['$scope', '$location', 'Api',
    function($scope, $location, Api){
    	Api.list({
    		ownerId : '1'
    	}, function(data){
    		$scope.apisList = data.data;
    	});
    	
    	$scope.deleteApi = function (id) {
			Api.remove({
				apiId : id
			}, function(data){
				$location.path('/');
			});
		};
    }
]);

app.controller('showApiCtrl', ['$scope', '$routeParams', '$location', 'Api', 'Resource', 'Policy', 'App',
    function($scope, $routeParams, $location, Api, Resource, Policy, App){
		var apiid = $routeParams.apiId;
		
		Api.getApi({
			apiId : apiid
		}, function(data){
			$scope.api = data.data;
		});
		
		$scope.deleteResource = function (id) {
			Resource.remove({
				apiId : apiid,
				resourceId : id
			}, function(data){
				$location.path('/apis');
			});
		};
		
		$scope.deletePolicy = function (id) {
			Policy.remove({
				apiId : apiid,
				policyId : id
			}, function(data){
				$location.path('/apis');
			});
		};
		
		$scope.deleteApp = function (id) {
			App.remove({
				apiId : apiid,
				appId : id
			}, function(data){
				$location.path('/apis');
			});
		};
		
	}
]);

app.controller('addApiCtrl', ['$scope', '$location', 'Api',
    function($scope, $location, Api){
		$scope.title = 'New';

        $scope.submit = function () {
        	$scope.api.ownerId = '1';
            Api.create($scope.api,
                function (data) {
            		if(data.status == 200){
            			$location.path('apis');
            		}else{
            			$scope.errorMsg = data.message;
            		}
                });
        };

	}
]);

app.controller('addResourceCtrl', ['$scope', '$location', '$routeParams', 'Resource',
     function ($scope, $location, $routeParams, Resource) {
		$scope.title = 'New';
		var apiid = $routeParams.apiId;

		$scope.submit = function () {
			Resource.create({
				apiId: apiid
				},$scope.resource,
					function (data) {
						if(data.status == 200){
							$location.path('/api/'+apiid);
						}else{
							$scope.errorMsg = data.message;
						}
				});
		};
     }
]);

app.controller('addPolicyCtrl', ['$scope', '$location', '$routeParams', 'Policy',
     function ($scope, $location, $routeParams, Policy) {
		$scope.title = 'New';
		var apiid = $routeParams.apiId;
		
		$scope.submit = function () {
			var type = $scope.policy.type;
			if(type=='Spike Arrest'){
				Policy.createSpikeArrest({
					apiId: apiid
					},$scope.policy,
					function (data) {
						if(data.status == 200){
							$location.path('api/'+apiid);
						}else{
							$scope.errorMsg = data.message;
						}
				});
			}
			else if(type=='Quota'){
				Policy.createQuota({
					apiId: apiid
					},$scope.policy,
					function (data) {
						if(data.status == 200){
							$location.path('api/'+apiid);
						}else{
							$scope.errorMsg = data.message;
						}
				});
			}else{
				Policy.create({
					apiId: apiid
					},$scope.policy,
					function (data) {
						if(data.status == 200){
							$location.path('api/'+apiid);
						}else{
							$scope.errorMsg = data.message;
						}
				});
			}
		};
     }
]);

app.controller('addAppCtrl', ['$scope', '$location', '$routeParams', 'App',
     function ($scope, $location, $routeParams, App) {
		$scope.title = 'New';
		var apiid = $routeParams.apiId;
	
		$scope.submit = function () {
			App.create({
				apiId: apiid
				},$scope.app,
					function (data) {
						if(data.status == 200){
							$location.path('api/'+apiid);
						}else{
							$scope.errorMsg = data.message;
						}
				});
		};
     }
]);


app.controller('editApiCtrl', ['$scope', '$location', '$routeParams', 'Api',
     function($scope, $location, $routeParams, Api){
     	$scope.title = 'Edit';
     	var apiid = $routeParams.apiId;
     	
     	Api.getApi({
			apiId: apiid
		}, function(data){
			$scope.api = data.data;
		});

        $scope.submit = function () {
        	Api.update($scope.api,
        			function (data) {
        				if(data.status == 200){
                        	$location.path('apis');
                        }else{
                        	$scope.errorMsg = data.message;
                        }
                     });
        };

     }
]);

app.controller('editResourceCtrl', ['$scope', '$location', '$routeParams', 'Resource',
		function($scope, $location, $routeParams, Resource){
			$scope.title = 'Edit';
			var apiid = $routeParams.apiId;
			var rid = $routeParams.resourceId;
			
			Resource.getResource({
				apiId: apiid,
				resourceId: rid
			}, function(data){
				$scope.resource = data.data;
			});
			
			$scope.submit = function () {
	            Resource.update({
	            	apiId: apiid
	            }, $scope.resource,
	                function (data) {
	            		if(data.status == 200){
	            			$location.path('api/'+apiid);
	            		}else{
	            			$scope.errorMsg = data.message;
	            		}
	                });
	        };
		}
]);

app.controller('editPolicyCtrl', ['$scope', '$location', '$routeParams', 'Policy',
        function($scope, $location, $routeParams, Policy){
			$scope.title = 'Edit';
			var apiid = $routeParams.apiId;
			var pid = $routeParams.policyId;
			
			Policy.getPolicy({
				apiId: apiid,
				policyId: pid
			}, function(data){
				$scope.policy = data.data;
			});
			
			$scope.submit = function () {
				var type = $scope.policy.type;
				if(type=='Spike Arrest'){
					Policy.updateSpikeArrest({
						apiId: apiid
						},$scope.policy,
						function (data) {
							if(data.status == 200){
								$location.path('api/'+apiid);
							}else{
								$scope.errorMsg = data.message;
							}
					});
				}
				else if(type=='Quota'){
					Policy.updateQuota({
						apiId: apiid
						},$scope.policy,
						function (data) {
							if(data.status == 200){
								$location.path('api/'+apiid);
							}else{
								$scope.errorMsg = data.message;
							}
					});
				}else{
					Policy.update({
						apiId: apiid
						}, $scope.policy,
						function (data) {
							if(data.status == 200){
								$location.path('api/'+apiid);
							}else{
								$scope.errorMsg = data.message;
							}
						});
				}
				
	        };
        }
]);

app.controller('editAppCtrl', ['$scope', '$location', '$routeParams', 'App',
        function($scope, $location, $routeParams, App){
			$scope.title = 'Edit';
			var apiid = $routeParams.apiId;
			var aid = $routeParams.appId;
			
			App.getApp({
				apiId: apiid,
				appId: aid
			}, function(data){
				$scope.app = data.data;
			});
			
			$scope.submit = function () {
	            App.update({
	            	apiId: apiid
	            }, $scope.app,
	                function (data) {
	            		if(data.status == 200){
	            			$location.path('api/'+apiid);
	            		}else{
	            			$scope.errorMsg = data.message;
	            		}
	                });
	        };
        }
]);
