package cn.jmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.jmall.common.pojo.EasyUIDataGridResult;
import cn.jmall.common.util.E3Result;
import cn.jmall.content.service.ContentService;
import cn.jmall.pojo.TbContent;

/**
 * 内容管理COntroller
 * @author Jmall
 * @CreateDate 2020年3月7日
 * @Description
 */
@Controller
public class ContentController {

	@Autowired
	private ContentService contentService;
	
	@RequestMapping(value="/content/save", method=RequestMethod.POST)
	@ResponseBody
	public E3Result addContent(TbContent content) {
		// 调用读物把内容保存到数据库
		
		return contentService.addContent(content);
	}
	
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult listContent(long categoryId, int page, int rows) {
		// 分页查询
		
		return contentService.listContent(categoryId, page, rows);
	}
	
	// 内容管理更新
	@RequestMapping("/rest/content/edit")
	@ResponseBody
	public E3Result updateContent(TbContent content) {
		
		return contentService.updateContent(content);
	}
	
}
