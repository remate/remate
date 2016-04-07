/**
 * 
 */
package com.vdlm.spider.queue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.vdlm.spider.core.BlockingFQueue;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 1:50:21 AM Jul 20, 2014
 */
public class TaskQueue {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private BlockingFQueue queue;

	@Value("${fqpath}")
	private String path;
	@Value("${fqsize}")
	private int capacity;

	public TaskQueue setPath(String path) {
		if (StringUtils.isNotBlank(path)) {
			this.path = path;
		}
		return this;
	}

	public TaskQueue setCapacity(int capacity) {
		if (capacity > 0) {
			this.capacity = capacity;
		}
		return this;
	}

	public TaskQueue init() {
		if (log.isInfoEnabled()) {
			log.info("start to init " + this.getClass().getSimpleName());
		}

		this.queue = new BlockingFQueue(this.path, this.capacity);

		if (log.isInfoEnabled()) {
			log.info("success to create FQueue({},{}), current index:{}", this.path, this.capacity, this.queue.size());
		}

		if (log.isInfoEnabled()) {
			log.info("success to init " + this.getClass().getSimpleName());
		}
		return this;
	}

	public void destroy() {
		if (log.isInfoEnabled()) {
			log.info("start to destroy " + this.getClass().getSimpleName());
		}

		this.queue.close();

		if (log.isInfoEnabled()) {
			log.info("success to destroy " + this.getClass().getSimpleName());
		}
	}

	public boolean add(byte[] bytes) {
		return this.queue.add(bytes);
	}

	public byte[] take() throws InterruptedException {
		return this.queue.take();
	}
	
	public byte[] poll() {
		return this.queue.poll();
	}

	public int size() {
		return this.queue.size();
	}
}
