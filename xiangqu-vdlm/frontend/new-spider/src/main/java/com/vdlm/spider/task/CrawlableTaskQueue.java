package com.vdlm.spider.task;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.config.ApplicationConfig;
import com.vdlm.config.SpiderConfig;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.core.BlockingFQueue;
import com.vdlm.spider.task.resubmit.QueueEventType;
import com.vdlm.spider.task.resubmit.ResubmitDefeater;
import com.vdlm.spider.task.resubmit.TaskTrackEvent;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

@Import({ SpiderConfig.class, ApplicationConfig.class })
public class CrawlableTaskQueue<T extends ItemListTaskBean> implements BusSignalListener<T> {

	private final static Logger LOG = LoggerFactory.getLogger(CrawlableTaskQueue.class);

//	private BlockingFQueue queue;
	
	private BlockingFQueue queue;

	private String path;// = "/ouer/data/fqueue";

	private int capacity;// = 1024 * 1024 * 10;
//	private int capacity = BigArrayImpl.MINIMUM_DATA_PAGE_SIZE; //1024 * 10;

	private ResubmitDefeater<T> defeater;
	
	@Autowired
	private BusSignalManager bsm;

	private TaskType taskType = TaskType.DEFAULT;
	
	public CrawlableTaskQueue<T> setPath(String path) {
		if (StringUtils.isNotBlank(path)) {
			this.path = path;
		}
		return this;
	}
	
//	public CrawlableTaskQueue<T> setName(String name) {
//		if (StringUtils.isNotBlank(name)) {
//			this.name = name;
//		}
//		return this;
//	}

	public CrawlableTaskQueue<T> setCapacity(int capacity) {
		if (capacity > 0) {
			this.capacity = capacity;
		}
		return this;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public CrawlableTaskQueue<T> init() {
		if (LOG.isInfoEnabled()) {
			LOG.info("start to init " + this.getClass().getSimpleName());
		}

		this.queue = new BlockingFQueue(this.path, this.capacity);

		if (LOG.isInfoEnabled()) {
			LOG.info("success to create FQueue({},{}), current index:{}", this.path, this.capacity, this.queue.size());
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("success to init " + this.getClass().getSimpleName());
		}
		return this;
	}

	public void destroy() throws IOException {
		if (LOG.isInfoEnabled()) {
			LOG.info("start to destroy " + this.getClass().getSimpleName());
		}

		this.queue.close();

		if (LOG.isInfoEnabled()) {
			LOG.info("success to destroy " + this.getClass().getSimpleName());
		}
	}

//	public boolean add(byte[] bytes) {
//		return this.queue.add(bytes);
//	}
	
	public boolean add(T task) {
		if (defeater != null) {
			if (!defeater.canSumbit(task)) {
				return false;
			}
		}
		if (defeater != null) {
			bsm.signal(new TaskTrackEvent(QueueEventType.ENQUEUED, task, task.getClass()));
		}
		final boolean success = this.queue.add(task.toJSONBytes());
		if (!success) {
			bsm.signal(new TaskTrackEvent(QueueEventType.ENQUEUE_FAILED, task, task.getClass()));
		}
		return success;
	}

	public byte[] take() throws InterruptedException, IOException {
		return this.queue.take();
	}
	
	public byte[] poll() throws IOException {
		final byte[] data = this.queue.poll();
		if (data != null) {
			if (Logs.QUEUE.isDebugEnabled()) {
				Logs.QUEUE.debug("polled from queue {} ", path);
			}
		}
		return data;
	}

	public long size() {
		return this.queue.size();
	}

	public ResubmitDefeater<T> getDefeater() {
		return defeater;
	}

	public void setDefeater(ResubmitDefeater<T> defeater) {
		this.defeater = defeater;
	}

	@Override
	public void signalFired(T signal) {
		if (!taskType.equals(signal.getTaskType())) {
			return;
		}
		final boolean ret = add(signal);
		if(!ret) {
			Logs.statistics.warn(
					"parser_" + signal.getTaskName() + ". shopId:{}, shopurl:{}, reason:{}",
					signal.getOuerShopId(), signal.getShopUrl(),
					Logs.StatsResult.FAIL);
		} else if (Logs.QUEUE.isDebugEnabled()) {
			Logs.QUEUE.debug("added into queue {} ", path);
		}
	}
}
