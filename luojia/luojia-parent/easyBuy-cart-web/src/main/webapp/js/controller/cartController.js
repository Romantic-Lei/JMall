// 购物车控制层
app.controller('cartController', function($scope, cartService){
	
	$scope.findCartList=function(){
		cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
				console.log($scope.cartList);
			}
		);
	}
	
//	添加商品到购物车（数量变化）
	$scope.add=function(itemId, num){
		cartService.addGoodsToCartList(itemId, num).success(
			function(response){
				if(response.success){
					$scope.findCartList();// 数量更新成功，重新查询显示最新数据变化
				}else{
					alert(response.message);
				}
			}
		);
	}
	
});