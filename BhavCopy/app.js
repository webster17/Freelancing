
const app = angular.module('stockMarketApp', ['ngRoute', 'stockMarketAppController']);

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
	}).when('/set-new-password', {
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
	}).when('/add-user', {
		templateUrl: 'add-user.html',
		controller: 'addUserController',
		resolve: {
			initialData: function (appService, $location) {
				appService.isInitialized($location.url())
			}
		}
	}).when('/update-user/:Id', {
		templateUrl: 'src/update_user.html',
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
	}).when('/updateProfile/:Id', {
		templateUrl: 'src/update-profile.html',
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

	// let _token = '';

	// if ($window.localStorage['logedInUser'] != undefined) {
	//     _token = JSON.parse($window.localStorage['logedInUser']).token;
	// }
	// let _headers = { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + _token, 'Accept': 'application/json' };


	if ($window.localStorage['logedInUser'] != undefined)
		if(JSON.parse($window.localStorage['logedInUser']).hasOwnProperty('token'))
			$http.defaults.headers.common.Authorization = 'Bearer ' + JSON.parse($window.localStorage['logedInUser']).token;

	this.login = function (credentials) { return $http({ method: 'POST', url: _baseUrl + '/auth/login', data: credentials }); };
	this.getAuthenticated = function () { return $http({ method: 'GET', url: _baseUrl + '/auth/getAuthenticated' }); };
	this.findAccount = function (email) { return $http({ method: 'GET', url: _baseUrl + '/findAccount/' + email }); };
	this.setNewPassword = function (credentials) { return $http({ method: 'POST', url: _baseUrl + '/setNewPassword', data: credentials }); };

	this.getAllUser = function () { return $http({ method: 'GET', url: _baseUrl + '/user', headers: _headers }); };
	this.getUser = function (id) { return $http({ method: 'GET', url: _baseUrl + '/user/' + id, headers: _headers }); };
	this.addUser = function (user) { return $http({ method: 'POST', url: _baseUrl + '/user', headers: _headers, data: user }); };
	this.updateUser = function (id, user) { return $http({ method: 'PUT', url: _baseUrl + '/user' + id, headers: _headers, data: user }); };
	this.deleteUser = function (id) { return $http({ method: 'DELETE', url: _baseUrl + '/user/' + id, headers: _headers }); };

	this.getAllProfile = function () { return $http({ method: 'GET', url: _baseUrl + '/profile', headers: _headers }); };
	this.getProfile = function (id) { return $http({ method: 'GET', url: _baseUrl + '/profile/' + id, headers: _headers }); };
	this.addProfile = function (profile) { return $http({ method: 'POST', url: _baseUrl + '/profile', headers: _headers, data: profile }); };
	this.updateProfile = function (id, profile) { return $http({ method: 'PUT', url: _baseUrl + '/profile' + id, headers: _headers, data: profile }); };
	this.deleteProfile = function (id) { return $http({ method: 'DELETE', url: _baseUrl + '/profile/' + id, headers: _headers }); };

	this.getAllScrip = function () { return $http({ method: 'GET', url: _baseUrl + '/scrip', headers: _headers }); };

	return this;
}]);
app.service('authService', ['$window', 'apiService', function ($window, apiService) {
	this.check = function (authorizedCallback, unAuthorizedCallback) {
		var user = {
			isAuthenticated: false
		};

		if ($window.localStorage['logedInUser'] != undefined) {
			var loginCredential = JSON.parse($window.localStorage['logedInUser']);
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
		}else {
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
app.service('appService', ['$window', '$location', 'authService', function ($window, $location, authService) {
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
});

app.directive('allowNumbersOnly', () => {
	return {
		restrict: "A",
		link: function (scope, element, attrs) {
			element.bind("keydown", function (event) {
				if (!((event.keyCode > 47 && event.keyCode < 58) || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 13)) {
					event.preventDefault();
					return false;
				}
			});
		}
	}
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

