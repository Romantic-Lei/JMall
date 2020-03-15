package cn.jmall.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cn.jmall.common.jedis.JedisClient;
import cn.jmall.common.util.E3Result;
import cn.jmall.mapper.TbOrderItemMapper;
import cn.jmall.mapper.TbOrderMapper;
import cn.jmall.mapper.TbOrderShippingMapper;
import cn.jmall.order.pojo.OrderInfo;
import cn.jmall.order.service.OrderService;
import cn.jmall.pojo.TbOrderItem;
import cn.jmall.pojo.TbOrderShipping;

public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	@Autowired
	private JedisClient jedisClient;

	@Value("${ORDER_ID_GEN_KEY}")
	private String ORDER_ID_GEN_KEY;
	@Value("${ORDER_ID_START}")
	private String ORDER_ID_START;
	@Value("${ORDER_DETAIL_ID_GEN_KEY}")
	private String ORDER_DETAIL_ID_GEN_KEY;

	@Override
	public E3Result createOrder(OrderInfo orderInfo) {
		// 生成订单号
		if (!jedisClient.exists(ORDER_ID_GEN_KEY)) {
			jedisClient.set(ORDER_ID_GEN_KEY, ORDER_ID_START);
		}

		String orderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
		orderInfo.setOrderId(orderId);
		// 1.未付款 ， 2.已付款， 3.未发货， 4.已发货， 5.交易成功， 6.交易关闭
		orderInfo.setStatus(1);
		Date date = new Date();
		orderInfo.setCreateTime(date);
		orderInfo.setUpdateTime(date);
		// 插入订单表
		orderMapper.insert(orderInfo);
		// 向订单明细表插入数据
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			// 生成明细id
			String odId = jedisClient.incr(ORDER_DETAIL_ID_GEN_KEY).toString();
			// 补全POJO属性
			tbOrderItem.setId(odId);
			tbOrderItem.setOrderId(orderId);
			// 向项目明细表插入数据
			orderItemMapper.insert(tbOrderItem);
		}
		// 向订单物流表插入数据
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(date);
		orderShipping.setUpdated(date);
		orderShippingMapper.insert(orderShipping);
		// 返回E3Result，包含订单号
		return E3Result.ok();
	}

}
