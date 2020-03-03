package cn.jmall.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.jmall.mapper.TbItemMapper;
import cn.jmall.pojo.TbItem;
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

}
