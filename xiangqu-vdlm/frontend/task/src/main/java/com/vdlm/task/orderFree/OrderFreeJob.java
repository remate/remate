package com.vdlm.task.orderFree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderFreeJob {

	private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * 双11，免单，
	 */
	//@Scheduled(cron = "0 0 10-20/1 11 11 ?")
	public void activity_11_11() {
//		log.info("开始执行免单逻辑，当前时间："
//				+ DateFormatUtils.format(new Date(System.currentTimeMillis()),
//						"yyyy-MM-dd HH:mm:ss"));
		//11单 x 11次
		
		
		
	}
}
