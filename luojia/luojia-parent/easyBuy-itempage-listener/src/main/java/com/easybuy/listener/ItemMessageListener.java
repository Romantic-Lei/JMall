package com.easybuy.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息监听类
 * @author Romantic
 * @CreateDate 2020年4月9日
 * @Description
 */
@Component
public class ItemMessageListener implements MessageListener {

	@Autowired
	private MyJob myJob;
	
	@Override
	public void onMessage(Message message) {
		
		TextMessage textMessage = (TextMessage)message;
		try {
			Long goodsId = Long.parseLong(textMessage.getText());
			myJob.genItem(goodsId);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
