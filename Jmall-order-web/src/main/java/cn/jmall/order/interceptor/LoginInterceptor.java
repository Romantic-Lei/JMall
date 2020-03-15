package cn.jmall.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.jmall.cart.service.CartService;
import cn.jmall.common.util.CookieUtils;
import cn.jmall.common.util.E3Result;
import cn.jmall.common.util.JsonUtils;
import cn.jmall.pojo.TbItem;
import cn.jmall.pojo.TbUser;
import cn.jmall.sso.service.TokenService;

/**
 * 用户登录拦截器
 * 
 * @author Jmall
 * @CreateDate 2020年3月15日
 * @Description
 */
public class LoginInterceptor implements HandlerInterceptor {

	@Value("${SSO_URL}")
	private String SSO_URL;

	@Autowired
	private TokenService tokenService;
	@Autowired
	private CartService cartService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 从cookie中取token
		String token = CookieUtils.getCookieValue(request, "token");
		// 判断token是否存在
		if (StringUtils.isBlank(token)) {
			// 如果token不存在，未登录状态，跳转到sso系统的登录页面，登录成功后，跳转到当前请求的url
			// 传递当前页面的地址给登录页面，登录之后直接跳转回来
			response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURL());
			// 拦截
			return false;
		}
		// 如果token存在，需要调用sso系统的服务，根据token取用户信息
		E3Result e3Result = tokenService.getUserByToken(token);
		// 如果取不到，用户登录信息已过期，需要登录
		if (e3Result.getStatus() != 200) {
			response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURL());
			// 拦截
			return false;
		}
		// 如果取到用户信息，是登录状态，需要把用户信息写入到request。
		TbUser user = (TbUser) e3Result.getData();
		request.setAttribute("user", user);
		// 判断cookie中是否有购物车数据，如果有，合并到服务端
		String jsonCartList = CookieUtils.getCookieValue(request, "cart", true);
		if (StringUtils.isNotBlank(jsonCartList)) {
			// 合并购物车
			cartService.mergeCart(user.getId(), JsonUtils.jsonToList(jsonCartList, TbItem.class));
		}
		// 放行
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
