package cn.jmall.solrj;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
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
		solrServer.deleteByQuery("id:158401674254732");
		// 提交
		solrServer.commit();
	}
	
	@Test
	public void queryIndex() throws SolrServerException {
		SolrServer solrServer = new HttpSolrServer("http://192.168.0.106:8080/solr/collection1");
		// 设置SolrServer 对象
		SolrQuery query = new SolrQuery();
		// 设置查询条件
		// query.setQuery("*:*");
		query.set("q", "*:*");
		// 执行查询，QueryResponse对象
		QueryResponse queryResponse = solrServer.query(query);
		// 取得文档，获取总记录数
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		System.out.println("查询到的总记录数：" + solrDocumentList.getNumFound());
		// 默认分页为10条
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_sell_point"));
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_image"));
			System.out.println(solrDocument.get("item_category_name"));
		}
	}
	
	@Test
	public void queryIndexFuza() throws SolrServerException {
		SolrServer solrServer = new HttpSolrServer("http://192.168.0.106:8080/solr/collection1");
		// 设置SolrServer 对象
		SolrQuery query = new SolrQuery();
		// 设置查询条件
		 query.setQuery("手机");
		 query.setStart(0); // 起始页
		 query.setRows(20); // 每页显示数
		 query.set("df", "item_title"); // 设置搜索域
		 query.setHighlight(true);	// 是否高亮
		 query.addHighlightField("item_title"); // 高亮字段
		 query.setHighlightSimplePre("<em>"); // 前缀
		 query.setHighlightSimplePost("</em>"); // 后缀
		// 执行查询，QueryResponse对象
		QueryResponse queryResponse = solrServer.query(query);
		// 取得文档，获取总记录数
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		System.out.println("查询到的总记录数：" + solrDocumentList.getNumFound());
		// 默认分页为10条
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			
			Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			String title = "";
			if(list != null && list.size() > 0 ) {
				title = list.get(0);
			}else {
				// 在 搜索域 item_title找不到 关键字则不高亮显示
				title = (String) solrDocument.get("item_title");
			}
			
			System.out.println(title);
			System.out.println(solrDocument.get("item_sell_point"));
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_image"));
			System.out.println(solrDocument.get("item_category_name"));
		}
	}
	
}
