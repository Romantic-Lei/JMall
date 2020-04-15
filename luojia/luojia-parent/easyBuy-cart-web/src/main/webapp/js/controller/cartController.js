// 购物车控制层
app.controller('cartController', function($scope, cartService){
	
	$scope.findCartList=function(){
		cartService.findCartList().success({
			function(response){
				$scope.cartList=response;
			}
		});
	}
	
});