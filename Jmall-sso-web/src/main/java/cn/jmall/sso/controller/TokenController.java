package cn.jmall.sso.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.jmall.common.util.E3Result;
import cn.jmall.common.util.JsonUtils;
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

//	解决json跨域问题方法一：
//	@RequestMapping(value = "/user/token/{token}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE/*"application/json;charset=utf-8"*/)
//	@ResponseBody
//	public String getUserByToken(@PathVariable String token, String callback) {
//		E3Result result = tokenService.getUserByToken(token);
//		// 响应结果这前，判断是否是jsonp请求
//		if (StringUtils.isNotBlank(callback)) {
//			// 把结果封装成一个js语句响应,必须是这样的格式
//			return callback + "(" + JsonUtils.objectToJson(result) + ");";
//		}
//		return JsonUtils.objectToJson(result);
//	}
	
//	解决json跨域问题方法二：此方法在spring4.1之后可用
	@RequestMapping(value = "/user/token/{token}")
	@ResponseBody
	public Object getUserByToken(@PathVariable String token, String callback) {
		E3Result result = tokenService.getUserByToken(token);
		// 响应结果这前，判断是否是jsonp请求
		if (StringUtils.isNotBlank(callback)) {
			// 把结果封装成一个js语句响应
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}
		return result;
	}

}
