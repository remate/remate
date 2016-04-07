package com.vdlm.umpay;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vdlm.dal.type.PaymentMode;
import com.vdlm.service.outpay.ThirdPartyPayment;
import com.vdlm.service.payBank.PayBankService;

@Component
public class UmpayJob {
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ThirdPartyPayment umPayment;
	
	@Autowired
	private PayBankService payBankService;
	
	/**
	 * 定时确认U付支持的银行列表
	 * 每天凌晨4点5分，发起任务
	 */
	@Scheduled(cron = "0 5 4 * * ?")
	public void autoConfirmUmpayBank() {

		long begin = System.currentTimeMillis();
		List<String> result = umPayment.loadBankList();
		if(result==null||result.size()==0){
			log.info("返回U付支持的银行列表异常");
			return;
		}
		payBankService.updateBankStatusFalse(result, PaymentMode.UMPAY);
		List<String> noSupportBanks = payBankService.noSupportBank(result, PaymentMode.UMPAY);
		long end = System.currentTimeMillis();
		StringBuffer info = new StringBuffer();
		info.append("自动更新umpay支持的银行列表：");
		info.append(result);
		info.append("条，花费：");
		info.append(end-begin);
		info.append("ms");
		if(noSupportBanks!=null && noSupportBanks.size()>0){
			info.append("，当前系统未支持的银行有：");
			info.append(noSupportBanks.toString());
		}
		log.info(info.toString());
	}
}
