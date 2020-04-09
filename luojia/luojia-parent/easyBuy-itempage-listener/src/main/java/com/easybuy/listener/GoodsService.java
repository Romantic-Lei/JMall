package com.easybuy.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easybuy.mapper.TbGoodsDescMapper;
import com.easybuy.mapper.TbGoodsMapper;
import com.easybuy.mapper.TbItemCatMapper;
import com.easybuy.mapper.TbItemMapper;
import com.easybuy.pojo.TbGoods;
import com.easybuy.pojo.TbGoodsDesc;
import com.easybuy.pojo.TbItem;
import com.easybuy.pojo.TbItemExample;
import com.easybuy.pojogroup.Goods;

/**
 * 商品业务逻辑类
 * 
 * @author Romantic
 * @CreateDate 2020年4月9日
 * @Description
 */
@Service
public class GoodsService {

	@Autowired
	public TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper GoodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;

	/**
	 * 根据商品ID获取商品（组合实体类）
	 * 
	 * @param goodsId
	 * @return
	 */
	public Goods findOne(Long goodsId) {
		// 查询商品信息
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
		// 查询商品扩展信息
		TbGoodsDesc tbGoodsDesc = GoodsDescMapper.selectByPrimaryKey(goodsId);
		Goods goods = new Goods();
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(tbGoodsDesc);

		// 查询商品明细
		TbItemExample example = new TbItemExample();
		com.easybuy.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andGoodsIdEqualTo(goodsId);// 商品id
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList);

		String category1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
		String category2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
		String category3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
		
		Map map = new HashMap<>();
		map.put("category1", category1);
		map.put("category2", category2);
		map.put("category3", category3);
		
		goods.setMap(map);
		
		return goods;
	}

}
