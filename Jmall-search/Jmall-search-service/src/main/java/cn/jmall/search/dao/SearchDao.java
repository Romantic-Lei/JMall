package cn.jmall.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.jmall.common.pojo.SearchItem;
import cn.jmall.common.pojo.SearchResult;

/**
 * 商品搜索DAO
 * @author Jmall
 * @CreateDate 2020年3月10日
 * @Description
 */
@Repository
public class SearchDao {
	
	@Autowired
	private SolrServer solrServer;

	public SearchResult search(SolrQuery query) throws SolrServerException {
		// 根据 query 查询索引库
		QueryResponse queryResponse = solrServer.query(query);
		// 查询结果
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		
		// 查询结果总记录数
		long numFound = solrDocumentList.getNumFound();
		SearchResult result = new SearchResult();
		result.setRecordCount(numFound);
		// 取商品列表，需要高亮显示 
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		List<SearchItem> itemList = new ArrayList<>();
		for (SolrDocument solrDocument : solrDocumentList) {
			SearchItem item = new SearchItem();
			item.setId((String) solrDocument.get("id"));
			item.setCategory_name((String) solrDocument.get("item_category_name"));
			item.setImage((String) solrDocument.get("item_image"));
			item.setPrice((long) solrDocument.get("item_price"));
			item.setSell_point((String) solrDocument.get("item_sell_point"));
			// 取高亮显示
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			String title = "";
			if (list != null && list.size() > 0) {
				title = list.get(0);
			} else {
				title = (String) solrDocument.get("item_title");
			}
			item.setTitle(title);
			// 添加到商品列表
			itemList.add(item);
		}
		result.setItemList(itemList);
		// 返回结果
		return result;
	}
}
