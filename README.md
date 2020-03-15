# JMall  珞珈商城
---



###缓存过期

​	为了避免数据库压力，我们在查询数据之后，应该将数据存放在**redis**缓存中，这样下次其他用户查询相同数据时就不用频繁操作数据库，减少数据库压力。

​	但是所有的数据都放进缓存中(缓存比内存小多了)，缓存利用率很低，所以我们需要设置过期时间，这样就避免了存储于缓存但是没有利用。

​	当我们在查询时，由于查询的是缓存，所以商品删除后一段时间，我们依旧可以查询到商品信息，但是不论我们商品是否删除，查询时剩余过期时间不会更新，只会减少。



###Activemq监听

项目中使用了索引库，所以我们增加和删除的时候都会操作索引库里的数据（修改时，我们索引库的主搜索名没有改变，我们依旧是使用商品id去数据库查询即时数据）。在增加和删除广播操作时，我们需要定义不同的Destination

```java
// 注入相同类对象，我们必须根据id来区分，否则会报错
@Resource(name="topicDestination")
private Destination topicDestination;
@Resource(name="topicDeleteDestination")
private Destination topicDeleteDestination;
```

在对应类的xml文件中：

```xml
<!--这个是主题目的地，一对多的 -->
<bean id="topicDestination" class="org.apache.activemq.command.ActiveMQTopic">
    <constructor-arg value="itemAddTopic" />
</bean>
<!--这个是主题目的地，一对多的 -->
<bean id="topicDeleteDestination" class="org.apache.activemq.command.ActiveMQTopic">
    <constructor-arg value="itemDeleteTopic" />
</bean>
```



因为在我们的Destination中，我们定义了不同的广播名，如果使用相同的广播名，我们增加数据到索引库时会被增加和删除索引库监听到，同理，删除时也会被增加和删除索引库监听到。

### 页面展示

方案一：直接做一个动态的jsp页面。当查询商品信息时，我们展示的是动态页面

方案二：使用freemarker展示一个静态页面。通过一个模板，当我们查询到某个商品时，直接将查询的信息放到模板的对应位置，生成一个文件到指定目录，然后通过Nginx代理访问到此目录展示到页面。



###SSO单点登陆

​	在电商项目中，不同的模块可能部署于不同的服务器，这么我们session就在物理上被隔离了，无法共享，这时我们可以利用redis来解决session共享问题。因为redis中也可以存储键值对，设置键值过期时间，完全可以模拟session共享问题。

**redis的设置key**：不能是用户的信息，比如id，用户名等。因为当我们在不同机器上登录，一台长时间没有操作，另一台一直在操作，那么长时间没操作的机器实际再次操作时需要登录，但是另一台机器一直在刷新session。两台机器相同的key无法满足我们要求。所以我们的**key可以设计成固定前缀+UUID避免了重复**。



### 跨域问题

##### 什么是跨域？

​	跨域指的是访问与自己ip不同的机器或者是ip相同但是端口不同，这就是跨域

​	js本身无法处理跨域问题的。我们可以其“漏洞”来处理跨域问题，我们处理跨域问题需要控制层和页面共同处理，在jQuery发送请求时，我们的请求类型应该为**dataType : "jsonp",**在控制层，我们有两种方法处理请求：

```java
//	解决json跨域问题方法一：
	@RequestMapping(value = "/user/token/{token}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE/*"application/json;charset=utf-8"*/)
	@ResponseBody
	public String getUserByToken(@PathVariable String token, String callback) {
		E3Result result = tokenService.getUserByToken(token);
		// 响应结果这前，判断是否是jsonp请求
		if (StringUtils.isNotBlank(callback)) {
			// 把结果封装成一个js语句响应,必须是这样的格式
			return callback + "(" + JsonUtils.objectToJson(result) + ");";
		}
		return JsonUtils.objectToJson(result);
	}
	
//	解决json跨域问题方法二：此方法在spring4.1之后可用
	@RequestMapping(value = "/user/token/{token}")
	@ResponseBody
	public Object getUserByToken(@PathVariable String token, String callback) {
		E3Result result = tokenService.getUserByToken(token);
		// 响应结果这前，判断是否是jsonp请求
		if (StringUtils.isNotBlank(callback)) {
			// 把结果封装成一个js语句响应
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}
		return result;
	}
```



### cookie

####	用户登陆保存cookie	

​	用户登录时，将cookie保存，默认cookie失效时间为用户关闭浏览器。设置cookie的作用是可以让其跨一级域和二级域，我们在商品首页转到商品搜索，商品详情等页面时，这是跨域请求，可以使用浏览器的cookie+json来处理跨域问题，在js文件中，获取用户登录的cookie信息,然后发送ajax请求:

```js
checkLogin : function(){
		var _ticket = $.cookie("token");
		if(!_ticket){
			return ;
		}
		$.ajax({
			url : "http://localhost:8088/user/token/" + _ticket,
			dataType : "jsonp",
			type : "GET",
			success : function(data){
				if(data.status == 200){
					var username = data.data.username;
					var html = username + "，欢迎来到珞珈购物网！";
					$("#loginbar").html(html);
				}
			}
		});
	}
```

在表现层Controller接受到请求后（见跨域问题代码），就去服务层取token。完成跨域。

####	cookie保存购物车信息

​	为了用户更好的购物体验，我们商品加入购物车不需要用户登录，那么我们商品加入购物车只能放在用户浏览器的cookie中，注意在cookie中在添加删除商品数量时，我们要更新cookie信息



###406(Not Acceptable) 错误问题解决方法

出现406错误超高概率是没有引入处理json的包，引入即可

```xml
<dependency>
　　<groupId>com.fasterxml.jackson.core</groupId>
　　<artifactId>jackson-core</artifactId>
　　<version>2.7.3</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.7.3</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>2.7.3</version>
</dependency>
```

还有一种可能是  对应请求的后缀是.html的

如果是以html为后缀的，返回的默认类型是text/html，而请求的是application/json的类型，浏览器无法解析，就会报错。

   **这里可以将请求的后缀改成如 .action等  也可以 就不会报错了** 





















