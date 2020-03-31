package com.easybuy.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.easybuy.TbSpecification.Specification;
import com.easybuy.entity.PageResult;
import com.easybuy.mapper.TbSpecificationMapper;
import com.easybuy.mapper.TbSpecificationOptionMapper;
import com.easybuy.pojo.TbSpecification;
import com.easybuy.pojo.TbSpecificationExample;
import com.easybuy.pojo.TbSpecificationOption;
import com.easybuy.pojo.TbSpecificationOptionExample;
import com.easybuy.sellergoods.service.SpecificationService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.easybuy.pojo.TbSpecificationExample.Criteria;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		specificationMapper.insert(specification.getSpecification());	//插入规格
		
		for( TbSpecificationOption specificationOption: specification.getSpecificationOptionList()){
			
			specificationOption.setSpecId(specification.getSpecification().getId());//设置规格ID
			specificationOptionMapper.insert(specificationOption);	//插入规格选项		
		}
		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//更新规格
		specificationMapper.updateByPrimaryKey(specification.getSpecification());
		
		//删除规格选项
		
		TbSpecificationOptionExample example=new TbSpecificationOptionExample();
		com.easybuy.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(specification.getSpecification().getId());//指定删除条件：规格ID
		
		specificationOptionMapper.deleteByExample(example);
		//插入规格选项
		for( TbSpecificationOption specificationOption: specification.getSpecificationOptionList()){			
			specificationOption.setSpecId(specification.getSpecification().getId());//设置规格ID
			specificationOptionMapper.insert(specificationOption);	//插入规格选项		
		}
		
		
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//查询规格实体
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id); 
		
		
		//查询规格选项列表 
		
		TbSpecificationOptionExample example=new TbSpecificationOptionExample();
		com.easybuy.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);//指定条件：规格ID		
		List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);
		
		
		//封装到规格组合实体返回		
		Specification specification=new Specification();
		specification.setSpecification(tbSpecification);
		specification.setSpecificationOptionList(specificationOptionList);
		return specification;
		
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//删除规格选项	
			TbSpecificationOptionExample example=new TbSpecificationOptionExample();
			com.easybuy.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);//删除条件
			specificationOptionMapper.deleteByExample(example);
			
			specificationMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		// TODO Auto-generated method stub
		return specificationMapper.selectOptionList();
	}
	
}
