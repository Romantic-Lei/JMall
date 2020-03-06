package cn.jmall.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页展示Controller
 * @author Jmall
 * @CreateDate 2020年3月6日
 * @Description
 */
@Controller
public class IndexController {

	@RequestMapping("/index")
	public String showIndex() {
		
		return "index";
	}
	
}
