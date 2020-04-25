package com.easybuy.order.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.easybuy.entity.PageResult;
import com.easybuy.mapper.TbOrderItemMapper;
import com.easybuy.mapper.TbOrderMapper;
import com.easybuy.mapper.TbPayLogMapper;
import com.easybuy.order.service.OrderService;
import com.easybuy.pojo.TbOrder;
import com.easybuy.pojo.TbOrderExample;
import com.easybuy.pojo.TbOrderItem;
import com.easybuy.pojo.TbPayLog;
import com.easybuy.pojogroup.Cart;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.easybuy.pojo.TbOrderExample.Criteria;

/**
 * 服务实现层
 * 
 * @author Romantic
 * @CreateDate 2020年4月18日
 * @Description
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbPayLogMapper payLogMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {

		// 获取购物车（redis）中的数据
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

		// 循环购物车列表，循环向订单表插入数据
		double total_money = 0.0;
		List orderIdList = new ArrayList();
		for (Cart cart : cartList) {
			TbOrder tborder = new TbOrder();
			tborder.setPaymentType(order.getPaymentType());// 支付类型
			tborder.setStatus("1");// 状态1：未付款
			tborder.setCreateTime(new Date());// 创建日期
			tborder.setUpdateTime(new Date());// 更新日期
			tborder.setUserId(order.getUserId());// 用户ID
			tborder.setReceiverAreaName(order.getReceiverAreaName());// 地址
			tborder.setReceiverMobile(order.getReceiverMobile());// 电话
			tborder.setReceiver(order.getReceiver());// 收货人
			tborder.setSellerId(cart.getSellerId());// 商家ID

			orderMapper.insert(tborder);// 保存到订单主表
			orderIdList.add(tborder.getOrderId() + "");

			double money = 0;
			for (TbOrderItem orderItem : cart.getOrderItemList()) {
				money += orderItem.getTotalFee().doubleValue();

				orderItem.setOrderId(tborder.getOrderId());// 订单ID
				orderItem.setSellerId(cart.getSellerId());// 商家ID
				orderItemMapper.insert(orderItem);// 保存到订单明细表
			}

			total_money += money;// 金额累加

			tborder.setPayment(new BigDecimal(money));// 合计金额

			orderMapper.updateByPrimaryKey(tborder);// 更新
		}
		// 清除购物车中的数据
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());

		// 如果是支付宝微信支付，那么就产生支付日志
		if ("1".equals(order.getPaymentType())) {
			// 向支付日志中插入记录
			TbPayLog payLog = new TbPayLog();
			payLog.setCreateTime(new Date());
			
			String orderIds = orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
			payLog.setOrderList(orderIds); // 订单号列表，逗号分隔

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String outTradNo = simpleDateFormat.format(new Date()) + (long)(Math.random() * 10000000000L);

			payLog.setOutTradeNo(outTradNo); // 订单号

			payLog.setPayType("1"); // 1支付宝， 2微信，3网银
			payLog.setTotalFee((long) (total_money)); // 支付金额
			payLog.setTradeState("0"); // 订单状态
			payLog.setUserId(order.getUserId()); // 用户id

			payLogMapper.insert(payLog);
			
			redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);//存入缓存
			
			
		}

	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order) {
		orderMapper.updateByPrimaryKey(order);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id) {
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			orderMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();

		if (order != null) {
			if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
				criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
			}
			if (order.getPostFee() != null && order.getPostFee().length() > 0) {
				criteria.andPostFeeLike("%" + order.getPostFee() + "%");
			}
			if (order.getStatus() != null && order.getStatus().length() > 0) {
				criteria.andStatusLike("%" + order.getStatus() + "%");
			}
			if (order.getShippingName() != null && order.getShippingName().length() > 0) {
				criteria.andShippingNameLike("%" + order.getShippingName() + "%");
			}
			if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
				criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
			}
			if (order.getUserId() != null && order.getUserId().length() > 0) {
				criteria.andUserIdLike("%" + order.getUserId() + "%");
			}
			if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
				criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
			}
			if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
				criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
			}
			if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
				criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
			}
			if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
				criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
			}
			if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
				criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
			}
			if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
				criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
			}
			if (order.getReceiver() != null && order.getReceiver().length() > 0) {
				criteria.andReceiverLike("%" + order.getReceiver() + "%");
			}
			if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
				criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
			}
			if (order.getSourceType() != null && order.getSourceType().length() > 0) {
				criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
			}
			if (order.getSellerId() != null && order.getSellerId().length() > 0) {
				criteria.andSellerIdLike("%" + order.getSellerId() + "%");
			}

		}

		Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

}
