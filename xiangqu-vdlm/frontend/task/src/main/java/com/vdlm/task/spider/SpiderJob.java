package com.vdlm.task.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vdlm.service.shop.ShopService;

/**
 * 每天定时执行搬家
 * @author Sallen
 *
 */
@Component
public class SpiderJob {
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ShopService shopService;
	
//	/**
//	 * 定时更新店铺商品
//	 * 每天9:01分，发起请求
//	 */
//	@Scheduled(cron = "0 1 9 * * ?")
//	public void autoMoveProduct() {		
//		shopService.autoMoveProductByTask();
//		StringBuffer info = new StringBuffer();
//		info.append("定时执行自动更新商品");
//		log.info(info.toString());
//	}
	
}
