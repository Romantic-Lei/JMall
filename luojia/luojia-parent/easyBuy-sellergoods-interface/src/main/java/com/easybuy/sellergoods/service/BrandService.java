package com.easybuy.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.easybuy.entity.PageResult;
import com.easybuy.pojo.TbBrand;

/**
 * 品牌服务接口层
 * @author Romantic
 * @CreateDate 2020年3月29日
 * @Description
 */
public interface BrandService {
	public List<TbBrand> findAll();								// 返回全部列表
	public PageResult findPage(int pageNum, int pageSize);		// 分页
	public void add(TbBrand brand);								// 品牌新增
	public TbBrand findOne(Long id);							// 根据id查询实体
	public void update(TbBrand brand);							// 更新实体
	public void delete(Long[] ids);								// 删除品牌
	public PageResult findPage(TbBrand brand,int pageNum, int pageSize);		//带条件分页
	public List<Map> selectOptionList();								// 下拉列表数据
}
