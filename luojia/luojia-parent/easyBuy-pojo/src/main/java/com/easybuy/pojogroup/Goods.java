package com.easybuy.pojogroup;

import java.io.Serializable;
import java.util.List;

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
