package cn.jmall.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.jmall.common.util.E3Result;
import cn.jmall.pojo.TbUser;
import cn.jmall.sso.service.RegistService;

/**
 * 注册功能Controller
 * @author Jmall
 * @CreateDate 2020年3月13日
 * @Description
 */
@Controller
public class RegisterController {
	
	@Autowired
	private RegistService registService;

	@RequestMapping("/page/register")
	public String showRegister() {
		
		return "register";
	}
	
	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody
	public E3Result checkData(@PathVariable String param, @PathVariable int type) {
		E3Result result = registService.checkData(param, type);
		
		return result;
	}
	
	@RequestMapping(value="/user/register", method=RequestMethod.POST)
	@ResponseBody
	public E3Result regist(TbUser user) {
		E3Result result = registService.register(user);
		
		return result;
	}
	
}
