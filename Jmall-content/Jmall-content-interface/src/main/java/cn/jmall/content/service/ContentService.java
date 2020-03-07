package cn.jmall.content.service;

import cn.jmall.common.util.E3Result;
import cn.jmall.pojo.TbContent;

/**
 * 内容管理
 * @author Jmall
 * @CreateDate 2020年3月7日
 * @Description
 */
public interface ContentService {

	public E3Result addContent(TbContent content);		// 新增内容管理
	
}
