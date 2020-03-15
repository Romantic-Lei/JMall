package cn.jmall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.jmall.cart.service.CartService;
import cn.jmall.pojo.TbItem;
import cn.jmall.pojo.TbUser;

/**
 * 订单管理Controller
 * @author Jmall
 * @CreateDate 2020年3月15日
 * @Description
 */
@Controller
public class OrderController {
	
	@Autowired
	private CartService cartService;

	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request) {
//		取用户id
		TbUser user = (TbUser) request.getAttribute("user");
//		根据用户id取收货地址列表  目前使用静态数据
//		取支付方式 目前使用静态数据
//		根据用户id取购物车列表
		List<TbItem> cartList = cartService.getCartList(user.getId());
//		把购物车列表传递给jsp
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}
	
}
