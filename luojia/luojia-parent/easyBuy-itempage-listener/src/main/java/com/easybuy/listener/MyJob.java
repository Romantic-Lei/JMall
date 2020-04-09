package com.easybuy.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.easybuy.pojogroup.Goods;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * 任务类，生成商品详情页
 * @author Romantic
 * @CreateDate 2020年4月9日
 * @Description
 */
@Component
public class MyJob {

	@Autowired
	private FreeMarkerConfigurer config;

	@Autowired
	private GoodsService goodsService;
	
	@Value("${TARGET_DIR}")
	private String target_dir;

	public void genItem(Long goodsId)
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		// 1. 获取配置对象
		Configuration configuration = config.getConfiguration();
		// 2. 获取模板对象
		Template template = configuration.getTemplate("item.ftl");
		// 3. 创建数据模型
		Goods goods = goodsService.findOne(goodsId);
		// 4.创建writer
		FileWriter out = new FileWriter(new File(target_dir + goodsId + ".html"));
		// 5.输出
		template.process(goods, out);
		// 6.关闭连接
		out.close();
	}

}
