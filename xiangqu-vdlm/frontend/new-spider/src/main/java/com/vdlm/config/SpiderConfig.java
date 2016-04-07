package com.vdlm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.common.bus.DefaultBusRegistry;
import com.vdlm.common.core.AppInfo;
import com.vdlm.common.core.statistic.ClassLoadingStatisticer;
import com.vdlm.common.core.statistic.GCStatisticer;
import com.vdlm.common.core.statistic.JVMMemory;
import com.vdlm.common.core.statistic.OperatingSystem;
import com.vdlm.common.core.statistic.Runtime;
import com.vdlm.common.core.statistic.ThreadStatisticer;
import com.vdlm.common.core.statistic.TransactionStatisticer;
import com.vdlm.limiter.HourBasedRateLimiter;
import com.vdlm.limiter.RateLimiterProvider;
import com.vdlm.spider.bean.DescTaskBean;
import com.vdlm.spider.bean.ImgTaskBean;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.bean.ShopTaskBean;
import com.vdlm.spider.bean.SkuTaskBean;
import com.vdlm.spider.bean.WDescTaskBean;
import com.vdlm.spider.bean.WItemTaskBean;
import com.vdlm.spider.bean.parial.PItemDescTaskBean;
import com.vdlm.spider.bean.parial.PItemGroupImgTaskBean;
import com.vdlm.spider.bean.parial.PItemSkusTaskBean;
import com.vdlm.spider.store.DescStore;
import com.vdlm.spider.store.HttpRequestErrorStore;
import com.vdlm.spider.store.ItemListProcessStore;
import com.vdlm.spider.store.ItemListStore;
import com.vdlm.spider.store.ItemProcessStore;
import com.vdlm.spider.store.ItemStore;
import com.vdlm.spider.store.ItemSyncStore;
import com.vdlm.spider.store.PartialItemStore;
import com.vdlm.spider.store.ShopStore;
import com.vdlm.spider.task.CrawlableTaskQueue;
import com.vdlm.spider.task.CrawlableTaskRetryManager;
import com.vdlm.spider.task.ItemTaskStrategy;
import com.vdlm.spider.task.TaskType;
import com.vdlm.spider.task.partial.PartialTaskProcessor;
import com.vdlm.spider.task.resubmit.ItemListTaskDefeater;
import com.vdlm.spider.task.resubmit.ItemTaskDefeater;
import com.vdlm.spider.task.resubmit.ResubmitDefeater;
import com.vdlm.spider.task.resubmit.ShopTaskDefeater;
import com.vdlm.spider.task.resubmit.TaskStatusHandler;
import com.vdlm.spider.task.resubmit.TaskStatusHandlerFactory;
import com.vdlm.spider.task.resubmit.TaskStatusTracker;
import com.vdlm.spider.task.resubmit.WItemTaskDefeater;

/**
 *
 * @author: chenxi
 */

@Configuration
@ImportResource("classpath:META-INF/applicationContext-spider.xml")
public class SpiderConfig {

	@Value("${fqpath}")
	String path;
	@Value("${fqsize}")
	int capacity;
	
	@Bean
	ItemTaskStrategy itemTaskStrategy() {
		return new ItemTaskStrategy();
	}
	
	@Bean
	BusSignalManager busSignalManager() {
		return new BusSignalManager(new DefaultBusRegistry());
	}
	
	@Autowired
	@Bean
	ShopStore shopStore(BusSignalManager bsm) {
		return new ShopStore(bsm);
	}
	
	@Autowired
	@Bean
	ItemListProcessStore itemListProcessStore(BusSignalManager bsm) {
		return new ItemListProcessStore(bsm);
	}
	
	@Autowired
	@Bean
	ItemProcessStore itemProcessStore(BusSignalManager bsm) {
		return new ItemProcessStore(bsm);
	}
	
	@Autowired
	@Bean
	ItemSyncStore ItemSyncStore(BusSignalManager bsm) {
		return new ItemSyncStore(bsm);
	}
	
	@Autowired
	@Bean
	ItemStore itemStore(BusSignalManager bsm) {
		return new ItemStore(bsm);
	}
	
	@Autowired
	@Bean
	ItemListStore itemListStore(BusSignalManager bsm) {
		return new ItemListStore(bsm);
	}
	
	@Autowired
	@Bean
	DescStore descStore(BusSignalManager bsm) {
		return new DescStore(bsm);
	}
	
	@Autowired
	@Bean
	PartialItemStore MItemStore(BusSignalManager bsm) {
		return new PartialItemStore(bsm);
	}
	
	@Autowired
	@Bean
	HttpRequestErrorStore httpRequestErrorStore(BusSignalManager bsm) {
		return new HttpRequestErrorStore(bsm);
	}
	
	@Autowired
	@Bean(name = "rateLimiterProvider")
	RateLimiterProvider rateLimiterProvider(BusSignalManager bsm) {
		return new RateLimiterProvider(bsm);
	}
	
	@Bean(name = "shopTaskDefeater")
	ResubmitDefeater<ShopTaskBean> shopTaskDefeater() {
		return new ShopTaskDefeater();
	}
	
	@Bean(name = "itemListTaskDefeater")
	ResubmitDefeater<ItemListTaskBean> itemListTaskDefeater() {
		return new ItemListTaskDefeater();
	}
	
	@Bean(name = "itemTaskDefeater")
	ResubmitDefeater<ItemTaskBean> itemTaskDefeater() {
		return new ItemTaskDefeater();
	}
	
	@Bean(name = "wItemTaskDefeater")
	ResubmitDefeater<WItemTaskBean> wItemTaskDefeater() {
		return new WItemTaskDefeater();
	}
	
	@Autowired
	@Bean
	TaskStatusTracker TaskStatusTracker(BusSignalManager bsm, TaskStatusHandlerFactory factory) {
		return new TaskStatusTracker(bsm, factory);
	}
	
	@Autowired
	@Bean
	TaskStatusHandlerFactory taskStatusHandlerFactory(@Qualifier("shopTaskStatusHandler") TaskStatusHandler shop,
															 @Qualifier("itemListTaskStatusHandler") TaskStatusHandler itemList, 
															 @Qualifier("itemTaskStatusHandler") TaskStatusHandler item) {
		return new TaskStatusHandlerFactory(shop, itemList, item);
	}
	
	@Bean(name = "shopQueue")
	CrawlableTaskQueue<ShopTaskBean> shopQueue(@Qualifier("shopTaskDefeater") ResubmitDefeater<ShopTaskBean> defeater) {
		final CrawlableTaskQueue<ShopTaskBean> shopQueue = new CrawlableTaskQueue<ShopTaskBean>();
		shopQueue.setPath(path + "/CrawlShopTask");
		shopQueue.setCapacity(capacity);
		shopQueue.setTaskType(TaskType.DEFAULT);
		shopQueue.setDefeater(defeater);
		shopQueue.init();
		return shopQueue;
	}
	
	@Autowired
	@Bean(name = "itemListQueue")
	CrawlableTaskQueue<ItemListTaskBean> itemListQueue(
			@Qualifier("itemListTaskDefeater") ResubmitDefeater<ItemListTaskBean> defeater) {
		final CrawlableTaskQueue<ItemListTaskBean> itemListQueue = new CrawlableTaskQueue<ItemListTaskBean>();
		itemListQueue.setPath(path + "/CrawlItemListTask");
		itemListQueue.setCapacity(capacity);
		itemListQueue.setTaskType(TaskType.DEFAULT);
		itemListQueue.setDefeater(defeater);
		itemListQueue.init();
		return itemListQueue;
	}
	
	@Autowired
	@Bean(name = "itemQueue")
	CrawlableTaskQueue<ItemTaskBean> itemQueue(@Qualifier("itemTaskDefeater") ResubmitDefeater<ItemTaskBean> defeater) {
		final CrawlableTaskQueue<ItemTaskBean> itemQueue = new CrawlableTaskQueue<ItemTaskBean>();
		itemQueue.setPath(path + "/CrawlItemTask");
		itemQueue.setCapacity(capacity);
		itemQueue.setTaskType(TaskType.DEFAULT);
		itemQueue.setDefeater(defeater);
		itemQueue.init();
		return itemQueue;
	}
	
	@Bean(name = "skuQueue")
	CrawlableTaskQueue<SkuTaskBean> skuQueue() {
		final CrawlableTaskQueue<SkuTaskBean> skuQueue = new CrawlableTaskQueue<SkuTaskBean>();
		skuQueue.setPath(path + "/CrawlSkuTask");
		skuQueue.setCapacity(capacity);
		skuQueue.setTaskType(TaskType.DEFAULT);
		skuQueue.init();
		return skuQueue;
	}
	
	@Bean(name = "descQueue")
	CrawlableTaskQueue<DescTaskBean> descQueue() {
		final CrawlableTaskQueue<DescTaskBean> descQueue = new CrawlableTaskQueue<DescTaskBean>();
		descQueue.setPath(path + "/CrawlDescTask");
		descQueue.setCapacity(capacity);
		descQueue.setTaskType(TaskType.DEFAULT);
		descQueue.init();
		return descQueue;
	}
	
	@Bean(name = "imgQueue")
	CrawlableTaskQueue<ImgTaskBean> imgQueue() {
		final CrawlableTaskQueue<ImgTaskBean> imgQueue = new CrawlableTaskQueue<ImgTaskBean>();
		imgQueue.setPath(path + "/CrawImgTask");
		imgQueue.setCapacity(capacity * 10);
		imgQueue.setTaskType(TaskType.DEFAULT);
		imgQueue.init();
		return imgQueue;
	}
	
	// w
	@Bean(name = "wItemQueue")
	CrawlableTaskQueue<WItemTaskBean> wItemQueue(@Qualifier("wItemTaskDefeater") ResubmitDefeater<WItemTaskBean> defeater) {
		final CrawlableTaskQueue<WItemTaskBean> wItemQueue = new CrawlableTaskQueue<WItemTaskBean>();
		wItemQueue.setPath(path + "/CrawlWItemTask");
		wItemQueue.setCapacity(capacity);
		wItemQueue.setTaskType(TaskType.DEFAULT);
		wItemQueue.setDefeater(defeater);
		wItemQueue.init();
		return wItemQueue;
	}
	
	@Bean(name = "wDescQueue")
	CrawlableTaskQueue<WDescTaskBean> wDescQueue() {
		final CrawlableTaskQueue<WDescTaskBean> wDescQueue = new CrawlableTaskQueue<WDescTaskBean>();
		wDescQueue.setPath(path + "/CrawlWDescTask");
		wDescQueue.setCapacity(capacity);
		wDescQueue.setTaskType(TaskType.DEFAULT);
		wDescQueue.init();
		return wDescQueue;
	}
	
	@Bean(name = "periodicalUpdater")
	HourBasedRateLimiter hourBasedRateLimiter() {
		return new HourBasedRateLimiter();
	}
	
	@Bean
	CrawlableTaskRetryManager crawlableTaskRetryManager() {
		return new CrawlableTaskRetryManager();
	}
	
//	@Bean(name = "fineGrainTaskProcessor")
//	FineGrainTaskProcessor fineGrainTaskProcessor() {
//		return new FineGrainTaskProcessor();
//	}
	
//	@Bean(name = "mFineGrainTaskProcessor")
//	FineGrainTaskProcessor mFineGrainTaskProcessor() {
//		return new FineGrainTaskProcessor();
//	}
	
	
//	@Bean(name = "mItemDescQueue")
//	CrawlableTaskQueue<MItemDescTaskBean> mItemDescQueue() {
//		final CrawlableTaskQueue<MItemDescTaskBean> mItemDescQueue = new CrawlableTaskQueue<MItemDescTaskBean>();
//		mItemDescQueue.setPath("/ouer/data/fqueue/MItemDescTask");
//		mItemDescQueue.init();
//		return mItemDescQueue;
//	}
	
//	@Bean
//	ManuallyTaskProcessor manuallyTaskProcessor() {
//		return new ManuallyTaskProcessor();
//	}

	@Bean
	AppInfo appInfo() {
		return new AppInfo();
	}
	
	@Bean
	ClassLoadingStatisticer classLoadingStatisticer() {
		return new ClassLoadingStatisticer();
	}
	
	@Bean
	GCStatisticer gcStatisticer() {
		return new GCStatisticer();
	}
	
	@Bean
	JVMMemory jvmMemory() {
		return new JVMMemory();
	}
	
	@Bean
	OperatingSystem OS() {
		return new OperatingSystem();
	}
	
	@Bean
	Runtime runtime() {
		return new Runtime();
	}
	
	@Bean
	ThreadStatisticer threadStatisticer() {
		return new ThreadStatisticer();
	}
	
	@Bean
	TransactionStatisticer transactionStatisticer() {
		return new TransactionStatisticer();
	}
	
	// for manually
	@Bean(name = "mShopQueue")
	CrawlableTaskQueue<ShopTaskBean> mShopQueue() {
		final CrawlableTaskQueue<ShopTaskBean> shopQueue = new CrawlableTaskQueue<ShopTaskBean>();
		shopQueue.setPath(path + "/MCrawlShopTask");
		shopQueue.setCapacity(capacity);
		shopQueue.setTaskType(TaskType.MANUALLY);
		shopQueue.init();
		return shopQueue;
	}
	
	@Autowired
	@Bean(name = "mItemListQueue")
	CrawlableTaskQueue<ItemListTaskBean> mItemListQueue(
			@Qualifier("itemListTaskDefeater") ResubmitDefeater<ItemListTaskBean> defeater) {
		final CrawlableTaskQueue<ItemListTaskBean> itemListQueue = new CrawlableTaskQueue<ItemListTaskBean>();
		itemListQueue.setPath(path + "/MCrawlItemListTask");
		itemListQueue.setCapacity(capacity);
		itemListQueue.setTaskType(TaskType.MANUALLY);
		itemListQueue.setDefeater(defeater);
		itemListQueue.init();
		return itemListQueue;
	}
	
	@Autowired
	@Bean(name = "mItemQueue")
	CrawlableTaskQueue<ItemTaskBean> mItemQueue(@Qualifier("itemTaskDefeater") ResubmitDefeater<ItemTaskBean> defeater) {
		final CrawlableTaskQueue<ItemTaskBean> itemQueue = new CrawlableTaskQueue<ItemTaskBean>();
		itemQueue.setPath(path + "/MCrawlItemTask");
		itemQueue.setCapacity(capacity);
		itemQueue.setTaskType(TaskType.MANUALLY);
		itemQueue.setDefeater(defeater);
		itemQueue.init();
		return itemQueue;
	}
	
	@Bean(name = "mSkuQueue")
	CrawlableTaskQueue<SkuTaskBean> mSkuQueue() {
		final CrawlableTaskQueue<SkuTaskBean> skuQueue = new CrawlableTaskQueue<SkuTaskBean>();
		skuQueue.setPath(path + "/MCrawlSkuTask");
		skuQueue.setCapacity(capacity);
		skuQueue.setTaskType(TaskType.MANUALLY);
		skuQueue.init();
		return skuQueue;
	}
	
	@Bean(name = "mDescQueue")
	CrawlableTaskQueue<DescTaskBean> mDescQueue() {
		final CrawlableTaskQueue<DescTaskBean> descQueue = new CrawlableTaskQueue<DescTaskBean>();
		descQueue.setPath(path + "/MCrawlDescTask");
		descQueue.setCapacity(capacity);
		descQueue.setTaskType(TaskType.MANUALLY);
		descQueue.init();
		return descQueue;
	}
	
	@Bean(name = "mImgQueue")
	CrawlableTaskQueue<ImgTaskBean> mImgQueue() {
		final CrawlableTaskQueue<ImgTaskBean> imgQueue = new CrawlableTaskQueue<ImgTaskBean>();
		imgQueue.setPath(path + "/MCrawImgTask");
		imgQueue.setCapacity(capacity * 10);
		imgQueue.setTaskType(TaskType.MANUALLY);
		imgQueue.init();
		return imgQueue;
	}
	
	// w
	@Bean(name = "mWItemQueue")
	CrawlableTaskQueue<WItemTaskBean> mwItemQueue(@Qualifier("wItemTaskDefeater") ResubmitDefeater<WItemTaskBean> defeater) {
		final CrawlableTaskQueue<WItemTaskBean> wItemQueue = new CrawlableTaskQueue<WItemTaskBean>();
		wItemQueue.setPath(path + "/MCrawlWItemTask");
		wItemQueue.setCapacity(capacity);
		wItemQueue.setTaskType(TaskType.MANUALLY);
		wItemQueue.setDefeater(defeater);
		wItemQueue.init();
		return wItemQueue;
	}
	
	@Bean(name = "mWDescQueue")
	CrawlableTaskQueue<WDescTaskBean> mwDescQueue() {
		final CrawlableTaskQueue<WDescTaskBean> wDescQueue = new CrawlableTaskQueue<WDescTaskBean>();
		wDescQueue.setPath(path + "/MCrawlWDescTask");
		wDescQueue.setCapacity(capacity);
		wDescQueue.setTaskType(TaskType.MANUALLY);
		wDescQueue.init();
		return wDescQueue;
	}
	
	// p
	@Bean
	PartialTaskProcessor partialTaskProcessor() {
		return new PartialTaskProcessor();
	}
	
	@Bean(name = "pItemDescQueue")
	CrawlableTaskQueue<PItemDescTaskBean> pItemDescQueue() {
		final CrawlableTaskQueue<PItemDescTaskBean> pItemDescQueue = new CrawlableTaskQueue<PItemDescTaskBean>();
		pItemDescQueue.setPath(path + "/PItemDescTaskBean");
		pItemDescQueue.setCapacity(capacity);
		pItemDescQueue.setTaskType(TaskType.MANUALLY);
		pItemDescQueue.init();
		return pItemDescQueue;
	}
	
	@Bean(name = "pItemGroupImgQueue")
	CrawlableTaskQueue<PItemGroupImgTaskBean> pItemGroupImgQueue() {
		final CrawlableTaskQueue<PItemGroupImgTaskBean> pItemGroupImgQueue = new CrawlableTaskQueue<PItemGroupImgTaskBean>();
		pItemGroupImgQueue.setPath(path + "/PItemGroupImgTaskBean");
		pItemGroupImgQueue.setCapacity(capacity);
		pItemGroupImgQueue.setTaskType(TaskType.MANUALLY);
		pItemGroupImgQueue.init();
		return pItemGroupImgQueue;
	}
	
	@Bean(name = "pItemSkusQueue")
	CrawlableTaskQueue<PItemSkusTaskBean> pItemSkusQueue() {
		final CrawlableTaskQueue<PItemSkusTaskBean> pItemSkusQueue = new CrawlableTaskQueue<PItemSkusTaskBean>();
		pItemSkusQueue.setPath(path + "/PItemSkusTaskBean");
		pItemSkusQueue.setCapacity(capacity);
		pItemSkusQueue.setTaskType(TaskType.MANUALLY);
		pItemSkusQueue.init();
		return pItemSkusQueue;
	}
	
//	@Bean
//	XiciHomeProxyCrawler xiciHomeProxy() throws Exception {
//		return new XiciHomeProxyCrawler();
//	}
//	
//	@Bean
//	XiciNnProxyCrawler xiciNnProxyCrawler() throws Exception {
//		return new XiciNnProxyCrawler();
//	}
//	
//	@Bean
//	Daili5566ProxyCrawler daili5566ProxyCrawler() throws Exception {
//		return new Daili5566ProxyCrawler();
//	}
//	
//	@Autowired
//	@Bean
//	ProxyValidator proxyValidator(BusSignalManager bsm) {
//		return new ProxyValidator(bsm);
//	}
}
