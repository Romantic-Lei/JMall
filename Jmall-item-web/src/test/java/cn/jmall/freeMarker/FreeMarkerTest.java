package cn.jmall.freeMarker;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarkerTest {

	@Test
	public void testFreeMarkerTest() throws Exception {
		// 1.创建一个模板文件
		// 2.创建一个Configuration对象
		Configuration configuration = new Configuration(Configuration.getVersion());
		// 3.设置模板文件保存的目录
		configuration.setDirectoryForTemplateLoading(new File(
				"F:\\Eclipse\\eclipse-jee-oxygen-R-win32-x86_64\\eclipse\\workspace\\Jmall\\Jmall-item-web\\src\\main\\webapp\\WEB-INF\\ftl"));
		// 4.模板文件的编码格式，一般就是utf-8
		configuration.setDefaultEncoding("utf-8");
		// 5.加载一个模板文件，创建一个模板对象
		// Template template = configuration.getTemplate("hello.ftl");
		Template template = configuration.getTemplate("student.ftl");
		// 6.创建一个数据集，可以是POJO也可以是map，推荐使用map
		Map data = new HashMap<>();
		data.put("hello", "hello freeMarker");
		// 创建一个POJO对象
		Student stu = new Student(1, "小明", 18, "北京");
		data.put("student", stu);
		// 添加一个list
		List<Student> stuList = new ArrayList<>();
		stuList.add(new Student(1, "小明", 18, "北京"));
		stuList.add(new Student(2, "小明2", 18, "北京"));
		stuList.add(new Student(3, "小明3", 13, "北京"));
		stuList.add(new Student(4, "小明4", 26, "北京"));
		stuList.add(new Student(5, "小明5", 23, "北京"));
		stuList.add(new Student(6, "小明6", 21, "北京"));
		data.put("stuList", stuList);
		// 添加日期类型
		data.put("date", new Date());
		// null值的处理
		data.put("val", null);
		// 7.创建一个Writer对象，指定输出文件的路径和文件名
		Writer out = new FileWriter("C:\\Users\\asus\\Desktop\\student.html");
		// 8.生成静态页面
		template.process(data, out);
		// 9.关闭流
		out.close();
	}

}
