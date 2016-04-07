package com.vdlm.spider.task;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.SkuTaskBean;
import com.vdlm.spider.entity.ItemProcess;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.event.ItemEvent;
import com.vdlm.spider.event.ItemEventType;
import com.vdlm.spider.event.ItemProcessEvent;
import com.vdlm.spider.event.ItemProcessEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.task.ParseSkuTask.SkuList;
import com.vdlm.spider.task.helper.SkuParseHelper;
import com.vdlm.spider.utils.Logs;

/**
 * // 解析sku

void parseSku() {

    // 通过ParseSkuThreadPool中的线程从池队列中取出一个task

    // 无需手工编写代码

    ParseSkuTask task = getTaskFromThreadPoolQueue();

    if (task != null) {

       // 从task中取出http result内容

       String content = getContent(task);;

       // 解析出sku对象

       Sku sku = parse(content);

       // 持久化sku

       save(sku);

        // 更新item_process表

       ItemProcess itemProcess = updateItemProcessAndGet(task);

       // 如果该item已全部爬完毕，则同步数据到vdlm?

       if (itemProcess.isCompleted()) {

           syncItemToVdlm();

           // 再做一些其他工作？

           cleanUp();

       }

    }

}
 * @author: chenxi
 */

public class ParseSkuTask extends ParsableTask<SkuList> {
	
	public static class SkuList {
		private final long itemId;
		private final List<Sku> skus;
		
		public SkuList(long itemId, List<Sku> skus) {
			this.itemId = itemId;
			this.skus = skus;
		}

		public long getItemId() {
			return itemId;
		}

		public List<Sku> getSkus() {
			return skus;
		}

	}
	
	public ParseSkuTask(BusSignalManager bsm, 
						  HttpClientInvoker invoker,
						  HttpInvokeResult result,
						  CrawlableTask<SkuTaskBean> retriable) {
		super(bsm, invoker, result, retriable);
	}

	@Override
	protected SkuList parse() {
		final SkuTaskBean bean = (SkuTaskBean) getBean();	
		final long itemId = bean.getItemTaskId();
		final String skuInfo = result.getContentStringAndReset();
		if (StringUtils.isNotBlank(skuInfo)) {
			// 根据获取都的远程数据，格式化sku信息			 
			if(bean.getShopType()==ShopType.TAOBAO){
				SkuParseHelper.parseRemoteTaoBaoSku(bean, skuInfo, bean.getSkus());
			}
			else if(bean.getShopType()==ShopType.TMALL){
				SkuParseHelper.parseRemoteTmallSku(bean, skuInfo, bean.getSkus());			
			}
			else{
				logStatistics(Logs.StatsResult.FAIL, bean);
				return null;			
		    }
		}		 
		final List<Sku> skus = bean.getSkus();
		return new SkuList(itemId, skus);
	}
	
	@Override
	protected void persistent(SkuList entity) {
		final ItemEvent event = new ItemEvent(ItemEventType.SAVE_SKUS);
		event.setItemId(entity.itemId);
		event.setSkus(entity.skus);
		bsm.signal(event);
		
		// update ItemProcess object
		final ItemProcess itemProcess = new ItemProcess();
		itemProcess.setItemId(entity.itemId);
		final ItemProcessEvent ipe = new ItemProcessEvent(ItemProcessEventType.SKU);
		ipe.setItemId(entity.itemId);
		ipe.setInitData(itemProcess);
		bsm.signal(ipe);
		
	}

	@Override
	protected void furtherCrawl(SkuList entity) {
		// TODO Auto-generated method stub
		
	} 

}
