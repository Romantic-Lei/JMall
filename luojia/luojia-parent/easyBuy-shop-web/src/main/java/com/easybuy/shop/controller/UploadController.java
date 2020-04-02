package com.easybuy.shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.easybuy.entity.Result;

import cn.jmall.common.util.FastDFSClient;

@RestController
public class UploadController {

	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;
	
	@RequestMapping("/upload")
	public Result upload(MultipartFile file) {

		try {
			// 原始文件名
			String originalFilename = file.getOriginalFilename();
			// 得到上传文件的扩展名
			String extName = originalFilename.substring(originalFilename.indexOf(".") + 1);
			
			// 创建一个工具类客户端
			FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");

			String filePath = client.uploadFile(file.getBytes(), extName);
			
			return new Result(true, FILE_SERVER_URL + filePath);

		} catch (Exception e) {

			e.printStackTrace();
			return new Result(false, "上传失败");
		}
	}

}
