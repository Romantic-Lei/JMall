package cn.jmall.content.service;

import java.util.List;

import cn.jmall.common.pojo.EasyUITreeNode;

public interface ContentCategoryService {

	public List<EasyUITreeNode> getContentCatList(long parentId);
	
	
}
