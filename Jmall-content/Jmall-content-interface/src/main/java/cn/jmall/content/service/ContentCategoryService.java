package cn.jmall.content.service;

import java.util.List;

import cn.jmall.common.pojo.EasyUITreeNode;
import cn.jmall.common.util.E3Result;

public interface ContentCategoryService {

	public List<EasyUITreeNode> getContentCatList(long parentId);	// 获取内容分类
	public E3Result addContentCategory(long parentId, String name);	// 添加子节点
	public void updateContentCategory(long id, String name);		// 更新当前结点
	public E3Result deleteContentCategory(long id);					// 删除当前结点
	
}
