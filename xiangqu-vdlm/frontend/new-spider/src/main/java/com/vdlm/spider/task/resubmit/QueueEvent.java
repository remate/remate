package com.vdlm.spider.task.resubmit;

/**
 *
 * @author: chenxi
 */

public class QueueEvent<T> {

	private final QueueEventType type;
	private final T taskId;
	
	public QueueEvent(QueueEventType type, T taskId) {
		this.type = type;
		this.taskId = taskId;
	}

	public QueueEventType getType() {
		return type;
	}

	public T getTaskId() {
		return taskId;
	}
	
}
