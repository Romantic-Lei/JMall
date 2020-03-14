package cn.jmall.cart.service.impl;

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

}