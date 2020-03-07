package cn.jmall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.jmall.common.pojo.EasyUIDataGridResult;
import cn.jmall.common.util.E3Result;
import cn.jmall.content.service.ContentService;
import cn.jmall.mapper.TbContentMapper;
import cn.jmall.pojo.TbContent;
import cn.jmall.pojo.TbContentExample;
import cn.jmall.pojo.TbContentExample.Criteria;

/**
 * 内容管理Service
 * @author Jmall
 * @CreateDate 2020年3月7日
 * @Description
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper tbContentMapper;
	
	@Override
	public E3Result addContent(TbContent content) {
		// 将内容数据插入到内容表
		Date date = new Date();
		content.setCreated(date);
		content.setUpdated(date);
		// 插入到数据库
		tbContentMapper.insert(content);
		return E3Result.ok();
	}

	@Override
	public EasyUIDataGridResult listContent(long categoryId, int page, int rows) {
		// 设置分页信息
		PageHelper.startPage(page, rows);
		// 设置查询条件
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		// selectByExampleWithBLOBs 可以查询到text类型字段
		List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
		// 创建返回值对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		// 获取分页结果
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		// 获取总结果数
		long total = pageInfo.getTotal();
		result.setTotal(total);
		
		return result;
	}

	// 内容更新
	@Override
	public E3Result updateContent(TbContent content) {
		content.setUpdated(new Date());
		tbContentMapper.updateByPrimaryKeySelective(content);
		return E3Result.ok();
	}

}
