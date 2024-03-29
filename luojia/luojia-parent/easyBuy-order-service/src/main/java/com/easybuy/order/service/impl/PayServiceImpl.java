package com.easybuy.order.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.easybuy.mapper.TbOrderMapper;
import com.easybuy.mapper.TbPayLogMapper;
import com.easybuy.order.service.PayService;
import com.easybuy.pojo.TbOrder;
import com.easybuy.pojo.TbPayLog;

import config.PayConfig;

/**
 * 二维码service
 * 
 * @author Romantic
 * @CreateDate 2020年4月23日
 * @Description
 */
@Service
public class PayServiceImpl implements PayService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private TbPayLogMapper payLogMapper;
	@Autowired
	private TbOrderMapper orderMapper;
	
	@SuppressWarnings("unchecked")
	@Override
	public Map createNative(String userId) {
		
		TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
		Map map = new HashMap();
		
		// 实例化客户端,填入所需参数
		AlipayClient alipayClient = new DefaultAlipayClient(PayConfig.GATEWAY_URL, PayConfig.APP_ID, PayConfig.APP_PRIVATE_KEY, 
				PayConfig.FORMAT, PayConfig.CHARSET, PayConfig.ALIPAY_PUBLIC_KEY, PayConfig.SIGN_TYPE);
		AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
		// 在公共参数中设置回跳和通知地址
		request.setReturnUrl(PayConfig.RETURN_URL);
		request.setNotifyUrl(PayConfig.NOTIFY_URL);

		// 商户订单号，商户网站订单系统中唯一订单号，必填
		// 生成随机Id
		String out_trade_no = payLog.getOutTradeNo();
		// 付款金额，必填
		Long total_amount = payLog.getTotalFee();
		// 订单名称，必填
		String subject = "易购商品支付";
		// 商品描述，可空
		String body = "易购商品支付";
		request.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"," + "\"total_amount\":\"" + total_amount
				+ "\"," + "\"subject\":\"" + subject + "\"," + "\"body\":\"" + body + "\","
				+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
		String form = "";
		try {
			AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
			
			if(response.isSuccess()) {
				form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
				map.put("form", form);
			} else {
				map.put("form", null);
			}
			
			map.put("out_trade_no", out_trade_no);// 订单号
			map.put("total_amount", total_amount);// 订单号
			
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		
		return map;
	}

	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		payLog.setTransactionId(transaction_id);// 交易流水号
		payLog.setPayTime(new Date());// 支付时间
		payLog.setTradeState("1");// 交易状态  -> 已支付
		String orderList = payLog.getOrderList();// 获取本次支付订单id
		
		String[] ids = orderList.split(",");
		
		for (int i = 0; i < ids.length; i++) {
			TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(ids[i]));
			order.setStatus("2");// 已支付
			orderMapper.updateByPrimaryKey(order);
		}
		
		payLogMapper.updateByPrimaryKey(payLog);
		
	}

}
