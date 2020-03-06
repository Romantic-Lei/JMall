package cn.jmall.content.service;

import java.util.List;

import cn.jmall.common.pojo.EasyUITreeNode;
import cn.jmall.common.util.E3Result;

public interface ContentCategoryService {

	public List<EasyUITreeNode> getContentCatList(long parentId);	// 获取内容分类
	public E3Result addContentCategory(long parentId, String name);	// 添加子节点
	
	
}
