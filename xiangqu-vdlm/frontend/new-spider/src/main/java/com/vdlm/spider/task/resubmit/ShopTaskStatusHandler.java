package com.vdlm.spider.task.resubmit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vdlm.spider.TaskStatus;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.ShopTaskBean;
import com.vdlm.spider.dao.ShopTaskDao;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

@Component("shopTaskStatusHandler")
public class ShopTaskStatusHandler implements TaskStatusHandler {

	@Autowired
	private ShopTaskDao shopTaskDao;
	
	@Override
	public void enqueued(TaskTrackEvent<? extends ItemListTaskBean> event) {
		shopTaskDao.insert((ShopTaskBean) event.getTask());
		Logs.RESUBMIT_DEFEATER.info("inserted shop task and enqueued, id: {}, task: {} ", event.getTaskId(), event.getTask());
	}

	@Override
	public void dequeued(TaskTrackEvent<? extends ItemListTaskBean> event) {
		shopTaskDao.updateStatus(event.getTaskId(), TaskStatus.DEQUEUED.ordinal());
		Logs.RESUBMIT_DEFEATER.info("dequeued shop task, id: {}, task: {} ", event.getTaskId(), event.getTask());
	}

	@Override
	public void enqueueFailed(TaskTrackEvent<? extends ItemListTaskBean> event) {
		shopTaskDao.deleteOne(event.getTask().getOuerShopId()); // ouer_shop_id  - -.
		Logs.RESUBMIT_DEFEATER.info("failed to enqueue shop task, id: {}, task: {} ", event.getTaskId(), event.getTask());
	}

	@Override
	public void handled(TaskTrackEvent<? extends ItemListTaskBean> event) {
		final long shopId = event.getHandledId();
		shopTaskDao.deleteOne(shopId);
		Logs.RESUBMIT_DEFEATER.info("removed shop task, handled id: {}", shopId);
	}

}
