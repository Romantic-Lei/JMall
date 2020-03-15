package cn.jmall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.jmall.cart.service.CartService;
import cn.jmall.common.util.E3Result;
import cn.jmall.order.pojo.OrderInfo;
import cn.jmall.order.service.OrderService;
import cn.jmall.pojo.TbItem;
import cn.jmall.pojo.TbUser;

/**
 * 订单管理Controller
 * 
 * @author Jmall
 * @CreateDate 2020年3月15日
 * @Description
 */
@Controller
public class OrderController {

	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;

	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request) {
		// 取用户id
		TbUser user = (TbUser) request.getAttribute("user");
		// 根据用户id取收货地址列表 目前使用静态数据
		// 取支付方式 目前使用静态数据
		// 根据用户id取购物车列表
		List<TbItem> cartList = cartService.getCartList(user.getId());
		// 把购物车列表传递给jsp
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}

	@RequestMapping(value = "/order/create", method = RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo, HttpServletRequest request) {
		// 取用户信息
		TbUser user = (TbUser) request.getAttribute("user");
		// 把信用户息添加到orderInfo中
		orderInfo.setUserId(user.getId());
		orderInfo.setBuyerNick(user.getUsername());
		// 调用服务生成订单
		E3Result e3Result = orderService.createOrder(orderInfo);
		// 如果订单生成成功，需要删除购物车
		if (e3Result.getStatus() == 200) {
			// 清空购物车
			cartService.clearCartItem(user.getId());
		}
		// 把订单号传递给页面
		request.setAttribute("orderId", e3Result.getData());
		request.setAttribute("payment", orderInfo.getPayment());
		// 返回逻辑视图
		return "success";
	}

}
