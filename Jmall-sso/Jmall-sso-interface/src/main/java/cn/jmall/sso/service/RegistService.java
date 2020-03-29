package cn.jmall.sso.service;

import cn.jmall.common.util.E3Result;
import cn.jmall.pojo.TbUser;

public interface RegistService {

	// 根据type参数不同来校验用户名，手机，邮箱是否可用
	public E3Result checkData(String param, int type);
	public E3Result register(TbUser user);
	
}
