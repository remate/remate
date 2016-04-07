package com.vdlm.spider.task.resubmit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vdlm.spider.TaskStatus;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.dao.ItemTaskDao;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

@Component("itemTaskStatusHandler")
public class ItemTaskStatusHandler implements TaskStatusHandler {

	@Autowired
	private ItemTaskDao itemTaskDao;

	@Override
	public void enqueued(TaskTrackEvent<? extends ItemListTaskBean> event) {
		itemTaskDao.insert((ItemTaskBean) event.getTask());
		Logs.RESUBMIT_DEFEATER.info("inserted item task and enqueued, id: {}, task: {} ", event.getTaskId(), event.getTask());
	}

	@Override
	public void dequeued(TaskTrackEvent<? extends ItemListTaskBean> event) {
		itemTaskDao.updateStatus(event.getTaskId(), TaskStatus.DEQUEUED.ordinal());
		Logs.RESUBMIT_DEFEATER.info("dequeued item task, id: {}, task: {} ", event.getTaskId(), event.getTask());
	}

	@Override
	public void enqueueFailed(TaskTrackEvent<? extends ItemListTaskBean> event) {
		itemTaskDao.deleteOne(event.getTask().getOuerShopId(), event.getTaskId());
		Logs.RESUBMIT_DEFEATER.info("failed to enqueue item task, id: {}, task: {} ", 
				event.getTaskId(), event.getTask());
	}

	@Override
	public void handled(TaskTrackEvent<? extends ItemListTaskBean> event) {
		final long itemId = event.getHandledId();
		itemTaskDao.deleteOne(itemId);
		Logs.RESUBMIT_DEFEATER.info("removed item task, handled id: {}", itemId);
	}
}
