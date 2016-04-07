package com.vdlm.spider.task.resubmit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vdlm.spider.TaskStatus;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.dao.ItemListTaskDao;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

@Component("itemListTaskStatusHandler")
public class ItemListTaskStatusHandler implements TaskStatusHandler {

	@Autowired
	private ItemListTaskDao itemListTaskDao;

	@Override
	public void enqueued(TaskTrackEvent<? extends ItemListTaskBean> event) {
		if (itemListTaskDao.exist(event.getTaskId()) == null) {
			itemListTaskDao.insert(event.getTask());
			Logs.RESUBMIT_DEFEATER.info("inserted itemlist task and enqueued, id: {}, task: {} ", 
					event.getTaskId(), event.getTask());
		} else {
			itemListTaskDao.updateStatus(event.getTaskId(), TaskStatus.ENQUEUED.ordinal());
			Logs.RESUBMIT_DEFEATER.info("itemlist task already exsit and enqueued, id: {}, task: {}  ", 
					event.getTaskId(), event.getTask());
		}
	}

	@Override
	public void dequeued(TaskTrackEvent<? extends ItemListTaskBean> event) {
		itemListTaskDao.updateStatus(event.getTaskId(), TaskStatus.DEQUEUED.ordinal());
		Logs.RESUBMIT_DEFEATER.info("dequeued itemlist task, id: {}, task: {} ", 
				event.getTaskId(), event.getTask());
	}

	@Override
	public void enqueueFailed(TaskTrackEvent<? extends ItemListTaskBean> event) {
		itemListTaskDao.deleteOne(event.getTaskId());
		Logs.RESUBMIT_DEFEATER.info("failed to enqueue itemlist task, id: {}, task: {} ", 
				event.getTaskId(), event.getTask());
	}

	@Override
	public void handled(TaskTrackEvent<? extends ItemListTaskBean> event) {
		final long shopId = event.getHandledId();
		itemListTaskDao.deleteOne(shopId);
		Logs.RESUBMIT_DEFEATER.info("removed itemlist task, handled id: {}", shopId);
	}
}
