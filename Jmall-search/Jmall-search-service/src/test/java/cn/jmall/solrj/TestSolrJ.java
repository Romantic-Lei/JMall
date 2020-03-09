package cn.jmall.solrj;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrJ {

	// 添加
	@Test
	public void addDocument() throws SolrServerException, IOException {
		// 创建一个SolrServer 对象，创建一个连接，参数solr服务的url
		SolrServer solrServer = new HttpSolrServer("http://192.168.0.106:8080/solr/collection1");
		// 创建一个文档对象SolrInputDocument
		SolrInputDocument document = new SolrInputDocument();
		// 向文档对象中添加域，文档中必须包含一个id域，所有的域的名称必须在schema.xml中定义
		document.addField("id", "doc1");
		document.addField("item_title", "添加商品");
		document.addField("item_price", 1000);
		// 把文档写入索引库
		solrServer.add(document);
		// 提交
		solrServer.commit();
	}
	
	// 更新没有特定的方法，就是使用的添加方法，保持id不变，然后回去数据库里面删除，最后添加新数据到数据库
	// 删除
	@Test
	public void deleteDocument() throws SolrServerException, IOException {
		HttpSolrServer solrServer = new HttpSolrServer("http://192.168.0.106:8080/solr/collection1");
		// 删除文档
		// solrServer.deleteById("doc1");
		// 和上面效果相同
		solrServer.deleteByQuery("id:doc1");
		// 提交
		solrServer.commit();
	}
	
}
