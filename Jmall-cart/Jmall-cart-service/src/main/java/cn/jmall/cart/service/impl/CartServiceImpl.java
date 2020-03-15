package cn.jmall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.jmall.cart.service.CartService;
import cn.jmall.common.jedis.JedisClient;
import cn.jmall.common.util.E3Result;
import cn.jmall.common.util.JsonUtils;
import cn.jmall.mapper.TbItemMapper;
import cn.jmall.pojo.TbItem;

/**
 * 购物车处理服务
 * 
 * @author Jmall
 * @CreateDate 2020年3月14日
 * @Description
 */
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbItemMapper itemMapper;

	@Value("${REDIS_CART_PRE}")
	private String REDIS_CART_PRE;

	@Override
	public E3Result addCart(long userId, long itemId, int num) {
		// 向redis中添加购物车
		// 数据类型是hash key：用户id field：商品id value：商品信息
		// 判断商品是否存在
		Boolean hexists = jedisClient.hexists(REDIS_CART_PRE + ":" + userId, itemId + "");
		if (hexists) {
			// 如果存在，数量相加
			String json = jedisClient.hget(REDIS_CART_PRE + ":" + userId, itemId + "");
			// 把json转换成TBItem
			TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
			item.setNum(item.getNum() + num);
			// 写回redis
			jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(item));
			return E3Result.ok();
		}

		// 如果不存在，根据商品id取商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		// 设置购物车数量
		item.setNum(num);
		// 取一张图片
		String images = item.getImage();
		if (StringUtils.isNoneBlank(images)) {
			item.setImage(images.split(",")[0]);
		}

		// 添加到购物车列表
		jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(item));
		// 返回成功
		return E3Result.ok();
	}

	@Override
	public E3Result mergeCart(long userId, List<TbItem> itemList) {
		// 建立商品列表
		// 把列表添加到购物车
		// 判断购物车是否有此商品
		// 如果有，数量相加
		// 如果没有，添加新商品
		for (TbItem tbItem : itemList) {
			addCart(userId, tbItem.getId(), tbItem.getNum());
		}
		// 返回成功
		return E3Result.ok();
	}

	@Override
	public List<TbItem> getCartList(long userId) {
		// 根据用户id查询购物车列表
		List<String> jsonList = jedisClient.hvals(REDIS_CART_PRE + ":" + userId);
		List<TbItem> itemList = new ArrayList<TbItem>();
		for (String str : jsonList) {
			// 创建一个TBItem对象
			TbItem item = JsonUtils.jsonToPojo(str, TbItem.class);
			// 添加都列表
			itemList.add(item);
		}

		return itemList;
	}

	@Override
	public E3Result updateCartNum(long userId, long itemId, int num) {
		// 从redis中获取商品信息
		String json = jedisClient.hget(REDIS_CART_PRE + ":" + userId, itemId + "");
		// 更新商品数量
		TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
		tbItem.setNum(num);
		// 写入redis
		jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
		return E3Result.ok();
	}

	@Override
	public E3Result deleteCartItem(long userId, long itemId) {
		// 删除购物车商品
		jedisClient.hdel(REDIS_CART_PRE + ":" + userId, itemId + "");
		return E3Result.ok();
	}

	@Override
	public E3Result clearCartItem(long userId) {
		// 删除购物车信息
		jedisClient.del(REDIS_CART_PRE + ":" + userId);
		return E3Result.ok();
	}

}
