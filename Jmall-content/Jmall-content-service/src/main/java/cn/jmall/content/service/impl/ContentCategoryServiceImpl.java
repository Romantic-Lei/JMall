package cn.jmall.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.jmall.common.pojo.EasyUITreeNode;
import cn.jmall.common.util.E3Result;
import cn.jmall.content.service.ContentCategoryService;
import cn.jmall.mapper.TbContentCategoryMapper;
import cn.jmall.pojo.TbContentCategory;
import cn.jmall.pojo.TbContentCategoryExample;
import cn.jmall.pojo.TbContentCategoryExample.Criteria;

/**
 * 内容分类管理
 * 
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
			node.setState(tbContentCategory.getIsParent() ? "closed" : "open");
			// 添加到列表
			nodeList.add(node);
		}

		return nodeList;
	}

	@Override
	public E3Result addContentCategory(long parentId, String name) {
		// 创建一个 tb_content_category表对应的POJO对象
		TbContentCategory tbContentCategory = new TbContentCategory();
		// 设置pojo的属性
		tbContentCategory.setParentId(parentId);
		tbContentCategory.setName(name);
		// 1-正常， 2-删除
		tbContentCategory.setStatus(1);
		// 默认了排序就是1
		tbContentCategory.setSortOrder(1);
		// 新插入的结点一定是叶子结点
		tbContentCategory.setIsParent(false);
		Date date = new Date();
		tbContentCategory.setCreated(date);
		tbContentCategory.setUpdated(date);
		// 插入到数据库
		contentCategoryMapper.insert(tbContentCategory);
		// 插入父节点的isparent属性。如果不是true就改为true
		// 根据 parentId查询父节点
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if (!parent.getIsParent()) {
			parent.setIsParent(true);
			// 更新到数据库
			contentCategoryMapper.updateByPrimaryKeySelective(parent);
		}
		// 返回结果，返回一个 E3Result 对象，包含POJO
		return E3Result.ok(tbContentCategory);
	}

	@Override
	public void updateContentCategory(long id, String name) {
		// 创建一个 tb_content_category表对应的POJO对象
		TbContentCategory tbContentCategory = new TbContentCategory();
		// 设置pojo的属性
		tbContentCategory.setId(id);
		tbContentCategory.setName(name);
		// 更新到数据库
		contentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
	}

	@Override
	public E3Result deleteContentCategory(long id) {
		TbContentCategory category = contentCategoryMapper.selectByPrimaryKey(id);
		// 父节点id
		Long parentId = category.getParentId();
		// 无法删除父节点
		if (category.getIsParent()) {
			return E3Result.build(-1, "无法删除文件夹");
		}
		
		// 删除结点
		contentCategoryMapper.deleteByPrimaryKey(id);
		// 查看父节点下面是否还有子节点
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		// 查询删除的同级结点信息
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		
		// 判断是否查询到了结果
		if (list.size() == 0) {
			// 没有同级结点，那么就修改父节点信息
			TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(parentId);
			// 修改为不是父节点
			contentCategory.setIsParent(false);
			contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
		}
		
		// 返回结果
		return E3Result.ok();
	}

}
