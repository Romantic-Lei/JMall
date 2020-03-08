package cn.jmall.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisTest {

	@Test
	public void testJedis() {
		// 创建一个连接的Jedis对象
		Jedis jedis = new Jedis("192.168.0.102", 6379);
		// 选择连接的数据库
		jedis.select(3);
		// 使用Jedis操作redis
		jedis.set("test", "jedis test");
		System.out.println(jedis.get("test"));
		// 关闭连接
		jedis.close();
		
	}
	
	@Test
	public void testJedisPool() {
		// 创建一个连接池对象
		JedisPool jedisPool = new JedisPool("192.168.0.102", 6379);
		// 从连接池获得一个连接
		Jedis jedis = jedisPool.getResource();
		// 使用 jedis操作redis
		System.out.println(jedis.get("test"));
		// 关闭连接 ，每次使用完毕后关闭连接，连接池回收资源
		jedis.close();
		// 关闭连接池
		jedisPool.close();
	}
	
//	@Test
	public void testJedisCluster() {
		// 创建一个JedisCluster对象，有一个参数nodes是一个set类型。set中包含着若干个HostAndPort对象
		Set<HostAndPort> nodes = new HashSet<>();
//		nodes.add(new HostAndPort("192.168.0.102", 7001));
//		nodes.add(new HostAndPort("192.168.0.102", 7002));
//		nodes.add(new HostAndPort("192.168.0.102", 7003));
//		nodes.add(new HostAndPort("192.168.0.102", 7004));
//		nodes.add(new HostAndPort("192.168.0.102", 7005));
		nodes.add(new HostAndPort("192.168.0.102", 6379));
		JedisCluster jedisCluster = new JedisCluster(nodes);
		// 直接使用JedisCluster对象操作redis
		jedisCluster.set("test", "123");
		System.out.println(jedisCluster.get("test"));
		// 关闭jedisCluster对象
		jedisCluster.close();
	}
	
}
