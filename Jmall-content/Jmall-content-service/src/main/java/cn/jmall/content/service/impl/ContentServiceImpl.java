package cn.jmall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.jmall.common.jedis.JedisClient;
import cn.jmall.common.pojo.EasyUIDataGridResult;
import cn.jmall.common.util.E3Result;
import cn.jmall.common.util.JsonUtils;
import cn.jmall.content.service.ContentService;
import cn.jmall.mapper.TbContentMapper;
import cn.jmall.pojo.TbContent;
import cn.jmall.pojo.TbContentExample;
import cn.jmall.pojo.TbContentExample.Criteria;

/**
 * 内容管理Service
 * 
 * @author Jmall
 * @CreateDate 2020年3月7日
 * @Description
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper tbContentMapper;
	@Autowired
	private JedisClient jedisClient;

	@Value("${CONTENT_LIST}")
	private String CONTENT_LIST;

	@Override
	public E3Result addContent(TbContent content) {
		// 将内容数据插入到内容表
		Date date = new Date();
		content.setCreated(date);
		content.setUpdated(date);
		// 插入到数据库
		tbContentMapper.insert(content);
		// 缓存同步，删除缓存中对应的数据
		jedisClient.hdel(CONTENT_LIST, content.getCategoryId().toString());
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
		// 缓存同步，删除缓存中对应的数据
		jedisClient.hdel(CONTENT_LIST, content.getCategoryId().toString());
		return E3Result.ok();
	}

	@Override
	public E3Result deleteBatchContent(String[] ids) {
		for (String id : ids) {
			TbContent content = tbContentMapper.selectByPrimaryKey(Long.valueOf(id));
			tbContentMapper.deleteByPrimaryKey(Long.valueOf(id));
			jedisClient.hdel(CONTENT_LIST, content.getCategoryId().toString());
		}
		// 缓存同步，删除缓存中对应的数据
		return E3Result.ok();
	}

	// 根据内容分类id查询内容列表
	@Override
	public List<TbContent> getContentListByCid(long cid) {
		try {
			// 查询缓存，若有直接响应结果。没有就直接查询缓存
			String json = jedisClient.hget(CONTENT_LIST, cid + "");
			if (StringUtils.isNotBlank(json)) {
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		// 设置查询条件
		criteria.andCategoryIdEqualTo(cid);
		// 执行查询
		List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
		try {
			// 把结果添加到缓存
			jedisClient.hset(CONTENT_LIST, cid + "", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
