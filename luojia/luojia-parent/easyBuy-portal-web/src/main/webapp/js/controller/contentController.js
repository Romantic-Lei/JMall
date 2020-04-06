app.controller("contentController",function($scope,contentService){
	
	$scope.contentList=[];//广告集合
	
	$scope.findByCategoryKey=function(key){
		contentService.findByCategoryKey(key).success(
			function(response){
				$scope.contentList[key]=response;
			}
		);		
	}	
	
});