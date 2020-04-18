app.controller('orderInfoController', function($scope, addressService) {

	// 查询当前用户地址列表
	$scope.findAddressList = function() {
		addressService.findListByLoginUser.success(function(response) {
			$scope.addressList = response;
		});
	}

	// 选择地址
	$scope.selectAddress = function(address) {
		$scope.address = address; // 设置当前地址
	}

	// 判断某地址是否为当前地址
	$scope.isSelectAddress = function(address) {
		if ($scope.address == address) {
			return true;
		} else {
			return false;
		}
	}

});