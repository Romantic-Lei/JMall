package com.easybuy.search.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.easybuy.search.service.ItemSearchService;

@RestController
@RequestMapping("/itemsearch")
public class SearchItemController {

	@Reference
	private ItemSearchService itemSearchService;
	
	@RequestMapping("/search")
	public Map<String,Object> searchMap(@RequestBody Map<String, Object> map){
		
		return itemSearchService.search(map);
	}
	
}
