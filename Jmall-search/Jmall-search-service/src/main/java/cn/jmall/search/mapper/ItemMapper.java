package cn.jmall.search.mapper;

import java.util.List;

import cn.jmall.common.pojo.SearchItem;

public interface ItemMapper {

	public List<SearchItem> getItemList();
	public SearchItem getItemById(long itemId);
	
}
