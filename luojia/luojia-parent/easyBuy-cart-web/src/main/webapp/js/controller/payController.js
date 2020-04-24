// 支付控制层
app.controller('payController', function($scope, payService){
	
	$scope.createNative = function(){
		payService.createNative().success(
			function(response){
				if(response.form != null){
					alert("执行前");
					var ewm = qrcode(10, "H");
//					ewm.addData(response.form);// 生成二维码数据
//					ewm.make();
					document.getElementById("qr").innerHTML=response.form;
					$scope.out_trade_no = response.out_trade_no;// 订单号
					$scope.total_amount = (response.total_amount/100).toFixed(2);// 订单金额
					alert("执行后");
				} else {
					alert("生成二维码发生错误");
				}
			}
		);
	}
	
});