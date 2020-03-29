package cn.jmall.order.service;

import cn.jmall.common.util.E3Result;
import cn.jmall.order.pojo.OrderInfo;

public interface OrderService {

	public E3Result createOrder(OrderInfo orderInfo);
	
}
