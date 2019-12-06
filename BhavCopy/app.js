
const app = angular.module('stockMarketApp', ['ngRoute', 'ngFileUpload', 'stockMarketAppController']);

// const { remote } = require('electron');

app.config(['$routeProvider', function ($routeProvider) {
	$routeProvider.when('/', {
		templateUrl: 'loading.html',
		controller: 'appLoaderController'
	}).when('/login', {
		templateUrl: 'login.html',
		controller: 'loginController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/find-account', {
		templateUrl: 'find-account.html',
		controller: 'findAccountController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/set-new-password/:email/:otp', {
		templateUrl: 'set-new-password.html',
		controller: 'setNewPasswordController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/dashboard', {
		templateUrl: 'dashboard.html',
		controller: 'dashboardController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/logout', {
		template: '',
		controller: 'logoutController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/update-data', {
		templateUrl: 'update-data.html',
		controller: 'updateDataController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/change-password', {
		templateUrl: 'change-password.html',
		controller: 'changePasswordController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/change-app-pin', {
		templateUrl: 'change-app-pin.html',
		controller: 'changeAppPinController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/add-user', {
		templateUrl: 'add-user.html',
		controller: 'addUserController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/update-user/:Id', {
		templateUrl: 'update-user.html',
		controller: 'updateUserController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/view-user', {
		templateUrl: 'view-user.html',
		controller: 'viewUserController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/add-profile', {
		templateUrl: 'add-profile.html',
		controller: 'addProfileController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/update-profile/:Id', {
		templateUrl: 'update-profile.html',
		controller: 'updateProfileController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/view-profile', {
		templateUrl: 'view-profile.html',
		controller: 'viewProfileController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/view-scrip', {
		templateUrl: 'view-scrip.html',
		controller: 'viewScripController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/price-adjustment', {
		templateUrl: 'price-adjustment.html',
		controller: 'priceAdjustmentController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).otherwise({
		redirectTo: '/'
	});
}]);

app.service('apiService', ['$http', '$window', function ($http, $window) {
	let _baseUrl = 'http://localhost:8090';

	this.initializeToken = (token) => {
		$http.defaults.headers.common.Authorization = 'Bearer ' + token;
	}

	this.login = function (credentials) { return $http({ method: 'POST', url: _baseUrl + '/auth/login', data: credentials }); };
	this.sendOTP = function (credentials) { return $http({ method: 'POST', url: _baseUrl + '/auth/sendOTP', data: credentials }); };
	this.setNewPassword = function (credentials) { return $http({ method: 'POST', url: _baseUrl + '/auth/setNewPassword', data: credentials }); };
	this.updatePassword = (credential) => { return $http({ method: 'POST', url: _baseUrl + '/auth/updatePassword', data: credential }); };
	this.getAuthenticated = function () { return $http({ method: 'GET', url: _baseUrl + '/auth/getAuthenticated' }); };

	this.updateData = (date, rawData) => { return $http({ method: 'POST', url: _baseUrl + '/record/update/' + date, data: rawData }); };
	this.getFilteredRecordData = (scripId, filterData) => { return $http({ method: 'POST', url: _baseUrl + '/record/filter/' + scripId, data: filterData }); };
	this.priceAdjustment = (data) => { return $http({ method:'POST', url: _baseUrl+'/record/price-adjustment', data: data}); }; 

	this.getAllUser = function () { return $http({ method: 'GET', url: _baseUrl + '/user' }); };
	this.getUser = function (id) { return $http({ method: 'GET', url: _baseUrl + '/user/' + id }); };
	this.addUser = function (user) { return $http({ method: 'POST', url: _baseUrl + '/user', data: user }); };
	this.updateUser = function (id, user) { return $http({ method: 'PUT', url: _baseUrl + '/user/' + id, data: user }); };
	this.deleteUser = function (id) { return $http({ method: 'DELETE', url: _baseUrl + '/user/' + id }); };

	this.getAllProfile = function () { return $http({ method: 'GET', url: _baseUrl + '/profile' }); };
	this.getProfile = function (id) { return $http({ method: 'GET', url: _baseUrl + '/profile/' + id }); };
	this.addProfile = function (profile) { return $http({ method: 'POST', url: _baseUrl + '/profile', data: profile }); };
	this.updateProfile = function (id, profile) { return $http({ method: 'PUT', url: _baseUrl + '/profile/' + id, data: profile }); };
	this.deleteProfile = function (id) { return $http({ method: 'DELETE', url: _baseUrl + '/profile/' + id }); };

	this.getAllScrip = function () { return $http({ method: 'GET', url: _baseUrl + '/scrip' }); };

	return this;
}]);
app.service('authService', ['$window', 'apiService', function ($window, apiService) {
	this.check = function (authorizedCallback, unAuthorizedCallback) {
		var user = {
			isAuthenticated: false
		};

		if ($window.localStorage['logedInUser'] != undefined) {
			var loginCredential = JSON.parse($window.localStorage['logedInUser']);

			if (JSON.parse($window.localStorage['logedInUser']).hasOwnProperty('token'))
				apiService.initializeToken(JSON.parse($window.localStorage['logedInUser']).token);

			if (loginCredential.isAuthenticated && loginCredential.rememberMe) {
				apiService.getAuthenticated().then(function (response) {
					let data = response.data;
					user.isAuthenticated = true;
					user.id = data.id;
					user.userName = data.userName;
					user.email = data.email;
					user.isAdmin = data.admin;
					user.rememberMe = loginCredential.rememberMe;
					user.token = loginCredential.token;

					apiService.initializeToken(loginCredential.token);
					$window.localStorage['logedInUser'] = JSON.stringify(user);
					authorizedCallback(user);
				}, function (response) {
					$window.localStorage.removeItem('logedInUser');
					unAuthorizedCallback();
				});
			} else {
				$window.localStorage.removeItem('logedInUser');
				unAuthorizedCallback();
			}
		} else {
			$window.localStorage.removeItem('logedInUser');
			unAuthorizedCallback();
		}
	}
	this.attempt = function (loginCredential, authorizedCallback, unAuthorizedCallback) {
		var user = {
			isAuthenticated: false
		};

		apiService.login(loginCredential).then(function (response, status, headers, config) {
			user.isAuthenticated = true;
			let data = response.data;
			user.id = data.id;
			user.userName = data.userName;
			user.email = data.email;
			user.isAdmin = data.admin;
			if (loginCredential.rememberMe)
				user.rememberMe = true;
			else
				user.rememberMe = false;
			user.token = data.token;

			apiService.initializeToken(data.token);
			$window.localStorage['logedInUser'] = JSON.stringify(user);
			authorizedCallback(user);
		}, function (response, status, headers, config) {
			console.log(response);
			$window.localStorage.removeItem('logedInUser');
			unAuthorizedCallback();
		});

		// apiService.login().then(function (response) {

		// 	$window.localStorage['logedInUser'] = JSON.stringify(response);
		// 	//return true
		// }, function (errorResponse) {
		// 	//something went wrong
		// 	//return false
		// }).then(function () {
		// 	//completed
		// });
	}
	this.release = function () {
		$window.localStorage.removeItem('logedInUser');
	}
	return this;
}]);
app.service('appService', ['$window', '$rootScope', '$location', function ($window, $rootScope, $location) {
	let initialized = false;

	this.isInitialized = (url) => {
		if (!initialized) {
			$window.localStorage['intendedUrl'] = $location.url();
			$location.path('/');
		}
		return initialized;
	}

	this.init = () => {
		initialized = true;
		if ($window.localStorage['applicationPin'] != undefined)
			$rootScope.applicationPin = $window.localStorage['applicationPin'];
		else
			$rootScope.applicationPin = "123456";
	}

	return this;
}]);


app.filter('gender', function () {
	return function (isMale) {
		var gender = 'Female';
		if (isMale)
			gender = 'Male';
		return gender;
	}
}).filter('validationClass', function () {
	return function (isValid) {
		if (isValid == 0)
			return '';
		else if (isValid == 1)
			return 'is-invalid';
		return 'is-valid';
	}
}).filter('bodyClass', function () {
	return function (bodyType) {
		if (bodyType == 2) {
			return 'hold-transition sidebar-mini layout-fixed layout-navbar-fixed';
		}
		else if (bodyType == 1) {
			return 'hold-transition login-page';
		}
		return '';
	}
}).filter('buttonClass', function(){
	return function (maColour) {
		if (maColour == "green") {
			return 'btn-success';
		}
		else if (maColour == "red") {
			return 'btn-danger';
		}
		else if (maColour == "grey") {
			return 'btn-secondary';
		}
		return '';
	}
});

app.directive('allowNumbersOnly', () => {
	return {
		restrict: "A",
		link: function (scope, element, attrs) {
			element.bind("keydown", function (event) {
				if (!((event.keyCode > 47 && event.keyCode < 58) || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 13 || event.keyCode == 17)) {
					event.preventDefault();
					return false;
				}
			});
		}
	}
});

app.directive('allowLettersAndSpaceOnly', function () {
	return {
		restrict: "A",
		link: function (scope, element, attrs) {
			element.bind("keydown", function (event) {
				if (!((event.keyCode > 64 && event.keyCode < 91) || event.keyCode == 32 || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 13 || event.keyCode == 17)) {
					event.preventDefault();
					return false;
				}
			});
		}
	}
})

app.directive('eventFocus', function (focus) {
	return function (scope, elem, attr) {
		elem.on(attr.eventFocus, function () {
			focus(attr.eventFocusId);
		});

		// Removes bound events in the element itself
		// when the scope is destroyed
		scope.$on('$destroy', function () {
			elem.off(attr.eventFocus);
		});
	};
});

app.factory('focus', function ($timeout, $window) {
	return function (id) {
		// timeout makes sure that it is invoked after any other event has been triggered.
		// e.g. click events that need to run before the focus or
		// inputs elements that are in a disabled state but are enabled when those events
		// are triggered.
		$timeout(function () {
			var element = $window.document.getElementById(id);
			if (element)
				element.focus();
		});
	};
});

app.directive('validFile', function () {
	return {
		require: 'ngModel',
		link: function (scope, elem, attrs, ngModel) {
			var validFormats = ['csv'];
			elem.bind('change', function () {
				validImage(false);
				scope.$apply(function () {
					ngModel.$render();
				});
			});
			ngModel.$render = function () {
				ngModel.$setViewValue(elem.val());
			};
			function validImage(bool) {
				ngModel.$setValidity('extension', bool);
			}
			ngModel.$parsers.push(function (value) {
				var ext = value.substr(value.lastIndexOf('.') + 1);
				if (ext == '') return;
				if (validFormats.indexOf(ext) == -1) {
					return value;
				}
				validImage(true);
				return value;
			});
		}
	};
});

app.directive('autoActive', ['$location', function ($location) {
	return {
		restrict: 'A',
		scope: false,
		link: function (scope, element) {
			function setActive() {
				var path = $location.path();
				if (path) {
					angular.forEach(element.find('li'), function (li) {
						var anchor = li.querySelector('a');
						if (anchor.href.match('#' + path + '(?=\\?|$)')) {
							angular.element(li).addClass('active');
						} else {
							angular.element(li).removeClass('active');
						}
					});
				}
			}

			setActive();

			scope.$on('$locationChangeSuccess', setActive);
		}
	}
}]);

app.directive('compareTo', () => {
	return {
		require: "ngModel",
		scope: {
			otherModelValue: "=compareTo"
		},
		link: function (scope, element, attributes, ngModel) {

			ngModel.$validators.compareTo = function (modelValue) {
				return modelValue == scope.otherModelValue;
			};

			scope.$watch("otherModelValue", function () {
				ngModel.$validate();
			});
		}
	};
});


app.directive('ngConfirmClick', [
	function(){
			return {
					link: function (scope, element, attr) {
							var msg = attr.ngConfirmClick || "Are you sure?";
							var clickAction = attr.confirmedClick;
							element.bind('click',function (event) {
									if ( window.confirm(msg) ) {
											scope.$eval(clickAction)
									}
							});
					}
			};
}]);

