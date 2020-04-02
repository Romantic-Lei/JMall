//服务层
app.service('itemCatService',function($http){
	
	//查询实体
	this.findOne=function(id){
		return $http.get('../itemCat/findOne.do?id='+id);
	}
	
	//根据上级id查询分类列表
	this.findByParentId=function(parentId){
		return $http.post('../itemCat/findByParentId.do?parentId='+parentId);
	}
});
