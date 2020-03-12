# JMall
珞珈商贸系统

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

