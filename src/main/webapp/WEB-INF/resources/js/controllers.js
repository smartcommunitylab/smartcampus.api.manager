'use strict';
app.controller('homeCtrl', ['$http', '$scope', '$rootScope', '$location', 'Auth',
    function ($http, $scope, $rootScope, $location, Auth) {
		
    }
]);

app.controller('navCtrl', ['$scope', '$location', '$rootScope',
    function ($scope, $location, $rootScope) {
	
	}
]);

app.controller('breadCtrl', [
    function () {}
]);

app.controller('apisCtrl', ['$scope', '$location', '$route', 'Api', 'Auth',
    function($scope, $location, $route, Api, Auth){
    	
		Api.list({
    		//ownerId : '1'
    	}, function(data){
    		if(data.status===403){
    			Auth.redirectLogin({},
    					function(data){
    						console.log(data);
    			});
    		}else{
    			$scope.apisList = data.data;
    		}
    		
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

app.controller('publishApiCtrl', ['$scope', '$location', '$route', '$routeParams', 'Api', 'Service',
    function($scope, $location, $route, $routeParams, Api, Service){
		var apiid = $routeParams.apiId;
		
		$scope.protocols = ['OAuth2', 'OpenID', 'Public'];
        $scope.formats = ['json', 'xml', 'yaml', 'txt'];
        $scope.accessInformation = {
            authentication: {
                accessProtocol: null,
                accessAttributes: {
                    client_id: null,
                    response_type: null,
                    authorizationUrl: null,
                    grant_type: null
                }
            }
        };
        
        $scope.service = {
				name : null,
				organizationId: null,
				description: null,
				tags: null,
				category: null,
				license: null,
				version: null,
				documentation: null,
				expiration: null,
				
				accessInformation:{ 
					
					authentication: {
		                accessProtocol: null,
		                accessAttributes: {
		                    client_id: null,
		                    response_type: null,
		                    authorizationUrl: null,
		                    grant_type: null
		                }
					},
					
					accessPolicies: null,
					testingEndpoint: null,
					productionEndpoint: null,
					formats: null,
					protocols: null
				},
				
				implementation:{
					executionEnvironment: null,
					hosting: null,
					sourceCode: null
				}
		};
		
		Api.getApi({
			apiId : apiid
		}, function(data){
			var api = data.data;
			var domain = window.location.protocol+"//:"+window.location.host+"/"+window.location.pathname.split('/')[1];

			$scope.service.name = api.name;
			$scope.service.accessInformation.testingEndpoint = domain+api.basePath;
			$scope.service.accessInformation.productionEndpoint= domain+api.basePath;
			
			//Policy
			if(!api.policy){
				$scope.service.accessInformation.accessPolicies = "No restrictions";
			}else{
				$scope.service.accessInformation.accessPolicies = "";
				
				for(var i=0;i<api.policy.length;i++){
					var type = api.policy[i].type;
					
					if(type=='Spike Arrest'){
						$scope.service.accessInformation.accessPolicies=$scope.service.accessInformation.accessPolicies.concat("Spike Arrest, ");
					}
					else if(type=='Quota'){
						$scope.service.accessInformation.accessPolicies=$scope.service.accessInformation.accessPolicies.concat("Quota, ");
					}
					else if(type=='IP Access Control'){
						$scope.service.accessInformation.accessPolicies=$scope.service.accessInformation.accessPolicies.concat("IP Access Control, ");
					}
					else if(type=='Verify App Key'){
						$scope.service.accessInformation.accessPolicies=$scope.service.accessInformation.accessPolicies.concat("Verify App Key, ");
					}
					else if(type=='OAuth'){
						$scope.service.accessInformation.accessPolicies=$scope.service.accessInformation.accessPolicies.concat("OAuth, ");
					}
					else if(type=='SAML'){
						$scope.service.accessInformation.accessPolicies=$scope.service.accessInformation.accessPolicies.concat("SAML, ");
					}
				}
			}
			
			
		});
		
		Service.categories({}, function (data) {
            $scope.categories = data.data;
        });

        Service.organizations({}, function (data) {
            $scope.orgs = data.data;
        });

        $scope.submit = function () {
            if ($scope.service.expiration) {
                $scope.service.expiration = new Date($scope.service.expiration).getTime();
            } else {
                $scope.service.expiration = null;
            }
            if (typeof $scope.service.tags == 'string') {
                $scope.service.tags = $scope.service.tags.split(',');
            }
            Service.publish($scope.service,
                function () {
                    $location.path('api/'+apiid);
                },
                function (res) {
                    $scope.errorMsg = res.data.error;
                });
        };
        $scope.keep = function () {
            $scope.service.accessInformation.authentication = $scope.accessInformation.authentication;
        };
		
	}
]);

app.controller('appsCtrl', ['$scope', '$location', '$route', 'App', 'Auth',
    function($scope, $location, $route, App, Auth){
		
		App.list({
        }, function(data){
        	if(data.status===403){
    			Auth.redirectLogin({},
    					function(data){
    						console.log(data);
    			});
    		}else{
    			$scope.appsList = data.data;
    		}
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

app.controller('showApiCtrl', ['$scope', '$route' ,'$routeParams', '$location', 'Api', 'Resource', 
                               'Policy',
    function($scope, $route, $routeParams, $location, Api, Resource, Policy){
		var apiid = $routeParams.apiId;
		
		//tab selection
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

app.controller('showAppCtrl', ['$scope', '$location', '$routeParams', '$route', 'App', 'Api',
    function($scope, $location, $routeParams, $route, App, Api){
		var appid = $routeParams.appId;
		
		//tab selection
		var elem = $routeParams.elem;
		
		if(elem){
			if(elem==='permission'){
				$scope.isTabApiActive = false;
				$scope.isTabPermissionActive = true;
				$scope.isTabKeyActive = false;
				
			}else if(elem==='key'){
				$scope.isTabApiActive = false;
				$scope.isTabPermissionActive = false;
				$scope.isTabKeyActive = true;
				
			}else{
				$scope.isTabApiActive = true;
				$scope.isTabPermissionActive = false;
				$scope.isTabKeyActive = false;
			}
		}else{
			$scope.isTabApiActive = true;
			$scope.isTabPermissionActive = false;
			$scope.isTabKeyActive = false;
		}
		
		//create a json object with api id, name and status list
		var listApi = function(id,status){
			
			if(!$scope.list){
				$scope.list = [];
			}
			
			Api.getApiName({
				apiId : id
			}, function(data){
				
				var obj = {apiId: id, 
						apiName: data.data , 
						apiStatus: status,
						apiStatusList: []
						};

				$scope.list.push(obj);
				
			});
			
		};
		
		//retrieve list of api in app
		App.getApp({
			appId : appid
		}, function(data){
			$scope.app = data.data;
			
			//get name of api
			for(var i=0;i<$scope.app.apis.length;i++){
				
				var id = $scope.app.apis[i].apiId;
				var status = $scope.app.apis[i].apiStatus;
				
				listApi(id,status);
				
			}
			
		});
		
		//delete api list of app
		$scope.deleteApi = function (id) {
			App.removeApiData({
				appId : appid,
				apiId : id
				}, function(data){
					$route.reload();
					//$location.path('/app/'+appId);
					});
			};
		
		//delete app
		$scope.remove = function () {
			App.remove({
				appId : appid
			}, function(data){
				$location.path('/apps');
			});
		};
			
		//Change permission tab
  		var list = [];
  	
  		Api.list({
  		}, function(data){
  			
  			if(!$scope.permissions){
  				$scope.permissions=[];
  			}
  			
  			for(var i=0;i<data.data.length;i++){
  				
  				var obj = {apiId: data.data[i].id, 
  						apiName: data.data[i].name , 
  						apiStatus: data.data[i].status};
  				
  				$scope.permissions.push(obj);
  			}
  			
  		});
  		
  		$scope.retrieveStatusPermission = function(name){
  			for(var i=0;i<$scope.list.length;i++){
  				if(name===$scope.list[i].apiName){
  					console.log($scope.list[i].apiStatus);
  					return $scope.list[i].apiStatus;
  				}
  			}
  		};
  		
  		$scope.addApiData = function(apiId, apiStatus){
  			
  			var obj = {apiId: apiId , apiStatus: apiStatus};
  			
  			if(!list){
  				list = [];
  			}
  		
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
  				}
  			
  			}
  		};

  		$scope.updatePermission = function () {
  			$scope.app.apis = list;
  			App.updateApiData({
  				
  				},$scope.app,
  					function (data) {
  						if(data.status == 200){
  							$route.reload();
  						}else{
  							$scope.errorMsg = data.message;
  						}
  				});
  		};
  		
  		//key tab
  		$scope.keygen = function(){
  			
  			App.update({
  			}, $scope.app, 
  			
  			function(data) {
				if (data.status == 200) {
					$scope.app=data.data;
					$scope.msg = data.message;
					$scope.errorMsg = null;
				} else {
					$scope.errorMsg = data.message;
					$scope.msg = null;
				}
  			});
  		};
  		
	}
	
]);

app.controller('addApiCtrl', ['$scope', '$location', 'Api',
    function($scope, $location, Api){
		$scope.title = 'New';

        $scope.submit = function () {
        	//$scope.api.ownerId = '1';
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
		
		$scope.goBack = function(){
			$location.path('/api/'+apiid);
		};
     }
]);

app.controller('addPolicyCtrl', ['$scope', '$location', '$routeParams', '$interval', 'Policy', 'Api',
     function ($scope, $location, $routeParams, $interval, Policy, Api) {
		$scope.title = 'New';
		var apiid = $routeParams.apiId;
		
		Api.getStatusList({
			apiId : apiid
		}, function(data){
			$scope.apiStatus = data.data;
		});
		
		//for quota policy
		$scope.addStatus = function(){
			if(!$scope.policy.qstatus){
				$scope.policy.qstatus = [];
			}
			var name = $scope.s.name;
			var quota = $scope.s.value;
			var obj = {name: name , quota: quota};
			
			var index = -1;
			for(var i=0;i<$scope.policy.qstatus.length;i++){
				if($scope.policy.qstatus[i].name === name){
					index = i;
				}
			}
			
			console.log('index: '+index);
			if(index > -1){
				if($scope.policy.qstatus[index].quota!==quota){
					$scope.policy.qstatus.splice(index,1);
					$scope.policy.qstatus.push(obj);
				}else console.log('Already in db');
			}
			else{
				$scope.policy.qstatus.push(obj);
			}
			console.log($scope.policy.qstatus);
		};
		
		//for ip access control policy
		//white list of ip
		$scope.addWIp = function(){
			if(!$scope.policy.whiteList){
				$scope.policy.whiteList = [];
			}
			var mask = $scope.ipw.mask;
			var ip = $scope.ipw.ip;
			var obj = {mask: mask , ip: ip};
			
			var index = -1;
			for(var i=0;i<$scope.policy.whiteList.length;i++){
				if($scope.policy.whiteList[i].mask === mask &&
						$scope.policy.whiteList[i].ip === ip){
					index = i;
				}
			}
			
			console.log('index: '+index);
			if(index > -1){
				console.log('Ip Already in');
			}
			else{
				$scope.policy.whiteList.push(obj);
			}
			console.log($scope.policy.whiteList);
		};
		
		$scope.deleteWIp = function(mask,ip){
			
			var index = -1;
			for(var i=0;i<$scope.policy.whiteList.length;i++){
				if($scope.policy.whiteList[i].mask === mask &&
						$scope.policy.whiteList[i].ip === ip){
					index = i;
				}
			}
			
			console.log('index: '+index);
			if(index > -1){
				$scope.policy.whiteList.splice(index,1);
			}
			else{
				console.log('Ip Not found');
			}
			console.log($scope.policy.whiteList);
		};
		
		//black list of ip
		$scope.addBIp = function(){
			if(!$scope.policy.blackList){
				$scope.policy.blackList = [];
			}
			var mask = $scope.ipb.mask;
			var ip = $scope.ipb.ip;
			var obj = {mask: mask , ip: ip};
			
			var index = -1;
			for(var i=0;i<$scope.policy.blackList.length;i++){
				if($scope.policy.blackList[i].mask === mask &&
						$scope.policy.blackList[i].ip === ip){
					index = i;
				}
			}
			
			console.log('index: '+index);
			if(index > -1){
				console.log('Ip Already in db');
			}
			else{
				$scope.policy.blackList.push(obj);
			}
			console.log($scope.policy.blackList);
		};
		
		$scope.deleteBIp = function(mask,ip){
			
			var index = -1;
			for(var i=0;i<$scope.policy.blackList.length;i++){
				if($scope.policy.blackList[i].mask === mask &&
						$scope.policy.blackList[i].ip === ip){
					index = i;
				}
			}
			
			console.log('index: '+index);
			if(index > -1){
				$scope.policy.blackList.splice(index,1);
			}
			else{
				console.log('Ip Not found');
			}
			console.log($scope.policy.blackList);
		};
		
		
		var timer = $interval(function () {
			$scope.errorMsg = null;
	    }, 20000);
		
		
		$scope.submit = function () {
			var type = $scope.policy.type;
			if(type=='Spike Arrest'){
				Policy.createSpikeArrest({
					apiId: apiid
					},$scope.policy,
					function (data) {
						if(data.status == 200){
							$interval.cancel(timer);
							$location.path('/api/'+apiid+'/policy');
						}else{
							$scope.errorMsg = data.message;
							//if error message is for a duplicate policy
							if($scope.errorMsg.indexOf('already exists')!=-1){
								$scope.policy = null;
							}
						}
				});
			}
			else if(type=='Quota'){
				
				Policy.createQuota({
					apiId: apiid
					},$scope.policy,
					function (data) {
						if(data.status == 200){
							$interval.cancel(timer);
							$location.path('/api/'+apiid+'/policy');
						}else{
							$scope.errorMsg = data.message;
							
							//if error message is for a duplicate policy
							if($scope.errorMsg.indexOf('already exists')!=-1){
								$scope.policy = null;
							}
						}
				});
			}
			else if(type=='IP Access Control'){
				console.log('Add');
				console.log($scope.policy.whiteList);
				console.log($scope.policy.blackList);
				
				Policy.createIP({
					apiId: apiid
					},$scope.policy,
					function (data) {
						if(data.status == 200){
							$interval.cancel(timer);
							$location.path('/api/'+apiid+'/policy');
						}else{
							$scope.errorMsg = data.message;

							//if error message is for a duplicate policy
							if($scope.errorMsg.indexOf('already exists')!=-1){
								$scope.policy = null;
							}
						}
				});
			}
			else if(type=='Verify App Key'){
				Policy.createVAppKey({
					apiId: apiid
					},$scope.policy,
					function (data) {
						if(data.status == 200){
							$interval.cancel(timer);
							$location.path('/api/'+apiid+'/policy');
						}else{
							$scope.errorMsg = data.message;
							
							//if error message is for a duplicate policy
							if($scope.errorMsg.indexOf('already exists')!=-1){
								$scope.policy = null;
							}
						}
				});
			}
			else if(type=='OAuth'){
				Policy.createOAuth({
					apiId: apiid
					},$scope.policy,
					function (data) {
						if(data.status == 200){
							$interval.cancel(timer);
							$location.path('/api/'+apiid+'/policy');
						}else{
							$scope.errorMsg = data.message;

							//if error message is for a duplicate policy
							if($scope.errorMsg.indexOf('already exists')!=-1){
								$scope.policy = null;
							}
						}
				});
			}
			else if(type=='SAML'){
				Policy.createSAML({
					apiId: apiid
					},$scope.policy,
					function (data) {
						if(data.status == 200){
							$interval.cancel(timer);
							$location.path('/api/'+apiid+'/policy');
						}else{
							$scope.errorMsg = data.message;

							//if error message is for a duplicate policy
							if($scope.errorMsg.indexOf('already exists')!=-1){
								$scope.policy = null;
							}
						}
				});
			}
		};
		
		$scope.goBack = function(){
			$interval.cancel(timer);
			$location.path('/api/'+apiid+'/policy');
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
						$location.path('/api/'+apiid+'/status');
					}else{
						$scope.errorMsg = data.message;
					}
					
				});
		};
		
		$scope.goBack = function(){
			$location.path('/api/'+apiid+'/status');
		};
		
	}
]);


app.controller('editApiCtrl', ['$scope', '$location', '$routeParams', '$interval', 'Api',
     function($scope, $location, $routeParams, $interval, Api){
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
        					$scope.api = data.data;
        					$scope.msg = data.message;
                        }else{
                        	$scope.errorMsg = data.message;
                        }
                     });
        };
        
        var timer = $interval(function () {
			$scope.errorMsg = null;
			$scope.msg = null;
	    }, 10000);
        
        $interval.cancel(timer);

     }
]);

app.controller('editResourceCtrl', ['$scope', '$location', '$routeParams', '$interval', 'Resource',
		function($scope, $location, $routeParams, $interval, Resource){
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
	            			$scope.resource = data.data;
	            			$scope.msg = data.message;
	            			$scope.errorMsg = null;
	            		}else{
	            			$scope.errorMsg = data.message;
	            			$scope.msg = null;
	            		}
	                });
	        };
	        
			var timer = $interval(function () {
				$scope.errorMsg = null;
				$scope.msg = null;
		    }, 10000);
	        
	        $scope.remove = function () {
				Resource.remove({
					apiId : apiid,
					resourceId : rid
				}, function(data){
					$interval.cancel(timer);
					$location.path('api/'+apiid);
				});
			};
			
			$scope.goBack = function(){
				$interval.cancel(timer);
				$location.path('/api/'+apiid);
			};
		}
]);

app.controller('editPolicyCtrl', ['$scope', '$location', '$routeParams', '$interval', 'Policy', 'Api',
        function($scope, $location, $routeParams, $interval, Policy, Api){
			$scope.title = 'Edit';
			var apiid = $routeParams.apiId;
			var pid = $routeParams.policyId;
			
			Policy.getPolicy({
				apiId: apiid,
				policyId: pid
			}, function(data){
				$scope.policy = data.data;
			});
			
			Api.getStatusList({
				apiId : apiid
			}, function(data){
				$scope.apiStatus = data.data;
			});
			
			//for Quota policy
			$scope.addStatus = function(){
				if(!$scope.policy.qstatus){
					$scope.policy.qstatus = [];
				}
				var name = $scope.s.name;
				var quota = $scope.s.value;
				var obj = {name: name , quota: quota};
				
				var index = -1;
				for(var i=0;i<$scope.policy.qstatus.length;i++){
					if($scope.policy.qstatus[i].name === name){
						index = i;
					}
				}
				
				console.log('index: '+index);
				if(index > -1){
					if($scope.policy.qstatus[index].quota!==quota){
						$scope.policy.qstatus.splice(index,1);
						$scope.policy.qstatus.push(obj);
					}else console.log('Already in db');
				}
				else{
					$scope.policy.qstatus.push(obj);
				}
				console.log($scope.policy.qstatus);
			};
			
			//for IP Access Control policy
			//white list of ip
			$scope.addWIp = function(){
				if(!$scope.policy.whiteList){
					$scope.policy.whiteList = [];
				}
				var mask = $scope.ipw.mask;
				var ip = $scope.ipw.ip;
				var obj = {mask: mask , ip: ip};
				
				var index = -1;
				for(var i=0;i<$scope.policy.whiteList.length;i++){
					if($scope.policy.whiteList[i].mask === mask &&
							$scope.policy.whiteList[i].ip === ip){
						index = i;
					}
				}
				
				console.log('index: '+index);
				if(index > -1){
					console.log('Ip Already in');
				}
				else{
					$scope.policy.whiteList.push(obj);
				}
				console.log($scope.policy.whiteList);
			};
			
			$scope.deleteWIp = function(mask,ip){
				
				var index = -1;
				for(var i=0;i<$scope.policy.whiteList.length;i++){
					if($scope.policy.whiteList[i].mask === mask &&
							$scope.policy.whiteList[i].ip === ip){
						index = i;
					}
				}
				
				console.log('index: '+index);
				if(index > -1){
					$scope.policy.whiteList.splice(index,1);
				}
				else{
					console.log('Ip Not found');
				}
				console.log($scope.policy.whiteList);
			};
			
			//black list of ip
			$scope.addBIp = function(){
				if(!$scope.policy.blackList){
					$scope.policy.blackList = [];
				}
				var mask = $scope.ipb.mask;
				var ip = $scope.ipb.ip;
				var obj = {mask: mask , ip: ip};
				
				var index = -1;
				for(var i=0;i<$scope.policy.blackList.length;i++){
					if($scope.policy.blackList[i].mask === mask &&
							$scope.policy.blackList[i].ip === ip){
						index = i;
					}
				}
				
				console.log('index: '+index);
				if(index > -1){
					console.log('Ip Already in db');
				}
				else{
					$scope.policy.blackList.push(obj);
				}
				console.log($scope.policy.blackList);
			};
			
			$scope.deleteBIp = function(mask,ip){
				
				var index = -1;
				for(var i=0;i<$scope.policy.blackList.length;i++){
					if($scope.policy.blackList[i].mask === mask &&
							$scope.policy.blackList[i].ip === ip){
						index = i;
					}
				}
				
				console.log('index: '+index);
				if(index > -1){
					$scope.policy.blackList.splice(index,1);
				}
				else{
					console.log('Ip Not found');
				}
				console.log($scope.policy.blackList);
			};
			
			
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
				else if(type=='IP Access Control'){
					console.log('Add');
					console.log($scope.policy.whiteList);
					console.log($scope.policy.blackList);
					
					Policy.updateIP({
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
				else if(type=='Verify App Key'){
					Policy.updateVAppKey({
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
				else if(type=='OAuth'){
					Policy.updateOAuth({
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
				else if(type=='SAML'){
					Policy.updateSAML({
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
			
	        var timer = $interval(function () {
				$scope.errorMsg = null;
				$scope.msg = null;
		    }, 10000);
	        
			$scope.remove = function () {
				Policy.remove({
					apiId : apiid,
					policyId : pid
				}, function(data){
					$interval.cancel(timer);
					$location.path('api/'+apiid);
				});
			};
			
			$scope.goBack = function(){
				$interval.cancel(timer);
				$location.path('/api/'+apiid+'/policy');
			};
        }
]);

app.controller('editAppCtrl', ['$scope', '$location', '$routeParams', '$interval', 'App',
        function($scope, $location, $routeParams, $interval, App){
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
	            	//apiId: apiid
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
	        
	        var timer = $interval(function () {
				$scope.errorMsg = null;
				$scope.msg = null;
		    }, 7000);
	        
			$scope.remove = function () {
				App.remove({
					apiId : apiid,
					appId : aid
				}, function(data){
					$interval.cancel(timer);
					$location.path('api/'+apiid);
				});
			};
			
        }
]);

app.controller('editStatusCtrl', ['$scope', '$location', '$routeParams', '$interval', 'Api',
    function ($scope, $location, $routeParams, $interval, Api) {
		$scope.title = 'Edit';
		var apiid = $routeParams.apiId;
		var statusname = $routeParams.statusName;
		
		Api.getStatus({
			apiId: apiid,
			statusName: statusname
		}, function(data){
			$scope.status = data.data;
		});
		
		var timer = $interval(function () {
			$scope.errorMsg = null;
			$scope.msg = null;
	    }, 7000);
		
		$scope.submit = function () {
			Api.updateStatus({
				apiId: apiid
			},$scope.status,
			function (data) {
				if(data.status == 200){
					$interval.cancel(timer);
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
				$interval.cancel(timer);
				$location.path('api/'+apiid);
			});
		};
		
		$scope.goBack = function(){
			$interval.cancel(timer);
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

app.controller('editResourcePolicyCtrl', ['$scope', '$location', '$routeParams', '$interval', 
                                          'Resource', 'Api',
            function($scope, $location, $routeParams, $interval, Resource, Api){
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
				
				Api.getStatusList({
					apiId : apiid
				}, function(data){
					$scope.apiStatus = data.data;
				});
				
				//for quota policy
				$scope.addStatus = function(){
					if(!$scope.policy.qstatus){
						$scope.policy.qstatus = [];
					}
					var name = $scope.s.name;
					var quota = $scope.s.value;
					var obj = {name: name , quota: quota};
					
					var index = -1;
					for(var i=0;i<$scope.policy.qstatus.length;i++){
						if($scope.policy.qstatus[i].name === name){
							index = i;
						}
					}
					
					console.log('index: '+index);
					if(index > -1){
						if($scope.policy.qstatus[index].quota!==quota){
							$scope.policy.qstatus.splice(index,1);
							$scope.policy.qstatus.push(obj);
						}else console.log('Already in db');
					}
					else{
						$scope.policy.qstatus.push(obj);
					}
					console.log($scope.policy.qstatus);
				};
				
				//for Ip Access Control policy
				//white list of ip
				$scope.addWIp = function(){
					if(!$scope.policy.whiteList){
						$scope.policy.whiteList = [];
					}
					var mask = $scope.ipw.mask;
					var ip = $scope.ipw.ip;
					var obj = {mask: mask , ip: ip};
					
					var index = -1;
					for(var i=0;i<$scope.policy.whiteList.length;i++){
						if($scope.policy.whiteList[i].mask === mask &&
								$scope.policy.whiteList[i].ip === ip){
							index = i;
						}
					}
					
					console.log('index: '+index);
					if(index > -1){
						console.log('Ip Already in');
					}
					else{
						$scope.policy.whiteList.push(obj);
					}
					console.log($scope.policy.whiteList);
				};
				
				$scope.deleteWIp = function(mask,ip){
					
					var index = -1;
					for(var i=0;i<$scope.policy.whiteList.length;i++){
						if($scope.policy.whiteList[i].mask === mask &&
								$scope.policy.whiteList[i].ip === ip){
							index = i;
						}
					}
					
					console.log('index: '+index);
					if(index > -1){
						$scope.policy.whiteList.splice(index,1);
					}
					else{
						console.log('Ip Not found');
					}
					console.log($scope.policy.whiteList);
				};
				
				//black list of ip
				$scope.addBIp = function(){
					if(!$scope.policy.blackList){
						$scope.policy.blackList = [];
					}
					var mask = $scope.ipb.mask;
					var ip = $scope.ipb.ip;
					var obj = {mask: mask , ip: ip};
					
					var index = -1;
					for(var i=0;i<$scope.policy.blackList.length;i++){
						if($scope.policy.blackList[i].mask === mask &&
								$scope.policy.blackList[i].ip === ip){
							index = i;
						}
					}
					
					console.log('index: '+index);
					if(index > -1){
						console.log('Ip Already in db');
					}
					else{
						$scope.policy.blackList.push(obj);
					}
					console.log($scope.policy.blackList);
				};
				
				$scope.deleteBIp = function(mask,ip){
					
					var index = -1;
					for(var i=0;i<$scope.policy.blackList.length;i++){
						if($scope.policy.blackList[i].mask === mask &&
								$scope.policy.blackList[i].ip === ip){
							index = i;
						}
					}
					
					console.log('index: '+index);
					if(index > -1){
						$scope.policy.blackList.splice(index,1);
					}
					else{
						console.log('Ip Not found');
					}
					console.log($scope.policy.blackList);
				};
				
		        
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
									var listPolicies = data.data.policy;
									for(var i=0;i<listPolicies.length;i++){
										if(listPolicies[i].id===$scope.policy.id){
											$scope.policy = listPolicies[i];
										}
									}
									//$scope.policy = data.data;
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
									var listPolicies = data.data.policy;
									for(var i=0;i<listPolicies.length;i++){
										if(listPolicies[i].id===$scope.policy.id){
											$scope.policy = listPolicies[i];
										}
									}
									$scope.msg = data.message;
									$scope.errorMsg = null;
								}else{
									$scope.errorMsg = data.message;
									$scope.msg = null;

								}
						});
					}
					else if(type=='IP Access Control'){
						console.log('Add');
						console.log($scope.policy.whiteList);
						console.log($scope.policy.blackList);
						
						Resource.updateIPResource({
							apiId: apiid,
							resourceId: rid
							},$scope.policy,
							function (data) {
								if(data.status == 200){
									var listPolicies = data.data.policy;
									for(var i=0;i<listPolicies.length;i++){
										if(listPolicies[i].id===$scope.policy.id){
											$scope.policy = listPolicies[i];
										}
									}
									$scope.msg = data.message;
									$scope.errorMsg = null;
								}else{
									$scope.errorMsg = data.message;
									$scope.msg = null;

								}
						});
					}
					else if(type=='Verify App Key'){
						Resource.updateVAppKeyResource({
							apiId: apiid,
							resourceId: rid
							},$scope.policy,
							function (data) {
								if(data.status == 200){
									var listPolicies = data.data.policy;
									for(var i=0;i<listPolicies.length;i++){
										if(listPolicies[i].id===$scope.policy.id){
											$scope.policy = listPolicies[i];
										}
									}
									$scope.msg = data.message;
									$scope.errorMsg = null;
								}else{
									$scope.errorMsg = data.message;
									$scope.msg = null;

								}
						});
					}
					else if(type=='OAuth'){
						Resource.updateOAuthResource({
							apiId: apiid,
							resourceId: rid
							},$scope.policy,
							function (data) {
								if(data.status == 200){
									var listPolicies = data.data.policy;
									for(var i=0;i<listPolicies.length;i++){
										if(listPolicies[i].id===$scope.policy.id){
											$scope.policy = listPolicies[i];
										}
									}
									$scope.msg = data.message;
									$scope.errorMsg = null;
								}else{
									$scope.errorMsg = data.message;
									$scope.msg = null;
									
								}
						});
					}
					else if(type=='SAML'){
						Resource.updateSAMLResource({
							apiId: apiid,
							resourceId: rid
							},$scope.policy,
							function (data) {
								if(data.status == 200){
									var listPolicies = data.data.policy;
									for(var i=0;i<listPolicies.length;i++){
										if(listPolicies[i].id===$scope.policy.id){
											$scope.policy = listPolicies[i];
										}
									}
									$scope.msg = data.message;
									$scope.errorMsg = null;
								}else{
									$scope.errorMsg = data.message;
									$scope.msg = null;

								}
						});
					}
		        };
		        
		        var timer = $interval(function () {
					$scope.errorMsg = null;
					$scope.msg = null;
			    }, 10000);
		        
		        $scope.remove = function () {
					Resource.removePolicy({
						apiId : apiid,
						resourceId: rid,
						policyId : pid
					}, function(data){
						$interval.cancel(timer);
						$location.path('/show/'+apiid+'/resource/'+rid);
					});
				};
				
			}
]);

app.controller('addResourcePolicyCtrl', ['$scope', '$location', '$routeParams', '$interval', 'Resource', 'Api',
            function($scope, $location, $routeParams, $interval, Resource, Api){
            	$scope.title = 'New Resource';
            	
            	var apiid = $routeParams.apiId;
            	var rid = $routeParams.resourceId;
            	
            	Api.getStatusList({
    				apiId : apiid
    			}, function(data){
    				$scope.apiStatus = data.data;
    			});
            	
            	//for Quota policy
            	$scope.addStatus = function(){
					if(!$scope.policy.qstatus){
						$scope.policy.qstatus = [];
					}
					var name = $scope.s.name;
					var quota = $scope.s.value;
					var obj = {name: name , quota: quota};
					
					var index = -1;
					for(var i=0;i<$scope.policy.qstatus.length;i++){
						if($scope.policy.qstatus[i].name === name){
							index = i;
						}
					}
					
					console.log('index: '+index);
					if(index > -1){
						if($scope.policy.qstatus[index].quota!==quota){
							$scope.policy.qstatus.splice(index,1);
							$scope.policy.qstatus.push(obj);
						}else console.log('Already in db');
					}
					else{
						$scope.policy.qstatus.push(obj);
					}
					console.log($scope.policy.qstatus);
				};
				
				//for Ip Access Control policy
				//white list of ip
				$scope.addWIp = function(){
					if(!$scope.policy.whiteList){
						$scope.policy.whiteList = [];
					}
					var mask = $scope.ipw.mask;
					var ip = $scope.ipw.ip;
					var obj = {mask: mask , ip: ip};
					
					var index = -1;
					for(var i=0;i<$scope.policy.whiteList.length;i++){
						if($scope.policy.whiteList[i].mask === mask &&
								$scope.policy.whiteList[i].ip === ip){
							index = i;
						}
					}
					
					console.log('index: '+index);
					if(index > -1){
						console.log('Ip Already in');
					}
					else{
						$scope.policy.whiteList.push(obj);
					}
					console.log($scope.policy.whiteList);
				};
				
				$scope.deleteWIp = function(mask,ip){
					
					var index = -1;
					for(var i=0;i<$scope.policy.whiteList.length;i++){
						if($scope.policy.whiteList[i].mask === mask &&
								$scope.policy.whiteList[i].ip === ip){
							index = i;
						}
					}
					
					console.log('index: '+index);
					if(index > -1){
						$scope.policy.whiteList.splice(index,1);
					}
					else{
						console.log('Ip Not found');
					}
					console.log($scope.policy.whiteList);
				};
				
				//black list of ip
				$scope.addBIp = function(){
					if(!$scope.policy.blackList){
						$scope.policy.blackList = [];
					}
					var mask = $scope.ipb.mask;
					var ip = $scope.ipb.ip;
					var obj = {mask: mask , ip: ip};
					
					var index = -1;
					for(var i=0;i<$scope.policy.blackList.length;i++){
						if($scope.policy.blackList[i].mask === mask &&
								$scope.policy.blackList[i].ip === ip){
							index = i;
						}
					}
					
					console.log('index: '+index);
					if(index > -1){
						console.log('Ip Already in db');
					}
					else{
						$scope.policy.blackList.push(obj);
					}
					console.log($scope.policy.blackList);
				};
				
				$scope.deleteBIp = function(mask,ip){
					
					var index = -1;
					for(var i=0;i<$scope.policy.blackList.length;i++){
						if($scope.policy.blackList[i].mask === mask &&
								$scope.policy.blackList[i].ip === ip){
							index = i;
						}
					}
					
					console.log('index: '+index);
					if(index > -1){
						$scope.policy.blackList.splice(index,1);
					}
					else{
						console.log('Ip Not found');
					}
					console.log($scope.policy.blackList);
				};
				
				
				var timer = $interval(function () {
					$scope.errorMsg = null;
			    }, 20000);
        		
        		$scope.submit = function () {
        			var type = $scope.policy.type;
        			if(type=='Spike Arrest'){
        				Resource.createSpikeArrestResource({
        					apiId: apiid,
        					resourceId: rid
        					},$scope.policy,
        					function (data) {
        						if(data.status == 200){
        							$interval.cancel(timer);
        							$location.path('/show/'+apiid+'/resource/'+rid);
        						}else{
        							$scope.errorMsg = data.message;
        							
        							//if error message is for a duplicate policy
									if($scope.errorMsg.indexOf('already exists')!=-1){
										$scope.policy = null;
									}
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
        							$interval.cancel(timer);
        							$location.path('/show/'+apiid+'/resource/'+rid);
        						}else{
        							$scope.errorMsg = data.message;
        							
        							//if error message is for a duplicate policy
									if($scope.errorMsg.indexOf('already exists')!=-1){
										$scope.policy = null;
									}
        						}
        				});
        			}
        			else if(type=='IP Access Control'){
						console.log('Add');
						console.log($scope.policy.whiteList);
						console.log($scope.policy.blackList);
						
						Resource.createIPResource({
							apiId: apiid,
							resourceId: rid
							},$scope.policy,
							function (data) {
								if(data.status == 200){
									$interval.cancel(timer);
									$location.path('/show/'+apiid+'/resource/'+rid);
								}else{
									$scope.errorMsg = data.message;
									
									//if error message is for a duplicate policy
									if($scope.errorMsg.indexOf('already exists')!=-1){
										$scope.policy = null;
									}
								}
						});
					}
        			else if(type=='Verify App Key'){
						Resource.createVAppKeyResource({
							apiId: apiid,
							resourceId: rid
							},$scope.policy,
							function (data) {
								if(data.status == 200){
									$interval.cancel(timer);
									$location.path('/show/'+apiid+'/resource/'+rid);
								}else{
									$scope.errorMsg = data.message;
									
									//if error message is for a duplicate policy
									if($scope.errorMsg.indexOf('already exists')!=-1){
										$scope.policy = null;
									}
								}
						});
					}
        			else if(type=='OAuth'){
						Resource.createOAuthResource({
							apiId: apiid,
							resourceId: rid
							},$scope.policy,
							function (data) {
								if(data.status == 200){
									$interval.cancel(timer);
									$location.path('/show/'+apiid+'/resource/'+rid);
								}else{
									$scope.errorMsg = data.message;
									
									//if error message is for a duplicate policy
									if($scope.errorMsg.indexOf('already exists')!=-1){
										$scope.policy = null;
									}
								}
						});
					}
        			else if(type=='SAML'){
						Resource.createSAMLResource({
							apiId: apiid,
							resourceId: rid
							},$scope.policy,
							function (data) {
								if(data.status == 200){
									$interval.cancel(timer);
									$location.path('/show/'+apiid+'/resource/'+rid);
								}else{
									$scope.errorMsg = data.message;
									
									//if error message is for a duplicate policy
									if($scope.errorMsg.indexOf('already exists')!=-1){
										$scope.policy = null;
									}
								}
						});
					}
        		};
        	
			}
]);


//Dashboard

app.controller('startCtrl', ['$scope', '$location', 'Stat', 'GGraph', 'Auth',
    function($scope, $location, Stat, GGraph, Auth){
	
		Stat.isDashEnabled({
		}, function(data){
			
			if(data.status===403){
    			Auth.redirectLogin({},
    					function(data){
    						console.log(data);
    			});
    			
    		}else{
			
    			if(data.data===true){
    				GGraph.enabled({
    				},function(data){
    					if(data.data===true){
    						$location.path('/dashboard/graphs');
    					}else{
    						$location.path('/dashboard/login');
    					}
				});
			}
			
    		}
		});
		
		//Google Chart
		
		//Pie chart Sample
		 var chart1 = {};
		    chart1.type = "PieChart";
		    chart1.data = [
		       ['Api', 'access'],
		       ['Api 2', 50],
		       ['Api 3', 80]
		      ];
		    chart1.data.push(['Api 1',20]);
		    chart1.options = {
		    	title: "Access to Apis",
		        displayExactValues: true,
		        width: 500,
		        height: 300,
		        is3D: true,
		        chartArea: {left:30,top:30,bottom:0,height:"100%"}
		    };

		    chart1.formatters = {
		      number : [{
		        columnNum: 1,
		        pattern: "#"
		      }]
		    };

		    $scope.chart = chart1;
		    
		    //Table chart Sample
		    var chart2 = {};
		    chart2.type="Table";
		    
		    chart2.data = [
		                   ['Path','Number of Access'],
		    		       ['Api1/resource/1', {v: 20, f:20}],
		    		       ['Api2' ,{v: 30, f:30}],
		                   ['Api2/resource3', {v: 20, f:20}],
		                   ['Api3', {v: 80, f:80}]
		    ];
		    
		    chart2.options = {
		    		title: 'Access Api and Resource', 
			        displayExactValues: true,
			        width: 500,
			        height: 300,
			        chartArea: {left:50,top:50,bottom:0,height:"100%"}
			    };

		    $scope.chart2 = chart2;

		    
	}
]);

app.controller('tridCtrl', ['$scope', '$location', 'Stat',
    function($scope, $location, Stat){
	
	$scope.submit = function () {
		
		Stat.saveTrID({
        }, $scope.trackingID,
            function (data) {
        		if(data.status == 200){
        			console.log('Done');
        			$location.path('/dashboard/login');
        		}else{
        			$scope.errorMsg = data.message;
        			console.log($scope.errorMsg);
        		}
            });
    	};
	}
]);

app.controller('dashLoginCtrl', ['$scope', '$location', 'Auth',
    function($scope, $location, Auth){
		
		$scope.submit = function () {
			Auth.gaLogin(
				function(){
	        	}, function(data){
	        		console.log('success google login');
	        		
	        	}, function(error){
	        		console.log('error google login');
	        		$scope.error = error;
	        	});
    	};
		
	}
]);

app.controller('graphsCtrl', ['$scope', '$location', 'Api', 'GGraph',
    function($scope, $location, Api, GGraph){
	
		$scope.charts = [];
		
		//api Name
		Api.listApiName({
			
		},function(data){
			$scope.apis = data.data;
			console.log($scope.apis);
		});
		
		$scope.addChart = function(name){
			//check if chart already exist
			var index = [];
			//retrieve index of api name, if exists
			for(var i=0;i<$scope.charts.length;i++){
				if($scope.charts[i].api === name){
					console.log(i);
					index.push(i);
				}
			}
			//if index then delete charts
			if(index.length > 0){
				$scope.charts.splice(index[0],3);
			}
			//if not, then draw
			if(index.length === 0){
				
			if($scope.charts===null){
				$scope.charts = [];
			}
			
			GGraph.eventApiAction({
				apiName : name
			}, function(data){
				
				var chartAction = {};
				chartAction.api = name;
				
				chartAction.type = "PieChart";
				
				chartAction.options = {
						title: name+" access granted and denied",
						displayExactValues: true,
						width: 500,
						height: 300,
						is3D: true,
						chartArea: {left:30,top:30,bottom:0,height:"100%"}
					};

				chartAction.formatters = {
						number : [{
							columnNum: 1,
							pattern: "#"
						}]
				};
				
				if(data.data!=null){
					
					//pie chart data one: access granted/access denied, value
					var accessNumber = [];
					
					for(var i=0;i<data.data.length;i++){
						var columns = data.data[i];
						accessNumber.push([columns[0],{v: parseInt(columns[2]), f:parseInt(columns[2])}]);
					}
					
					chartAction.data = accessNumber;
					chartAction.data.unshift(['Access', 'Total Number']);
				
				}else{
					chartAction.data = [['Access', 'Total Number']];
					chartAction.msg = "This chart is not available at the moment. An error in Google Analytics occured. Please try again later or reload the page.";
				}
				
				$scope.charts.push(chartAction);
			});
			
			GGraph.eventApiLabel({
				apiName : name
			}, function(data){
				
				var chartLabel = {};
				chartLabel.api = name;
				
				chartLabel.type = "PieChart";
				
				chartLabel.options = {
						title: name+" and its resources",
						displayExactValues: true,
						width: 500,
						height: 300,
						is3D: true,
						chartArea: {left:30,top:30,bottom:0,height:"100%"}
					};

				chartLabel.formatters = {
						number : [{
							columnNum: 1,
							pattern: "#"
						}]
					};
				
				if(data.data!=null){
					
					//pie chart data two: path, value
					var pathNumber = [];
					
					for(var i=0;i<data.data.length;i++){
						var columns = data.data[i];
						pathNumber.push([columns[0],{v: parseInt(columns[2]), f:parseInt(columns[2])}]);
					}
					
					chartLabel.data = pathNumber;
					chartLabel.data.unshift([name+' resources', 'Total Number']);

				}else{
					chartLabel.data = [[name+' resources', 'Total Number']];
					chartLabel.msg = "This chart is not available at the moment. An error in Google Analytics occured. Please try again later or reload the page.";
				}
				
				$scope.charts.push(chartLabel);
			});
			
			GGraph.exceptionApi({
				apiName : name
			}, function(data){
				
				var chartExc = {};
				chartExc.api = name;
				
				chartExc.type = "PieChart";
				
				chartExc.options = {
						title: name+" Exception",
						displayExactValues: true,
						width: 500,
						height: 300,
						is3D: true,
						chartArea: {left:30,top:30,bottom:0,height:"100%"}
					};

				chartExc.formatters = {
						number : [{
							columnNum: 1,
							pattern: "#"
						}]
					};
				
				if(data.data!=null){
					
					var excData = [];
					
					for(var i=0;i<data.data.length;i++){
						var columns = data.data[i];
						excData.push([columns[0],{v: parseInt(columns[1]), f:parseInt(columns[1])}]);
					}
				
					chartExc.data = excData;
					chartExc.data.unshift([name+' Exception', 'Total Number']);
				
				}else{
					chartExc.data = [[name+' Exception', 'Total Number']];
					chartExc.msg = "This chart is not available at the moment. An error in Google Analytics occured. Please try again later or reload the page.";
				}
				
				$scope.charts.push(chartExc);
			});
			
			}
			
		};
		
		//chart data
		/*
		GGraph.eventApiAction({
			apiName : 'Geocoding'
		}, function(data){
			
			
			if(data.data!=null){
				
				//pie chart data one: access granted/access denied, value
				var accessNumber = [];
				
				for(var i=0;i<data.data.length;i++){
					var columns = data.data[i];
					accessNumber.push([columns[0],{v: parseInt(columns[2]), f:parseInt(columns[2])}]);
				}
				
				var chart1 = {};
			
				chart1.type = "PieChart";
				
				chart1.data = accessNumber;
				chart1.data.unshift(['Access', 'Total Number']);
				console.log(chart1.data);
				
				chart1.options = {
					title: "Geocoding access granted and denied",
					displayExactValues: true,
					width: 500,
					height: 300,
					is3D: true,
					chartArea: {left:30,top:30,bottom:0,height:"100%"}
				};

				chart1.formatters = {
					number : [{
						columnNum: 1,
						pattern: "#"
					}]
				};

				$scope.chart = chart1;
			
			}else{
				$scope.msg1 = "This chart is not available at the moment. An error in Google Analytics occured. Please try again later or reload the page.";
			}
		});
		
		GGraph.eventApiLabel({
			apiName : 'Geocoding'
		}, function(data){
			
			
			if(data.data!=null){
				
				//pie chart data two: path, value
				var pathNumber = [];
				
				for(var i=0;i<data.data.length;i++){
					var columns = data.data[i];
					pathNumber.push([columns[0],{v: parseInt(columns[2]), f:parseInt(columns[2])}]);
				}
				
				var chart2 = {};
				
				chart2.type = "PieChart";
				
				chart2.data = pathNumber;
				chart2.data.unshift(['Geocoding resources', 'Total Number']);
				console.log(chart2.data);
				
				chart2.options = {
					title: "Geocoding and its resources",
					displayExactValues: true,
					width: 500,
					height: 300,
					is3D: true,
					chartArea: {left:30,top:30,bottom:0,height:"100%"}
				};

				chart2.formatters = {
					number : [{
						columnNum: 1,
						pattern: "#"
					}]
				};

				$scope.chart2 = chart2;
			
			}else{
				$scope.msg2 = "This chart is not available at the moment. An error in Google Analytics occured. Please try again later or reload the page.";
			}
		});
		
		GGraph.exceptionApi({
			apiName : 'Geocoding'
		}, function(data){
			
			if(data.data!=null){
				var chart4 = {};
				
				var excData = [];
				
				for(var i=0;i<data.data.length;i++){
					var columns = data.data[i];
					excData.push([columns[0],{v: parseInt(columns[1]), f:parseInt(columns[1])}]);
				}
			
				chart4.type = "PieChart";
				
				chart4.data = excData;
				chart4.data.unshift(['Geocoding Exception', 'Total Number']);
				console.log(chart4.data);
				
				chart4.options = {
					title: "Api 3 exception",
					displayExactValues: true,
					width: 500,
					height: 300,
					is3D: true,
					chartArea: {left:30,top:30,bottom:0,height:"100%"}
				};

				chart4.formatters = {
					number : [{
						columnNum: 1,
						pattern: "#"
					}]
				};

				$scope.chart4 = chart4;
			
			}else{
				$scope.msg4 = "This chart is not available at the moment. An error in Google Analytics occured. Please try again later or reload the page.";
			}
		});
		*/
		GGraph.eventList({
		}, function(data){
			if(data.data!=null){
				
				var chartData = [];
				
				for(var i=0;i<data.data.length;i++){
					var columns = data.data[i];
					chartData.push([columns[0],{v: parseInt(columns[2]), f:parseInt(columns[2])}]);
				}
				
				var chart5 = {};
				chart5.type="Table";
			
			
				chart5.data = chartData;
				chart5.data.unshift(['Path','Number of Access']);
				console.log(chart5.data);
		    
				chart5.options = {
						title: 'Api Exception',
						displayExactValues: true,
						width: 500,
						height: 300,
						chartArea: {left:40,top:40,bottom:0,height:"100%"}
			    	};

				$scope.chart5 = chart5;
			}else{
				$scope.msg5 = "This chart is not available at the moment. An error in Google Analytics occured. Please try again later or reload the page.";
			}
		});
		
		GGraph.exceptionList({
		}, function(data){
			
			if(data.data!=null){
				var excData = [];
				
				for(var i=0;i<data.data.length;i++){
					var columns = data.data[i];
					excData.push([columns[0],{v: parseInt(columns[1]), f:parseInt(columns[1])}]);
				}
				
				var chart7 = {};
				chart7.type="Table";
			
			
				chart7.data = excData;
				chart7.data.unshift((['Api Exception','Total Number']));
				console.log(chart7.data);
		    
				chart7.options = {
						title: 'Api Exception',
						displayExactValues: true,
						width: 500,
						height: 300,
						chartArea: {left:40,top:40,bottom:0,height:"100%"}
			    	};

				$scope.chart7 = chart7;
			}else{
				$scope.msg7 = "This chart is not available at the moment. An error in Google Analytics occured. Please try again later or reload the page.";
			}
		});
		
	}
]);

