package cn.jmall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.jmall.common.pojo.EasyUITreeNode;
import cn.jmall.common.util.E3Result;
import cn.jmall.content.service.ContentCategoryService;

/**
 * 内容分类管理Controller
 * @author Jmall
 * @CreateDate 2020年3月6日
 * @Description
 */
@Controller
public class ContentCatController {

	@Autowired
	private ContentCategoryService contentCategoryService;
	
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCatList(
			@RequestParam(name="id", defaultValue="0") Long parentId){
		
		return contentCategoryService.getContentCatList(parentId);
	}
	
	// 添加分类结点
	@RequestMapping(value="/content/category/create", method=RequestMethod.POST)
	@ResponseBody
	public E3Result addContentCategory(long parentId, String name){
		// 调用服务添加结点
		
		return contentCategoryService.addContentCategory(parentId, name);
	}
	
}
