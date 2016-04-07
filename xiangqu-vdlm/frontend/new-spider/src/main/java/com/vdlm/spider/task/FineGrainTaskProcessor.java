package com.vdlm.spider.task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.DescTaskBean;
import com.vdlm.spider.bean.ImgTaskBean;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.bean.ShopTaskBean;
import com.vdlm.spider.bean.SkuTaskBean;
import com.vdlm.spider.bean.TaskBean;
import com.vdlm.spider.bean.WDescTaskBean;
import com.vdlm.spider.bean.WItemTaskBean;
import com.vdlm.spider.core.NamedThreadFactory;
import com.vdlm.spider.http.HttpClientProvider;
//import com.vdlm.spider.ratelimit.RateLimitController;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;
import com.vdlm.spider.task.resubmit.QueueEventType;
import com.vdlm.spider.task.resubmit.TaskTrackEvent;

/**
 *
 * @author: chenxi
 */

public class FineGrainTaskProcessor {
	
//	private final static String TASK_POOLS_FILE = "taskpools.properties";
	
	private final static String CRAWL_SHOP_SIZE = "crawl.shop.size";
	private final static String CRAWL_SHOP_INITDELAY = "crawl.shop.initialDelay";
	private final static String CRAWL_SHOP_DELAY = "crawl.shop.delay";
	private final static String CRAWL_ITEMLIST_SIZE = "crawl.itemlist.size";
	private final static String CRAWL_ITEMLIST_INITDELAY = "crawl.itemlist.initialDelay";
	private final static String CRAWL_ITEMLIST_DELAY = "crawl.itemlist.delay";
	private final static String CRAWL_ITEM_SIZE = "crawl.item.size";
	private final static String CRAWL_ITEM_INITDELAY = "crawl.item.initialDelay";
	private final static String CRAWL_ITEM_DELAY = "crawl.item.delay";
	private final static String CRAWL_SKU_SIZE = "crawl.sku.size";
	private final static String CRAWL_SKU_INITDELAY = "crawl.sku.initialDelay";
	private final static String CRAWL_SKU_DELAY = "crawl.sku.delay";
	private final static String CRAWL_DESC_SIZE = "crawl.desc.size";
	private final static String CRAWL_DESC_INITDELAY = "crawl.desc.initialDelay";
	private final static String CRAWL_DESC_DELAY = "crawl.desc.delay";
	private final static String CRAWL_IMG_SIZE = "crawl.img.size";
	private final static String CRAWL_IMG_INITDELAY = "crawl.img.initialDelay";
	private final static String CRAWL_IMG_DELAY = "crawl.img.delay";
	private final static String PARSE_SHOP_SIZE = "parse.shop.size";
	private final static String PARSE_ITEMLIST_SIZE = "parse.itemlist.size";
	private final static String PARSE_ITEM_SIZE = "parse.item.size";
	private final static String PARSE_SKU_SIZE = "parse.sku.size";
	private final static String PARSE_DESC_SIZE = "parse.desc.size";
	private final static String PARSE_IMG_SIZE = "parse.img.size";
	// w 
	private final static String CRAWL_WITEM_SIZE = "crawl.witem.size";
	private final static String CRAWL_WITEM_INITDELAY = "crawl.witem.initialDelay";
	private final static String CRAWL_WITEM_DELAY = "crawl.witem.delay";
	private final static String PARSE_WITEM_SIZE = "parse.witem.size";
	private final static String CRAWL_WDESC_SIZE = "crawl.wdesc.size";
	private final static String CRAWL_WDESC_INITDELAY = "crawl.wdesc.initialDelay";
	private final static String CRAWL_WDESC_DELAY = "crawl.wdesc.delay";
	private final static String PARSE_WDESC_SIZE = "parse.wdesc.size";
	
	private final static Logger LOG = LoggerFactory.getLogger(FineGrainTaskProcessor.class);
	
	@Autowired
	private ItemTaskStrategy strategy;
	@Autowired
	private BusSignalManager bsm;
	@Autowired
	private HttpClientProvider provider;
	
	// crawlable tasks queue -- io cost
//	@Resource
	private CrawlableTaskQueue<ShopTaskBean> shopQueue;
//	@Resource
	private CrawlableTaskQueue<ItemListTaskBean> itemListQueue;
//	@Resource
	private CrawlableTaskQueue<ItemTaskBean> itemQueue;
//	@Resource
	private CrawlableTaskQueue<SkuTaskBean> skuQueue;
//	@Resource
	private CrawlableTaskQueue<DescTaskBean> descQueue;
//	@Resource
	private CrawlableTaskQueue<ImgTaskBean> imgQueue;
	// w 
//	@Resource
	private CrawlableTaskQueue<WItemTaskBean> wItemQueue;
//	@Resource
	private CrawlableTaskQueue<WDescTaskBean> wDescQueue;
	
	private ScheduledExecutorService crawlShopPool;
	private ScheduledExecutorService crawlItemListPool;
	private ScheduledExecutorService crawlItemPool;
	private ScheduledExecutorService crawlSkuPool;
	private ScheduledExecutorService crawlDescPool;
	private ScheduledExecutorService crawlImgPool;
	// w
	private ScheduledExecutorService crawlWItemPool;
	private ScheduledExecutorService crawlWDescPool;
	
	// parsable tasks pool -- cpu cost
	private ExecutorService parseShopPool;
	private ExecutorService parseItemListPool;
	private ExecutorService parseItemPool;
	private ExecutorService parseSkuPool;
	private ExecutorService parseDescPool;
	private ExecutorService parseImgPool;
	// w
	private ExecutorService parseWItemPool;
	private ExecutorService parseWDescPool;
	
	private ParseShopTaskListener shopListener;
	private ParseItemListTaskListener itemListListener;
	private ParseItemTaskListener itemListener;
	private ParseSkuTaskListener skuListener;
	private ParseDescTaskListener descListener;
	private ParseImgTaskListener imgListener;
	// w
	private ParseWItemTaskListener wItemListener;
	private ParseWDescTaskListener wDescListener;
	
	@Autowired
	private StatusCodeErrorHandlerFactory scehf;
	
	@Autowired
	private CrawlableTaskRetryManager retryManager;
	
	private String taskPoolConfig;
	
	private TaskType taskType;

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
                                     .getResourceAsStream(this.taskPoolConfig);
        try {
            properties.load(resource);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load " + this.taskPoolConfig + " under classpath", e);
        }
        
        int poolSize = Integer.valueOf(properties.getProperty(CRAWL_SHOP_SIZE));
        crawlShopPool = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("shop crawler"));
        for (int i = 0; i < poolSize; i++) {
        	crawlShopPool.scheduleWithFixedDelay(new ShopTaskQueuePoller(), 
        			Long.valueOf(properties.getProperty(CRAWL_SHOP_INITDELAY)) + i * 200, 
            		Long.valueOf(properties.getProperty(CRAWL_SHOP_DELAY)), TimeUnit.MILLISECONDS);
        }
        poolSize = Integer.valueOf(properties.getProperty(CRAWL_ITEMLIST_SIZE));
        crawlItemListPool = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("itemlist crawler"));
        for (int i = 0; i < poolSize; i++) {
        	crawlItemListPool.scheduleWithFixedDelay(new ItemListTaskQueuePoller(), 
        			Long.valueOf(properties.getProperty(CRAWL_ITEMLIST_INITDELAY)) + i * 200, 
            		Long.valueOf(properties.getProperty(CRAWL_ITEMLIST_DELAY)), TimeUnit.MILLISECONDS);
        }
        poolSize = Integer.valueOf(properties.getProperty(CRAWL_ITEM_SIZE));
        crawlItemPool = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("item crawler"));
        for (int i = 0; i < poolSize; i++) {
        	crawlItemPool.scheduleWithFixedDelay(new ItemTaskQueuePoller(), 
        			Long.valueOf(properties.getProperty(CRAWL_ITEM_INITDELAY)) + i * 200, 
            		Long.valueOf(properties.getProperty(CRAWL_ITEM_DELAY)), TimeUnit.MILLISECONDS);
        }
        poolSize = Integer.valueOf(properties.getProperty(CRAWL_SKU_SIZE));
        crawlSkuPool = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("sku crawler"));
        for (int i = 0; i < poolSize; i++) {
        	crawlSkuPool.scheduleWithFixedDelay(new SkuTaskQueuePoller(), 
        			Long.valueOf(properties.getProperty(CRAWL_SKU_INITDELAY)) + i * 200, 
            		Long.valueOf(properties.getProperty(CRAWL_SKU_DELAY)), TimeUnit.MILLISECONDS);
        }
        poolSize = Integer.valueOf(properties.getProperty(CRAWL_DESC_SIZE));
        crawlDescPool = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("desc crawler"));
        for (int i = 0; i < poolSize; i++) {
        	crawlDescPool.scheduleWithFixedDelay(new DescTaskQueuePoller(), 
        			Long.valueOf(properties.getProperty(CRAWL_DESC_INITDELAY)) + i * 200, 
            		Long.valueOf(properties.getProperty(CRAWL_DESC_DELAY)), TimeUnit.MILLISECONDS);
        }
        poolSize = Integer.valueOf(properties.getProperty(CRAWL_IMG_SIZE));
        crawlImgPool = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("img crawler"));
        for (int i = 0; i < poolSize; i++) {
        	crawlImgPool.scheduleWithFixedDelay(new ImgTaskQueuePoller(), 
        			Long.valueOf(properties.getProperty(CRAWL_IMG_INITDELAY)) + i * 200, 
            		Long.valueOf(properties.getProperty(CRAWL_IMG_DELAY)), TimeUnit.MILLISECONDS);
        }
        
        parseShopPool = Executors.newFixedThreadPool(Integer.valueOf(properties.getProperty(PARSE_SHOP_SIZE)), 
        		new NamedThreadFactory("shop parser"));
        parseItemListPool = Executors.newFixedThreadPool(Integer.valueOf(properties.getProperty(PARSE_ITEMLIST_SIZE)), 
        		new NamedThreadFactory("item list parser"));
		parseItemPool = Executors.newFixedThreadPool(Integer.valueOf(properties.getProperty(PARSE_ITEM_SIZE)), 
				new NamedThreadFactory("item parser"));
		parseSkuPool = Executors.newFixedThreadPool(Integer.valueOf(properties.getProperty(PARSE_SKU_SIZE)),
				new NamedThreadFactory("sku parser"));
		parseDescPool = Executors.newFixedThreadPool(Integer.valueOf(properties.getProperty(PARSE_DESC_SIZE)),
				new NamedThreadFactory("desc parser"));
		parseImgPool = Executors.newFixedThreadPool(Integer.valueOf(properties.getProperty(PARSE_IMG_SIZE)), 
				new NamedThreadFactory("img parser"));
		
        // w
		poolSize = Integer.valueOf(properties.getProperty(CRAWL_WITEM_SIZE));
        crawlWItemPool = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("witem crawler"));
        for (int i = 0; i < poolSize; i++) {
        	crawlWItemPool.scheduleWithFixedDelay(new WItemTaskQueuePoller(), 
        			Long.valueOf(properties.getProperty(CRAWL_WITEM_INITDELAY)) + i * 200, 
            		Long.valueOf(properties.getProperty(CRAWL_WITEM_DELAY)), TimeUnit.MILLISECONDS);
        }
        poolSize = Integer.valueOf(properties.getProperty(CRAWL_WDESC_SIZE));
        crawlWDescPool = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory("wdesc crawler"));
        for (int i = 0; i < poolSize; i++) {
        	crawlWDescPool.scheduleWithFixedDelay(new WDescTaskQueuePoller(), 
        			Long.valueOf(properties.getProperty(CRAWL_WDESC_INITDELAY)) + i * 200, 
            		Long.valueOf(properties.getProperty(CRAWL_WDESC_DELAY)), TimeUnit.MILLISECONDS);
        }

        parseWItemPool = Executors.newFixedThreadPool(Integer.valueOf(properties.getProperty(PARSE_WITEM_SIZE)), 
				new NamedThreadFactory("witem parser"));
        parseWDescPool = Executors.newFixedThreadPool(Integer.valueOf(properties.getProperty(PARSE_WDESC_SIZE)), 
				new NamedThreadFactory("wdesc parser"));
	}
	
	private void initTaskListeners() {
		shopListener = new ParseShopTaskListener();
		itemListListener = new ParseItemListTaskListener();
		itemListener = new ParseItemTaskListener();
		skuListener = new ParseSkuTaskListener();
		descListener = new ParseDescTaskListener();
		imgListener = new ParseImgTaskListener();
		// w
		wItemListener = new ParseWItemTaskListener();
		wDescListener = new ParseWDescTaskListener();
	}
	
	private void initEvents() {
		bsm.bind(ShopTaskBean.class, shopQueue);
		bsm.bind(ItemListTaskBean.class, itemListQueue);
		bsm.bind(ItemTaskBean.class, itemQueue);
		bsm.bind(SkuTaskBean.class, skuQueue);
		bsm.bind(DescTaskBean.class, descQueue);
		bsm.bind(ImgTaskBean.class, imgQueue);
		bsm.bind(ParseShopTask.class, shopListener);
		bsm.bind(ParseItemListTask.class, itemListListener);
		bsm.bind(ParseItemTask.class, itemListener);
		bsm.bind(ParseSkuTask.class, skuListener);
		bsm.bind(ParseDescTask.class, descListener);
		bsm.bind(ParseImgTask.class, imgListener);
		// w
		bsm.bind(WItemTaskBean.class, wItemQueue);
		bsm.bind(ParseWItemTask.class, wItemListener);
		bsm.bind(WDescTaskBean.class, wDescQueue);
		bsm.bind(ParseWDescTask.class, wDescListener);
	}
	
	@PreDestroy
	public void destroy() throws IOException {
		destroyThreadPools();
		destroyEvents();
		destroyQueues();
		destroyTaskListeners();
	}
	
	private void destroyThreadPools() {
		parseShopPool.shutdown();
		parseItemListPool.shutdown();
		parseItemPool.shutdown();
		parseSkuPool.shutdown();
		parseDescPool.shutdown();
		parseImgPool.shutdown();
		// w
		parseWItemPool.shutdown();
		parseWDescPool.shutdown();
	}
	
	private void destroyEvents() {
		bsm.unbind(ShopTaskBean.class, shopQueue);
		bsm.unbind(ItemListTaskBean.class, itemListQueue);
		bsm.unbind(ItemTaskBean.class, itemQueue);
		bsm.unbind(SkuTaskBean.class, skuQueue);
		bsm.unbind(DescTaskBean.class, descQueue);
		bsm.unbind(ImgTaskBean.class, imgQueue);
		bsm.unbind(ParseShopTask.class, shopListener);
		bsm.unbind(ParseItemTask.class, itemListener);
		bsm.unbind(ParseSkuTask.class, skuListener);
		bsm.unbind(ParseDescTask.class, descListener);
		bsm.unbind(ParseImgTask.class, imgListener);
		// w
		bsm.unbind(WItemTaskBean.class, wItemQueue);
		bsm.unbind(ParseWItemTask.class, wItemListener);
		bsm.unbind(WDescTaskBean.class, wDescQueue);
		bsm.unbind(ParseWDescTask.class, wDescListener);
	}
	
	private void destroyQueues() throws IOException {
		shopQueue.destroy();
		itemListQueue.destroy();
		itemQueue.destroy();
		skuQueue.destroy();
		descQueue.destroy();
		imgQueue.destroy();
		// w
		wItemQueue.destroy();
		wDescQueue.destroy();
	}
	
	private void destroyTaskListeners() {
		shopListener = null;
		itemListListener = null;
		itemListener = null;
		skuListener = null;
		descListener = null;
		imgListener = null;
		// w 
		wItemListener = null;
		wDescListener = null;
	}
	
	abstract class TaskQueuePoller<T extends ItemListTaskBean> implements Runnable {

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
	
	class ShopTaskQueuePoller extends TaskQueuePoller<ShopTaskBean> {

		public ShopTaskQueuePoller() {
			super(shopQueue);
		}

		@Override
		protected CrawlShopTask createTask(byte[] bytes) {
			final CrawlShopTask task = new CrawlShopTask(bsm, TaskBean.parse(bytes, ShopTaskBean.class), 
					provider, scehf, retryManager.getMaxRetryTime(CrawlShopTask.class.getName()));
			// send task dequeue event
			bsm.signal(new TaskTrackEvent(QueueEventType.DEQUEUED, task.bean, task.bean.getClass()));
			return task;
		}
		
	}
	
	class ItemListTaskQueuePoller extends TaskQueuePoller<ItemListTaskBean> {

		public ItemListTaskQueuePoller() {
			super(itemListQueue);
		}

		@Override
		protected CrawlItemListTask createTask(byte[] bytes) {
			final CrawlItemListTask task = new CrawlItemListTask(bsm, TaskBean.parse(bytes, ItemListTaskBean.class), 
					provider, scehf, retryManager.getMaxRetryTime(CrawlItemListTask.class.getName()), strategy);
			// send task dequeue event
			bsm.signal(new TaskTrackEvent(QueueEventType.DEQUEUED, task.bean, task.bean.getClass()));
			return task;
		}
		
	}
	
	class ItemTaskQueuePoller extends TaskQueuePoller<ItemTaskBean> {

		public ItemTaskQueuePoller() {
			super(itemQueue);
		}

		@Override
		protected CrawlItemTask createTask(byte[] bytes) {
			final CrawlItemTask task = new CrawlItemTask(bsm, TaskBean.parse(bytes, ItemTaskBean.class), 
					provider, scehf, retryManager.getMaxRetryTime(CrawlItemTask.class.getName()));
			// send task dequeue event
			bsm.signal(new TaskTrackEvent(QueueEventType.DEQUEUED, task.bean, task.bean.getClass()));
			return task;
		}
		
	}
	
	class SkuTaskQueuePoller extends TaskQueuePoller<SkuTaskBean> {

		public SkuTaskQueuePoller() {
			super(skuQueue);
		}

		@Override
		protected CrawlSkuTask createTask(byte[] bytes) {
			return new CrawlSkuTask(bsm, TaskBean.parse(bytes, SkuTaskBean.class), 
					provider, scehf, retryManager.getMaxRetryTime(CrawlSkuTask.class.getName()));
		}
		
	}
	
	class DescTaskQueuePoller extends TaskQueuePoller<DescTaskBean> {

		public DescTaskQueuePoller() {
			super(descQueue);
		}

		@Override
		protected CrawlDescTask createTask(byte[] bytes) {
			return new CrawlDescTask(bsm, TaskBean.parse(bytes, DescTaskBean.class), 
					provider, scehf, retryManager.getMaxRetryTime(CrawlDescTask.class.getName()));
		}
		
	}
	
	class ImgTaskQueuePoller extends TaskQueuePoller<ImgTaskBean> {

		public ImgTaskQueuePoller() {
			super(imgQueue);
		}

		@Override
		protected CrawlImgTask createTask(byte[] bytes) {
			return new CrawlImgTask(bsm, TaskBean.parse(bytes, ImgTaskBean.class), 
					provider, scehf, retryManager.getMaxRetryTime(CrawlImgTask.class.getName()));
		}
		
	}
	// w
	class WItemTaskQueuePoller extends TaskQueuePoller<WItemTaskBean> {

		public WItemTaskQueuePoller() {
			super(wItemQueue);
		}

		@Override
		protected CrawlWItemTask createTask(byte[] bytes) {
			final CrawlWItemTask task = new CrawlWItemTask(bsm, TaskBean.parse(bytes, WItemTaskBean.class), 
					provider, scehf, retryManager.getMaxRetryTime(CrawlWItemTask.class.getName()));
			// send task dequeue event
			bsm.signal(new TaskTrackEvent(QueueEventType.DEQUEUED, task.bean, task.bean.getClass()));
			return task;
		}
		
	}
	
	class WDescTaskQueuePoller extends TaskQueuePoller<WDescTaskBean> {

		public WDescTaskQueuePoller() {
			super(wDescQueue);
		}

		@Override
		protected CrawlableTask<WDescTaskBean> createTask(byte[] bytes) {
			return new CrawlWDescTask(bsm, TaskBean.parse(bytes, WDescTaskBean.class), 
					provider, scehf, retryManager.getMaxRetryTime(CrawlWDescTask.class.getName()));
		}
		
	}
	
	class ParseShopTaskListener implements BusSignalListener<ParseShopTask> {

		@Override
		public void signalFired(ParseShopTask signal) {
			if (!taskType.equals(signal.getBean().getTaskType())) {
				return;
			}
			parseShopPool.submit(signal);
		}
		
	}
	
	class ParseItemListTaskListener implements BusSignalListener<ParseItemListTask> {

		@Override
		public void signalFired(ParseItemListTask signal) {
			if (!taskType.equals(signal.getBean().getTaskType())) {
				return;
			}
			parseItemListPool.submit(signal);
		}
		
	}
	
	class ParseItemTaskListener implements BusSignalListener<ParseItemTask> {

		@Override
		public void signalFired(ParseItemTask signal) {
			if (!taskType.equals(signal.getBean().getTaskType())) {
				return;
			}
			parseItemPool.submit(signal);
		}
		
	}
	
	class ParseSkuTaskListener implements BusSignalListener<ParseSkuTask> {

		@Override
		public void signalFired(ParseSkuTask signal) {
			if (!taskType.equals(signal.getBean().getTaskType())) {
				return;
			}
			parseSkuPool.submit(signal);
		}
		
	}
	
	class ParseDescTaskListener implements BusSignalListener<ParseDescTask> {

		@Override
		public void signalFired(ParseDescTask signal) {
			if (!taskType.equals(signal.getBean().getTaskType())) {
				return;
			}
			parseDescPool.submit(signal);
		}
		
	}
	
	class ParseImgTaskListener implements BusSignalListener<ParseImgTask> {

		@Override
		public void signalFired(ParseImgTask signal) {
			if (!taskType.equals(signal.getBean().getTaskType())) {
				return;
			}
			parseImgPool.submit(signal);
		}
		
	}
	
	// w
	class ParseWItemTaskListener implements BusSignalListener<ParseWItemTask> {

		@Override
		public void signalFired(ParseWItemTask signal) {
			if (!taskType.equals(signal.getBean().getTaskType())) {
				return;
			}
			parseWItemPool.submit(signal);
		}
		
	}
	
	class ParseWDescTaskListener implements BusSignalListener<ParseWDescTask> {

		@Override
		public void signalFired(ParseWDescTask signal) {
			if (!taskType.equals(signal.getBean().getTaskType())) {
				return;
			}
			parseWDescPool.submit(signal);
		}
		
	}
	
	// jmx
	public long shopQueueSize() {
		return shopQueue.size();
	}

	public long itemListQueueSize() {
		return itemListQueue.size();
	}
	
	public long itemQueueSize() {
		return itemQueue.size();
	}
	
	public long skuQueueSize() {
		return skuQueue.size();
	}
	
	public long descQueueSize() {
		return descQueue.size();
	}
	
	public long imgQueueSize() {
		return imgQueue.size();
	}
	// w
	public long wItemQueueSize() {
		return wItemQueue.size();
	}
	
	public long wDescQueueSize() {
		return wDescQueue.size();
	}


	public int shopParseActiveCount() {
		return ((ThreadPoolExecutor) parseShopPool).getActiveCount();
	}

	public int itemListParseActiveCount() {
		return ((ThreadPoolExecutor) parseItemListPool).getActiveCount();
	}
	
	public int itemParseActiveCount() {
		return ((ThreadPoolExecutor) parseItemPool).getActiveCount();
	}
	
	public int skuParseActiveCount() {
		return ((ThreadPoolExecutor) parseSkuPool).getActiveCount();
	}
	
	public int descParseActiveCount() {
		return ((ThreadPoolExecutor) parseDescPool).getActiveCount();
	}
	
	public int imgParseActiveCount() {
		return ((ThreadPoolExecutor) parseImgPool).getActiveCount();
	}
	// w
	public int wItemParseActiveCount() {
		return ((ThreadPoolExecutor) parseWItemPool).getActiveCount();
	}
	
	public int wDescParseActiveCount() {
		return ((ThreadPoolExecutor) parseWDescPool).getActiveCount();
	}
	
	
	// set
	public void setCrawlShopPoolSize(int coreSize) {
		((ThreadPoolExecutor) crawlShopPool).setCorePoolSize(coreSize);
		((ThreadPoolExecutor) crawlShopPool).setMaximumPoolSize(coreSize);
	}
	
	public void setCrawlItemListPoolSize(int coreSize) {
		((ThreadPoolExecutor) crawlItemListPool).setCorePoolSize(coreSize);
		((ThreadPoolExecutor) crawlItemListPool).setMaximumPoolSize(coreSize);
	}
	
	public void setCrawlItemPoolSize(int coreSize) {
		((ThreadPoolExecutor) crawlItemPool).setCorePoolSize(coreSize);
		((ThreadPoolExecutor) crawlItemPool).setMaximumPoolSize(coreSize);
	}
	
	public void setCrawlSkuPoolSize(int coreSize) {
		((ThreadPoolExecutor) crawlSkuPool).setCorePoolSize(coreSize);
		((ThreadPoolExecutor) crawlSkuPool).setMaximumPoolSize(coreSize);
	}
	
	public void setCrawlDescPoolSize(int coreSize) {
		((ThreadPoolExecutor) crawlDescPool).setCorePoolSize(coreSize);
		((ThreadPoolExecutor) crawlDescPool).setMaximumPoolSize(coreSize);
	}
	
	public void setCrawlImgPoolSize(int coreSize) {
		((ThreadPoolExecutor) crawlImgPool).setCorePoolSize(coreSize);
		((ThreadPoolExecutor) crawlImgPool).setMaximumPoolSize(coreSize);
	}
	
	public void setCrawlWItemPoolSize(int coreSize) {
		((ThreadPoolExecutor) crawlWItemPool).setCorePoolSize(coreSize);
		((ThreadPoolExecutor) crawlWItemPool).setMaximumPoolSize(coreSize);
	}
	
	public void setCrawlWDescPoolSize(int coreSize) {
		((ThreadPoolExecutor) crawlWDescPool).setCorePoolSize(coreSize);
		((ThreadPoolExecutor) crawlWDescPool).setMaximumPoolSize(coreSize);
	}
	
	// spring inject
	
	public void setTaskPoolConfig(String taskPoolConfig) {
		this.taskPoolConfig = taskPoolConfig;
	}

	public void setTaskType(String taskType) {
		this.taskType = TaskType.fromName(taskType);
	}

	public void setShopQueue(CrawlableTaskQueue<ShopTaskBean> shopQueue) {
		this.shopQueue = shopQueue;
	}

	public void setItemListQueue(CrawlableTaskQueue<ItemListTaskBean> itemListQueue) {
		this.itemListQueue = itemListQueue;
	}

	public void setItemQueue(CrawlableTaskQueue<ItemTaskBean> itemQueue) {
		this.itemQueue = itemQueue;
	}

	public void setSkuQueue(CrawlableTaskQueue<SkuTaskBean> skuQueue) {
		this.skuQueue = skuQueue;
	}

	public void setDescQueue(CrawlableTaskQueue<DescTaskBean> descQueue) {
		this.descQueue = descQueue;
	}

	public void setImgQueue(CrawlableTaskQueue<ImgTaskBean> imgQueue) {
		this.imgQueue = imgQueue;
	}

	public void setWItemQueue(CrawlableTaskQueue<WItemTaskBean> wItemQueue) {
		this.wItemQueue = wItemQueue;
	}

	public void setWDescQueue(CrawlableTaskQueue<WDescTaskBean> wDescQueue) {
		this.wDescQueue = wDescQueue;
	}
	
}
