package com.easybuy.manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.easybuy.entity.PageResult;
import com.easybuy.pojo.TbBrand;
import com.easybuy.sellergoods.service.BrandService;

@RestController
public class BrandController {
	
	@Reference
	private BrandService brandService;
	
	@RequestMapping("/brand/findAll")
	public List<TbBrand> findAll(){
		
		return brandService.findAll();
	}
	
	@RequestMapping("/brand/findPage")
	public PageResult findPage(int page, int rows) {
		
		return brandService.findPage(page, rows);
	}
}
