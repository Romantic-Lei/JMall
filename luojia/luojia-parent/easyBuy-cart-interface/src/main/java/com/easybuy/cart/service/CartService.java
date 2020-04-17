package com.easybuy.cart.service;

import java.util.List;

import com.easybuy.pojogroup.Cart;

/**
 * 购物车服务
 * @author Romantic
 * @CreateDate 2020年4月15日
 * @Description
 */
public interface CartService {

	/**
	 * 向购物车综合添加商品
	 * @param cartList 购物车商品集合
	 * @param itemId   商品sku id
	 * @param num      添加商品数量
	 * @return         返回操作后的购物车列表
	 */
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);
	
	/**
	 * 根据当前登录人登录账号提取购物车信息
	 * @param username
	 * @return
	 */
	public List<Cart> findCartListFromRedis(String username);
	
}
