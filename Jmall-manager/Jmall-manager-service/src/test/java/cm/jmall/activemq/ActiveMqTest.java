package cm.jmall.activemq;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class ActiveMqTest {

	/**
	 * 点对点发送消息
	 * 当消息接受者未开启时，我们的消息就保存在队列中，如果消息接收到了就从队列中删除
	 * @throws JMSException 
	 */
	@Test
	public void testQueueProduct() throws JMSException {
//		1.创建一个连接工厂，需要指定服务的ip及端口
		ConnectionFactory ConnectionFactory = new ActiveMQConnectionFactory("tcp://192.168.0.102:61616");
//		2.使用工厂对象创建一个Connection对象
		Connection connection = ConnectionFactory.createConnection();
//		3.开启连接，调用对象Connection对象的start方法
		connection.start();
//		4.创建一个Session对象
//		第一个参数：是否开启事务。如果开启事务，第二个参数无意义一般不开启事务
//		第二个参数：应答模式。自动应答或者手动应答。一般自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		5.使用Session对象创建Destination对象,两种形式， queue，topic。现在使用queue
		Queue queue = session.createQueue("test-queue");
//		6.使用Session对象创建一个Producer对象
		MessageProducer producer = session.createProducer(queue);
//		7.创建一个Message对象，可以使用TextMessage
//		ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
//		textMessage.setText("hello ActiveMQ");
		TextMessage textMessage = session.createTextMessage("hello ActiveMQ");
//		8.发送消息
		producer.send(textMessage);
//		9.关闭资源
		producer.close();
		session.close();
		connection.close();
	}
	
	@Test
	public void testQueueConsumer() throws JMSException, IOException {
//		创建一个ConnectionFactory对象连接MQ服务器
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.0.102:61616");
//		创建一个连接对象
		Connection connection = connectionFactory.createConnection();
//		开启连接
		connection.start();
//		使用Connection对象创建一个Session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		创建一个Destination对象创建一个消费者对象
		Queue queue = session.createQueue("test-queue");
//		使用Session对象创建一个消费者对象
		MessageConsumer consumer = session.createConsumer(queue);
//		接收消息
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
//				打印结果
				TextMessage textMessage = (TextMessage) message;
				String text;
				try {
					text = textMessage.getText();
					System.out.println(text);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
//		等待接收消息
		System.in.read();
//		关闭资源
		connection.close();
		session.close();
		connection.close();
	}
	
	
	/**
	 * 广播,只有消息接收开启着才能接收到我们的广播，否则发送的消息接收不到
	 */
	@Test
	public void testTopicProduct() throws JMSException {
//		1.创建一个连接工厂，需要指定服务的ip及端口
		ConnectionFactory ConnectionFactory = new ActiveMQConnectionFactory("tcp://192.168.0.102:61616");
//		2.使用工厂对象创建一个Connection对象
		Connection connection = ConnectionFactory.createConnection();
//		3.开启连接，调用对象Connection对象的start方法
		connection.start();
//		4.创建一个Session对象
//		第一个参数：是否开启事务。如果开启事务，第二个参数无意义一般不开启事务
//		第二个参数：应答模式。自动应答或者手动应答。一般自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		5.使用Session对象创建Destination对象，两种形式， queue，topic。现在使用topic
		Topic topic = session.createTopic("test-topic");
//		6.使用Session对象创建一个Producer对象
		MessageProducer producer = session.createProducer(topic);
//		7.创建一个Message对象，可以使用TextMessage
//		ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
//		textMessage.setText("hello ActiveMQ");
		TextMessage textMessage = session.createTextMessage("topic message");
//		8.发送消息
		producer.send(textMessage);
//		9.关闭资源
		producer.close();
		session.close();
		connection.close();
	}
	
	@Test
	public void testTopicConsumer() throws JMSException, IOException {
//		创建一个ConnectionFactory对象连接MQ服务器
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.0.102:61616");
//		创建一个连接对象
		Connection connection = connectionFactory.createConnection();
//		开启连接
		connection.start();
//		使用Connection对象创建一个Session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		创建一个Destination对象创建一个消费者对象
		Topic topic = session.createTopic("test-topic");
//		使用Session对象创建一个消费者对象
		MessageConsumer consumer = session.createConsumer(topic);
//		接收消息
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
//				打印结果
				TextMessage textMessage = (TextMessage) message;
				String text;
				try {
					text = textMessage.getText();
					System.out.println(text);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
//		等待接收消息
		System.in.read();
//		关闭资源
		connection.close();
		session.close();
		connection.close();
	}
	
}
