package com.easybuy.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.easybuy.pojo.TbOrderItem;

public class Cart implements Serializable {

	private String sellerId; // 商家id
	private String sellerName; // 商家名称
	private List<TbOrderItem> orderItemList; // 商品集合

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public List<TbOrderItem> getOrderItemList() {
		return orderItemList;
	}

	public void setOrderItemList(List<TbOrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}

}
