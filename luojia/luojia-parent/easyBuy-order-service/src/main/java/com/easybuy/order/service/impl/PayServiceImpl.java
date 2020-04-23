package com.easybuy.order.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.easybuy.order.service.PayService;
import com.easybuy.pojo.TbPayLog;

import config.PayConfig;

/**
 * 二维码service
 * 
 * @author Romantic
 * @CreateDate 2020年4月23日
 * @Description
 */
public class PayServiceImpl implements PayService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Override
	public Map createNative(String userId, HttpServletResponse response) throws IOException {
		
		TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
		
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
			form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		response.setContentType("text/html;charset=" + PayConfig.CHARSET);
		response.getWriter().write(form);// 直接将完整的表单html输出到页面
		response.getWriter().flush();
		response.getWriter().close();
	}

}
