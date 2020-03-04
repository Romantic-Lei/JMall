package cn.jmall.service;

import cn.jmall.common.pojo.EasyUIDataGridResult;
import cn.jmall.pojo.TbItem;

/**
 * 
 * @author Jmall
 * @CreateDate 2020年3月3日
 * @Description
 */
public interface ItemService {
	
	public TbItem getItemById(long itemId);
	public EasyUIDataGridResult getItemList(int page, int rows);

}
