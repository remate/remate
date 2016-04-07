package com.vdlm.task.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vdlm.service.msg.SmsMessageService;

/**
 * 发送sms失败后的重试程序
 * @author odin
 *
 */
@Component
public class SmsMessageJob {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	SmsMessageService smsMessageService;
	
	/**
	 * 发送sms失败后的重试程序
	 * 每一分种执行一次自动上架
	 */
	@Scheduled(cron = "0 */1 * * * ?")
	public void reSendSms(){
		long begin = System.currentTimeMillis();
		int result = smsMessageService.reSend();
		long end = System.currentTimeMillis();
		StringBuffer info = new StringBuffer();
		info.append("发送sms失败后的重试成功执行：");
		info.append(result);
		info.append("条，花费：");
		info.append(end-begin);
		info.append("ms");
		log.info(info.toString());
	} 
	
}
