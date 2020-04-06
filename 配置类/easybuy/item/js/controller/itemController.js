app.controller('itemController', function($scope){
	//数量加减操作
	$scope.addNum=function(num){
		$scope.num=$scope.num+num;
		if($scope.num < 1){
			$scope.num=1;
		}
	}
});