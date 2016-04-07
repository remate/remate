package com.vdlm.spider.task.resubmit;

import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.spider.bean.ShopTaskBean;
import com.vdlm.spider.dao.ShopTaskDao;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public class ShopTaskDefeater implements ResubmitDefeater<ShopTaskBean> {

	@Autowired
	private ShopTaskDao shopTaskDao;
	
	@Override
	public boolean canSumbit(ShopTaskBean task) {
		final boolean result = (shopTaskDao.exist(task.getOuerShopId()) == null);
		if (result) {
			Logs.RESUBMIT_DEFEATER.info("shop task {} is going to be submitted", task);
		} else {
			Logs.RESUBMIT_DEFEATER.warn("shop task {} has been submitted", task);
		}
		return result;
	}

}
