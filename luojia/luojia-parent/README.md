

# easybuy  易购商城

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



###缓存过期

​	目前redis只支持设置一级key的过期时间，所以在hash类型中，只要某一个key在更新，或者增加了二级key那么缓存过期时间又会被刷新，如果不断有二级key新增，那么所有的key都不会被删除。

```java
在生成短信验证码的时候，可以设计一级key不同才能有效的设置过期时间。或者直接使用String类型来保存验证码
//1.生成随机数6位
String smscode= (long) (Math.random()*1000000)+"";
System.out.println("短信验证码："+smscode);
//2.存入redis
redisTemplate.boundHashOps("smscode" + phone).put(phone, smscode);
redisTemplate.expire("smscode" + phone, 180, TimeUnit.SECONDS);
```





###Activemq监听



#####	在springboot和spring中使用activemq

在用户号注册时，需要有手机接收验证码，此处可以使用阿里大于来做短信验证码平台，在用户注册页面，输入用户相关信息之后，在服务层生成一串数字（你喜欢的话，生成字母也行......）

```java
@Autowired
private JmsTemplate jmsTemplate;
@Autowired
private Destination smsDestination;

public void createSmsCode(String phone) {
    //1.生成随机数6位,更加严谨的方法是在此处判断验证码是否有六位，没有的话需要重新生成或者补上0
    String smscode= (long) (Math.random()*1000000)+"";
    System.out.println("短信验证码："+smscode);
    //2.存入redis
    redisTemplate.boundHashOps("smscode" + phone).put(phone, smscode);
    redisTemplate.expire("smscode" + phone, 180, TimeUnit.SECONDS);

    //3.发送给activeMQ

    jmsTemplate.send(smsDestination, new MessageCreator() {

        @Override
        public Message createMessage(Session session) throws JMSException {

            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("mobile", phone);

            Map map=new HashMap<>();
            map.put("number", smscode);
			// JSON.toJSONString(map) 将map转成json串
            mapMessage.setString("param", JSON.toJSONString(map));
            return mapMessage;
        }
    });
		
	}
```

XML中的主要配置:

```xml
<!-- Spring提供的JMS工具类，它可以进行消息发送、接收等 -->  
<bean id="jmsTemplate" class="org.springframework.jms.core.JmsTemplate">  
    <!-- 这个connectionFactory对应的是我们定义的Spring提供的那个ConnectionFactory对象 -->  
    <property name="connectionFactory" ref="connectionFactory"/>  
</bean>      
<!--这个是队列目的地，点对点的  文本信息-->  
<bean id="smsDestination" class="org.apache.activemq.command.ActiveMQQueue">  
    <constructor-arg value="ebSms"/>  
```

在spring中使用activemq中配置非常少,在服务端发送消息后，我们在短信生成微服务上就可以监听到消息：

```java
@JmsListener(destination="ebSms") // 需要和上文对应即可
	public void sendSms(Map<String, String> map) {
		try {
			SendSmsResponse response = SmsUtil.sendSms(
					map.get("mobile"),
					map.get("signName"), 
					map.get("templateCode"),
					map.get("param"));
			System.out.println("code " + response.getCode());
			
		} catch (ClientException e) {
			
			e.printStackTrace();
		}
	}
```




