app.service("contentService",function($http){
	
	//根据分类KEY查询广告列表
	this.findByCategoryKey=function(key){
		return $http.get("content/findByCategoryKey.do?key="+key);		
	}
	
});