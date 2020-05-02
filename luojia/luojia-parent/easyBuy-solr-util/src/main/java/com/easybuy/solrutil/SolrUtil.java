package com.easybuy.solrutil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easybuy.mapper.TbItemMapper;
import com.easybuy.pojo.TbItem;
import com.easybuy.pojo.TbItemExample;
import com.easybuy.pojo.TbItemExample.Criteria;

@Component
public class SolrUtil {
	
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private SolrTemplate solrTemplate;

	public static void main(String[] args) {
		
		ApplicationContext content=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil=  (SolrUtil) content.getBean("solrUtil");
		solrUtil.importData();
	}
	
	// 导入数据
	public void importData() {
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1"); // 状态为1的才可以查询
		
		List<TbItem> list = itemMapper.selectByExample(example);
		
		for (TbItem item : list) {
			
			String specJsonString = item.getSpec();
			Map specMap = JSON.parseObject(specJsonString);
			
			item.setSpecMap(specMap);
			
		}
		
		System.out.println("开始导入数据");
		
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
		
		System.out.println("导入数据完成");
		
	}
	
}
