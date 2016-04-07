package com.vdlm.spider.task.resubmit;

import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.spider.bean.WItemTaskBean;
import com.vdlm.spider.dao.ItemTaskDao;

/**
 *
 * @author: chenxi
 */

public class WItemTaskDefeater implements ResubmitDefeater<WItemTaskBean> {

	@Autowired
	private ItemTaskDao itemTaskDao;
	
	@Override
	public boolean canSumbit(WItemTaskBean task) {
		return itemTaskDao.exist(task.getOuerShopId(), task.getItemId()) == null;
	}

}
