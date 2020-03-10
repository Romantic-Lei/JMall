package cn.jmall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.jmall.common.pojo.SearchResult;
import cn.jmall.search.service.SearchService;

/**
 * 商品搜索Controller
 * @author Jmall
 * @CreateDate 2020年3月10日
 * @Description
 */
@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;
	@Value("${SEARCH_RESULT_ROWS}")
	private Integer SEARCH_RESULT_ROWS;
	
	@RequestMapping
	public String searchItemList(
			String keyword, 
			@RequestParam(defaultValue="1")Integer page, 
			Model model) throws Exception {
		keyword = new String(keyword.getBytes("iso-8859-1"), "utf-8");
		// 查询商品
		SearchResult searchResult = searchService.search(keyword, page, SEARCH_RESULT_ROWS);
		// 把结果传递到页面
		model.addAttribute("query", keyword);
		model.addAttribute("totalPages", searchResult.getTotalPage());
		model.addAttribute("page", page);
		model.addAttribute("recordCount", searchResult.getRecordCount());
		model.addAttribute("itemList", searchResult.getItemList());
		// 返回逻辑视图
		return "search";
	}
	
}
