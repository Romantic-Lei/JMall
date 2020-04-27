var app=angular.module('pinyougou',[]);//定义品优购模块
app.config(['$locationProvider', function($locationProvider) {
	$locationProvider.html5Mode(true);
}]);