package com.easybuy.portal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.easybuy.content.service.ContentService;
import com.easybuy.pojo.TbContent;

@RestController
@RequestMapping("/content")
public class ContentConroller {

	@Reference
	private ContentService contentService;
	
	@RequestMapping("/findByCategoryKey")
	public List<TbContent> findByCategoryKey(String key){
		
		return contentService.findByCategoryKey(key);
	}
	
}
