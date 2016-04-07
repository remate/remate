package com.vdlm.task.alipay;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vdlm.service.outpay.ThirdPartyPayment;
import com.vdlm.utils.DateUtils;

@Component
public class AlipayTradeAccountReport {

	@Autowired
	private ThirdPartyPayment aliPayment;
	
	/**
	 * 检查支付宝成功的订单是否与系统不一致, 每天凌晨检查头一天的
	 */
	@Scheduled(cron = "0 0 6 * * ?")
	public void checkTradeStatusAlipay(){
		Date now = new Date(System.currentTimeMillis());
		Date yesterdayStart = DateUtils.getYesterdayStart(now);
		Date yesterdayEnd = DateUtils.getYesterdayEnd(now);
		String startTime = DateFormatUtils.format(yesterdayStart, "yyyy-MM-dd HH:mm:ss");
		String endTime = DateFormatUtils.format(yesterdayEnd, "yyyy-MM-dd HH:mm:ss");
		aliPayment.tradeAccountReport(startTime, endTime);
	}
	
}
