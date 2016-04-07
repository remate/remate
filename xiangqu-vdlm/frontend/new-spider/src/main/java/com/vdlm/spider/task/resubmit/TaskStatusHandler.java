package com.vdlm.spider.task.resubmit;

import com.vdlm.spider.bean.ItemListTaskBean;

/**
 *
 * @author: chenxi
 */

public interface TaskStatusHandler {

	public void enqueued(TaskTrackEvent<? extends ItemListTaskBean> event);
	
	public void dequeued(TaskTrackEvent<? extends ItemListTaskBean> event);
	
	public void handled(TaskTrackEvent<? extends ItemListTaskBean> event);
	
	public void enqueueFailed(TaskTrackEvent<? extends ItemListTaskBean> event);
}
