package cn.jmall.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.jmall.common.pojo.EasyUIDataGridResult;
import cn.jmall.common.util.E3Result;
import cn.jmall.common.util.IDUtils;
import cn.jmall.mapper.TbItemDescMapper;
import cn.jmall.mapper.TbItemMapper;
import cn.jmall.pojo.TbItem;
import cn.jmall.pojo.TbItemDesc;
import cn.jmall.pojo.TbItemExample;
import cn.jmall.pojo.TbItemExample.Criteria;
import cn.jmall.service.ItemService;

/**
 * 商品管理Service
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
	
	@Override
	public TbItem getItemById(long itemId) {
		// 根据主键查询
		// TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
		// return tbItem;
		
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		// 设置查询条件
		criteria.andIdEqualTo(itemId);
		// 执行查询
		List<TbItem> list = tbItemMapper.selectByExample(example);
		if(list != null && list.size() >= 0) {
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
		item.setStatus((byte)1);
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
		// 返回结构
		return E3Result.ok();
	}

	@Override
	public E3Result selectItemById(long itemId) {
		TbItem itemById = this.getItemById(itemId);
		return E3Result.ok(itemById);;
	}
	
	// 获取商品描述
	@Override
	public TbItemDesc getTbItemDesc(long itemId) {
		
		return tbItemDescMapper.selectByPrimaryKey(itemId);
	}

}
