package com.easybuy.search.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.easybuy.pojo.TbItem;
import com.easybuy.search.service.ItemSearchService;

/**
 * 搜索服务
 * 
 * @author Romantic
 * @CreateDate 2020年4月26日
 * @Description
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;

	@Override
	public Map<String, Object> search(Map<String, Object> searchMap) {

		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

		Query query = new SimpleQuery(criteria);

		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("rows", page.getContent());

		return resultMap;
	}

}
