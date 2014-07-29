'use strict';
var directives = angular.module('apiManager.directives', []);

directives.directive('gravatar', ['Gravatar', '$compile',
    function (Gravatar, $compile) {
        return {

            link: function (scope, element, attrs) {
                attrs.$observe('gravatar', function (email) {
                    if (email) {
                        var html = '<img class="img-responsive img-circle" style="width: ' + attrs.size + 'px;height:' + attrs.size + 'px;" src="' + Gravatar.picture(attrs.size, email) + '" />';
                        var e = $compile(html)(scope);
                        element.replaceWith(e);
                    }

                });
            }
        };
    }
]);

directives.directive('showValidation', [
    function() {
	    return {
	        restrict: "A",
	        link: function(scope, element, attrs, ctrl) {
	
	            if (element.get(0).nodeName.toLowerCase() === 'form') {
	                element.find('.form-group').each(function(i, formGroup) {
	                    showValidation(angular.element(formGroup), attrs.showValidation);
	                });
	            } else {
	                showValidation(element, attrs.showValidation);
	            }
	
	            function showValidation(formGroupEl, msg) {
	                var input = formGroupEl.find('input[ng-model],textarea[ng-model],select[ng-model]');
	                if (input.length > 0) {
	                    scope.$watch(function() {
	                        return input.hasClass('ng-invalid');// && input.hasClass('ng-dirty');
	                    }, function(isInvalid) {
	                        formGroupEl.toggleClass('has-error', isInvalid);
	                        var errmsg = formGroupEl.find('#err');
	                        errmsg.remove();
	                        if (isInvalid) {
		                    	var label = formGroupEl.find('label');
	                        	msg = label.text();
	                        	if (msg) {
	                        		formGroupEl.append('<span id="err" class="control-label">'+constructMsg(msg, input)+'</span>');
	                        	}
	                        }
	                    });
	                }
	            }
	            function constructMsg(fieldLabel, input) {
	            	if (input.hasClass('ng-invalid-required')) return fieldLabel + ' is required';
	            	return null;
	            }
	            
	        }
	    };
    }
]);

directives.directive('integer', function() {
    return {
        restrict: 'A',
        require: '?ngModel',
        link: function(scope, elem, attr, ngModel) {
            if (!ngModel)
                return;

            function isValid(val) {
                if (val === "")
                    return true;

                var asInt = parseInt(val, 10);
                if (asInt === NaN || asInt.toString() !== val) {
                    return false;
                }

                return true;
            }

            var prev = scope.$eval(attr.ngModel);
            ngModel.$parsers.push(function (val) {
                // short-circuit infinite loop
                if (val === prev)
                    return val;

                if (!isValid(val)) {
                    ngModel.$setViewValue(prev);
                    ngModel.$render();
                    return prev;
                }

                prev = val;
                return val;
            });
        }
    };
});