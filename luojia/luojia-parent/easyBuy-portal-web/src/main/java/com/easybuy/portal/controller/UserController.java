package com.easybuy.portal.controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.easybuy.entity.Result;
import com.easybuy.pojo.TbUser;
import com.easybuy.service.UserService;

/**
 * controller
 * @author Romantic
 * @CreateDate 2020年4月11日
 * @Description
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String code){
		//判断验证码是否正确
		boolean checkSmsCode = userService.checkSmsCode(user.getPhone(), code);
		if(checkSmsCode==false){
			return new Result(false, "验证码输入错误！");
		}
		
		try {
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	
	@RequestMapping("/sendCode")
	public Result sendCode(String phone){
		
		try {
			userService.createSmsCode(phone);
			return new Result(true, "短信验证码发送成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "短信验证码发送失败");
		}
	}
	
	
}
