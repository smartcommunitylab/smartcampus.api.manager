'use strict';
var services = angular.module('apiManager.services', ['ngResource', 'ngCookies']);

services.factory('Auth', ['$resource', '$http', '$window',
    function($resource, $http, $window){
		
		return {
			
			gaLogin: function (error){
	            	$http.get('api/oauth/google/auth')
	            	.success(function(data){
	                	$window.location.href = data.data;
	                	
	                }).error(function (data) {
	                	 error(data.error);
	                });
	            },
	         
	         redirectLogin: function(error){
	        	 $http.get('login')
	            	.success(function(data){
	                	$window.location.href = data.data;
	                	
	                }).error(function (data) {
	                	 error(data.error);
	                });
	         }
        	
		};
	}
]);

services.factory('Api', ['$resource',
    function($resource){
		return $resource('api/id/:appId', {}, {
			create:{
				method: 'POST',
                url: 'api/add'
			},
			update:{
				method: 'PUT',
                url: 'api/update'
			},
			remove:{
				method: 'DELETE',
                url: 'api/delete/:apiId'
			},
			list:{
				method: 'GET',
				url: 'api/ownerId'
			},
			getApi:{
				method: 'GET',
				url: 'api/id/:apiId'
			},
			getApiName:{
				method: 'GET',
				url: 'api/name/:apiId'
			},
			listApiName:{
				method: 'GET',
				url: 'api/ownerId/api'
			},
			createStatus:{
				method: 'POST',
                url: 'api/add/:apiId/status'
			},
			updateStatus:{
				method: 'PUT',
                url: 'api/update/:apiId/status'
			},
			removeStatus:{
				method: 'DELETE',
                url: 'api/delete/:apiId/status/:statusName'
			},
			getStatus:{
				method: 'GET',
				url: 'api/:apiId/status/:statusName'
			},
			getStatusList:{
				method: 'GET',
				url: 'api/:apiId/status'
			}
		});
	}
]);

services.factory('Resource', ['$resource',
    function($resource){
		return $resource('api/:apiId/resource/:resourceId', {}, {
			create:{
				method: 'POST',
				url: 'api/add/:apiId/resource'
			},
			update:{
				method: 'PUT',
				url: 'api/update/:apiId/resource'
            },
            remove:{
            	method: 'DELETE',
            	url: 'api/delete/:apiId/resource/:resourceId'
            },
            getResource:{
				method: 'GET',
				url: 'api/:apiId/resource/:resourceId'
			},
			getPolicyResource:{
				method: 'GET',
				url: 'api/:apiId/resource/:resourceId/policy/:policyId'
			},
            createSpikeArrestResource:{
				method: 'POST',
				url: 'api/:apiId/resource/:resourceId/add/policy/spikeArrest'
            },
            createQuotaResource:{
				method: 'POST',
				url: 'api/:apiId/resource/:resourceId/add/policy/quota'
            },
            createIPResource:{
				method: 'POST',
				url: 'api/:apiId/resource/:resourceId/add/policy/ip'
            },
            createVAppKeyResource:{
				method: 'POST',
				url: 'api/:apiId/resource/:resourceId/add/policy/appkey'
            },
            createOAuthResource:{
				method: 'POST',
				url: 'api/:apiId/resource/:resourceId/add/policy/oauth'
            },
            createSAMLResource:{
				method: 'POST',
				url: 'api/:apiId/resource/:resourceId/add/policy/saml'
            },
            updateSpikeArrestResource:{
				method: 'PUT',
				url: 'api/:apiId/resource/:resourceId/update/policy/spikeArrest'
            },
            updateQuotaResource:{
				method: 'PUT',
				url: 'api/:apiId/resource/:resourceId/update/policy/quota'
            },
            updateIPResource:{
				method: 'PUT',
				url: 'api/:apiId/resource/:resourceId/update/policy/ip'
            },
            updateVAppKeyResource:{
				method: 'PUT',
				url: 'api/:apiId/resource/:resourceId/update/policy/appkey'
            },
            updateOAuthResource:{
				method: 'PUT',
				url: 'api/:apiId/resource/:resourceId/update/policy/oauth'
            },
            updateSAMLResource:{
				method: 'PUT',
				url: 'api/:apiId/resource/:resourceId/update/policy/saml'
            },
			removePolicy:{
            	method: 'DELETE',
            	url: 'api/:apiId/resource/:resourceId/delete/:policyId'
            }
		});
	}
]);

services.factory('App', ['$resource',
    function($resource){
		return $resource('api/app/:appId', {}, {
			create:{
				method: 'POST',
				url: 'api/app/add'
			},
			update:{
				method: 'PUT',
				url: 'api/app/update'
			},
			remove:{
				method: 'DELETE',
				url: 'api/app/delete/:appId'
			},
			getApp:{
				method: 'GET',
				url: 'api/app/:appId'
			},
			list:{
				method: 'GET',
				url: 'api/app/list'
			},
			updateApiData:{
				method: 'PUT',
				url: 'api/app/update/apidata'
			},
			removeApiData:{
				method: 'DELETE',
				url: 'api/app/delete/:appId/api/:apiId'
			}
		});
	}
]);

services.factory('Policy', ['$resource',
    function($resource){
		return $resource('api/:apiId/policy/:policyId', {}, {
			createSpikeArrest:{
				method: 'POST',
				url: 'api/add/:apiId/policy/spikeArrest'
			},
			createQuota:{
				method: 'POST',
				url: 'api/add/:apiId/policy/quota'
			},
			createIP:{
				method: 'POST',
				url: 'api/add/:apiId/policy/ip'
			},
			createVAppKey:{
				method: 'POST',
				url: 'api/add/:apiId/policy/appkey'
			},
			createOAuth:{
				method: 'POST',
				url: 'api/add/:apiId/policy/oauth'
			},
			createSAML:{
				method: 'POST',
				url: 'api/add/:apiId/policy/saml'
			},
			updateSpikeArrest:{
				method: 'PUT',
				url: 'api/update/:apiId/policy/spikeArrest'
			},
			updateQuota:{
				method: 'PUT',
				url: 'api/update/:apiId/policy/quota'
			},
			updateIP:{
				method: 'PUT',
				url: 'api/update/:apiId/policy/ip'
			},
			updateVAppKey:{
				method: 'PUT',
				url: 'api/update/:apiId/policy/appkey'
			},
			updateOAuth:{
				method: 'PUT',
				url: 'api/update/:apiId/policy/oauth'
			},
			updateSAML:{
				method: 'PUT',
				url: 'api/update/:apiId/policy/saml'
			},
			remove:{
				method: 'DELETE',
				url: 'api/delete/:apiId/policy/:policyId'
			},
			getPolicy:{
				method: 'GET',
				url: 'api/:apiId/policy/:policyId'
			}
		});
	}
]);

services.factory('Stat', ['$resource',
    function($resource){
		return $resource('api/ga', {}, {
			saveTrID:{
				method: 'POST',
				url: 'api/ga'
			},
			isDashEnabled:{
				method: 'GET',
				url: 'api/ga/isEnabled'
			}
		});
	}
]);

services.factory('GGraph', ['$resource',
    function($resource){
		return $resource('api/oauth/google', {}, {
			eventApiLabel:{
				method: 'GET',
				url: 'api/oauth/google/eventlabel/:apiName'
			},
			eventApiAction:{
				method: 'GET',
				url: 'api/oauth/google/eventaction/:apiName'
			},
			exceptionApi:{
				method: 'GET',
				url: 'api/oauth/google/exception/:apiName'
			},
			eventList:{
				method: 'GET',
				url: 'api/oauth/google/event/list'
			},
			exceptionList:{
				method: 'GET',
				url: 'api/oauth/google/exception/list'
			},
			enabled:{
				method: 'GET',
				url: 'api/oauth/google/logged'
			}
		});
	}
]);

services.factory('Service', ['$resource',
    function($resource){
		return $resource('api/ga', {}, {
			categories:{
				 method: 'GET',
	             url: 'http://localhost:8080/openservice/api/category/',
			},
			organizations:{
				 method: 'GET',
	             url: 'http://localhost:8080/openservice/api/org/my',
			},
			publish:{
				method: 'POST',
                url: 'http://localhost:8080/openservice/api/service/add'
			},
			
		});
	}
]);

services.factory('Gravatar', ['$http', '$rootScope',
    function ($http, $rootScope) {

        return {
            picture: function (size, email) {
                var MD5 = function (s) {
                    function L(k, d) {
                        return (k << d) | (k >>> (32 - d))
                    }

                    function K(G, k) {
                        var I, d, F, H, x;
                        F = (G & 2147483648);
                        H = (k & 2147483648);
                        I = (G & 1073741824);
                        d = (k & 1073741824);
                        x = (G & 1073741823) + (k & 1073741823);
                        if (I & d) {
                            return (x ^ 2147483648 ^ F ^ H)
                        }
                        if (I | d) {
                            if (x & 1073741824) {
                                return (x ^ 3221225472 ^ F ^ H)
                            } else {
                                return (x ^ 1073741824 ^ F ^ H)
                            }
                        } else {
                            return (x ^ F ^ H)
                        }
                    }

                    function r(d, F, k) {
                        return (d & F) | ((~d) & k)
                    }

                    function q(d, F, k) {
                        return (d & k) | (F & (~k))
                    }

                    function p(d, F, k) {
                        return (d ^ F ^ k)
                    }

                    function n(d, F, k) {
                        return (F ^ (d | (~k)))
                    }

                    function u(G, F, aa, Z, k, H, I) {
                        G = K(G, K(K(r(F, aa, Z), k), I));
                        return K(L(G, H), F)
                    }

                    function f(G, F, aa, Z, k, H, I) {
                        G = K(G, K(K(q(F, aa, Z), k), I));
                        return K(L(G, H), F)
                    }

                    function D(G, F, aa, Z, k, H, I) {
                        G = K(G, K(K(p(F, aa, Z), k), I));
                        return K(L(G, H), F)
                    }

                    function t(G, F, aa, Z, k, H, I) {
                        G = K(G, K(K(n(F, aa, Z), k), I));
                        return K(L(G, H), F)
                    }

                    function e(G) {
                        var Z;
                        var F = G.length;
                        var x = F + 8;
                        var k = (x - (x % 64)) / 64;
                        var I = (k + 1) * 16;
                        var aa = Array(I - 1);
                        var d = 0;
                        var H = 0;
                        while (H < F) {
                            Z = (H - (H % 4)) / 4;
                            d = (H % 4) * 8;
                            aa[Z] = (aa[Z] | (G.charCodeAt(H) << d));
                            H++
                        }
                        Z = (H - (H % 4)) / 4;
                        d = (H % 4) * 8;
                        aa[Z] = aa[Z] | (128 << d);
                        aa[I - 2] = F << 3;
                        aa[I - 1] = F >>> 29;
                        return aa
                    }

                    function B(x) {
                        var k = "",
                            F = "",
                            G, d;
                        for (d = 0; d <= 3; d++) {
                            G = (x >>> (d * 8)) & 255;
                            F = "0" + G.toString(16);
                            k = k + F.substr(F.length - 2, 2)
                        }
                        return k
                    }

                    function J(k) {
                        k = k.replace(/rn/g, "n");
                        var d = "";
                        for (var F = 0; F < k.length; F++) {
                            var x = k.charCodeAt(F);
                            if (x < 128) {
                                d += String.fromCharCode(x)
                            } else {
                                if ((x > 127) && (x < 2048)) {
                                    d += String.fromCharCode((x >> 6) | 192);
                                    d += String.fromCharCode((x & 63) | 128)
                                } else {
                                    d += String.fromCharCode((x >> 12) | 224);
                                    d += String.fromCharCode(((x >> 6) & 63) | 128);
                                    d += String.fromCharCode((x & 63) | 128)
                                }
                            }
                        }
                        return d
                    }
                    var C = Array();
                    var P, h, E, v, g, Y, X, W, V;
                    var S = 7,
                        Q = 12,
                        N = 17,
                        M = 22;
                    var A = 5,
                        z = 9,
                        y = 14,
                        w = 20;
                    var o = 4,
                        m = 11,
                        l = 16,
                        j = 23;
                    var U = 6,
                        T = 10,
                        R = 15,
                        O = 21;
                    s = J(s);
                    C = e(s);
                    Y = 1732584193;
                    X = 4023233417;
                    W = 2562383102;
                    V = 271733878;
                    for (P = 0; P < C.length; P += 16) {
                        h = Y;
                        E = X;
                        v = W;
                        g = V;
                        Y = u(Y, X, W, V, C[P + 0], S, 3614090360);
                        V = u(V, Y, X, W, C[P + 1], Q, 3905402710);
                        W = u(W, V, Y, X, C[P + 2], N, 606105819);
                        X = u(X, W, V, Y, C[P + 3], M, 3250441966);
                        Y = u(Y, X, W, V, C[P + 4], S, 4118548399);
                        V = u(V, Y, X, W, C[P + 5], Q, 1200080426);
                        W = u(W, V, Y, X, C[P + 6], N, 2821735955);
                        X = u(X, W, V, Y, C[P + 7], M, 4249261313);
                        Y = u(Y, X, W, V, C[P + 8], S, 1770035416);
                        V = u(V, Y, X, W, C[P + 9], Q, 2336552879);
                        W = u(W, V, Y, X, C[P + 10], N, 4294925233);
                        X = u(X, W, V, Y, C[P + 11], M, 2304563134);
                        Y = u(Y, X, W, V, C[P + 12], S, 1804603682);
                        V = u(V, Y, X, W, C[P + 13], Q, 4254626195);
                        W = u(W, V, Y, X, C[P + 14], N, 2792965006);
                        X = u(X, W, V, Y, C[P + 15], M, 1236535329);
                        Y = f(Y, X, W, V, C[P + 1], A, 4129170786);
                        V = f(V, Y, X, W, C[P + 6], z, 3225465664);
                        W = f(W, V, Y, X, C[P + 11], y, 643717713);
                        X = f(X, W, V, Y, C[P + 0], w, 3921069994);
                        Y = f(Y, X, W, V, C[P + 5], A, 3593408605);
                        V = f(V, Y, X, W, C[P + 10], z, 38016083);
                        W = f(W, V, Y, X, C[P + 15], y, 3634488961);
                        X = f(X, W, V, Y, C[P + 4], w, 3889429448);
                        Y = f(Y, X, W, V, C[P + 9], A, 568446438);
                        V = f(V, Y, X, W, C[P + 14], z, 3275163606);
                        W = f(W, V, Y, X, C[P + 3], y, 4107603335);
                        X = f(X, W, V, Y, C[P + 8], w, 1163531501);
                        Y = f(Y, X, W, V, C[P + 13], A, 2850285829);
                        V = f(V, Y, X, W, C[P + 2], z, 4243563512);
                        W = f(W, V, Y, X, C[P + 7], y, 1735328473);
                        X = f(X, W, V, Y, C[P + 12], w, 2368359562);
                        Y = D(Y, X, W, V, C[P + 5], o, 4294588738);
                        V = D(V, Y, X, W, C[P + 8], m, 2272392833);
                        W = D(W, V, Y, X, C[P + 11], l, 1839030562);
                        X = D(X, W, V, Y, C[P + 14], j, 4259657740);
                        Y = D(Y, X, W, V, C[P + 1], o, 2763975236);
                        V = D(V, Y, X, W, C[P + 4], m, 1272893353);
                        W = D(W, V, Y, X, C[P + 7], l, 4139469664);
                        X = D(X, W, V, Y, C[P + 10], j, 3200236656);
                        Y = D(Y, X, W, V, C[P + 13], o, 681279174);
                        V = D(V, Y, X, W, C[P + 0], m, 3936430074);
                        W = D(W, V, Y, X, C[P + 3], l, 3572445317);
                        X = D(X, W, V, Y, C[P + 6], j, 76029189);
                        Y = D(Y, X, W, V, C[P + 9], o, 3654602809);
                        V = D(V, Y, X, W, C[P + 12], m, 3873151461);
                        W = D(W, V, Y, X, C[P + 15], l, 530742520);
                        X = D(X, W, V, Y, C[P + 2], j, 3299628645);
                        Y = t(Y, X, W, V, C[P + 0], U, 4096336452);
                        V = t(V, Y, X, W, C[P + 7], T, 1126891415);
                        W = t(W, V, Y, X, C[P + 14], R, 2878612391);
                        X = t(X, W, V, Y, C[P + 5], O, 4237533241);
                        Y = t(Y, X, W, V, C[P + 12], U, 1700485571);
                        V = t(V, Y, X, W, C[P + 3], T, 2399980690);
                        W = t(W, V, Y, X, C[P + 10], R, 4293915773);
                        X = t(X, W, V, Y, C[P + 1], O, 2240044497);
                        Y = t(Y, X, W, V, C[P + 8], U, 1873313359);
                        V = t(V, Y, X, W, C[P + 15], T, 4264355552);
                        W = t(W, V, Y, X, C[P + 6], R, 2734768916);
                        X = t(X, W, V, Y, C[P + 13], O, 1309151649);
                        Y = t(Y, X, W, V, C[P + 4], U, 4149444226);
                        V = t(V, Y, X, W, C[P + 11], T, 3174756917);
                        W = t(W, V, Y, X, C[P + 2], R, 718787259);
                        X = t(X, W, V, Y, C[P + 9], O, 3951481745);
                        Y = K(Y, h);
                        X = K(X, E);
                        W = K(W, v);
                        V = K(V, g)
                    }
                    var i = B(Y) + B(X) + B(W) + B(V);
                    return i.toLowerCase()
                };

                var size = size || 80;
                return 'http://www.gravatar.com/avatar/' + MD5(email) + '.jpg?s=' + size;

            }
        }
    }
]);

services.factory('Bread', ['$location',
    function ($location) {
        var loc = $location.path().split('/');
        loc.splice(0, 1);
        return loc;
    }
]);
