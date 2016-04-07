package com.vdlm.task.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vdlm.service.order.OrderRefundService;

/**
 * 退款订单相关的定时程序
 *
 */
@Component
public class OrderRefundJob {
	
    private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	OrderRefundService orderRefundService;
	
	/**
	 * 买家提交申请，卖家超过3天未操作时自动同意
	 * 每五分种执行一次
	 */
	@Scheduled(cron = "0 */1 * * * ?")
    public void autoConfirm() {
	    log.info("begin on order refund autoConfirm");
	    log.debug("调用自动审核买家退款申请的方法");
        
	    // 记录系统自动审核时间
	    long begin = System.currentTimeMillis();
        int result = orderRefundService.autoConfirm();
        long end = System.currentTimeMillis();
        
        createLog("买家提交退款申请，卖家超过3天未操作时自动同意：",result, end-begin);
    }

	/**
	 * 卖家同意退款，买家7天内未发货，视为申请取消
	 * 每五分种执行一次
	 */
	@Scheduled(cron = "0 */1 * * * ?")
    public void autoSuccessWithBuyerNoShip() {
	    log.info("begin on order refund autoSuccessWithBuyerNoShip");
	    log.debug("调用自动审核买家退款申请时，买家7天未发货的方法");
        long begin = System.currentTimeMillis();
        int result = orderRefundService.autoSuccessWithBuyerNoShip();
        long end = System.currentTimeMillis();
        
        createLog("卖家同意退款，买家7天内未发货，视为订单完成：", result,end-begin);
    }
	
	/**
	 * 卖家同意退款，买家发货，卖家10天内未确认收货，视为订单完成==卖家确认收货
	 * 每五分种执行一次
	 */
	@Scheduled(cron = "0 */1 * * * ?")
    public void autoClosedWithBuyerNoShip() {
		log.debug("调用自动审核买家退款申请，买家发货，卖家未的方法");
	    log.info("begin on order refund autoClosedWithBuyerNoShip");
	    
        long begin = System.currentTimeMillis();
        int result = orderRefundService.autoClosedWithSellerNoSign();
        long end = System.currentTimeMillis();
        
        createLog("卖家同意退款，买家发货，卖家10天内未确认收货，视为订单关闭：", result, end-begin);
    }
	
	/**
	 * 每隔三天下午14点提醒有退款订单的卖家
	 */
	@Scheduled(cron = "0 0 14 1,4,7,10,13,16,19,22,25,28 * ?")
	public void autoRemindShipped(){
	    log.info("begin on order autoRemindSeller");
		long begin = System.currentTimeMillis();
		int result = orderRefundService.autoRemindSeller();
		log.info("end on order autoRemindSeller");
		long end = System.currentTimeMillis();
		
		createLog("系统推送提醒三天未处理退款的卖家：", result, end-begin);
	} 
	
	// log 记录任务信息
	private void createLog(String tip, int result, long time) {
		StringBuffer info = new StringBuffer();
        info.append(tip);
        info.append(result);
        info.append("条，花费：");
        info.append(time);
        info.append("ms");
        log.info(info.toString());
	}
}
