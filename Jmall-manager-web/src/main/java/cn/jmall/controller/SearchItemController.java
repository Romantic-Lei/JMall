package cn.jmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.jmall.common.util.E3Result;
import cn.jmall.search.service.SearchItemService;

/**
 * 导入商品到索引库
 * @author Jmall
 * @CreateDate 2020年3月9日
 * @Description
 */
@Controller
public class SearchItemController {

	@Autowired
	private SearchItemService serachItemService;	
	
	@RequestMapping("/index/item/import")
	@ResponseBody
	public E3Result importItemList() {
		
		return serachItemService.importAllItems();
	}
	
}
