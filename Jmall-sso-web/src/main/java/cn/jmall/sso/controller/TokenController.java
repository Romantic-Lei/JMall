package cn.jmall.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.jmall.common.util.E3Result;
import cn.jmall.sso.service.TokenService;

/**
 * 
 * @author Jmall
 * @CreateDate 2020年3月13日
 * @Description
 */
@Controller
public class TokenController {
	
	@Autowired
	private TokenService tokenService;
	
	@RequestMapping("/user/token/{token}")
	@ResponseBody
	public E3Result getUserByToken(@PathVariable String token) {
		E3Result result = tokenService.getUserByToken(token);
		return result;
	}

}
