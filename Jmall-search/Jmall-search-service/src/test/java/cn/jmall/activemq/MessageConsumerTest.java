package cn.jmall.activemq;

import java.io.IOException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MessageConsumerTest {

	/**
	 * 监听消息的发送 ，监听到之后会自动打印
	 * @throws IOException
	 */
//	@Test
	public void msgConsumer() throws IOException {
//		初始化容器
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
		System.in.read();
	}
	
}
