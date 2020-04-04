package com.easybuy.sellergoods.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.easybuy.entity.PageResult;
import com.easybuy.mapper.TbBrandMapper;
import com.easybuy.mapper.TbGoodsDescMapper;
import com.easybuy.mapper.TbGoodsMapper;
import com.easybuy.mapper.TbItemCatMapper;
import com.easybuy.mapper.TbItemMapper;
import com.easybuy.mapper.TbSellerMapper;
import com.easybuy.pojo.TbBrand;
import com.easybuy.pojo.TbGoods;
import com.easybuy.pojo.TbGoodsExample;
import com.easybuy.pojo.TbGoodsExample.Criteria;
import com.easybuy.pojo.TbItem;
import com.easybuy.pojo.TbItemCat;
import com.easybuy.pojo.TbSeller;
import com.easybuy.pojogroup.Goods;
import com.easybuy.sellergoods.service.GoodsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper GoodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {

		goods.getGoods().setAuditStatus("0"); // 设置初始化状态，商品未上架
		// 添加商品
		goodsMapper.insert(goods.getGoods());

		// 添加商品描述
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId()); // 设置商品描述id
		GoodsDescMapper.insert(goods.getGoodsDesc());

		if ("1".equals(goods.getGoods().getIsEnableSpec())) {// 如果启用规格
			// 添加SKU商品信息
			List<Map> skuList = goods.getSkuList();
			for (Map map : skuList) {
				TbItem item = new TbItem();

				// 构建标题 SPU名称+规格选项值
				String title = goods.getGoods().getGoodsName();// SPU名称
				Map<String, Object> specMap = (Map) map.get("spec");
				for (String key : specMap.keySet()) {
					title += " " + specMap.get(key);
				}

				item.setTitle(title);// 标题
				item.setPrice(new BigDecimal((String) map.get("price")));// 价格

				if (map.get("num") instanceof String) {
					item.setNum(Integer.parseInt((String) map.get("num")));// 库存
					System.out.println("String...");
				}

				if (map.get("num") instanceof Integer) {
					item.setNum((Integer) map.get("num"));// 库存
					System.out.println("Integer...");
				}
				item.setStatus((String) map.get("status"));// 状态
				item.setIsDefault((String) map.get("isDefault"));// 是否默认

				// 商品分类
				item.setCategoryid(goods.getGoods().getCategory3Id()); // 三级分类id
				item.setCreateTime(new Date());// 创建日期
				item.setUpdateTime(new Date()); // 更新日期

				item.setGoodsId(goods.getGoods().getId()); // 商品id
				item.setSellerId(goods.getGoods().getSellerId());// 商家id

				// 分类名称
				TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
				item.setCategory(itemCat.getName());

				// 品牌名称
				TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
				item.setBrand(brand.getName());

				// 商家名称
				TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
				item.setSeller(seller.getNickName());

				// 图片
				List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
				if (imageList.size() > 0) {
					item.setImage((String) imageList.get(0).get("url"));
				}

				itemMapper.insert(item);
			}
		} else {// 如果不启用规格，只生成一条sku数据

			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());// 商品名称
			item.setPrice(goods.getGoods().getPrice());// 价格
			item.setNum(99999);// 库存数
			item.setStatus("1");// 有效状态
			item.setIsDefault("1");// 设置为默认
			item.setSellerId(goods.getGoods().getSellerId());// 商家ID
			item.setGoodsId(goods.getGoods().getId());// 商品ID

			itemMapper.insert(item);
		}
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods) {
		goodsMapper.updateByPrimaryKey(goods);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id) {
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			goodsMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		if (goods != null) {
			if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
				// criteria.andSellerIdLike("%" + goods.getSellerId() + "%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
				criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
			}
			if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
				criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
			}
			if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
				criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
			}
			if (goods.getCaption() != null && goods.getCaption().length() > 0) {
				criteria.andCaptionLike("%" + goods.getCaption() + "%");
			}
			if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
				criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
			}
			if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
				criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
			}
			if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
				criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
			}

		}

		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

}
