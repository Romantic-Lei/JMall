package com.easybuy.cart.service;
import java.util.List;

import com.easybuy.entity.PageResult;
import com.easybuy.pojo.TbAddress;

/**
 * 服务层接口
 * @author Romantic
 * @CreateDate 2020年4月18日
 * @Description
 */
public interface AddressService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbAddress> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbAddress address);
	
	
	/**
	 * 修改
	 */
	public void update(TbAddress address);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbAddress findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbAddress address, int pageNum,int pageSize);
	
	
	/**
	 * 根据用户ID查询地址列表
	 * @param userId
	 * @return
	 */
	public List<TbAddress> findListByUserId(String userId);
	
}
