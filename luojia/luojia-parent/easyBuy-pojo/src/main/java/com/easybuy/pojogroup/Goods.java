package com.easybuy.pojogroup;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.easybuy.pojo.TbGoods;
import com.easybuy.pojo.TbGoodsDesc;
import com.easybuy.pojo.TbItem;

/**
 * 商品组合实体类
 * 
 * @author Romantic
 * @CreateDate 2020年4月2日
 * @Description
 */
public class Goods implements Serializable {

	private TbGoods goods;
	private TbGoodsDesc goodsDesc;
	private List<TbItem> itemList;
	private List<Map> skuList;// SKU商品列表
	private Map map;		// 扩展属性

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public List<Map> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<Map> skuList) {
		this.skuList = skuList;
	}

	public TbGoods getGoods() {
		return goods;
	}

	public void setGoods(TbGoods goods) {
		this.goods = goods;
	}

	public TbGoodsDesc getGoodsDesc() {
		return goodsDesc;
	}

	public void setGoodsDesc(TbGoodsDesc goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public List<TbItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<TbItem> itemList) {
		this.itemList = itemList;
	}

}
