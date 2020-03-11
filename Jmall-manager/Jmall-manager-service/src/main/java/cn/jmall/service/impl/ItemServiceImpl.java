package cn.jmall.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.jmall.common.jedis.JedisClient;
import cn.jmall.common.pojo.EasyUIDataGridResult;
import cn.jmall.common.util.E3Result;
import cn.jmall.common.util.IDUtils;
import cn.jmall.common.util.JsonUtils;
import cn.jmall.mapper.TbItemDescMapper;
import cn.jmall.mapper.TbItemMapper;
import cn.jmall.pojo.TbItem;
import cn.jmall.pojo.TbItemDesc;
import cn.jmall.pojo.TbItemExample;
import cn.jmall.pojo.TbItemExample.Criteria;
import cn.jmall.service.ItemService;

/**
 * 商品管理Service
 * 
 * @author Jmall
 * @CreateDate 2020年3月3日
 * @Description
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper tbItemMapper;
	@Autowired
	private TbItemDescMapper tbItemDescMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource
	private Destination topicDestination;
	@Autowired
	private JedisClient jedisClient;

	@Value("${REDIS_ITEM_PRE}")
	private String REDIS_ITEM_PRE;
	@Value("${REDIS_CACHE_EXPIRE}")
	private Integer REDIS_CACHE_EXPIRE;

	@Override
	public TbItem getItemById(long itemId) {
		// 添加缓存
		try {
			String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":BASE");
			if (StringUtils.isNotBlank(json)) {
				TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
				return tbItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 根据主键查询
		// TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		// 设置查询条件
		criteria.andIdEqualTo(itemId);
		// 执行查询
		List<TbItem> list = tbItemMapper.selectByExample(example);
		if (list != null && list.size() >= 0) {
			// 把结果添加到缓存
			try {
				jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":BASE", JsonUtils.objectToJson(list.get(0)));
				// 设置过期时间一小时
				jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":BASE", REDIS_CACHE_EXPIRE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list.get(0);
		}
		return null;
	}

	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		// 设置分页信息
		PageHelper.startPage(page, rows);
		// 执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = tbItemMapper.selectByExample(example);
		// 创建返回值对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		// 取分页结果
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		// 获取总记录数
		long total = pageInfo.getTotal();
		result.setTotal(total);
		return result;
	}

	@Override
	public E3Result addItem(TbItem item, String desc) {
		// 生成商品id
		long itemId = IDUtils.genItemId();
		// 补全商品item属性
		item.setId(itemId);
		// 1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		// 向商品表插入数据
		tbItemMapper.insert(item);
		// 创建一个商品描述表对应的POJO
		TbItemDesc itemDesc = new TbItemDesc();
		// 补全属性
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		// 向商品描述表插入数据
		tbItemDescMapper.insert(itemDesc);
		// 发送商品添加信息
		jmsTemplate.send(topicDestination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(itemId + "");
				return textMessage;
			}
		});
		// 返回结构
		return E3Result.ok();
	}

	@Override
	public E3Result selectItemById(long itemId) {
		TbItem itemById = this.getItemById(itemId);
		return E3Result.ok(itemById);
	}

	// 获取商品描述
	@Override
	public E3Result getTbItemDesc(long itemId) {
		TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);

		return E3Result.ok(tbItemDesc);
	}

	@Override
	public E3Result updateItem(TbItem item, String desc) {
		// 更新商品信息
		Date date = new Date();
		item.setUpdated(date);
		tbItemMapper.updateByPrimaryKeySelective(item);
		// 设置商品对应的id来更新商品描述
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(date);
		tbItemDescMapper.updateByPrimaryKeySelective(itemDesc);
		// 更新商品描述
		return E3Result.ok();
	}

	@Override
	public E3Result deleteBatchItem(String[] ids) {
		for (String id : ids) {
			// 删除商品信息
			tbItemMapper.deleteByPrimaryKey(Long.valueOf(id));
			// 删除商品描述
			tbItemDescMapper.deleteByPrimaryKey(Long.valueOf(id));
		}
		return E3Result.ok();
	}

	// 商品上架
	@Override
	public E3Result productOnShelves(String[] ids) {
		for (String id : ids) {
			TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(id));
			// 1：上架
			tbItem.setStatus((byte) 1);
			tbItemMapper.updateByPrimaryKeySelective(tbItem);
		}
		return E3Result.ok();
	}

	// 商品下架
	@Override
	public E3Result productOffShelves(String[] ids) {
		for (String id : ids) {
			TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(id));
			// 2：下架
			tbItem.setStatus((byte) 2);
			tbItemMapper.updateByPrimaryKeySelective(tbItem);
		}
		return E3Result.ok();
	}

	@Override
	public TbItemDesc getItemDescById(long itemId) {
		// 添加缓存
		try {
			String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":Desc");
			if (StringUtils.isNotBlank(json)) {
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return tbItemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemDesc itemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
		// 把结果添加到缓存
		try {
			jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":Desc", JsonUtils.objectToJson(itemDesc));
			// 设置过期时间一小时
			jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":Desc", REDIS_CACHE_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}

}
