let stockMarketAppController = angular.module('stockMarketAppController', []);

stockMarketAppController.controller('appLoaderController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 0;
	$rootScope.passwordRegex = "/^((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[$@$!%*#?&])).{6,20}$/";
	$rootScope.passwordValidCharacterRegex = "/^[a-zA-Z0-9$@$!%*#?&]*$/";
	appService.init();
	$rootScope.getShowableFeedback = function (message, hasError) {
		let feedback = {};
		if (message != '') {
			feedback.showable = true
			if (hasError)
				feedback.class = "text-danger";
			else
				feedback.class = "text-success";
		}
		else {
			feedback.class = '';
			feedback.showable = false;
		}
		feedback.message = message;
		return feedback;
	};

	authService.check(function (user) {
		$timeout(function () {
			$rootScope.logedInUser = user;
			if ($window.localStorage['intendedUrl'] != undefined) {
				var intendedUrl = $window.localStorage['intendedUrl'];
				if (intendedUrl != '/find-account' && intendedUrl != '/set-new-password' && intendedUrl != '/login') {
					$window.localStorage.removeItem('intendedUrl');
					$location.path(intendedUrl);
					return;
				}
			}
			$location.path('/dashboard');
		}, 600);
	}, function () {
		$timeout(function () {
			if ($window.localStorage['intendedUrl'] != undefined) {
				var intendedUrl = $window.localStorage['intendedUrl'];
				if (intendedUrl == '/find-account' || intendedUrl == '/set-new-password') {
					$window.localStorage.removeItem('intendedUrl');
					$location.path(intendedUrl);
					return;
				}
			}
			$location.path('/login');
		}, 600);
	});
}]);

stockMarketAppController.controller('loginController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 1;
	$scope.credential = {
		email: '',
		password: '',
		rememberMe: false
	};
	let defaultButtonText = $sce.trustAsHtml('Login');
	$scope.loginButtonElement = defaultButtonText;
	$scope.disabledLoginButtonElement = false;
	$scope.formFeedback = $rootScope.getShowableFeedback('', false);

	$scope.authAttempt = () => {
		$scope.loginButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> Logging in...');
		authService.attempt($scope.credential, function (user) {
			$rootScope.logedInUser = user;
			$timeout(function () {
				$scope.loginButtonElement = $sce.trustAsHtml('Loged In');
				$scope.formFeedback = $rootScope.getShowableFeedback('Successfully loged in', false);
				$location.path('/dashboard');
			}, 600);
		}, function () {
			$timeout(function () {
				$scope.formFeedback = $rootScope.getShowableFeedback('Username and password is invalid!', true);
				$scope.credential.password = '';
				$scope.loginForm.password.$setPristine(false);
				$scope.loginButtonElement = defaultButtonText;
				$scope.disabledLoginButtonElement = false;
				$timeout(function () {
					$scope.formFeedback = $rootScope.getShowableFeedback('', false);
				}, 1500);
			}, 600);
		});
	};

}]);

stockMarketAppController.controller('findAccountController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 1;
	$scope.findAccountFormData = {
		email: ''
	};
	let defaultButtonText = $sce.trustAsHtml('Find Account');
	$scope.findAccountButtonElement = defaultButtonText;
	$scope.disabledFindAccountButtonElement = false;
	$scope.formFeedback = $rootScope.getShowableFeedback('', false);

	$scope.findAccount = () => {
		$scope.formFeedback = $rootScope.getShowableFeedback('We have sent you a six digit code', false);
		$scope.findAccountButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> Finding...');
		$scope.disabledFindAccountButtonElement = true;
		$timeout(function () {
			$scope.formFeedback = $rootScope.getShowableFeedback('', false);
			$scope.findAccountButtonElement = defaultButtonText;
			$scope.disabledFindAccountButtonElement = false;
			$location.path('/set-new-password');
		}, 2000);
	};

}]);

stockMarketAppController.controller('setNewPasswordController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 1;
	$scope.setNewPasswordFormData = {
		otp: '',
		password: '',
		confirmPassword: ''
	};
	$scope.formFeedback = $rootScope.getShowableFeedback('', false);
	let defaultButtonText = $sce.trustAsHtml('Set New Password');
	$scope.SetNewPasswordButtonElement = defaultButtonText;
	$scope.disabledSetNewPasswordButtonElement = false;

	$scope.setNewPassword = () => {
		$scope.formFeedback = $rootScope.getShowableFeedback('Password has been set successfully', false);
		$scope.SetNewPasswordButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> setting...');
		$scope.disabledSetNewPasswordButtonElement = true;
		$timeout(function () {
			$scope.formFeedback = $rootScope.getShowableFeedback('', false);
			$scope.SetNewPasswordButtonElement = defaultButtonText;
			$scope.disabledSetNewPasswordButtonElement = false;
			$location.path('/dashboard');
		}, 2000);
	};
}]);

stockMarketAppController.controller('dashboardController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
}]);

stockMarketAppController.controller('changePasswordController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
}]);

stockMarketAppController.controller('updateDataController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
}]);

stockMarketAppController.controller('addUserController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
}]);

stockMarketAppController.controller('viewUserController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
}]);

stockMarketAppController.controller('addProfileController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
}]);

stockMarketAppController.controller('viewProfileController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
}]);

stockMarketAppController.controller('updateProfileController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
	console.log($routeParams.Id)
}]);

stockMarketAppController.controller('viewScripController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
}]);

stockMarketAppController.controller('priceAdjustmentController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
}]);

stockMarketAppController.controller('logoutController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
	if ($rootScope.logedInUser != undefined) {
		authService.release();
		$location.path('/');
	}
}]);

stockMarketAppController.controller('NavigationCtrl', ['$scope', '$location', function ($scope, $location) {
	$scope.isCurrentPath = function (path) {
		return $location.path() == path;
	};
}]);


