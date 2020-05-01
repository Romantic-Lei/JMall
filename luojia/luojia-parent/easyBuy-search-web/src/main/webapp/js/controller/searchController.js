app.controller('searchController',function($scope,searchService){
	
	//搜索
	$scope.search=function(){
		searchService.search($scope.searchMap).success(
			function(response){
				$scope.resultMap=response;
			}
		);		
	}	
	
	// 搜索对象
	$scope.searchMap={ "keywords":"" ,"category":"","brand":"","spec":{} };
	
	$scope.addSearchItem=function(key, value){
		if(key=='category' || key=='brand'){
			// 点击的是分类或者品牌
			$scope.searchMap[key]=value;
		}else{
			// 点击的是规格
			$scope.searchMap.spec[key]=value;
		}
	}
	
	// 删除搜索项
	$scope.removeSearchItem=function(key){
		if(key=='category' || key=='brand'){
			// 移除的是分类或者品牌
			$scope.searchMap[key]="";
		}else{
			// 移除的是规格
			delete $scope.searchMap.spec[key];
		}
	}
	
	
});