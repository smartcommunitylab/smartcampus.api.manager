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

app.controller('appsCtrl', ['$scope', '$location', '$route', 'App',
    function($scope, $location, $route, App){
		App.list({
        }, function(data){
            $scope.appsList = data.data;
        });
                       	
		$scope.deleteApp = function (id) {
			App.remove({
				appId : id
			}, function(data){
				$route.reload();
			});
		};
     }
]);

app.controller('appApiDataCtrl', ['$scope', '$location', '$route', 'App', 'Api',
    function($scope, $location, $route, App, Api){
		//TODO
		var list=[];
	
		Api.list({
			ownerId : '1'
		}, function(data){
			$scope.apisList = data.data;
		});
	
		/*$scope.isApiDataChecked = function(apiId){
			console.log('isApiDataChecked');
			console.log('list');
			console.log(list);
			var index = -1;
			for(var i in $scope.app.apis){
				if($scope.app.apis[i].apiId === apiId){
					index = i;
				}
			}
			
			if(index === -1){
				return false;
			}else{
				$scope.addApiData($scope.app.apis[i].apiId,$scope.app.apis[i].apiStatus );
				return true;
			}
		};*/
		
		$scope.addApiData = function(apiId, apiStatus){
			console.log('addApiData');
			console.log('param status: '+apiStatus);
			console.log('param api id: '+apiId);
			var obj = {apiId: apiId , apiStatus: apiStatus};
		
			var index = -1;
			for(var i=0;i<list.length;i++){
				if(list[i].apiId === apiId){
					index = i;
				}
			}
		
			console.log('index: '+index);
			if(index > -1){
				//if status is different then push
				if(list[index].apiStatus !== apiStatus){
					list.push(obj);
				}
				list.splice(index,1);
			}
			else{
				if(!!apiStatus){
					list.push(obj);
				}else{
					console.log('Chose a status!');
					//$scope.errorMsg = "Error chose a status";
				}
			
			}
		};

		$scope.submit = function () {
			$scope.app.apis = list;
			App.updateApiData({
				
				},$scope.app,
					function (data) {
						if(data.status == 200){
							$location.path('apps');
						}else{
							$scope.errorMsg = data.message;
						}
				});
		};
		
	}
]);

app.controller('showApiCtrl', ['$scope', '$route' ,'$routeParams', '$location', 'Api', 'Resource', 
                               'Policy',
    function($scope, $route, $routeParams, $location, Api, Resource, Policy){
		var apiid = $routeParams.apiId;
		var elem = $routeParams.elem;
		
		if(elem){
			if(elem==='resource'){
				$scope.isTabResourceActive = true;
				$scope.isTabPolicyActive = false;
				$scope.isTabStatusActive = false;
				
			}else if(elem==='policy'){
				$scope.isTabResourceActive = false;
				$scope.isTabPolicyActive = true;
				$scope.isTabStatusActive = false;
				
			}else if(elem==='status'){
				$scope.isTabResourceActive = false;
				$scope.isTabPolicyActive = false;
				$scope.isTabStatusActive = true;
				
			}else{
				console.log('Error. Elem can be resource, policy or status.');
				$scope.isTabResourceActive = true;
				$scope.isTabPolicyActive = false;
				$scope.isTabStatusActive = false;
			}
		}else{
			$scope.isTabResourceActive = true;
			$scope.isTabPolicyActive = false;
			$scope.isTabStatusActive = false;
		}
		
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
		
		$scope.deleteStatus = function(name){
			Api.removeStatus({
				apiId : apiid,
				statusName : name
			}, status,
					function(data){
				$route.reload();
			});
		};
		
	}
]);

app.controller('showAppCtrl', ['$scope', '$location', '$routeParams', 'App', 'Api',
    function($scope, $location, $routeParams, App, Api){
		var appid = $routeParams.appId;
		
		App.getApp({
			appId : appid
		}, function(data){
			$scope.app = data.data;
		});
		
		$scope.deleteApi = function (id) {
			App.removeApiData({
				appId : appid,
				apiId : id
				}, function(data){
					$location.path('apps');
					});
			};
			
		$scope.remove = function () {
			App.remove({
				appId : appid
			}, function(data){
				$location.path('apps');
			});
		};
			
		//for change permission, see appApiDataCtrl
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

app.controller('addPolicyCtrl', ['$scope', '$location', '$routeParams', 'Policy', 'Api',
     function ($scope, $location, $routeParams, Policy, Api) {
		$scope.title = 'New';
		var apiid = $routeParams.apiId;
		var qstatus = [];
		
		Api.getStatusList({
			apiId : apiid
		}, function(data){
			$scope.apiStatus = data.data;
		});
		
		$scope.addStatus = function(name,quota){
			console.log('param name: '+name);
			console.log('param quota: '+quota);
			var obj = {name: name , quota: quota};
			
			var index = -1;
			for(var i=0;i<qstatus.length;i++){
				if(qstatus[i].name === name){
					index = i;
				}
			}
			
			console.log('index: '+index);
			if(index > -1){
				if(qstatus[index].quota!==quota){
					qstatus.splice(index,1);
					qstatus.push(obj);
				}else console.log('Already in db');
			}
			else{
				qstatus.push(obj);
			}
			console.log(qstatus);
		};
		
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
				//check qstatus list - add only data where quota is different from 0
				console.log('Check qstatus');
				console.log(qstatus);
				$scope.policy.qstatus = qstatus;
				
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

app.controller('addAppCtrl', ['$scope', '$location', '$routeParams', 'App', 'Api',
     function ($scope, $location, $routeParams, App, Api) {
		$scope.title = 'New';
		var list=[];
		
		Api.list({
    		ownerId : '1'
    	}, function(data){
    		$scope.apisList = data.data;
    	});
		
		$scope.addApiData = function(apiId, apiStatus){
			console.log('scope.a: '+$scope.a);
			console.log('param status: '+apiStatus);
			console.log('param api id: '+apiId);
			var obj = {apiId: apiId , apiStatus: apiStatus};
			
			var index = -1;
			for(var i=0;i<list.length;i++){
				if(list[i].apiId === apiId){
					index = i;
				}
			}
			
			console.log('index: '+index);
			if(index > -1){
				list.splice(index,1);
			}
			else{
				if(!!apiStatus){
					list.push(obj);
				}else{
					console.log('Chose a status!');
					//$scope.errorMsg = "Error chose a status";
				}
				
			}
			console.log(list);
		};
	
		$scope.submit = function () {
			$scope.app.apis = list;
			App.create({
				
				},$scope.app,
					function (data) {
						if(data.status == 200){
							$location.path('apps');
						}else{
							$scope.errorMsg = data.message;
						}
				});
		};
     }
]);

app.controller('addStatusCtrl', ['$scope', '$location', '$routeParams', 'Api',
    function ($scope, $location, $routeParams, Api) {
		$scope.title = 'New';
		var apiid = $routeParams.apiId;
		
		$scope.submit = function () {
			Api.createStatus({
				apiId: apiid
				},$scope.status,
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
        					$scope.api = data.data;
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
	            			$scope.resource = data.data;
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
			
			$scope.goBack = function(){
				$location.path('/api/'+apiid);
			};
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
								$scope.policy=data.data;
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
								$scope.policy=data.data;
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
			
			$scope.goBack = function(){
				$location.path('/api/'+apiid+'/policy');
			};
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
							$scope.app=data.data;
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

app.controller('editStatusCtrl', ['$scope', '$location', '$routeParams', '$timeout', 'Api',
    function ($scope, $location, $routeParams, $timeout, Api) {
		$scope.title = 'Edit';
		var apiid = $routeParams.apiId;
		var statusname = $routeParams.statusName;
		
		Api.getStatus({
			apiId: apiid,
			statusName: statusname
		}, function(data){
			$scope.status = data.data;
		});
		
		$scope.submit = function () {
			Api.updateStatus({
				apiId: apiid
			},$scope.status,
			function (data) {
				if(data.status == 200){
					$location.path('api/'+apiid);
				}else{
					$scope.errorMsg = data.message;
				}
				
			});

		};
		
		$scope.remove = function () {
			Api.removeStatus({
				apiId : apiid,
				statusName : statusname
			}, status,
			function(data){
				$location.path('api/'+apiid);
			});
		};
		
		$timeout(function () {
			$scope.errorMsg = null;
			$scope.msg = null;
	    }, 7000);
		
		$scope.goBack = function(){
			$location.path('/api/'+apiid+'/status');
		};
		
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
				$scope.title = 'Edit Resource';			
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
									$scope.policy = data.data;
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
									$scope.policy = data.data;
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
            	$scope.title = 'New Resource';
            	
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

