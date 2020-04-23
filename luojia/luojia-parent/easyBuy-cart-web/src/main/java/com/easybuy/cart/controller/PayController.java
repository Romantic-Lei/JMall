package com.easybuy.cart.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.easybuy.order.service.PayService;

/**
 * 支付控制层
 * @author Romantic
 * @CreateDate 2020年4月23日
 * @Description
 */
@RestController
@RequestMapping("/pay")
public class PayController {

	@Reference
	private PayService payService;
	
	@RequestMapping("/createNative")
	public Map createNative() throws IOException {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		Map map = payService.createNative(userId);
		
		return map;
	}
	
}
