package cn.jmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.jmall.common.pojo.EasyUIDataGridResult;
import cn.jmall.common.util.E3Result;
import cn.jmall.pojo.TbItem;
import cn.jmall.service.ItemService;

/**
 * 商品管理
 * @author Jmall
 * @CreateDate 2020年3月3日
 * @Description
 */
@Controller
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem getItemById(@PathVariable Long itemId) {
		TbItem itemById = itemService.getItemById(itemId);
		return itemById;
	}
	
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
		EasyUIDataGridResult result = itemService.getItemList(page, rows);
		return result;
	}
	
	// 商品添加功能
	@RequestMapping(value="/item/save", method=RequestMethod.POST)
	@ResponseBody
	public E3Result addItem(TbItem tbItem, String desc) {
		E3Result result = itemService.addItem(tbItem, desc);
		return result;
	}
	

}
