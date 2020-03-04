package cn.jmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	
	@RequestMapping("/")
	public String showIndex() {
		return "index";
	}
	
	// 请求哪一个页面就去对应的页面的jsp文件
	@RequestMapping("/{page}")
	public String showPage(@PathVariable("page") String page) {
		return page;
	}

}
