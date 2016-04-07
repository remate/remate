package com.vdlm.task.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vdlm.service.pricing.CouponService;

@Component
public class CouponJob {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	CouponService couponService;
	
	/**
	 * 优惠活动到期自动关闭
	 * 每30分种执行一次
	 */
	@Scheduled(cron = "0 */30 * * * ?")
	public void reSendSms(){
		long begin = System.currentTimeMillis();
		int result = couponService.autoCloseActivity();
		long end = System.currentTimeMillis();
		StringBuffer info = new StringBuffer();
		info.append("关闭优惠活动：");
		info.append(result);
		info.append("条，花费：");
		info.append(end-begin);
		info.append("ms");
		log.info(info.toString());
	} 
}
