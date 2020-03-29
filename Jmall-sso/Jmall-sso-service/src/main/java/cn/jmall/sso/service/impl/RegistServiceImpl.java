package cn.jmall.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.jmall.common.util.E3Result;
import cn.jmall.mapper.TbUserMapper;
import cn.jmall.pojo.TbUser;
import cn.jmall.pojo.TbUserExample;
import cn.jmall.pojo.TbUserExample.Criteria;
import cn.jmall.sso.service.RegistService;

/**
 * 用户注册处理Service
 * 
 * @author Jmall
 * @CreateDate 2020年3月13日
 * @Description
 */
@Service
public class RegistServiceImpl implements RegistService {

	@Autowired
	private TbUserMapper tbUserMapper;

	@Override
	public E3Result checkData(String param, int type) {
		// 根于type生成不同的查询条件
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		// 1：用户名 2：手机号 3：邮箱
		if (type == 1) {
			criteria.andUsernameEqualTo(param);
		} else if (type == 2) {
			criteria.andPhoneEqualTo(param);
		} else if (type == 3) {
			criteria.andEmailEqualTo(param);
		} else {
			return E3Result.build(400, "数据类型错误");
		}
		// 执行查询
		List<TbUser> list = tbUserMapper.selectByExample(example);
		// 判断结果中是否包含数据
		if (list != null && list.size() > 0) {
			// 如果有数据返回false
			return E3Result.ok(false);
		}
		// 如果没有数据返回true
		return E3Result.ok(true);
	}

	@Override
	public E3Result register(TbUser user) {
//		对数据有效性进行校验,防止用户改前端校验的false为true强制提交
		if(StringUtils.isBlank(user.getUsername())
				|| StringUtils.isBlank(user.getPassword())
				|| StringUtils.isBlank(user.getPhone())) {
			return E3Result.build(400, "用户注册不完整，注册失败");
		}
		// 1：用户名 2：手机号 3：邮箱
		E3Result result = checkData(user.getUsername(), 1);
		if(!(boolean) result.getData()) {
			return E3Result.build(400, "用户名被占用");
		}
		result = checkData(user.getPassword(), 2);
		if(!(boolean) result.getData()) {
			return E3Result.build(400, "请输入密码");
		}
		result = checkData(user.getPhone(), 3);
		if(!(boolean) result.getData()) {
			return E3Result.build(400, "手机号被占用");
		}
//		补全POJO的属性
		Date date = new Date();
		user.setCreated(date);
		user.setUpdated(date);
//		对密码进行md5加密
		String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5Pass);
//		把用户数据插入到数据库中
		tbUserMapper.insert(user);
//		返回添加成功
		return E3Result.ok();
	}

}
