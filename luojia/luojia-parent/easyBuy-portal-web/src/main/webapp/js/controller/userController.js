 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	
	//查询实体 
	$scope.findOne=function(id){				
		userService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	$scope.entity={};
	
	//注册
	$scope.reg=function(){		
		
		//判断两次输入的密码是否一致
		if($scope.entity.username=='' || $scope.entity.username==null){
			alert("请输入用户名");
			return ;
		}
		
		if($scope.entity.password=='' || $scope.entity.password==null){
			alert("请输入密码");
			return ;
		}
		
		if($scope.password=='' || $scope.password==null ){
			alert("请输入确认密码");
			return ;
		}
		
		if($scope.entity.password!=$scope.password  ){
			alert("两次输入的密码不一致!");
			return ;
		}	
		
		userService.add( $scope.entity ,$scope.smscode ).success(
			function(response){
				alert(response.message);
			}		
		);				
	}
	
	//发送短信验证码
	$scope.sendCode=function(){
		
		userService.sendCode( $scope.entity.phone  ).success(
			function(response){
				alert(response.message);				
			}			
		);		
	}
	
});	
