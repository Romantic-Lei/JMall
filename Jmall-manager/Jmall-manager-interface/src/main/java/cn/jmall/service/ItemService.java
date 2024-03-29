package cn.jmall.service;

import cn.jmall.common.pojo.EasyUIDataGridResult;
import cn.jmall.common.util.E3Result;
import cn.jmall.common.util.FastDFSClient;
import cn.jmall.pojo.TbItem;
import cn.jmall.pojo.TbItemDesc;

/**
 * 
 * @author Jmall
 * @CreateDate 2020年3月3日
 * @Description
 */
public interface ItemService {
	
	public TbItem getItemById(long itemId);							// 根据商品id获取商品信息
	public EasyUIDataGridResult getItemList(int page, int rows);	// 分页获取商品信息
	public E3Result addItem(TbItem item, String desc);				// 商品添加
	public E3Result selectItemById(long itemId);					// 根据商品id获取商品信息
	public E3Result getTbItemDesc(long itemId);						// 根据商品id获取商品描述
	public E3Result updateItem(TbItem item, String desc);			// 商品信息修改
	public E3Result deleteBatchItem(String[] ids);					// 商品信息删除
	public E3Result productOnShelves(String[] ids);					// 商品上架
	public E3Result productOffShelves(String[] ids);				// 商品下架
	public TbItemDesc getItemDescById(long itemId);					// 根据商品id获取商品描述

}
