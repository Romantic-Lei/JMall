

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



###spring-security安全管理

在业务流程中，常常不同的登陆者对应着不同的管理权限，而springSecurity能很好的满足这一点，在使用springSecurity时需要导入对应的maven依赖

```markdown
<!-- 身份验证 -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>4.1.0.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>4.1.0.RELEASE</version>
</dependency>
```

web.xml配置：

```xml
<filter>
    <filter-name>springSecurityFilterChain</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>
<filter-mapping>
    <filter-name>springSecurityFilterChain</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

在spring-security.xml文件中，配置用户的权限，配置登录页，登录成功跳转页，登录失败跳转页，登录成功默认跳转形式

```xml
<!-- 页面的拦截规则  use-expressions:是否启用SPEL表达式，默认是true -->
<http use-expressions="false">
    <!-- 当前用户必须有ROLE_ADMIN的角色才可以访问根目录及所属子目录的资源 -->
    <!-- 如果use-expressions="true"，那么下面access="hasRole('ROLE_SELLER')" -->
    <intercept-url pattern="/**" access="ROLE_SELLER"/>
    <!-- 开启表单登录功能 -->
    <!-- 
   login-page：拦截登录页
   default-target-url：登录成功跳转页
   authentication-failure-url：登录验证失败跳转页
   always-use-default-target：登录成功访问默认登陆成功页，不会跳转到输入的请求页面
   -->
    <form-login login-page="/shoplogin.html" default-target-url="/admin/index.html" authentication-failure-url="/shoplogin.html" always-use-default-target="true"/>
    <csrf disabled="true"/>
    <headers>
        <!-- 不拦截静态帧框架 -->
        <frame-options policy="SAMEORIGIN"/>
    </headers>
    <logout/>
</http>
```

认证配置

```xml
<!-- 认证管理器 -->
<authentication-manager>
    <authentication-provider user-service-ref="userDetailService">
        <password-encoder ref="passwordEncoder"/>
    </authentication-provider>
</authentication-manager>

<!-- 认证类 -->
<bean:bean id="userDetailService" class="com.easybuy.service.UserDetailServiceImpl">
    <bean:property name="sellerService" ref="sellerService"></bean:property>
</bean:bean>

<!-- 引用dubbo 服务 -->
	<dubbo:application name="easybuy-shop-web" />
	<dubbo:registry address="zookeeper://192.168.1.7:2181"/>
	<dubbo:reference id="sellerService" interface="com.easybuy.sellergoods.service.SellerService" timeout="300000"></dubbo:reference>
	
<!-- bcrypt加密 -->
<bean:bean id="passwordEncoder" class="org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder"></bean:bean>
```

用户角色管理：

```java
public class UserDetailServiceImpl implements UserDetailsService {

	private SellerService sellerService;
	
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		// 构建角色列表
		Collection<GrantedAuthority> authorities = new ArrayList();
		authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		
		TbSeller seller = sellerService.findOne(username);
		if(seller != null) {
			// 商家状态必须是已审核通过才让登陆
			if(seller.getStatus().equals("1")) {
				// springsecurity 会自动将用户名和密码拿去比对看是否正确
				return new User(username, seller.getPassword(), authorities);
			}
			// 未通过审核
			return null;
		}else {
			// 商家不存在
			return null;
		}
	}
}
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




