package cn.jmall.sso.service;

import cn.jmall.common.util.E3Result;

/**
 * 根据token查询用户信息
 * @author Jmall
 * @CreateDate 2020年3月13日
 * @Description
 */
public interface TokenService {

	public E3Result getUserByToken(String token);		// 根据token取用户信息
	
}
