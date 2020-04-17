package com.easybuy.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.easybuy.cart.service.CartService;
import com.easybuy.entity.Result;
import com.easybuy.pojogroup.Cart;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference
	private CartService cartService;

	/**
	 * 添加商品到购物车
	 * 
	 * @param itemId
	 * @param num
	 * @return
	 */
	@RequestMapping("/addGoodsToCartList")
	public Result addGoodToCartList(Long itemId, Integer num, HttpServletRequest request,
			HttpServletResponse response) {

		// 第二个参数是需要跨域请求的地址，如果购物车不使用cookie的话， 第二个参数可以是*号，* 表示该资源谁都可以用
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:9100");
		// 如果请求包含cookie，我们需要加上这样一句话
		response.setHeader("Access-Control-Allow-Credentials", "true");

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		try {
			List<Cart> cartList = findCartList(request);
			// 添加商品到购物车
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);

			// 用户未登录，将购物车放到cookie中
			if ("anonymousUser".equals(username)) {
				System.out.println("将购物车放到cookie中");
				// 将购物车json字符串存入cookie
				util.CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList));
			} else {
				System.out.println("将购物车放入到redis");
				// 如果已登录，将购物车放入到redis中
				cartService.saveCartListToRedis(username, cartList);
			}

			return new Result(true, "添加购物车成功");
		} catch (RuntimeException e) {

			return new Result(false, e.getMessage());
		} catch (Exception e) {
			return new Result(false, "添加购物车失败");
		}
	}

	@RequestMapping("/findCartList")
	public List<Cart> findCartList(HttpServletRequest request) {

		// 获取当前登录人,未登录时，用户名为anonymousUser
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		if ("anonymousUser".equals(username)) {
			// 如果未登录，爸商品存放于购物车
			// 在cookie中提取购物车列表json字符串
			String cartListJson = util.CookieUtil.getCookieValue(request, "cartList");

			if (cartListJson == null || cartListJson.equals("")) {
				cartListJson = "[]";
			}
			// 获取购物车列表
			System.out.println("从cookie中提取购物车");
			return JSON.parseArray(cartListJson, Cart.class);
		} else {
			System.out.println("从Redis中提取购物车");
			return cartService.findCartListFromRedis(username);
		}
	}

	@RequestMapping("/mergeCartList")
	public void mergeCartList(HttpServletRequest request, HttpServletResponse response) {
		// 获取当前登录人,未登录时，用户名为anonymousUser
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		// 1.从cookie中提取购物车
		String cartListJson = util.CookieUtil.getCookieValue(request, "cartList");

		if (cartListJson == null || cartListJson.equals("")) {
			cartListJson = "[]";
		}

		List<Cart> cartList_cookie = JSON.parseArray(cartListJson, Cart.class);

		// 2.从redis中提取购物车
		List<Cart> cartList_redis = cartService.findCartListFromRedis(username);

		// 3.将cookie中提取的购物车和redis中的购物车合并
		// 此法合并有bug，相同商家商品合并时，他会不会合并成一个商家栏，而是两个
		cartList_redis.addAll(cartList_cookie);

		// 4.将合并后的购物车存放到redis
		cartService.saveCartListToRedis(username, cartList_redis);

		// 5.清空cookie中购物车
		util.CookieUtil.setCookie(request, response, "cartList", "[]");

		// 页面重定向
		try {
			response.sendRedirect("/cart.html");
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
