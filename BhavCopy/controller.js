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
		$scope.findAccountButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> Finding...');
		$timeout(function () {
			apiService.sendOTP($scope.findAccountFormData).then(function (response) {
				console.log(response.data)
				$scope.formFeedback = $rootScope.getShowableFeedback('We have sent you an OTP!', false);
				$location.path('/set-new-password/' + $scope.findAccountFormData.email + '/' + response.data.otp);
			}, function (errorResponse) {
				console.log(errorResponse.data)
				$scope.formFeedback = $rootScope.getShowableFeedback('Can not find any account!', true);
			}).then(function () {
				$scope.findAccountButtonElement = defaultButtonText;
				$scope.disabledFindAccountButtonElement = false;
				$timeout(function () {
					$scope.formFeedback = $rootScope.getShowableFeedback('', false);
				}, 3000);
			});
		}, 600);
	};
}]);

stockMarketAppController.controller('setNewPasswordController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', '$routeParams', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService, $routeParams) {
	$rootScope.bodyType = 1;
	$scope.setNewPasswordFormData = {
		email: $routeParams.email,
		otp: $routeParams.otp,
		appPin: '',
		password: '',
		confirmPassword: ''
	};
	$scope.formFeedback = $rootScope.getShowableFeedback('', false);
	let defaultButtonText = $sce.trustAsHtml('Set New Password');
	$scope.setNewPasswordButtonElement = defaultButtonText;
	$scope.disabledSetNewPasswordButtonElement = false;

	$scope.setNewPassword = () => {

		if ($scope.setNewPasswordFormData.appPin == $rootScope.applicationPin) {

			$scope.setNewPasswordButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> setting...');
			$scope.disabledSetNewPasswordButtonElement = true;

			$timeout(function () {
				apiService.setNewPassword($scope.setNewPasswordFormData).then(function (response) {
					console.log(response.data)
					$scope.formFeedback = $rootScope.getShowableFeedback('New password has been set successfully', false);
					$timeout(function () {
						$location.path('/login');
					}, 1000);
				}, function (errorResponse) {
					console.log(errorResponse)
					$scope.formFeedback = $rootScope.getShowableFeedback(errorResponse.data.message, true);
				}).then(function () {
					$scope.setNewPasswordButtonElement = defaultButtonText;
					$scope.disabledSetNewPasswordButtonElement = false;
					$timeout(function () {
						$scope.formFeedback = $rootScope.getShowableFeedback('', false);
					}, 2000);
				});
			}, 600);
		} else {
			$scope.formFeedback = $rootScope.getShowableFeedback('Application pin does not match!', true);
			$timeout(function () {
				$scope.formFeedback = $rootScope.getShowableFeedback('', false);
			}, 2000);
		}
	};
}]);

stockMarketAppController.controller('dashboardController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
	$scope.records=Array();

	var now = new Date();
	now.setMonth(now.getMonth()-4);

	$scope.scan = {
		selectedProfile: null,
		selectedScrip: null,
		startDate: now,
		endDate: new Date(),
		timeFrame: "daily"
	};

	$scope.profiles = [];
	$scope.scrips = [];

	$scope.showData = (index) => {
		$scope.selectedRecord  = $scope.records[index];
		console.log($scope.selectedRecord)
	};


	let defaultButtonText = $sce.trustAsHtml('<i class="fas fa-search"></i>');
	$scope.scanButtonElement = defaultButtonText;
	$scope.disabledScanButtonElement = false;

	$timeout(function () {
		apiService.getAllProfile().then(function (response) {
			$scope.profiles = response.data;
		});
	}, 100);

	$scope.fetchProfileScrip = () => {
		$scope.scan.selectedScrip = null;
		if ($scope.scan.selectedProfile != null) {
			apiService.getProfile($scope.scan.selectedProfile).then(function (response) {
				$scope.scrips = response.data.scrips;
			});
		}
	};

	$scope.fetchFilteredRecord = () => {
		let startDate = new Date($scope.scan.startDate);
		let endDate = new Date($scope.scan.endDate);
		let filterData = {
			"startDate": startDate.getDate() + "-" + (startDate.getMonth()+1) + "-" + startDate.getFullYear(),
			"endDate": endDate.getDate() + "-" + (endDate.getMonth()+1) + "-" + endDate.getFullYear(),
			"timeFrame": $scope.scan.timeFrame
		}

		$scope.scanButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i>');
		$scope.disabledScanButtonElement = true;

		apiService.getFilteredRecordData($scope.scan.selectedScrip, filterData).then(function (response) {
			$scope.records = response.data.records;
			console.log(response.data)
			$scope.selectedRecord=null;
		}, function (errorResponse) {
			console.log(errorResponse.data)
			console.log(errorResponse)
		}).then(function () {
			$scope.scanButtonElement = defaultButtonText;
			$scope.disabledScanButtonElement = false;
		});
	};

}]);

stockMarketAppController.controller('changeAppPinController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;

	$scope.formData = {
		currentPin: '',
		newPin: ''
	};
	$scope.formFeedback = $rootScope.getShowableFeedback('', false);
	let defaultButtonText = $sce.trustAsHtml('Change Application Pin');
	$scope.changeAppPinButtonElement = defaultButtonText;
	$scope.disabledChangeAppPinButtonElement = false;

	$scope.changeAppPin = () => {
		if ($scope.formData.currentPin == $rootScope.applicationPin) {
			$scope.changeAppPinButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> Changing...');
			$scope.disabledChangeAppPinButtonElement = true;
			$timeout(function () {
				$window.localStorage['applicationPin'] = $scope.formData.newPin;
				$rootScope.applicationPin = $scope.formData.newPin;
				$scope.formData.currentPin = '';
				$scope.formData.newPin = '';
				$scope.changeAppPinForm.$setPristine();
				$scope.changeAppPinForm.$setUntouched();
				$scope.formFeedback = $rootScope.getShowableFeedback('Application pin has been successfully changed', false);
				$scope.changeAppPinButtonElement = defaultButtonText;
				$scope.disabledChangeAppPinButtonElement = false;
				$timeout(function () {
					$scope.formFeedback = $rootScope.getShowableFeedback('', false);
				}, 1000);
			}, 600);
		} else {
			$scope.formFeedback = $rootScope.getShowableFeedback('Current pin does not match!', true);
			$timeout(function () {
				$scope.formFeedback = $rootScope.getShowableFeedback('', false);
			}, 2000);
		}
	};
}]);

stockMarketAppController.controller('changePasswordController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;

	$scope.formData = {
		currentPassword: '',
		newPassword: '',
		confirmPassword: ''
	};
	$scope.formFeedback = $rootScope.getShowableFeedback('', false);
	let defaultButtonText = $sce.trustAsHtml('Change Password');
	$scope.changePasswordButtonElement = defaultButtonText;
	$scope.disabledChangePasswordButtonElement = false;


	$scope.changePassword = () => {

		$scope.changePasswordButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> changing...');
		$scope.disabledChangePasswordButtonElement = true;

		$timeout(function () {
			apiService.updatePassword($scope.formData).then(function (response) {
				console.log(response.data)
				$scope.formData.currentPassword = '';
				$scope.formData.newPassword = '';
				$scope.formData.confirmPassword = '';
				$scope.changePasswordForm.$setPristine();
				$scope.changePasswordForm.$setUntouched();

				$scope.formFeedback = $rootScope.getShowableFeedback('New password has been changed successfully', false);
			}, function (errorResponse) {
				console.log(errorResponse)
				// $window.document.getElementById('current-password').focus();
				$scope.formFeedback = $rootScope.getShowableFeedback('Current password does not match!', true);
			}).then(function () {
				$scope.changePasswordButtonElement = defaultButtonText;
				$scope.disabledChangePasswordButtonElement = false;
				$timeout(function () {
					$scope.formFeedback = $rootScope.getShowableFeedback('', false);
				}, 2000);
			});
		}, 600);
	};
}]);

stockMarketAppController.controller('updateDataController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
	$scope.fileName = "";

	let defaultButtonText = $sce.trustAsHtml('Update Data');
	$scope.updateDataButtonElement = defaultButtonText;
	$scope.formFeedback = $rootScope.getShowableFeedback('', false);
	$scope.disabledUpdateDataButtonElement = false;

	$scope.selectFile = function (file) {
		$scope.selectedFile = file;
		console.log('selected');
		console.log(file)
	};

	function showFeedbackMessage(message, hasError) {
		$scope.updateDataButtonElement = defaultButtonText;
		$scope.formFeedback = $rootScope.getShowableFeedback(message, hasError);
		$scope.disabledUpdateDataButtonElement = false;
		$timeout(function () {
			$scope.formFeedback = $rootScope.getShowableFeedback('', false);
		}, 3000);
	}

	$scope.updateData = () => {
		$scope.updateDataButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> Validating...');
		$scope.disabledUpdateDataButtonElement = true;
		var regex = /^([a-zA-Z0-9\s_\\.\-:])+(.csv)$/;
		if (regex.test($scope.selectedFile.name.toLowerCase())) {
			if (typeof (FileReader) != "undefined") {
				var reader = new FileReader();
				reader.onload = function (e) {
					var rawRecords = new Array();
					var date = "";
					var isValidData = true;
					var message = "";
					var rows = e.target.result.split("\n");
					for (var i = 1; i < rows.length - 1; i++) {
						var cells = rows[i].split(",");
						if (cells.length > 13) {
							if (i == 1) {
								date = cells[10];
							}
							else if (date != cells[10]) {
								isValidData = false;
								message = "Records date are not matching";
							}
							var rawRecord = {};
							rawRecord.scripName = cells[0];
							rawRecord.series = cells[1];
							rawRecord.open = cells[2];
							rawRecord.high = cells[3];
							rawRecord.low = cells[4];
							rawRecord.close = cells[5];
							rawRecord.last = cells[6];
							rawRecord.previousClose = cells[7];
							rawRecord.totalTradeQuantity = cells[8];
							rawRecord.totalTradeValue = cells[9];
							rawRecord.timestamp = cells[10];
							rawRecord.totalTrades = cells[11];
							rawRecord.isin = cells[12];
							rawRecords.push(rawRecord);
						} else {
							isValidData = false;
							message = "Records columns are not matching";
						}
					}
					if (isValidData) {
						$timeout(function () {
							$scope.updateDataButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> Updating...');
							$timeout(function () {
								apiService.updateData(date, rawRecords).then(function (response) {
									console.log(response.data)
									$scope.fileName = '';
									$scope.selectedFile = null;
									angular.element("input[type='file']").val(null);
									$scope.updateDataForm.dataFile.$setPristine();
									$scope.updateDataForm.dataFile.$setUntouched();
									showFeedbackMessage('Record has been updated of Date: ' + date, false);
								}, function (errorResponse) {
									console.log(errorResponse.data)
									$scope.formFeedback = $rootScope.getShowableFeedback('Server error!', true);
								}).then(function () {
									$scope.updateDataButtonElement = defaultButtonText;
									$scope.disabledUpdateDataButtonElement = false;
									$timeout(function () {
										showFeedbackMessage('', false);
									}, 2000);
								});
							}, 600);
						}, 100);
					} else {
						$timeout(function () {
							showFeedbackMessage(message, true);
						}, 100);
					}
				}
				reader.readAsText($scope.selectedFile);
			} else {
				$timeout(function () {
					showFeedbackMessage('This browser does not support file uploading', true);
				}, 100);
			}
		} else {
			$timeout(function () {
				showFeedbackMessage('Please upload a valid csv file', true);
			}, 100);
		}
	}

}]);

stockMarketAppController.controller('addUserController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;

	$scope.formData = {
		name: "",
		email: "",
		password: "",
		confirmPassword: "",
		admin: false
	};

	$scope.formFeedback = $rootScope.getShowableFeedback('', false);
	let defaultButtonText = $sce.trustAsHtml('Add New User');
	$scope.addUserButtonElement = defaultButtonText;
	$scope.disabledAddUserButtonElement = false;

	$scope.addUser = () => {
		console.log($scope.formData);

		$scope.addUserButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> Adding...');
		$scope.disabledAddUserButtonElement = true;

		$timeout(function () {
			apiService.addUser($scope.formData).then(function (response) {
				console.log(response.data)

				$scope.formData = {
					name: "",
					email: "",
					password: "",
					confirmPassword: "",
					admin: false
				};
				$scope.addUserForm.name.$setPristine();
				$scope.addUserForm.name.$setUntouched();
				$scope.addUserForm.email.$setPristine();
				$scope.addUserForm.email.$setUntouched();
				$scope.addUserForm.password.$setPristine();
				$scope.addUserForm.password.$setUntouched();
				$scope.addUserForm.confirmPassword.$setPristine();
				$scope.addUserForm.confirmPassword.$setUntouched();


				$scope.formFeedback = $rootScope.getShowableFeedback('New User has been successfully added', false);
			}, function (errorResponse) {
				console.log(errorResponse)
				if (errorResponse.status == 409) {
					$scope.formFeedback = $rootScope.getShowableFeedback('User email is already exist!', true);
				}
			}).then(function () {
				$scope.addUserButtonElement = defaultButtonText;
				$scope.disabledAddUserButtonElement = false;
				$timeout(function () {
					$scope.formFeedback = $rootScope.getShowableFeedback('', false);
				}, 2000);
			});
		}, 200);
	};
}]);

stockMarketAppController.controller('updateUserController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', '$routeParams', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService, $routeParams) {
	$rootScope.bodyType = 2;

	$scope.formData = {
		name: "",
		email: "",
		password: "",
		confirmPassword: "",
		admin: false
	};

	$scope.formFeedback = $rootScope.getShowableFeedback('', false);
	let defaultButtonText = $sce.trustAsHtml('Update User');
	$scope.updateUserButtonElement = defaultButtonText;
	$scope.disabledUpdateUserButtonElement = false;

	function fetchUser() {
		apiService.getUser($routeParams.Id).then(function (response) {
			$scope.formData = response.data;
			$scope.formData.password = "";
			$scope.formData.confirmPassword = "";
		}, function (errorResponse) {
			if (errorResponse.status) {
				console.log(errorResponse)
			}
		});
	}

	$scope.updateUser = () => {
		console.log($scope.formData);

		$scope.updateUserButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> Updating...');
		$scope.disabledUpdateUserButtonElement = true;

		$timeout(function () {
			apiService.updateUser($routeParams.Id, $scope.formData).then(function (response) {
				console.log(response.data)

				$scope.formData = {
					name: "",
					email: "",
					password: "",
					confirmPassword: "",
					admin: false
				};
				$scope.updateUserForm.name.$setPristine();
				$scope.updateUserForm.name.$setUntouched();
				$scope.updateUserForm.email.$setPristine();
				$scope.updateUserForm.email.$setUntouched();
				$scope.updateUserForm.password.$setPristine();
				$scope.updateUserForm.password.$setUntouched();
				$scope.updateUserForm.confirmPassword.$setPristine();
				$scope.updateUserForm.confirmPassword.$setUntouched();
				fetchUser();
				$scope.formFeedback = $rootScope.getShowableFeedback('User has been successfully updated', false);
			}, function (errorResponse) {
				console.log(errorResponse)
				if (errorResponse.status == 409) {
					$scope.formFeedback = $rootScope.getShowableFeedback('User with same email is already exist!', true);
				}
			}).then(function () {
				$scope.updateUserButtonElement = defaultButtonText;
				$scope.disabledUpdateUserButtonElement = false;
				$timeout(function () {
					$scope.formFeedback = $rootScope.getShowableFeedback('', false);
				}, 2000);
			});
		}, 200);
	};

	fetchUser();

}]);

stockMarketAppController.controller('viewUserController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;

	$scope.users = [];
	$scope.deleteDisabledButtonElement = false;

	function fetchAllUser() {
		apiService.getAllUser().then(function (response) {
			$scope.users = response.data;
			// console.log(response.data);

			dTable = $('#example1')
			dTable.DataTable();
		}, function (errorResponse) {
			if (errorResponse.status) {
				console.log(errorResponse)
			}
		});
	};

	fetchAllUser();

	angular.element(document).ready(function () {
		dTable = $('#example1')
		dTable.DataTable();
	});


	$scope.deleteUser = (id) => {
		$scope.deleteDisabledButtonElement = true;
		apiService.deleteUser(id).then(function (response) {
			console.log('deleted')
			fetchAllUser();
		}, function (errorResponse) {
			console.log(errorResponse)
			if (errorResponse.status == 204) {
				console.log('not exist')
			}
		}).then(function () {
			$scope.deleteDisabledButtonElement = false;
		});
	};


}]);

stockMarketAppController.controller('addProfileController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;
	$scope.formData = {
		scripIds: [],
		name: ''
	};
	$scope.scrips = [];

	$scope.formFeedback = $rootScope.getShowableFeedback('', false);
	let defaultButtonText = $sce.trustAsHtml('Add New Profile');
	$scope.addProfileButtonElement = defaultButtonText;
	$scope.disabledAddProfileButtonElement = false;

	$timeout(function () {
		apiService.getAllScrip().then(function (response) {
			$scope.scrips = response.data;
			console.log(response.data);
		});
	}, 100);

	$scope.addProfile = () => {
		console.log($scope.formData);

		$scope.addProfileButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> Adding...');
		$scope.disabledAddProfileButtonElement = true;

		$timeout(function () {
			apiService.addProfile($scope.formData).then(function (response) {
				console.log(response.data)

				$scope.formData.scripIds = [];
				$scope.formData.name = "";
				$scope.addProfileForm.name.$setPristine();
				$scope.addProfileForm.name.$setUntouched();
				$scope.addProfileForm.scripIds.$setPristine();
				$scope.addProfileForm.scripIds.$setUntouched();


				$scope.formFeedback = $rootScope.getShowableFeedback('New profile has been successfully added', false);
			}, function (errorResponse) {
				console.log(errorResponse)
				if (errorResponse.status == 409) {
					$scope.formFeedback = $rootScope.getShowableFeedback('Profile name is already exist!', true);
				}
			}).then(function () {
				$scope.addProfileButtonElement = defaultButtonText;
				$scope.disabledAddProfileButtonElement = false;
				$timeout(function () {
					$scope.formFeedback = $rootScope.getShowableFeedback('', false);
				}, 2000);
			});
		}, 200);
	};
}]);

stockMarketAppController.controller('viewProfileController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;

	$scope.profiles = [];
	$scope.deleteDisabledButtonElement = false;

	function fetchAllProfile() {
		apiService.getAllProfile().then(function (response) {
			$scope.profiles = response.data;
			// console.log(response.data);
		}, function (errorResponse) {
			if (errorResponse.status) {

			}
		});
	};

	$timeout(function () {
		fetchAllProfile();
	}, 100);


	$scope.deleteProfile = (id) => {
		$scope.deleteDisabledButtonElement = true;
		apiService.deleteProfile(id).then(function (response) {
			console.log('deleted')
			fetchAllProfile();
		}, function (errorResponse) {
			if (errorResponse.status == 204) {
				console.log('not exist')
			}
		}).then(function () {
			$scope.deleteDisabledButtonElement = false;
		});
	};

}]);

stockMarketAppController.controller('updateProfileController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', '$routeParams', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService, $routeParams) {
	$rootScope.bodyType = 2;
	console.log($routeParams.Id)
	$scope.formData = {
		scripIds: Array(),
		name: ''
	};
	$scope.scrips = [];

	function fetchProfile() {
		apiService.getProfile($routeParams.Id).then(function (response) {
			$scope.formData.scripIds = Array();
			response.data.scrips.forEach((scrip, index) => {
				$scope.formData.scripIds.push(scrip.id);
			});
			$scope.formData.name = response.data.name;
			getAllScrip();

			console.log($scope.formData)
		}, function (errorResponse) {
			if (errorResponse.status) {
				console.log(errorResponse)
			}
		});
	}


	$scope.formFeedback = $rootScope.getShowableFeedback('', false);
	let defaultButtonText = $sce.trustAsHtml('Update Profile');
	$scope.updateProfileButtonElement = defaultButtonText;
	$scope.disabledUpdateProfileButtonElement = false;


	function getAllScrip() {
		apiService.getAllScrip().then(function (response) {
			$scope.scrips = response.data;
		})
	}

	$scope.updateProfile = () => {
		console.log($scope.formData);

		$scope.updateProfileButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> Updating...');
		$scope.disabledUpdateProfileButtonElement = true;

		$timeout(function () {
			apiService.updateProfile($routeParams.Id, $scope.formData).then(function (response) {
				console.log(response.data)

				$scope.formData.scripIds = [];
				$scope.formData.name = "";
				$scope.updateProfileForm.name.$setPristine();
				$scope.updateProfileForm.name.$setUntouched();
				$scope.updateProfileForm.scripIds.$setPristine();
				$scope.updateProfileForm.scripIds.$setUntouched();

				fetchProfile();

				$scope.formFeedback = $rootScope.getShowableFeedback('profile has been successfully updated', false);
			}, function (errorResponse) {
				console.log(errorResponse)
				if (errorResponse.status == 409) {
					$scope.formFeedback = $rootScope.getShowableFeedback('Profile with same name is already exist!', true);
				}
			}).then(function () {
				$scope.updateProfileButtonElement = defaultButtonText;
				$scope.disabledUpdateProfileButtonElement = false;
				$timeout(function () {
					$scope.formFeedback = $rootScope.getShowableFeedback('', false);
				}, 2000);
			});
		}, 200);
	};

	fetchProfile();

}]);

stockMarketAppController.controller('priceAdjustmentController', ['$rootScope', '$scope', 'apiService', '$window', '$location', '$sce', 'authService', '$timeout', 'appService', function ($rootScope, $scope, apiService, $window, $location, $sce, authService, $timeout, appService) {
	$rootScope.bodyType = 2;

	$scope.formData = {
		scripId: '',
		priceAdjustmentType: '1',
		numerator:'',
		denominator:'',
		date:new Date()
	};
	$scope.scrips = [];

	// priceAdjustment

	$scope.formFeedback = $rootScope.getShowableFeedback('', false);
	let defaultButtonText = $sce.trustAsHtml('AdjustPrice');
	$scope.adjustPriceButtonElement = defaultButtonText;
	$scope.disabledAdjustPriceButtonElement = false;

	$timeout(function () {
		apiService.getAllScrip().then(function (response) {
			$scope.scrips = response.data;
		});
	}, 100);

	$scope.adjustPrice = () => {

	let date = new Date($scope.formData.date);

		var data = {
			scripId: $scope.formData.scripId,
			priceAdjustmentType: $scope.formData.priceAdjustmentType, 
			numerator: $scope.formData.numerator,
			denominator: $scope.formData.denominator,
			date: date.getDate() + "-" + (date.getMonth()+1) + "-" + date.getFullYear()
		}
		console.log(data);

		$scope.adjustPriceButtonElement = $sce.trustAsHtml('<i class="fa fa-spinner fa-spin"></i> Adjusting...');
		$scope.disabledAdjustPriceButtonElement = true;

		$timeout(function () {
			apiService.priceAdjustment(data).then(function (response) {
				console.log(response.data)

				$scope.formData = {
					scripId: '',
					priceAdjustmentType: '1',
					numerator:'',
					denominator:'',
					date:new Date()
				};

				$scope.priceAdjustmentForm.scrip.$setPristine();
				$scope.priceAdjustmentForm.scrip.$setUntouched();
				$scope.priceAdjustmentForm.priceAdjustmentType.$setPristine();
				$scope.priceAdjustmentForm.priceAdjustmentType.$setUntouched();
				$scope.priceAdjustmentForm.numerator.$setPristine();
				$scope.priceAdjustmentForm.numerator.$setUntouched();
				$scope.priceAdjustmentForm.denominator.$setPristine();
				$scope.priceAdjustmentForm.denominator.$setUntouched();
				$scope.priceAdjustmentForm.date.$setPristine();
				$scope.priceAdjustmentForm.date.$setUntouched();


				$scope.formFeedback = $rootScope.getShowableFeedback('Price has been adjusted successfully', false);
			}, function (errorResponse) {
				console.log(errorResponse)
				if (errorResponse.status == 4090) {
					$scope.formFeedback = $rootScope.getShowableFeedback('Scrip is not found!', true);
				}
			}).then(function () {
				$scope.adjustPriceButtonElement = defaultButtonText;
				$scope.disabledAdjustPriceButtonElement = false;
				$timeout(function () {
					$scope.formFeedback = $rootScope.getShowableFeedback('', false);
				}, 2000);
			});
		}, 200);
	};
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
	$scope.isCurrentPathContains = function (path) {
		return $location.path().includes(path);
	};
}]);


