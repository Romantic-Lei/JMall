package cn.jmall.sso.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.jmall.common.jedis.JedisClient;
import cn.jmall.common.util.E3Result;
import cn.jmall.common.util.JsonUtils;
import cn.jmall.mapper.TbUserMapper;
import cn.jmall.pojo.TbUser;
import cn.jmall.pojo.TbUserExample;
import cn.jmall.pojo.TbUserExample.Criteria;
import cn.jmall.sso.service.LoginService;

/**
 * // 用户登录处理 // @author Jmall // @CreateDate 2020年3月13日 // @Description
 */
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private TbUserMapper tbUserMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;

	@Override
	public E3Result userLogin(String username, String password) {
		// 1.判断用户名和密码是否正确
		// 根据用户名查询用户信息
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		// 执行查询
		List<TbUser> list = tbUserMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			// 2.如果不正确，返回登录信息
			return E3Result.build(400, "用户名或密码错误");
		}
		TbUser user = list.get(0);
		if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
			// 2.如果不正确，返回登录信息
			return E3Result.build(400, "用户名或密码错误");
		}
		// 3.如果正确则生成token
		String token = UUID.randomUUID().toString();
		// 4.把用户信息写入redis，key：token value：用户信息，模拟session
		// 不要将密码保存，尽管MD5加密后无法解密
		user.setPassword(null);
		jedisClient.set("SESSION:" + token, JsonUtils.objectToJson(user));
		// 5.设置session的过期时间
		jedisClient.expire("SESSION:" + token, SESSION_EXPIRE);
		// 6.把token返回
		return E3Result.ok(token);
	}

	@Override
	public E3Result userLoginOut(String token) {
		jedisClient.del("SESSION:" + token);
		return E3Result.ok();
	}

}
