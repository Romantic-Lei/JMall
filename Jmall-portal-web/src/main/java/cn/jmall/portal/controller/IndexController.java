package cn.jmall.portal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.jmall.content.service.ContentService;
import cn.jmall.pojo.TbContent;

/**
 * 首页展示Controller
 * @author Jmall
 * @CreateDate 2020年3月6日
 * @Description
 */
@Controller
public class IndexController {

	@Value("${CONTENT_LUNBO_ID}")
	private Long CONTENT_LUNBO_ID;
	
	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/index")
	public String showIndex(Model model) {
		// 查询内容列表
		List<TbContent> ad1List = contentService.getContentListByCid(CONTENT_LUNBO_ID);
		// 将结果传递给页面
		model.addAttribute("ad1List", ad1List);
		
		return "index";
	}
	
}
