package com.easybuy.cart.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.easybuy.order.service.PayService;

import config.PayConfig;

/**
 * 支付控制层
 * @author Romantic
 * @CreateDate 2020年4月23日
 * @Description
 */
@RestController
public class PayController {

	@Reference
	private PayService payService;
	
	@RequestMapping("/pay/createNative")
	public void createNative(HttpServletResponse response) throws IOException {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		Map map = payService.createNative(userId);
		String form = (String) map.get("form");
		
		response.setContentType("text/html;charset=" + PayConfig.CHARSET);
		response.getWriter().write(form);// 直接将完整的表单html输出到页面
		response.getWriter().flush();
		response.getWriter().close();
		
//		return map;
	}
	
	@RequestMapping(value = "/returnUrl", method = RequestMethod.GET)
	public void returnUrl(HttpServletRequest request, HttpServletResponse response)
	        throws IOException, AlipayApiException {
	    System.out.println("=================================同步回调=====================================");

	    // 获取支付宝GET过来反馈信息
	    Map<String, String> params = new HashMap<String, String>();
	    Map<String, String[]> requestParams = request.getParameterMap();
	    for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
	        String name = (String) iter.next();
	        String[] values = (String[]) requestParams.get(name);
	        String valueStr = "";
	        for (int i = 0; i < values.length; i++) {
	            valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
	        }
	        // 乱码解决，这段代码在出现乱码时使用
	        valueStr = new String(valueStr.getBytes("utf-8"), "utf-8");
	        params.put(name, valueStr);
	    }

	    System.out.println(params);//查看参数都有哪些
	    boolean signVerified = AlipaySignature.rsaCheckV1(params, PayConfig.ALIPAY_PUBLIC_KEY, PayConfig.CHARSET, PayConfig.SIGN_TYPE); // 调用SDK验证签名
	    //验证签名通过
	    if(signVerified){
	        // 商户订单号
	        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
	        
	        // 支付宝交易号
	        String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
	        
	        // 付款金额
	        String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
	        
	        System.out.println("商户订单号="+out_trade_no);
	        System.out.println("支付宝交易号="+trade_no);
	        System.out.println("付款金额="+total_amount);
	        
	        //支付成功，修复支付状态
	        payService.updateOrderStatus(out_trade_no, trade_no);
	        response.sendRedirect("/paysuccess.html");//跳转付款成功页面
	    }else{
	    	response.sendRedirect("/payfail.html");//跳转付款失败页面
	    }
	    
	}
	
}
