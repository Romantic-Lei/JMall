package cn.jmall.solrj;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrCloud {
	
//	@Test
	public void testAddDocument() throws SolrServerException, IOException {
		// 创建一个集群
		CloudSolrServer solrServer = new CloudSolrServer("192.168.0.106:2180");
		// zkHost:zookeeper的地址列表
		// 设置一个defaultCollection属性：
		solrServer.setDefaultCollection("collection2");
		// 创建一个文档对象
		SolrInputDocument document = new SolrInputDocument();
		// 向文档中添加域
		document.setField("id", "solrcloud01");
		document.setField("item_title", "商品01");
		document.setField("item_price", 123);
		// 把文件写入到索引库
		solrServer.add(document);
		// 提交
		solrServer.commit();
	}

}
