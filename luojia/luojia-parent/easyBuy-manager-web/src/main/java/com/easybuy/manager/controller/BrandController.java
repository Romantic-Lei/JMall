package com.easybuy.manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.easybuy.entity.PageResult;
import com.easybuy.entity.Result;
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
	
	@RequestMapping("/brand/add")
	public Result add(@RequestBody TbBrand brand) {
		
		try {
			brandService.add(brand);
			return new Result(true, "添加成功");
		} catch (Exception e) {
			return new Result(false, "添加失败");
		}
	}
	
	@RequestMapping("/brand/findOne")
	public TbBrand findOne(Long id) {
		
		return brandService.findOne(id);
	}
	
	@RequestMapping("/brand/update")
	public Result update(TbBrand brand) {
		
		try {
			brandService.update(brand);
			return new Result(true, "更新成功");
		} catch (Exception e) {
			return new Result(false, "更新失败");
		}
	}
	
}
