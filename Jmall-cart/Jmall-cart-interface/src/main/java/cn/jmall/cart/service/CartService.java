package cn.jmall.cart.service;

import cn.jmall.common.util.E3Result;

public interface CartService {
	
	public E3Result addCart(long userId, long itemId, int num);

}
