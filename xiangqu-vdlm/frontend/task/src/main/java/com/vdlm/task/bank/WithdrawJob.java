package com.vdlm.task.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vdlm.service.bank.WithdrawApplyService;
import com.vdlm.service.union.UnionService;

@Component
public class WithdrawJob {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private WithdrawApplyService withdrawApplyService;
	
	@Autowired
	private UnionService unionService;
	
	/**
	 * 定时生成提现申请
	 * 每天凌晨5分，发起提现申请
	 */
	@Scheduled(cron = "0 5 0 * * ?")
	public void autoWithdraw() {

		long begin = System.currentTimeMillis();
		Long result = withdrawApplyService.autoWithdrawByTask();
		long end = System.currentTimeMillis();
		StringBuffer info = new StringBuffer();
		info.append("执行自动提取：");
		info.append(result);
		info.append("条，花费：");
		info.append(end-begin);
		info.append("ms");
		log.info(info.toString());
	}
	
	/**
	 * 把超过30天的佣金账户转入到可用账户
	 */
	//@Scheduled(cron = "0 1 0 * * ?")
	@Scheduled(cron = "0 6 0 * * ?")
	public void commissionToAvailable(){
		long begin = System.currentTimeMillis();
		Long result = unionService.autoCommissionToAvailable();
		long end = System.currentTimeMillis();
		StringBuffer info = new StringBuffer();
		info.append("执行佣金账户转入可用账户：");
		info.append(result);
		info.append("条，花费：");
		info.append(end-begin);
		info.append("ms");
		log.info(info.toString());
	}
	
	/**
	 * 定時同步提現账号
	 * 每天凌晨2分执行
	 */
	@Scheduled(cron = "0 2 0 * * ?")
	public void autoSynchronousWithdraw() {
		long begin = System.currentTimeMillis();
		Long result = withdrawApplyService.SynchronousWithdrawByTask();
		long end = System.currentTimeMillis();
		StringBuffer info = new StringBuffer();
		info.append("执行同步提现账号：");
		info.append(result);
		info.append("条，花费：");
		info.append(end-begin);
		info.append("ms");
		log.info(info.toString());
	}
	
}
