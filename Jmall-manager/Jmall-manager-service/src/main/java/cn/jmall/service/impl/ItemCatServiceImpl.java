package cn.jmall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.jmall.common.pojo.EasyUITreeNode;
import cn.jmall.mapper.TbItemCatMapper;
import cn.jmall.pojo.TbItemCat;
import cn.jmall.pojo.TbItemCatExample;
import cn.jmall.service.ItemCatService;

/**
 * 商品分类管理
 * @author Jmall
 * @CreateDate 2020年3月4日
 * @Description
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Override
	public List<EasyUITreeNode> getItemCatlist(long parentId) {
		// 根据parentId 查询子节点列表
		TbItemCatExample example = new TbItemCatExample();
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		// 创建返回结果list
		List<EasyUITreeNode> resultList = new ArrayList<>();
		// 把列表转换成EasyUITreeNode列表
		for (TbItemCat tbItemCat : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			// 设置属性
			node.setId(tbItemCat.getId());
			node.setText(tbItemCat.getName());
			node.setState(tbItemCat.getIsParent()?"closed" : "open");
			// 添加到结果列表
			resultList.add(node);
		}
		// 返回列表
		return resultList;
	}

}
