package cn.jmall.item.listener;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.jmall.item.pojo.Item;
import cn.jmall.pojo.TbItem;
import cn.jmall.pojo.TbItemDesc;
import cn.jmall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 监听商品添加消息，生成对应的静态页面
 * 
 * @author Jmall
 * @CreateDate 2020年3月12日
 * @Description
 */
public class HtmlGenListener implements MessageListener {

	@Autowired
	private ItemService itemService;
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@Value("${HTML_GEN_PATH}")
	private String HTML_GEN_PATH;

	@Override
	public void onMessage(Message message) {
		try {
			// 创建一个模板，可以参考jsp
			// 从消息中取商品id
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			Long itemId = new Long(text);
			// 等待事务提交
			Thread.sleep(100);
			// 根据商品id查询商品信息，商品基本信息和商品描述
			TbItem tbItem = itemService.getItemById(itemId);
			Item item = new Item(tbItem);
			// 取商品描述
			TbItemDesc itemDesc = itemService.getItemDescById(itemId);
			// 创建一个数据集，把商品数据封装
			Map data = new HashMap<>();
			data.put("item", itemDesc);
			data.put("itemDesc", itemDesc);
			// 加载模板对象
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			// 创建一个输出流，指定输出目录及文件名
			Writer out = new FileWriter(HTML_GEN_PATH + itemId + ".html");
			// 生成静态页面
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 关闭流
	}

}
