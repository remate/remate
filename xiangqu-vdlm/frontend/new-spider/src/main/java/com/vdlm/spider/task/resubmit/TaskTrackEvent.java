package com.vdlm.spider.task.resubmit;

import com.vdlm.spider.bean.ItemListTaskBean;

/**
 *
 * @author: chenxi
 */

public class TaskTrackEvent<T extends ItemListTaskBean> extends QueueEvent<String> {

	private final T task;
	private final Class<T> clazz;
	
	private Long handledId;
	
	public TaskTrackEvent(QueueEventType type, Class<T> clazz) {
		super(type, null);
		this.task = null;
		this.clazz = clazz;
	}
	
	public TaskTrackEvent(QueueEventType type, T task, Class<T> clazz) {
		super(type, task.getId());
		this.task = task;
		this.clazz = clazz;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public T getTask() {
		return task;
	}

	public Long getHandledId() {
		return handledId;
	}

	public void setHandledId(Long handledId) {
		this.handledId = handledId;
	}

}
