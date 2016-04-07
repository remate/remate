package com.vdlm.spider.task.resubmit;

import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.dao.ItemTaskDao;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public class ItemTaskDefeater implements ResubmitDefeater<ItemTaskBean> {

	@Autowired
	private ItemTaskDao itemTaskDao;
	
	@Override
	public boolean canSumbit(ItemTaskBean task) {
		final boolean result = (itemTaskDao.exist(task.getOuerShopId(), task.getItemId()) == null);
		if (result) {
			Logs.RESUBMIT_DEFEATER.info("item task {} is going to be submitted", task);
		} else {
			Logs.RESUBMIT_DEFEATER.warn("item task {} has been submitted", task);
		}
		return result;
	}

}
