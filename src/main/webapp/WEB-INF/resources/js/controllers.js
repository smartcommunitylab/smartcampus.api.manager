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

app.controller('apisCtrl', ['$scope', '$location', '$route', 'Api',
    function($scope, $location, $route, Api){
    	Api.list({
    		ownerId : '1'
    	}, function(data){
    		$scope.apisList = data.data;
    	});
    	
    	$scope.deleteApi = function (id) {
			Api.remove({
				apiId : id
			}, function(data){
				$route.reload();
			});
		};
    }
]);

app.controller('showApiCtrl', ['$scope', '$route' ,'$routeParams', '$location', 'Api', 'Resource', 
                               'Policy', 'App',
    function($scope, $route, $routeParams, $location, Api, Resource, Policy, App){
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
				$route.reload();
			});
		};
		
		$scope.deletePolicy = function (id) {
			Policy.remove({
				apiId : apiid,
				policyId : id
			}, function(data){
				$route.reload();
			});
		};
		
		$scope.deleteApp = function (id) {
			App.remove({
				apiId : apiid,
				appId : id
			}, function(data){
				$route.reload();
			});
		};
		
		$scope.setAppId = function(id){
			$scope.appId = id;
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
							$location.path('api/'+apiid);
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


app.controller('editApiCtrl', ['$scope', '$location', '$routeParams', '$timeout', 'Api',
     function($scope, $location, $routeParams, $timeout, Api){
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
                        	//$location.path('apis');
        					$scope.msg = data.message;
                        }else{
                        	$scope.errorMsg = data.message;
                        }
                     });
        };
        
        $timeout(function () {
			$scope.errorMsg = null;
			$scope.msg = null;
	    }, 10000);

     }
]);

app.controller('editResourceCtrl', ['$scope', '$location', '$routeParams', '$timeout', 'Resource',
		function($scope, $location, $routeParams, $timeout, Resource){
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
				$scope.resource.policy = null;
	            Resource.update({
	            	apiId: apiid
	            }, $scope.resource,
	                function (data) {
	            		if(data.status == 200){
	            			//$location.path('api/'+apiid);
	            			$scope.msg = data.message;
	            			$scope.errorMsg = null;
	            		}else{
	            			$scope.errorMsg = data.message;
	            			$scope.msg = null;
	            		}
	                });
	        };
	        
	        $scope.remove = function () {
				Resource.remove({
					apiId : apiid,
					resourceId : rid
				}, function(data){
					$location.path('api/'+apiid);
				});
			};
			
			$timeout(function () {
				$scope.errorMsg = null;
				$scope.msg = null;
		    }, 10000);
		}
]);

app.controller('editPolicyCtrl', ['$scope', '$location', '$routeParams', '$timeout', 'Policy',
        function($scope, $location, $routeParams, $timeout, Policy){
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
		            			//$location.path('api/'+apiid);
		            			$scope.msg = data.message;
		            			$scope.errorMsg = null;
		            		}else{
		            			$scope.errorMsg = data.message;
		            			$scope.msg = null;
		            		}
					});
				}
				else if(type=='Quota'){
					Policy.updateQuota({
						apiId: apiid
						},$scope.policy,
						function (data) {
							if(data.status == 200){
		            			//$location.path('api/'+apiid);
		            			$scope.msg = data.message;
		            			$scope.errorMsg = null;
		            		}else{
		            			$scope.errorMsg = data.message;
		            			$scope.msg = null;
		            		}
					});
				}
				
	        };
			
			$scope.remove = function () {
				Policy.remove({
					apiId : apiid,
					policyId : pid
				}, function(data){
					$location.path('api/'+apiid);
				});
			};
			
			$timeout(function () {
				$scope.errorMsg = null;
				$scope.msg = null;
		    }, 10000);
        }
]);

app.controller('editAppCtrl', ['$scope', '$location', '$routeParams', '$timeout', 'App',
        function($scope, $location, $routeParams, $timeout, App){
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
 	                function(data) {
						if (data.status == 200) {
							// $location.path('api/'+apiid);
							$scope.msg = data.message;
							$scope.errorMsg = null;
						} else {
							$scope.errorMsg = data.message;
							$scope.msg = null;
						}
				});
	        };
	        
			$scope.remove = function () {
				App.remove({
					apiId : apiid,
					appId : aid
				}, function(data){
					$location.path('api/'+apiid);
				});
			};
			
			$timeout(function () {
				$scope.errorMsg = null;
				$scope.msg = null;
		    }, 7000);
        }
]);

app.controller('showResourceCtrl', ['$scope', '$location', '$route', '$routeParams', 'Resource',
          function($scope, $location, $route, $routeParams, Resource){
				var apiid = $routeParams.apiId;
				var rd = $routeParams.resourceId;
				$scope.apiid=apiid;
				
				Resource.getResource({
					apiId: apiid,
					resourceId: rd
					},function(data){
						$scope.resource = data.data;
				});
				
				$scope.deletePolicy = function (rId, pId) {
					Resource.removePolicy({
						apiId : apiid,
						resourceId: rId,
						policyId : pId
					}, function(data){
						$route.reload();
					});
				};
			}
]);

app.controller('editResourcePolicyCtrl', ['$scope', '$location', '$routeParams', '$timeout', 
                                          'Resource',
            function($scope, $location, $routeParams, $timeout, Resource){
				$scope.title = 'Edit';			
				var apiid = $routeParams.apiId;
				var rid = $routeParams.resourceId;
				var pid = $routeParams.policyId;
				
				Resource.getPolicyResource({
					apiId : apiid,
					resourceId: rid,
					policyId : pid
				}, function(data){
					$scope.policy = data.data;
				});
		        
		        $scope.submit = function () {
		        	var type = $scope.policy.type;
					if(type=='Spike Arrest'){
						Resource.updateSpikeArrestResource({
							apiId: apiid,
			            	resourceId: rid
							},$scope.policy,
							function (data) {
								if(data.status == 200){
									//$location.path('/show/'+apiid+'/resource/'+rid);
									$scope.msg = data.message;
									$scope.errorMsg = null;
								}else{
									$scope.errorMsg = data.message;
									$scope.msg = null;
								}
						});
					}
					else if(type=='Quota'){
						Resource.updateQuotaResource({
							apiId: apiid,
			            	resourceId: rid
							},$scope.policy,
							function (data) {
								if(data.status == 200){
									//$location.path('/show/'+apiid+'/resource/'+rid);
									$scope.msg = data.message;
									$scope.errorMsg = null;
								}else{
									$scope.errorMsg = data.message;
									$scope.msg = null;
								}
						});
					}
		        };
		        
		        $scope.remove = function () {
					Resource.removePolicy({
						apiId : apiid,
						resourceId: rid,
						policyId : pid
					}, function(data){
						$location.path('/show/'+apiid+'/resource/'+rid);
					});
				};
				
				$timeout(function () {
					$scope.errorMsg = null;
					$scope.msg = null;
			    }, 10000);
			}
]);

app.controller('addResourcePolicyCtrl', ['$scope', '$location', '$routeParams', 'Resource',
            function($scope, $location, $routeParams, Resource){
            	$scope.title = 'New';
            	
            	var apiid = $routeParams.apiId;
            	var rid = $routeParams.resourceId;
        		
        		$scope.submit = function () {
        			var type = $scope.policy.type;
        			if(type=='Spike Arrest'){
        				Resource.createSpikeArrestResource({
        					apiId: apiid,
        					resourceId: rid
        					},$scope.policy,
        					function (data) {
        						if(data.status == 200){
        							$location.path('/show/'+apiid+'/resource/'+rid);
        						}else{
        							$scope.errorMsg = data.message;
        						}
        				});
        			}
        			else if(type=='Quota'){
        				Resource.createQuotaResource({
        					apiId: apiid,
        					resourceId: rid
        					},$scope.policy,
        					function (data) {
        						if(data.status == 200){
        							$location.path('/show/'+apiid+'/resource/'+rid);
        						}else{
        							$scope.errorMsg = data.message;
        						}
        				});
        			}
        		};
			}
]);

