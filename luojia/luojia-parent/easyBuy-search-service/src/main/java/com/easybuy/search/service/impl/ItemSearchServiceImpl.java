package com.easybuy.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
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

		Map<String, Object> resultMap = new HashMap<>();
		Query query = new SimpleQuery();
		// 1.根据关键字查询商品列表
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);

		resultMap.put("rows", page.getContent());

		// 2.根据关键字查询分类列表（分组查询 类似sql的 group by）

		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
		GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		List<GroupEntry<TbItem>> cataList = groupEntries.getContent();

		List<String> categoryList = new ArrayList<String>();
		for (GroupEntry<TbItem> entry : cataList) {
			categoryList.add(entry.getGroupValue());
		}

		resultMap.put("categoryList", categoryList);

		return resultMap;
	}

}
