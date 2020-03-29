package cn.jmall.sso.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.jmall.common.jedis.JedisClient;
import cn.jmall.common.util.E3Result;
import cn.jmall.common.util.JsonUtils;
import cn.jmall.pojo.TbUser;
import cn.jmall.sso.service.TokenService;

/**
 * 根据token取用户信息
 * @author Jmall
 * @CreateDate 2020年3月13日
 * @Description
 */
@Service
public class TokenServiceImpl implements TokenService {

	@Autowired
	private JedisClient jedisClient;
	
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	
	@Override
	public E3Result getUserByToken(String token) {
//		根据token到redis中取用户信息
		String json = jedisClient.get("SESSION:" + token);
//		取不到用户信息，登录已经过期，返回登录过期
		if (StringUtils.isBlank(json)) {
			return E3Result.build(201, "登录信息已过期");
		}
//		取到用户信息更新token的过期时间
		jedisClient.expire("SESSION:" + token, SESSION_EXPIRE);
//		返回结果，E3Result中包含TBUser对象
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		return E3Result.ok(user);
	}

}
