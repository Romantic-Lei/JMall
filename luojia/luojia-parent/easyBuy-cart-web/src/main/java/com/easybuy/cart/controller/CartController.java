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

		try {
			List<Cart> cartList = findCartList(request);
			// 添加商品到购物车
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);
			// 将购物车json字符串存入cookie
			util.CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList));

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
			return JSON.parseArray(cartListJson, Cart.class);
		} else {
			return cartService.findCartListFromRedis(username);
		}
	}

}
