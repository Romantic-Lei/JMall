package com.easybuy.manager.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.easybuy.pojogroup.Goods;
import com.easybuy.sellergoods.service.GoodsService;
import com.easybuy.sellergoods.service.ItemCatService;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@RestController
public class HtmlController {

	@Autowired
	private FreeMarkerConfigurer config;
	@Reference
	private GoodsService goodsService;
	@Reference
	private ItemCatService itemCatService;

	@RequestMapping("/gen_item")
	public void gen_item(long goodsId) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		// 1. 获取配置对象
		Configuration configuration = config.getConfiguration();
		// 2. 获取模板对象
		Template template = configuration.getTemplate("item.ftl");
		// 3. 创建数据模型
		Goods goods = goodsService.findOne(goodsId);
		
		// 查询商品分类
		String category1 = itemCatService.findOne(goods.getGoods().getCategory1Id()).getName();
		String category2 = itemCatService.findOne(goods.getGoods().getCategory2Id()).getName();
		String category3 = itemCatService.findOne(goods.getGoods().getCategory3Id()).getName();
		Map map = new HashMap();
		map.put("category1", category1);
		map.put("category2", category2);
		map.put("category3", category3);
		goods.setMap(map);
		
		// 4.创建writer
		FileWriter out = new FileWriter(new File("D:/item/" + goodsId +".html"));
		// 5.输出
		template.process(goods, out);
		// 6.关闭连接
		out.close();
	}

}
