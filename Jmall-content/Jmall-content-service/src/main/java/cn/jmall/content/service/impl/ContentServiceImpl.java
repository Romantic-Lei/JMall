package cn.jmall.content.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.jmall.common.util.E3Result;
import cn.jmall.content.service.ContentService;
import cn.jmall.mapper.TbContentMapper;
import cn.jmall.pojo.TbContent;

/**
 * 内容管理Service
 * @author Jmall
 * @CreateDate 2020年3月7日
 * @Description
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper tbContentMapper;
	
	@Override
	public E3Result addContent(TbContent content) {
		// 将内容数据插入到内容表
		Date date = new Date();
		content.setCreated(date);
		content.setUpdated(date);
		// 插入到数据库
		tbContentMapper.insert(content);
		return E3Result.ok();
	}

}
