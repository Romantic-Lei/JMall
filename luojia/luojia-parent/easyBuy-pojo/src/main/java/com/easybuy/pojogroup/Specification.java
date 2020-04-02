package com.easybuy.pojogroup;
import java.io.Serializable;
import java.util.List;

import com.easybuy.pojo.TbSpecification;
import com.easybuy.pojo.TbSpecificationOption;
/**
 * 规格组合实体
 * @author Administrator
 *
 */
public class Specification implements Serializable{

	private TbSpecification specification;//规格
	
	private List<TbSpecificationOption> specificationOptionList;//规格选项列表

	public TbSpecification getSpecification() {
		return specification;
	}

	public void setSpecification(TbSpecification specification) {
		this.specification = specification;
	}

	public List<TbSpecificationOption> getSpecificationOptionList() {
		return specificationOptionList;
	}

	public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
		this.specificationOptionList = specificationOptionList;
	}
	
}
