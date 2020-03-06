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
import cn.jmall.pojo.TbItemDesc;
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
	
	// 加载商品信息
	@RequestMapping(value="/rest/item/param/item/query/{id}")
	@ResponseBody
	public E3Result getItemById(@PathVariable("id") long id) {
		
		return itemService.selectItemById(id);
	}
	
	// 加载商品描述
	@RequestMapping(value="/rest/item/query/item/desc/{id}")
	@ResponseBody
	public E3Result getItemDescById(@PathVariable("id") long id) {
		
		return itemService.getTbItemDesc(id);
	}
	
	// 商品信息修改保存
	@RequestMapping(value="/rest/item/update")
	@ResponseBody
	public E3Result updateItem(TbItem item, String desc) {
		
		return itemService.updateItem(item, desc);
	}

}
