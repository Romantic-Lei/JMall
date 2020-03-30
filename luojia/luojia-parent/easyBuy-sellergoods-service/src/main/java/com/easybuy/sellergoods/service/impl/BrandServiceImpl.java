package com.easybuy.sellergoods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.easybuy.entity.PageResult;
import com.easybuy.mapper.TbBrandMapper;
import com.easybuy.pojo.TbBrand;
import com.easybuy.pojo.TbBrandExample;
import com.easybuy.pojo.TbBrandExample.Criteria;
import com.easybuy.sellergoods.service.BrandService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;

	@Override
	public List<TbBrand> findAll() {

		return brandMapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);

		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(TbBrand brand) {

		brandMapper.insert(brand);
	}

	@Override
	public TbBrand findOne(Long id) {

		return brandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void update(TbBrand brand) {

		brandMapper.updateByPrimaryKey(brand);
	}

	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			brandMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();

		if (brand != null) {
			if (brand.getName() != null && brand.getName().length() > 0) {
				criteria.andNameLike("%" + brand.getName() + "%");
			}
			if (brand.getFirstChar() != null && brand.getFirstChar().length() > 0) {
				criteria.andFirstCharEqualTo(brand.getFirstChar());
			}
		}

		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

}
