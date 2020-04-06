package com.easybuy.content.service.impl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.easybuy.content.service.ContentService;
import com.easybuy.entity.PageResult;
import com.easybuy.mapper.TbContentCategoryMapper;
import com.easybuy.mapper.TbContentMapper;
import com.easybuy.pojo.TbContent;
import com.easybuy.pojo.TbContentCategory;
import com.easybuy.pojo.TbContentCategoryExample;
import com.easybuy.pojo.TbContentExample;
import com.easybuy.pojo.TbContentExample.Criteria;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);	
		
		clearCache(content.getCategoryId());//清除缓存
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//得到原来的内容
		TbContent content_old = contentMapper.selectByPrimaryKey(content.getId());
		
		contentMapper.updateByPrimaryKey(content);
		
		clearCache(content_old.getCategoryId());//清除原来的缓存
		clearCache(content.getCategoryId());//清除更改后  缓存
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			clearCache(contentMapper.selectByPrimaryKey(id).getCategoryId()  );//清除缓存
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getContent()!=null && content.getContent().length()>0){
				criteria.andContentLike("%"+content.getContent()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


	
	
	@Autowired
	private RedisTemplate<String, TbContent>  redisTemplate;
		
	@Override
	public List<TbContent> findByCategoryKey(String key) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		com.easybuy.pojo.TbContentCategoryExample.Criteria criteria = example.createCriteria();
		criteria.andContentKeyEqualTo(key);
		// 返回分类列表
		List<TbContentCategory> categoryList = contentCategoryMapper.selectByExample(example);
		if(categoryList.size() == 0) {
			return new ArrayList();
		}
		
		// 查询广告列表
		TbContentExample example2 = new TbContentExample();
		Criteria criteria2 = example2.createCriteria();
		criteria2.andCategoryIdEqualTo(categoryList.get(0).getId());
		List<TbContent> contentList = contentMapper.selectByExample(example2);
		
		return contentList;
	}
	

	
	//清除缓存
	private void clearCache(Long categoryId){
		//广告分类对象
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(categoryId);
		String key = contentCategory.getContentKey();
		System.out.println("清除缓存"+key);
//		redisTemplate.boundHashOps("content").delete(key);
	}
	
}
