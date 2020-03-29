package cn.jmall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.jmall.common.pojo.EasyUITreeNode;
import cn.jmall.service.ItemCatService;

/**
 * 商品分类管理Controller
 * @author Jmall
 * @CreateDate 2020年3月4日
 * @Description
 */
@Controller
public class ItemCatController {
	
	@Autowired
	private ItemCatService itemCatService;
	
	@RequestMapping("/item/cat/list")
	@ResponseBody
	public List<EasyUITreeNode> getItemCatList(
			@RequestParam(name="id", defaultValue="0") Long parentId){
		// 调用服务查询
		List<EasyUITreeNode> list = itemCatService.getItemCatlist(parentId);
		return list;
	}

}
