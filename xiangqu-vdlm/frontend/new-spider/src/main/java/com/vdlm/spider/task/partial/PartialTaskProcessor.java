package com.vdlm.spider.task.partial;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.bean.TaskBean;
import com.vdlm.spider.bean.parial.PItemDescTaskBean;
import com.vdlm.spider.bean.parial.PItemGroupImgTaskBean;
import com.vdlm.spider.bean.parial.PItemSkusTaskBean;
import com.vdlm.spider.core.NamedThreadFactory;
import com.vdlm.spider.http.HttpClientProvider;
//import com.vdlm.spider.ratelimit.RateLimitController;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;
import com.vdlm.spider.task.CrawlableTask;
import com.vdlm.spider.task.CrawlableTaskQueue;

/**
 *
 * @author: chenxi
 */

public class PartialTaskProcessor {
	
	private final static String TASK_POOLS_FILE = "p_taskpools.properties";
	
	private final static String CRAWL_ITEM_DESC_SIZE = "crawl.item.desc.size";
	private final static String CRAWL_ITEM_DESC_INITDELAY = "crawl.item.desc.initialDelay";
	private final static String CRAWL_ITEM_DESC_DELAY = "crawl.item.desc.delay";
	private final static String CRAWL_ITEM_GROUPIMG_SIZE = "crawl.item.groupimg.size";
	private final static String CRAWL_ITEM_GROUPIMG_INITDELAY = "crawl.item.groupimg.initialDelay";
	private final static String CRAWL_ITEM_GROUPIMG_DELAY = "crawl.item.groupimg.delay";
	private final static String CRAWL_ITEM_SKUS_SIZE = "crawl.item.skus.size";
	private final static String CRAWL_ITEM_SKUS_INITDELAY = "crawl.item.skus.initialDelay";
	private final static String CRAWL_ITEM_SKUS_DELAY = "crawl.item.skus.delay";
	private final static String PARSE_ITEM_DESC_SIZE = "parse.item.desc.size";
	private final static String PARSE_ITEM_GROUPIMG_SIZE = "parse.item.groupimg.size";
	private final static String PARSE_ITEM_SKUS_SIZE = "parse.item.skus.size";
	
	private final static Logger LOG = LoggerFactory.getLogger(PartialTaskProcessor.class);
	
	@Autowired
	private BusSignalManager bsm;
	@Autowired
	private HttpClientProvider provider;
	
	// crawlable tasks queue -- io cost
	@Resource
	private CrawlableTaskQueue<PItemDescTaskBean> pItemDescQueue;
	@Resource
	private CrawlableTaskQueue<PItemGroupImgTaskBean> pItemGroupImgQueue;
	@Resource
	private CrawlableTaskQueue<PItemSkusTaskBean> pItemSkusQueue;
	
	private ScheduledExecutorService crawlItemDescPool;
	private ScheduledExecutorService crawlItemGroupImgPool;
	private ScheduledExecutorService crawlItemSkusPool;
	
	// parsable tasks pool -- cpu cost
	private ExecutorService parseItemDescPool;
	private ExecutorService parseItemGroupImgPool;
	private ExecutorService parseItemSkusPool;
	

	
	private ParseItemDescTaskListener itemDescListener;
	private ParseItemItemGroupTaskListener itemItemGroupListener;
	private ParseItemSkusTaskListener itemSkusListener;
	
	@Autowired
	private StatusCodeErrorHandlerFactory scehf;
	
	@PostConstruct
	public void init() throws Exception {
		initThreadPools();
		initTaskListeners();
		initEvents();
	}
	
	private void initThreadPools() {
		final Properties properties = new Properties();
        final InputStream resource = Thread.currentThread()
                                     .getContextClassLoader()
                                     .getResourceAsStream(TASK_POOLS_FILE);
        try {
            properties.load(resource);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load " + TASK_POOLS_FILE + " under classpath", e);
        }
        
        int poolSize = Integer.valueOf(properties.getProperty(CRAWL_ITEM_DESC_SIZE));
        crawlItemDescPool = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("partial item desc crawler"));
        for (int i = 0; i < poolSize; i++) {
        	crawlItemDescPool.scheduleWithFixedDelay(new ItemDescTaskQueuePoller(), 
        			Long.valueOf(properties.getProperty(CRAWL_ITEM_DESC_INITDELAY)) + i * 200, 
            		Long.valueOf(properties.getProperty(CRAWL_ITEM_DESC_DELAY)), TimeUnit.MILLISECONDS);
        }
        poolSize = Integer.valueOf(properties.getProperty(CRAWL_ITEM_GROUPIMG_SIZE));
        crawlItemGroupImgPool = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("partial item group img crawler"));
        for (int i = 0; i < poolSize; i++) {
        	crawlItemGroupImgPool.scheduleWithFixedDelay(new ItemGroupImgTaskQueuePoller(), 
        			Long.valueOf(properties.getProperty(CRAWL_ITEM_GROUPIMG_INITDELAY)) + i * 200, 
            		Long.valueOf(properties.getProperty(CRAWL_ITEM_GROUPIMG_DELAY)), TimeUnit.MILLISECONDS);
        }
        poolSize = Integer.valueOf(properties.getProperty(CRAWL_ITEM_SKUS_SIZE));
        crawlItemSkusPool = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("partial item skus crawler"));
        for (int i = 0; i < poolSize; i++) {
        	crawlItemSkusPool.scheduleWithFixedDelay(new ItemSkusTaskQueuePoller(), 
        			Long.valueOf(properties.getProperty(CRAWL_ITEM_SKUS_INITDELAY)) + i * 200, 
            		Long.valueOf(properties.getProperty(CRAWL_ITEM_SKUS_DELAY)), TimeUnit.MILLISECONDS);
        }
        
        parseItemDescPool = Executors.newFixedThreadPool(Integer.valueOf(properties.getProperty(PARSE_ITEM_DESC_SIZE)), 
        		new NamedThreadFactory("partial item desc parser"));
        parseItemGroupImgPool = Executors.newFixedThreadPool(Integer.valueOf(properties.getProperty(PARSE_ITEM_GROUPIMG_SIZE)), 
        		new NamedThreadFactory("partial item group img parser"));
        parseItemSkusPool = Executors.newFixedThreadPool(Integer.valueOf(properties.getProperty(PARSE_ITEM_SKUS_SIZE)), 
        		new NamedThreadFactory("partial item skus parser"));
	}
	
	private void initTaskListeners() {
		itemDescListener = new ParseItemDescTaskListener();
		itemItemGroupListener = new ParseItemItemGroupTaskListener();
		itemSkusListener = new ParseItemSkusTaskListener();
	}
	
	private void initEvents() {
		bsm.bind(PItemDescTaskBean.class, pItemDescQueue);
		bsm.bind(PItemGroupImgTaskBean.class, pItemGroupImgQueue);
		bsm.bind(PItemSkusTaskBean.class, pItemSkusQueue);
		bsm.bind(ParsePDescTask.class, itemDescListener);
		bsm.bind(ParsePGroupImgTask.class, itemItemGroupListener);
		bsm.bind(ParsePSkusTask.class, itemSkusListener);
	}

	
	@PreDestroy
	public void destroy() throws IOException {
		destroyThreadPools();
		destroyEvents();
		destroyQueues();
		destroyTaskListeners();
	}
	
	private void destroyThreadPools() {
		parseItemDescPool.shutdown();
		parseItemGroupImgPool.shutdown();
		parseItemSkusPool.shutdown();
	}
	
	private void destroyEvents() {
		bsm.unbind(PItemDescTaskBean.class, pItemDescQueue);
		bsm.unbind(PItemGroupImgTaskBean.class, pItemGroupImgQueue);
		bsm.unbind(PItemSkusTaskBean.class, pItemSkusQueue);
		bsm.unbind(ParsePDescTask.class, itemDescListener);
		bsm.unbind(ParsePGroupImgTask.class, itemItemGroupListener);
		bsm.unbind(ParsePSkusTask.class, itemSkusListener);
	}
	
	private void destroyQueues() throws IOException {
		pItemDescQueue.destroy();
		pItemGroupImgQueue.destroy();
		pItemSkusQueue.destroy();
	}
	
	private void destroyTaskListeners() {
		itemDescListener = null;
		itemItemGroupListener = null;
		itemSkusListener = null;
	}
	
	abstract class TaskQueuePoller<T extends ItemTaskBean> implements Runnable {

		private final CrawlableTaskQueue<T> queue;
		
		public TaskQueuePoller(CrawlableTaskQueue<T> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {
			byte[] bytes = null;
			try {
				bytes = queue.poll();
				if (bytes == null) {
					return;
				}
				final CrawlableTask<T> task = createTask(bytes);
				task.crawl();
			}
			catch (final Exception e) {
				return;
			} finally {
			}		
		}

		protected abstract CrawlableTask<T> createTask(byte[] bytes);
	}
	
	class ItemDescTaskQueuePoller extends TaskQueuePoller<PItemDescTaskBean> {

		public ItemDescTaskQueuePoller() {
			super(pItemDescQueue);
		}

		@Override
		protected CrawlPDescTask createTask(byte[] bytes) {
			return new CrawlPDescTask(bsm, TaskBean.parse(bytes, PItemDescTaskBean.class), provider, scehf);
		}
		
	}
	
	class ItemGroupImgTaskQueuePoller extends TaskQueuePoller<PItemGroupImgTaskBean> {

		public ItemGroupImgTaskQueuePoller() {
			super(pItemGroupImgQueue);
		}

		@Override
		protected CrawlPGroupImgTask createTask(byte[] bytes) {
			return new CrawlPGroupImgTask(bsm, TaskBean.parse(bytes, PItemGroupImgTaskBean.class), provider, scehf);
		}
		
	}
	
	class ItemSkusTaskQueuePoller extends TaskQueuePoller<PItemSkusTaskBean> {

		public ItemSkusTaskQueuePoller() {
			super(pItemSkusQueue);
		}

		@Override
		protected CrawlPSkusTask createTask(byte[] bytes) {
			return new CrawlPSkusTask(bsm, TaskBean.parse(bytes, PItemSkusTaskBean.class), provider, scehf);
		}
		
	}
	
	class ParseItemDescTaskListener implements BusSignalListener<ParsePDescTask> {

		@Override
		public void signalFired(ParsePDescTask signal) {
			parseItemDescPool.submit(signal);
		}
		
	}
	
	class ParseItemItemGroupTaskListener implements BusSignalListener<ParsePGroupImgTask> {

		@Override
		public void signalFired(ParsePGroupImgTask signal) {
			parseItemGroupImgPool.submit(signal);
		}
		
	} 
	
	class ParseItemSkusTaskListener implements BusSignalListener<ParsePSkusTask> {

		@Override
		public void signalFired(ParsePSkusTask signal) {
			parseItemSkusPool.submit(signal);
		}
		
	}

}
