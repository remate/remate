package com.vdlm.task.product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vdlm.service.product.ProductService;

/**
 * 
 * @author huxaya
 * 示例：
 * @Scheduled(cron = "0 0/30 * * * ?") 每半小时执行一次自动上架
 * @Scheduled(fixedRate = 1000*3) 启动时执行一次，之后每隔3秒钟执行一次 
 * @Scheduled(fixedDelay = 1000*2) 在每次调度完成后都delay相同时间   
 */

@Component
public class ProductJob {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private ProductService productService;
	
	/**
	 * 定时上架
	 * 每5分钟执行一次自动上架
	 */
	@Scheduled(cron = "0 */5 * * * ?")
	public void autoOnSale() {
		
		long begin = System.currentTimeMillis();
		int result = productService.autoOnSaleByTask();
		long end = System.currentTimeMillis();
		StringBuffer info = new StringBuffer();
		info.append("执行自动上架：");
		info.append(result);
		info.append("条，花费：");
		info.append(end-begin);
		info.append("ms");
		log.info(info.toString());
	}
	
}
