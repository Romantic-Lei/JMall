//服务层
app.service('payService',function($http){
	    	
	this.createNative=function(){
		return $http.get("pay/createNative.do");
	}
	
});
