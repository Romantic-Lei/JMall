package com.easybuy.sms.listener;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.easybuy.sms.util.SmsUtil;

/**
 * 短信消息监听类
 * @author Romantic
 * @CreateDate 2020年4月11日
 * @Description
 */
public class SmsListener {

	@Autowired
	private SmsUtil SmsUtil;
	
	@JmsListener(destination="ebSms")
	public void sendSms(Map<String, String> map) {
		try {
			SendSmsResponse response = SmsUtil.sendSms(
					map.get("mobile"),
					map.get("signName"), 
					map.get("templateCode"),
					map.get("param"));
			System.out.println("code " + response.getCode());
			
		} catch (ClientException e) {
			
			e.printStackTrace();
		}
	}
	
}
