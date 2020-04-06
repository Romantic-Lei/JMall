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

	@RequestMapping("/gen_item")
	public void gen_item(long goodsId) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		// 1. 获取配置对象
		Configuration configuration = config.getConfiguration();
		// 2. 获取模板对象
		Template template = configuration.getTemplate("test.ftl");
		// 3. 创建数据模型
		Map map = new HashMap();
		map.put("name", "李四");
		// 4.创建writer
		FileWriter out = new FileWriter(new File("D:/"));
		// 5.输出
		template.process(map, out);
		// 6.关闭连接
		out.close();
	}

}
