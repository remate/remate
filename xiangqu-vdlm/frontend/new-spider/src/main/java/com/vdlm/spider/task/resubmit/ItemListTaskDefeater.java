package com.vdlm.spider.task.resubmit;

import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.dao.ItemListTaskDao;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public class ItemListTaskDefeater implements ResubmitDefeater<ItemListTaskBean> {

	@Autowired
	private ItemListTaskDao itemListTaskDao;
	
	@Override
	public boolean canSumbit(ItemListTaskBean task) {
		final boolean result = (itemListTaskDao.existNoPartially(task.getOuerShopId()) == null);
		if (result) {
			Logs.RESUBMIT_DEFEATER.info("itemlist task {} is going to be submitted", task);
		} else {
			Logs.RESUBMIT_DEFEATER.warn("itemlist task {} has been submitted", task);
		}
		return result;
	}

}
