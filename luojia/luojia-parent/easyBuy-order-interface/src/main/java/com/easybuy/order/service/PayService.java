package com.easybuy.order.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * 支付服务接口
 * @author Romantic
 * @CreateDate 2020年4月23日
 * @Description
 */
public interface PayService {
	
	// 生成二维码
	public Map createNative(String userId);
	
	// 检测订单支付状态
//	public Map queryPayStatus(String out_trade_no);
	
	// 更改支付状态
	public void updateOrderStatus(String out_trade_no, String transaction_id);

}
