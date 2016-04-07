package com.vdlm.task.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vdlm.service.order.OrderService;

/**
 * 订单相关的定时程序
 * @author odin
 *
 */
@Component
public class OrderJob {
	
    private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	OrderService orderService;
	
	/**
	 * 自动签收日提前两天提醒买家
	 * 每天上午10点检查一次
	 */
	@Scheduled(cron = "0 0 10 * * ?")
	public void autoSignRemind(){
	    log.info("begin on order autoSignRemind");
		long begin = System.currentTimeMillis();
		int result = orderService.autoRemindSign();
		long end = System.currentTimeMillis();
		log.info("end on order autoSignRemind");
		StringBuffer info = new StringBuffer();
		info.append("提醒买家未签收订单即将由系统自动签收：");
		info.append(result);
		info.append("条短信，花费：");
		info.append(end-begin);
		info.append("ms");
		log.info(info.toString());
	} 
	
	/**
	 * 每隔三天下午14点提醒未发货的卖家
	 */
	@Scheduled(cron = "0 0 14 1,4,7,10,13,16,19,22,25,28 * ?")
	public void autoRemindShipped(){
	    log.info("begin on order autoRemindShipped");
		long begin = System.currentTimeMillis();
		int result = orderService.autoRemindShipped();
		log.info("end on order autoRemindShipped");
		StringBuffer info = new StringBuffer();
		info.append("系统推送提醒三天未发货的卖家：").append(result).append("家，花费：");
		info.append(System.currentTimeMillis()-begin).append("ms");
		log.info(info.toString());
	} 
	
	
	/**
	 * 买家未付款超过3天自动取消订单
	 * 每五分种执行一次自动上架
	 */
	@Scheduled(cron = "0 */3 * * * ?")
	public void autoCancel(){
	    log.info("begin on order autoCancel");
		long begin = System.currentTimeMillis();
		int result = orderService.autoCancel();
		long end = System.currentTimeMillis();
		log.info("end on order autoCancel");
		StringBuffer info = new StringBuffer();
		info.append("买家未付款超过1天自动取消订单成功：");
		info.append(result);
		info.append("条，花费：");
		info.append(end-begin);
		info.append("ms");
		log.info(info.toString());
		
	} 
	
	@Scheduled(cron = "0 */5 * * * ?")
    public void autoSign() {
	    log.info("begin on order autoSign");
        long begin = System.currentTimeMillis();
        int result = orderService.autoSign();
        long end = System.currentTimeMillis();
        StringBuffer info = new StringBuffer();
        info.append("卖家发货超过10天自动签收：");
        info.append(result);
        info.append("条，花费：");
        info.append(end-begin);
        info.append("ms");
        log.info(info.toString());
    }
	
	
	
}
