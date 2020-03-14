package cn.jmall.cart.service;

import java.util.List;

import cn.jmall.common.util.E3Result;
import cn.jmall.pojo.TbItem;

public interface CartService {
	
	public E3Result addCart(long userId, long itemId, int num);			// 添加购物车
	public E3Result mergeCart(long userId, List<TbItem> itemList);		// 合并购物车
	public List<TbItem> getCartList(long userId);						// 获取购物车列表信息
	public E3Result updateCartNum(long userId, long itemId, int num);	// 更新商品信息
	public E3Result deleteCartNum(long userId, long itemId);			// 删除商品信息

}
