

# JMall  珞珈商城

---

### ssm整合dubbo

SpringMVC在整合dubbo时，需要在服务层暴露需要提供服务的接口

```xml
<!-- 使用dubbo发布服务 -->
	<!-- 提供方应用信息，用于计算依赖关系 -->
	<dubbo:application name="jmall-content" />
	<dubbo:registry protocol="zookeeper" address="ip:2181" />
	<!-- 用dubbo协议在20881端口暴露服务 -->
	<dubbo:protocol name="dubbo" port="20881" />
	<!-- 声明需要暴露的服务接口 -->
<!--
	<dubbo:service interface="接口的全类名]" ref="实现类对象" timeout="毫秒" />
-->

	<dubbo:service interface="cn.jmall.content.service.ContentCategoryService" ref="contentCategoryServiceImpl" timeout="600000"/>
	<dubbo:service interface="cn.jmall.content.service.ContentService" ref="contentServiceImpl" timeout="600000"/>
```

然后需要在表现层的配置文件中引用服务

```xml
<!-- 引用dubbo服务 -->
	<dubbo:application name="jmall-portal-web"/>
	<dubbo:registry protocol="zookeeper" address="ip:2181"/>	
	<!-- 引用服务 -->
	<dubbo:reference interface="cn.jmall.content.service.ContentService" id="contentService" />
```

#### sorl索引库

需要配置sorl和Tomcat整合。

在使用sorl索引库时，需要有商品一键导入和商品单独导入的功能。在商品一键导入功能中，我们需要查询到所有商品，然后利用SolrInputDocument对象把文档添加到索引库里

```java
public E3Result importAllItems() {
    try {
        // 查询商品列表
        List<SearchItem> itemList = itemMapper.getItemList();
        // 遍历商品列表
        for (SearchItem searchItem : itemList) {
            // 创建文档对象
            SolrInputDocument document = new SolrInputDocument();
            // 向文档中添加作用域
            document.addField("id", searchItem.getId());
            // 把文档对象写入索引库
            solrServer.add(document);
        }
        // 提交
        solrServer.commit();
        // 返回导入成功
        return Result.ok();
    } catch (Exception e) {
        e.printStackTrace();
        return Result.build(500, "数据导入时发生异常");
    }
}
```

**若是自动同步到索引库我们就需要使用Activemq来监听商品添加 修改和删除同步索引库**

即我们在商品管理页面，在增删改时，都需要整合activemq的消息发送

```java
public Result deleteBatchItem(String[] ids) {
    for (String id : ids) {
        // 发送商品删除信息
        jmsTemplate.send(topicDeleteDestination, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(id + "");
                return textMessage;
            }
        });
    }
}
```

在消息监听方法上需要实现MessageListener方法

```java
public class ItemDeleteMessageListener implements MessageListener {
    @Autowired
    private SolrServer solrServer;

    @Override
    public void onMessage(Message message) {
        try {
            // 从消息中取商品id
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            Long itemId = new Long(text);
            solrServer.deleteById(itemId.toString());
            // 提交
            solrServer.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```







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

#####限制用户只能登录在一台机器上

用户登录后，我们将用户登录token保存在redis，但是此时redis中的key必须是可以识别出用户身份的信息，例如id或者用户名，当另一用户登录时，我们首先去redis中查询是否存在当前用户信息，存在则提示不允许，不存在就去数据库查找，找到了就放入redis中，登录成功。



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

### 购物车结算

​	用户可以不登录浏览商品，可以不用登陆将商品加入购物车，可以不用登陆浏自己cookie中保存的商品信息，但是点击结算时，我们会使用登陆拦截器判断用户是否登陆，如果登录，则直接结算，如果没有登录，我们会跳转到登录页，那么cookie中的购物车商品也会加入到用户实际的购物车中。

​	当用户没有登录点击结算时，我们来到了登录页，登录之后会跳转到商城首页，不符合用户习惯，我们需要的是在什么页面登录的就来到什么页面。所以在购物车结算页面登录我们需要回到结算页面。这时我们在结算登录页通过url传参方式传递当前页面给登录页 **?redirect=URL**

```java
response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURL());
```

然后在SSO系统的登录方法中，加上相对应的参数:

```java
@RequestMapping("/page/login")
public String showLogin(String redirect, Model model) {
    model.addAttribute("redirect", redirect);
    return "login";
}
```

然后在登录页login.jsp中判断 ，参数redirect是否有值：

```js
var redirectUrl = "${redirect}";
doLogin:function() {
    $.post("/user/login", $("#formlogin").serialize(),function(data){
        if (data.status == 200) {
            jAlert('登录成功！',"提示", function(){
                if (redirectUrl == "") {
                    location.href = "http://localhost:8082";
                } else {
                    location.href = redirectUrl; // 其他页面传递归来的URL
                }
            });

        } else {
            jAlert("登录失败，原因是：" + data.msg,"失败");
        }
    });
}
}
```



##总结：

####基于SOA架构

​	面向服务的架构，要求表现层和服务层分开

#### 工程搭建

​	maven管理工程

​	父工程（所有的工程都需要继承它）

​	聚合工程（一个聚合工程下面可以有多个模块，每个聚合工程必须要有一个war包）

​	模块（既可以是jar包也可以是war包）

​	工程的继承、依赖。

####dubbo：服务治理工具

​	服务提供者

​	服务消费者

​	注册中心，使用zookeeper实现，相当于一个总结

​	监控中心（运维的时候可能才需要查看）

####商品图片，实现图片上传

​	①图片服务器

​		1）FastDFS保存图片

​			Tracker，相当于注册中心的作用，管理服务器集群。

​			Storage，保存文件的服务器。

​		2）访问图片http服务器，推荐使用Nginx

​			a.静态资源的访问，配置server可以实现

​				1.通过端口区分不同的server

​				2.通过域名区分不同的server

​			b.反向代理

​				1.proxy_pass http://upstream_name

​				2.需要配置upstream节点

​				3.节点中有应用服务的地址列表

​			c.负载均衡

​				需要在upstream节点中配置多个服务器就可以实现负载均衡，可以调整每个服务器的权重

​	②图片上传的实现

​			（1）FastDFS的java客户端

​			（2）使用KindEditor 的多图片上传插件

#### 向业务逻辑添加缓存

​	1.商城首页访问量巨大，对数据库压力要求高，需要使用使用redis做缓存

​		1）string

​		2）hash

​		3）list

​		4）set

​		5）SortedSet

​	2.redis集群

​		1）没有代理层

​		2）投票容错

​		3）客户端链接任意节点即可

​		4）slot槽 0-16383

​	3.jedis客户端

​	1）JedisPool（单机版）

​	2）JedisCluster（集群版）

​	4.向业务逻辑中添加缓存

​		添加缓存功能不能影响其他业务逻辑。查询时，我们首先查缓存，缓存中没有再去查询数据库，查到之后再放入到缓存中。

​	5.缓存同步。

​		在增删改时，我们需要将变化的缓存key删掉，下次查询的时候，自动添加到缓存中，或者增删改的时候，修改key将结果放入到缓存中。

#### 搜索功能实现

​	1.solr服务搭建。

​	2.配置业务域。中文分析器的配置。

​	3.把商品数据导入到索引库

​		solr的客户端：solrJ

​	4.搜索功能实现

​		使用solrJ实现搜索

#### 使用MQ实现系统之间的通信

​	1.Activemq，支持jms规范

​	2.通信方式

​		queue 点对点

​		topic  广播

​	3.Activemq整合spring

​		发送消息：JmsTemplate

​		接收消息：

​			1）实现MessageListener接口

​			2）配置MessageListenerContainer

​	4.添加商品到索引库

​		添加商品时，发送消息，activemq接收到消息后自动同步索引库。

#### 商品详情也页面

​	1.动态展示

​		jsp+redis，缓存需要设置过期时间，提高缓存利用率。

​	2.网页静态化freemarker

​		1）模板 --》基于jsp改造

​		2）数据 --》推荐使用map实现

​	3.静态化方案

​		1）mq发送消息

​		2）接收到消息，生成静态页面

​		3）Nginx访问

#### sso系统

​	主要解决的是Session共享的问题

​	1.使用redis管理Session

​		1）key：token（UUID生成）

​		2）value：用户信息

​		3）key需要设置有效期

​		4）需要把token保存到cookie中，供不同的域名访问到token

​	2.根据token取用户信息

​		1）从cookie中取token

​		2）到redis中查询用户信息

​		3）更新key的过期时间

#### 购物车

​	1.未登录：使用cookie保存购物车

​	2.登陆后：把购物车数据保存到redis

​	3.购物车合并，用户登陆后，需要把cookie中商品信息和redis中的商品信息合并

#####订单系统

​	订单号的生成，使用redis的incr

​	使用mycat实现分库分表



