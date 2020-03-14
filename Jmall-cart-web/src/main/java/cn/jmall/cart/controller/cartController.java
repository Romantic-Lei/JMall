package cn.jmall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.jmall.cart.service.CartService;
import cn.jmall.common.util.CookieUtils;
import cn.jmall.common.util.E3Result;
import cn.jmall.common.util.JsonUtils;
import cn.jmall.pojo.TbItem;
import cn.jmall.pojo.TbUser;
import cn.jmall.service.ItemService;

/**
 * 购物车处理Controller
 * 
 * @author Jmall
 * @CreateDate 2020年3月14日
 * @Description
 */
@Controller
public class cartController {

	@Autowired
	private ItemService itemService;
	@Autowired
	private CartService cartService;
	
	@Value("${COOKIE_CART_EXPIRE}")
	private Integer COOKIE_CART_EXPIRE;

	@RequestMapping("cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId, @RequestParam(defaultValue = "1") Integer num,
			HttpServletRequest request, HttpServletResponse response) {
		// 判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("user");
		if (user != null) {
			// 用户登录，保存到服务端
			cartService.addCart(user.getId(), itemId, num);
			// 保存逻辑视图
			return "cartSuccess";
		}
		// 从cookie中取购物车列表
		List<TbItem> cartList = getCartListFromCookie(request);
		boolean flag = false;
		// 判断商品在商品列表中是否存在
		for (TbItem tbItem : cartList) {
			if (tbItem.getId() == itemId.longValue()) {
				// 如果存在，商品数量相加
				tbItem.setNum(tbItem.getNum() + num);
				flag = true;
				// 跳出循环
				break;
			}
		}

		// 不存在
		if (!flag) {
			// 根据商品id查询商品信息，得到一个TBItem对象
			TbItem tbItem = itemService.getItemById(itemId);
			// 设置商品数量
			tbItem.setNum(num);
			// 取一张图片
			String image = tbItem.getImage();
			if (StringUtils.isNotBlank(image)) {
				tbItem.setImage(image.split(",")[0]);
			}
			// 把商品调价到商品列表
			cartList.add(tbItem);
		}
		// 写入cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
		// 返回添加成功页面
		return "cartSuccess";
	}

	/**
	 * 展示购物车列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/cart/cart")
	public String showCatList(HttpServletRequest request) {
		// 从cookie中取购物车列表
		List<TbItem> cartList = getCartListFromCookie(request);
		// 把列表传递给页面
		request.setAttribute("cartList", cartList);
		// 返回逻辑视图
		return "cart";
	}

	/**
	 * 更新购物车商品数量信息
	 * 
	 * @return
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public E3Result updateCartNum(@PathVariable Long itemId, @PathVariable Integer num, HttpServletRequest request,
			HttpServletResponse response) {
		// 从cookie中取购物车列表
		List<TbItem> cartList = getCartListFromCookie(request);
		// 遍历商品列表找到对应的商品
		for (TbItem tbItem : cartList) {
			if (tbItem.getId().longValue() == itemId) {
				// 更新数量
				tbItem.setNum(num);
				break;
			}
		}
		// 把购物车列表写回cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
		// 返回成功
		return E3Result.ok();
	}

	/**
	 * 删除购物车商品牌
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId, HttpServletRequest request, HttpServletResponse response) {
		// 从cookie中取购物车列表
		List<TbItem> cartList = getCartListFromCookie(request);
		// 遍历商品列表找到对应的商品
		for (TbItem tbItem : cartList) {
			if (tbItem.getId().longValue() == itemId) {
				// 遍历列表，找到要删除的商品
				cartList.remove(tbItem);
				break;
			}
		}
		// 把购物车列表更新到cookie信息
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);

		// 返回逻辑视图
		return "redirect:/cart/cart.html";
	}

	/**
	 * 从cookie中取得购物车列表的处理
	 * 
	 * @param request
	 * @return
	 */
	private List<TbItem> getCartListFromCookie(HttpServletRequest request) {
		// 获取cookie中购物车商品信息
		String json = CookieUtils.getCookieValue(request, "cart", true);
		// 判断json是否为空
		if (StringUtils.isBlank(json)) {
			return new ArrayList<>();
		}

		// 把json转换成商品列表
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}

}
