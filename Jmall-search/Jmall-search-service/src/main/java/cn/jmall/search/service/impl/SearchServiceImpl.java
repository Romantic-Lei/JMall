package cn.jmall.search.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.jmall.common.pojo.SearchResult;
import cn.jmall.search.dao.SearchDao;
import cn.jmall.search.service.SearchService;

/**
 * 商品搜索Service
 * 
 * @author Jmall
 * @CreateDate 2020年3月10日
 * @Description
 */
@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SearchDao searchDao;

	@Override
	public SearchResult search(String keyword, int page, int rows) throws Exception {
		// 创建一个SolrQuery对象
		SolrQuery query = new SolrQuery();
		// 设置查询条件
		query.setQuery(keyword);
		// 设置分页条件
		if (page <= 0) {
			page = 1;
		}
		
		query.setStart((page - 1) * rows);
		query.setRows(rows);
		// 设置默认搜索域
		query.set("df", "item_title");
		// 设置高亮显示
		query.setHighlight(true);
		query.addHighlightField("item_title");
		// 设置样式颜色
		query.setHighlightSimplePre("<em style=\"color:red\">");
		query.setHighlightSimplePost("</em>");
		// 调用dao执行查询
		SearchResult searchResult = searchDao.search(query);
		// 计算总页数
		long recordCount = searchResult.getRecordCount();
		int totalPage = (int) (recordCount / rows);
		
		if (recordCount % rows > 0) {
			totalPage++;
		}
		
		// 返回结果
		searchResult.setTotalPage(totalPage);
		return null;
	}

}
