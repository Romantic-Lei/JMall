package cn.jmall.service;

import java.util.List;

import cn.jmall.common.pojo.EasyUITreeNode;

public interface ItemCatService {

	public List<EasyUITreeNode> getItemCatlist(long parentId);
}
