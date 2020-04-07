app.controller('itemController', function($scope){
	//数量加减操作
	$scope.addNum=function(num){
		$scope.num=$scope.num+num;
		if($scope.num < 1){
			$scope.num=1;
		}
	}

	$scope.specification={}; // 记录用户选择的规格

	// 选择规格
	$scope.selectSpecification=function(name, value){
		$scope.specification[name]=value;
		readSku();
	}

	// 判断某个规格值是否被选中
	$scope.isSelect=function(name, value){
		if($scope.specification[name]==value){
			return true;
		}else{
			return false;
		}
	}

	// 读取SKU信息
	readSku=function(){
		for(var i=0; i<skuList.length;i++){
			if(checkTitle(skuList[i].title)){
				$scope.sku=skuList[i];// 找到了我们当前的sku
				break;
			}
		}
	}

	// 匹配标题是否和用户选择一致
	checkTitle=function(title){
		for(var k in $scope.specification){
			if(title.indexOf($scope.specification[k])<0){
				// 如果小于0，则不存在
				return false;
			}
		}
		return true;
	}

	// 加载默认的sku
	$scope.loadDefaultSku=function(){
		for(var i=0; i<skuList.length;i++){
			if(skuList[i].isDefault == '1'){
				$scope.sku=skuList[i];// 找到了我们当前的sku
				break;
			}
		}
		if($scope.sku == null){
			// 找不到默认值，就默认第一个
			$scope.sku=skuList[0];
		}
	}

});