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
    		//ownerId : '1'
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

app.controller('startCtrl', ['$scope', '$location', 'Stat',
    function($scope, $location, Stat){
		Stat.isDashEnabled({
		}, function(data){
			if(data.data===true){
				$location.path('/dashboard/login');
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
		        chartArea: {left:10,top:10,bottom:0,height:"100%"}
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
		    
		    /*chart2.cols = [
		                ['string','Path'],
		                ['number', 'Number of Access']
		    ];*/

		    /*chart2.rows = [
		                   ['Api1/resource/1', {v: 20, f:20}],
		    		       ['Api2' ,{v: 30, f:30}],
		                   ['Api2/resource3', {v: 20, f:20}],
		                   ['Api3', {v: 80, f:80}]
		    ];*/
		    
		    chart2.data = [
		                   ['Path','Number of Access'],
		    		       ['Api1/resource/1', {v: 20, f:20}],
		    		       ['Api2' ,{v: 30, f:30}],
		                   ['Api2/resource3', {v: 20, f:20}],
		                   ['Api3', {v: 80, f:80}]
		    ];
		    
		    chart2.options = {
			        displayExactValues: true,
			        width: 500,
			        height: 300,
			        chartArea: {left:10,top:10,bottom:0,height:"100%"}
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

