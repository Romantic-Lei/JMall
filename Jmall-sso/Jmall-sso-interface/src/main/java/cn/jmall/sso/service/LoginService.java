package cn.jmall.sso.service;

import cn.jmall.common.util.E3Result;

public interface LoginService {

	// 参数：用户名，密码
	// 业务逻辑
	/*
	 * 1.判断用户名和密码是否正确 
	 * 2.如果不正确，返回登录信息 
	 * 3.如果正确则生成token 
	 * 4.把用户信息写入redis，key：token value：用户信息，模拟session 
	 * 5.设置session的过期时间 
	 * 6.把token返回
	 */
	// 返回值：E3Result，其中包括token信息
	public E3Result userLogin(String username, String password);
}
