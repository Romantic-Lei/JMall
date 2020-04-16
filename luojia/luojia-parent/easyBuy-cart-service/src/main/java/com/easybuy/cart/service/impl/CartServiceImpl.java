package com.easybuy.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.easybuy.cart.service.CartService;
import com.easybuy.mapper.TbItemMapper;
import com.easybuy.pojo.TbItem;
import com.easybuy.pojo.TbOrderItem;
import com.easybuy.pojogroup.Cart;

/**
 * 购物车服务实现类
 * 
 * @author Romantic
 * @CreateDate 2020年4月15日
 * @Description
 */
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private TbItemMapper itemMapper;

	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

		if (cartList == null) {
			cartList = new ArrayList();
		}
		// 1. 根据商品id， 查找商品
		TbItem item = itemMapper.selectByPrimaryKey(itemId);

		if (item == null) {
			throw new RuntimeException("商品没有找到");
		}

		if (!item.getStatus().equals("1")) {
			// 买家购买时显示正常，但是商家可能在这时候下架商品，所以需要在后端再次进行判断
			throw new RuntimeException("商品已下架");
		}

		// 2. 获取商家id
		String sellerId = item.getSellerId();
		// 3. 判断购物车列表中是否存在该商家的购物车
		Cart cart = searchCartListBySellerId(cartList, sellerId);

		if (cart == null) {
			if (num <= 0) {
				throw new RuntimeException("数量非法");
			}
			// 如果为空，则当前购物车没有此商家商品
			cart = new Cart();
			cart.setSellerId(sellerId);
			cart.setSellerName(item.getSeller());// 商家名称
			List<TbOrderItem> orderItemList = new ArrayList();

			TbOrderItem orderItem = createOrderItem(item, num);// 构建购物车明细

			orderItemList.add(orderItem);// 将新建的购物车明细添加到购物车对象中的明细表中
			cart.setOrderItemList(orderItemList);// 当新建的购物车明细列表添加到购物车对象中
			cartList.add(cart);// 将购物车对象装入购物车列表中

		} else {
			// 根据商品SKU id 查询购物车明细
			TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
			if (orderItem == null) {
				// 没有查到，新增商品明细
				orderItem = createOrderItem(item, num);// 构建购物车明细
				cart.getOrderItemList().add(orderItem);// 将购物车明细添加到购物车对象中

			} else {
				// 查到了，增加数量
				orderItem.setNum(orderItem.getNum() + num);// 更改数量
				orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));// 更改金额

				// 如果修改的数量小于等于0，移除商品
				if (orderItem.getNum() <= 0) {
					cart.getOrderItemList().remove(orderItem);
				}

				// 如果修改后，购物车中对应商家对象中没有明细，从购物车列表中移除购物车对象
				if (cart.getOrderItemList().size() == 0) {
					cartList.remove(cart);
				}
			}
		}
		return cartList;
	}

	/**
	 * 在购物车列表中根据商家id查询购物车对象
	 * 
	 * @param cartList
	 * @param sellerId
	 * @return
	 */
	private Cart searchCartListBySellerId(List<Cart> cartList, String sellerId) {
		for (Cart cart : cartList) {
			if (sellerId.equals(cart.getSellerId())) {
				return cart;
			}
		}
		return null;
	}

	/**
	 * 根据sku的id在购物车明细列表中查询明细对象
	 * 
	 * @param orderItem
	 *            购物车明细列表
	 * @param itemId
	 *            SKU ID
	 * @return 购物车辨析对象
	 */
	private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
		for (TbOrderItem orderItem : orderItemList) {
			if (orderItem.getItemId().longValue() == itemId.longValue()) {
				// Long是引用类型，不能直接去判断相等，必须转换成值类型
				return orderItem;
			}
		}
		return null;
	}

	/**
	 * 根据sku构建购物车商品明细
	 * 
	 * @param item
	 * @param num
	 * @return
	 */
	private TbOrderItem createOrderItem(TbItem item, Integer num) {
		TbOrderItem orderItem = new TbOrderItem(); // 购物车明细
		orderItem.setItemId(item.getId());// SKU
		orderItem.setGoodsId(item.getGoodsId());// SPU
		orderItem.setTitle(item.getTitle());// 商品标题
		orderItem.setSellerId(item.getSellerId());// 商家id
		orderItem.setPrice(item.getPrice());// 价格
		orderItem.setNum(num);// 数量
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));// 金额
		orderItem.setPrice(item.getPrice());

		return orderItem;
	}

}
