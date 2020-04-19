// 购物车服务层
app.service('cartService', function($http) {
	// 查询购物车列表
	this.findCartList = function() {
		return $http.get('cart/findCartList.do');
	}

	// 添加商品到购物车
	this.addGoodsToCartList = function(itemId, num) {
		return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
	}
	
	// 提交订单
	this.submitOrder=function(order){
		return $http.post('order/add.do', order);
	}

});