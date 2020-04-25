app.controller('orderInfoController', function($scope, addressService, cartService) {

	// 查询当前用户地址列表
	$scope.findAddressList = function() {
		addressService.findListByLoginUser().success(
				function(response) {
					 
			$scope.addressList = response;

			// 选择默认地址
			for (var i = 0; i < $scope.addressList.length; i++) {
				if ($scope.addressList[i].isDefault == '1') {
					$scope.address = $scope.addressList[i];
				}
			}

			// 如果没有找到默认地址，则第一个地址被选中
			if ($scope.address == null && $scope.addressList.length > 0) {
				$scope.address = $scope.addressList[0];
			}

		});
	}

	// 选择地址
	$scope.selectAddress = function(address) {
		$scope.address = address; // 设置当前地址
	}

	// 判断某地址是否为当前地址
	$scope.isSelectedAddress = function(address) {
		if ($scope.address == address) {
			return true;
		} else {
			return false;
		}
	}

	$scope.order = {
		paymentType : '1'
	};// 订单对象
	
	// 选择支付类型
	$scope.selectPayType = function(type) {
		$scope.order.paymentType = type;
	}

	// 读取购物车列表
	$scope.findCartList = function() {
		cartService.findCartList().success(function(response) {
			$scope.cartList = response;
			sum();
		});
	}
	
	//求合计
	sum=function(){
		$scope.totalNum=0;//数量合计
		$scope.totalMoney=0.00;//金额合计
		
		for(var i=0;i<$scope.cartList.length;i++){
			var cart = $scope.cartList[i];			
			for(var j=0;j<cart.orderItemList.length;j++){
				var orderItem=cart.orderItemList[j];
				$scope.totalNum+=orderItem.num;
				$scope.totalMoney+=orderItem.totalFee;				
			}				
		}	
	}
	
	// 提交订单
	$scope.submitOrder=function(){
		cartService.submitOrder($scope.order).success(
			function(response){
				alert(response.message);
				if(response.success && $scope.order.paymentType=='1'){
					// 提交成功，并且为支付宝支付
//					location.href='pay.html';// 跳转为扫码页面
					location.href='/pay/createNative.do';// 跳转为扫码页面
				}
			}
		);
	}
	
	// 增加地址
	$scope.addAddress=function(){
		addressService.add($scope.address_entity).success(
			function(response){
				alert(response.message);
				if(response.success){
					$scope.findAddressList();
				}
			}
		);
	}

});