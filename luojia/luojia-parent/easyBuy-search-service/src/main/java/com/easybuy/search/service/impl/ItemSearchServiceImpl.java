package com.easybuy.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.easybuy.mapper.TbItemCatMapper;
import com.easybuy.mapper.TbSpecificationOptionMapper;
import com.easybuy.mapper.TbTypeTemplateMapper;
import com.easybuy.pojo.TbItem;
import com.easybuy.pojo.TbItemCat;
import com.easybuy.pojo.TbItemCatExample;
import com.easybuy.pojo.TbSpecificationOption;
import com.easybuy.pojo.TbSpecificationOptionExample;
import com.easybuy.pojo.TbTypeTemplate;
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
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public Map<String, Object> search(Map<String, Object> searchMap) {

		Map<String, Object> resultMap = new HashMap<>();
		Query query = new SimpleQuery();
		// 1.根据关键字查询商品列表
		// 1.1 构建查询条件，按关键字搜索
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);

		
		// 1.2 构建筛选条件，按分类搜索
		if(searchMap.get("category") != null && !searchMap.get("category").equals("")) {
			FilterQuery filterQuery = new SimpleFacetQuery(new Criteria("item_category").is(searchMap.get("category")));
			query.addFilterQuery(filterQuery );
		}
		
		// 1.3 构建筛选条件，按品牌搜索
		if(searchMap.get("brand") != null && !searchMap.get("brand").equals("")) {
			FilterQuery filterQuery = new SimpleFacetQuery(new Criteria("item_brand").is(searchMap.get("brand")));
			query.addFilterQuery(filterQuery );
		}
		
		// 1.4构建筛选条件，按规格筛选
		Map<String, Object> sepcMap = (Map) searchMap.get("spec");
		if(sepcMap != null) {
			for (String key : sepcMap.keySet()) {
				
				FilterQuery filterQuery = new SimpleFacetQuery(new Criteria("item_spec_" + key).is(sepcMap.get(key)));
				query.addFilterQuery(filterQuery );
			}
		}
		
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
		resultMap.put("rows", page.getContent());

		// 2.根据关键字查询分类列表（分组查询 类似sql的 group by）

		List<String> categoryList = new ArrayList<String>();
		if(searchMap.get("category").equals("")) {
			// 若果用户没有选择分类条件
			GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
			query.setGroupOptions(groupOptions);
			GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
			GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
			Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
			List<GroupEntry<TbItem>> cataList = groupEntries.getContent();
			
			
			for (GroupEntry<TbItem> entry : cataList) {
				categoryList.add(entry.getGroupValue());
			}
			
			resultMap.put("categoryList", categoryList);
		}else {
			// 将条件添加到列表
			categoryList.add((String) searchMap.get("category"));
		}

		// 3.根据查询的商品第一个分类查询品牌列表
		if (categoryList.size() > 0) {
			String category = categoryList.get(0); // 分类列表
			Map<String, Object> map = searchTemplateByCategoryFromRedis(category);
			
			if(searchMap.get("brand").equals("")) {
				resultMap.put("brandList", map.get("brandList"));
			}
			
			// 4.根据第一个商品分类查询规格
			// 判断规格结果中，如果规格名称在查询条件（resultMap）中已经存在,则移除
			
			Map spec_result = (Map) map.get("specMap");// 规格查询的结果
			Map<String, Object> spec_search = (Map) searchMap.get("spec");// 查询条件-规格
			
			for (String key : spec_search.keySet()) {
				spec_result.remove(key);
			}
			
			resultMap.put("specMap", spec_result);
			
		}

		return resultMap;
	}

	/**
	 * 根据商品分类查询模板（在缓存中提取）
	 * @param category
	 * @return
	 */
	private Map<String, Object> searchTemplateByCategoryFromRedis(String category) {
		
		// 首先在缓存中查询
		Map<String, Object> result = (Map<String, Object>) redisTemplate.boundHashOps("searchTemplate").get(category);
		if(result == null) {
			// 如果缓存中没有，去数据库中查询
			result = searchTemplateByCategory(category);
			// 将数据装入缓存
			redisTemplate.boundHashOps("searchTemplate").put(category, result);
		}
		
		return result;
	}
	
	/**
	 * 根据商品分类查询模板
	 * 
	 * @param category
	 * @return
	 */
	private Map<String, Object> searchTemplateByCategory(String category) {
		Map<String, Object> map = new HashMap<>();
		// 1.根据分类名称去tb_item_cat表中查询模板id		
		TbItemCatExample example = new TbItemCatExample();
		com.easybuy.pojo.TbItemCatExample.Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(category);
		List<TbItemCat> itemCatList = itemCatMapper.selectByExample(example);
		
		// 2.根据模板id查询模板对象
		if(itemCatList.size() > 0) {
			TbItemCat itemCat = itemCatList.get(0);
			TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(itemCat.getTypeId());
			
			// 3.得到品牌列表
			List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
			map.put("brandList", brandList);
			
			// 4.得到规格列表
			List<Map> specList = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
			Map specMap = new HashMap<>();
			
			for (Map spec : specList) {
				// 根据规格ID查询规格选项列表
				
				TbSpecificationOptionExample example1 = new TbSpecificationOptionExample();
				com.easybuy.pojo.TbSpecificationOptionExample.Criteria criteria2 = example1.createCriteria();
				Integer specId = (Integer) spec.get("id");
				
				criteria2.andSpecIdEqualTo(Long.parseLong(specId + ""));
				
				List<TbSpecificationOption> optionList = specificationOptionMapper.selectByExample(example1);
				specMap.put(spec.get("text"), optionList);
			}
			map.put("specMap", specMap);
		}

		return map;
	}

}
