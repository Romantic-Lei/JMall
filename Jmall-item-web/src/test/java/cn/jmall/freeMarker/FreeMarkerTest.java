package cn.jmall.freeMarker;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarkerTest {

	@Test
	public void testFreeMarkerTest() throws Exception {
//		1.创建一个模板文件
//		2.创建一个Configuration对象
		Configuration configuration = new Configuration(Configuration.getVersion());
//		3.设置模板文件保存的目录
		configuration.setDirectoryForTemplateLoading(new File("F:\\Eclipse\\eclipse-jee-oxygen-R-win32-x86_64\\eclipse\\workspace\\Jmall\\Jmall-item-web\\src\\main\\webapp\\WEB-INF\\ftl"));
//		4.模板文件的编码格式，一般就是utf-8
		configuration.setDefaultEncoding("utf-8");
//		5.加载一个模板文件，创建一个模板对象
		Template template = configuration.getTemplate("hello.ftl");
//		6.创建一个数据集，可以是POJO也可以是map，推荐使用map
		Map data = new HashMap<>();
		data.put("hello", "hello freeMarker");
//		7.创建一个Writer对象，指定输出文件的路径和文件名
		Writer out = new FileWriter("C:\\Users\\asus\\Desktop\\hello.txt");
//		8.生成静态页面
		template.process(data, out);
//		9.关闭流
		out.close();
	}
	
}
