package com.easybuy.sellergoods.service;

import java.util.List;

import com.easybuy.pojo.TbBrand;

/**
 * 品牌服务接口层
 * @author Romantic
 * @CreateDate 2020年3月29日
 * @Description
 */
public interface BrandService {
	public List<TbBrand> findAll();
}
