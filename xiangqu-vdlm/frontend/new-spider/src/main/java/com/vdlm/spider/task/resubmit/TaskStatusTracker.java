package com.vdlm.spider.task.resubmit;

import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ItemListTaskBean;

/**
 *
 * @author: chenxi
 */

public class TaskStatusTracker implements BusSignalListener<TaskTrackEvent<? extends ItemListTaskBean>> {

	private final TaskStatusHandlerFactory factory;
	
	public TaskStatusTracker(BusSignalManager bsm, TaskStatusHandlerFactory factory) {
		bsm.bind(TaskTrackEvent.class, this);
		this.factory = factory;
	}
	
	@Override
	public void signalFired(TaskTrackEvent<? extends ItemListTaskBean> signal) {
		final QueueEventType type = signal.getType();
		if (QueueEventType.ENQUEUED.equals(type)) {
			final TaskStatusHandler handler = factory.getHandler(signal.getClazz());
			if (handler != null) {
				handler.enqueued(signal);
			}
			return;
		}
		if (QueueEventType.DEQUEUED.equals(type)) {
			final TaskStatusHandler handler = factory.getHandler(signal.getClazz());
			if (handler != null) {
				handler.dequeued(signal);
			}
			return;
		}
		if (QueueEventType.ENQUEUE_FAILED.equals(type)) {
			final TaskStatusHandler handler = factory.getHandler(signal.getClazz());
			if (handler != null) {
				handler.enqueueFailed(signal);
			}
			return;
		}
		if (QueueEventType.HANDLED.equals(type)) {
			final TaskStatusHandler handler = factory.getHandler(signal.getClazz());
			if (handler != null) {
				handler.handled(signal);
			}
			return;
		}
	}

}
