package cn.jmall.search.message;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;

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
