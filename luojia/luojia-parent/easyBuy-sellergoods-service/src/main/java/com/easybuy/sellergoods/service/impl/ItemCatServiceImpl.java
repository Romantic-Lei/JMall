package com.easybuy.sellergoods.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.easybuy.entity.PageResult;
import com.easybuy.mapper.TbItemCatMapper;
import com.easybuy.pojo.TbItemCat;
import com.easybuy.pojo.TbItemCatExample;
import com.easybuy.sellergoods.service.ItemCatService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.easybuy.pojo.TbItemCatExample.Criteria;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 * @throws Exception 
	 */
	@Override
	public void delete(Long[] ids) throws Exception {
		for(Long id:ids){
			TbItemCatExample example = new TbItemCatExample();
			Criteria criteria = example.createCriteria();
			criteria.andParentIdEqualTo(id);
			
			List<TbItemCat> itemCat = itemCatMapper.selectByExample(example);
			if(itemCat == null || itemCat.size() == 0) {
				// 当前结点不是父节点，可以删除
				itemCatMapper.deleteByPrimaryKey(id);
			}else {
				throw new Exception();
			}
			
		}		
	}
	
	
		@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
						if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}
	
		}
		
		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public List<TbItemCat> findByParentId(Long parentId) {
			TbItemCatExample example = new TbItemCatExample();
			Criteria criteria = example.createCriteria();
			criteria.andParentIdEqualTo(parentId);		// 上级分类ID
			
			return itemCatMapper.selectByExample(example);
		}
	
}
