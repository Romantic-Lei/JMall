package cn.jmall.content.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.jmall.common.pojo.EasyUITreeNode;
import cn.jmall.content.service.ContentCategoryService;
import cn.jmall.mapper.TbContentCategoryMapper;
import cn.jmall.pojo.TbContentCategory;
import cn.jmall.pojo.TbContentCategoryExample;
import cn.jmall.pojo.TbContentCategoryExample.Criteria;

/**
 * 内容分类管理
 * @author Jmall
 * @CreateDate 2020年3月6日
 * @Description
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCatList(long parentId) {
		// 根据parentId查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		// 设置查询条件
		criteria.andParentIdEqualTo(parentId);
		// 执行查询
		List<TbContentCategory> catList = contentCategoryMapper.selectByExample(example);
		// 转换成EasyUITreeNode的列表
		List<EasyUITreeNode> nodeList = new ArrayList();
		for (TbContentCategory tbContentCategory : catList) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			// 添加到列表
			nodeList.add(node);
		}
		
		return nodeList;
	}

}
