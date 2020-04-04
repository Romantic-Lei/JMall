 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location   ,goodsService, itemCatService, uploadService, typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){	
		
		var id = $location.search()["id"];
		
		if(typeof(id)=='undefined'){
			return ;
		}
		
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				// 操作富文本编辑器
				editor.html($scope.entity.goodsDesc.introduction);// 商品介绍
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
			}
		);				
	}
	
	//保存 
	$scope.save=function(){		
		
		$scope.entity.goodsDesc.introduction=editor.html(); // 商品介绍
		
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
					alert(response.message);
					$scope.entity={};
					editor.html(""); // 清空富文本框
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
	// 查询一级分类列表
	$scope.selectItemCat1List=function(){
		itemCatService.findByParentId(0).success(
			function(response){
				$scope.itemCat1List=response;
			}
		);
	}
	
	//查询二级分类列表
	$scope.$watch('entity.goods.category1Id',function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
			function(response){
				$scope.itemCat2List=response;				
			}
		);		
	});
	
	//查询三级分类列表
	$scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
			function(response){
				$scope.itemCat3List=response;				
			}
		);		
	});
	
	//查询模板ID
	$scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
		itemCatService.findOne(newValue).success(
			function(response){
				$scope.entity.goods.typeTemplateId=response.typeId;				
			}
		);		
	});
	
	//监控模板ID ，读取品牌列表
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		//读取品牌列表和扩展属性
		typeTemplateService.findOne(newValue).success(
			function(response){
				$scope.typeTemplate=response;	//获取模板数据
				$scope.typeTemplate.brandIds= JSON.parse($scope.typeTemplate.brandIds); //品牌列表类型转换
				// 判断是都有id参数
				if( typeof($location.search()['id']) == 'undefined'){
					$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
				}
			}
		);
		
		//读取规格列表
		typeTemplateService.findSpecList(newValue).success(
			function(response){
				$scope.specList=response; //规格列表 
			}
		);
		
	});
	
	// 上传文件
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(
			function(response){
				if(response.success){
					$scope.imageEntity.url=response.message;
				} else {
					alert(response.message);
				}
			}
		).error(
			function(){
				alert("上传出错");
			}
		);
	}
	
	$scope.entity={goods:{}, goodsDesc:{itemImages:[], specificationItems:[]}};// 定义实体结构
	
	//向图片服务器添加图片
	$scope.addImageEntity=function(){
		
		$scope.entity.goodsDesc.itemImages.push($scope.imageEntity);
	}
	
	//从图片列表中删除图片
	$scope.removeImageEntity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index, 1);
	}
	
	//更新规格选项
	$scope.updateSpecAttribute=function($event,name,value){
		// 判断当前操作的规格名称是否在你要添加的集合变量中
		// 如果存在，直接添加值，如果不存在记录，添加记录
		
		var specList=$scope.entity.goodsDesc.specificationItems;	
		for(var i=0; i<specList.length;i++){
			if(specList[i].attributeName==name){//如果存在规格
				if($event.target.checked){//如果是选中
					specList[i].attributeValue.push(value);		
				}else{//如果取消选中
					specList[i].attributeValue.splice(specList[i].attributeValue.indexOf(value),1);
				}
				
				return ;
			}			
		}
		//如果不存在
		specList.push({attributeName:name,attributeValue:[value]});
	}
	
	//构建sku表格
	$scope.createSKUTable=function(){
		
		var list=[{spec:{},price:0,num:99999,status:'0', isDefault:'0'  }];//初始化集合
		
		var specList=$scope.entity.goodsDesc.specificationItems;
		
		for(var i=0;i< specList.length;i++ ){//循环规格
			
			list=addColumns(list, specList[i].attributeName, specList[i].attributeValue );
		}
		
		$scope.entity.skuList=list;//SKU商品列表
		
	}
	
	//向sku表添加列
	addColumns=function(list,columnName,columnValues){
		
		var newList=[];//最后生成的记录个数=  原来的记录个数* 规格选项的个数
		
		for(var i=0;i<list.length;i++){
						
			for(var j=0;j<columnValues.length;j++){  
				var newRow = JSON.parse(JSON.stringify( list[i]));//深克隆
				newRow.spec[columnName]=columnValues[j];
				newList.push(newRow);
			}				
		}		
		return newList;
	}
	
	// 定义状态
	$scope.status=['未审核','已审核', '已驳回', '已关闭'];
	
	$scope.itemCatList=[]; // 商品分类数据
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(
			function(response){
				//$scope.findItemCatList=response;
				for(var i=0; i<response.length;i++){
					$scope.itemCatList[response[i].id]=response[i].name;
				}
			}
		);
	}
	
	// 验证规格和规格选项是否被勾选
	$scope.checkAttributeValue=function(specName, optionsName){
		var specList = $scope.entity.goodsDesc.specificationItems;// 用户选择的规格列表
		for(var i=0; i<specList.length; i++){
			if(specList[i].attributeName==specName){
				if(specList[i].attributeValue.indexOf(optionsName)>=0){
					return true;
				}
			}
		}
		return false;
	}
	
});	
