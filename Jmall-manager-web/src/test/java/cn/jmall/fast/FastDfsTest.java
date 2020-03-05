package cn.jmall.fast;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import cn.jmall.common.util.FastDFSClient;

public class FastDfsTest {

	// 文件上传
//	@Test
	public void testUpload() throws FileNotFoundException, IOException, MyException {
		// 创建配置文件，文件名任意，内容是tracker服务器地址
		// 使用全局对象加载配置文件
		ClientGlobal.init("F:\\Eclipse\\eclipse-jee-oxygen-R-win32-x86_64\\eclipse\\workspace\\Jmall\\Jmall-manager-web\\src\\main\\resources\\conf\\client.conf");
		// 创建一个TrackerClient对象
		TrackerClient trackerClient = new TrackerClient();
		// 通过TrackerClient获取一个TrackerServer对象
		TrackerServer trackerServer = trackerClient.getConnection();
		// 创建一个StorageServer的引用，可以是null
		StorageServer storageServer = null;
		// 创建一个StorageClient， 参数需要TrackerServer和StorageServer
		StorageClient storageClient = new StorageClient(trackerServer, storageServer);
		// 创建StorageClient上传文件
		String[] files = storageClient.upload_file("F:\\桌面美图\\58da2a0732b1f.jpg", "jpg", null);
		for (String string : files) {
			System.out.println(string);
		}
	}
	
	// 文件上传
//	@Test
	public void testFastDfsClient() throws Exception {
		FastDFSClient fastDFSClient = new FastDFSClient("F:\\Eclipse\\eclipse-jee-oxygen-R-win32-x86_64\\eclipse\\workspace\\Jmall\\Jmall-manager-web\\src\\main\\resources\\conf\\client.conf");
		String string = fastDFSClient.uploadFile("F:\\桌面美图\\58da2a0732b1f.jpg");
		System.out.println(string);
	}
}
