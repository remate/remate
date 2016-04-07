package com.vdlm.spider.task;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.Config;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.bean.ItemTaskBeans;
import com.vdlm.spider.bean.TaskBeanFactory;
import com.vdlm.spider.bean.WItemTaskBean;
import com.vdlm.spider.entity.ItemListProcess;
import com.vdlm.spider.event.ItemListProcessEvent;
import com.vdlm.spider.event.ItemListProcessEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.parser.config.ParserConfigProviders;
import com.vdlm.spider.task.ItemTaskStrategy.Strategies;
import com.vdlm.spider.task.helper.ItemListParseHelper;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public class ParseItemListTask extends ParsableTask<List<ItemTaskBean>> {

	static final Pattern pattern = Pattern.compile("inshopn=(\\d+)");
	
	private final ItemTaskStrategy strategy;
	
	public ParseItemListTask(BusSignalManager bsm, 
							   HttpClientInvoker invoker,
							   HttpInvokeResult result, 
							   CrawlableTask<ItemListTaskBean> retriable) {
		super(bsm, invoker, result, retriable);
		this.strategy = ((CrawlItemListTask) retriable).getStrategy();
	}	

	@Override
	protected List<ItemTaskBean> parse() {		
		final ItemListTaskBean bean = getBean();
		JSONObject json = null;
		final List<ItemTaskBean> itemTaskList=new ArrayList<ItemTaskBean>();
		try {
			json = JSON.parseObject(result.getContentStringAndReset());
		} catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Error to parse jsonString from itemlist.htm, maybe invalid ParseRule:{}\n,jsonString:\n{}",
							bean, result.getContentStringAndReset());		
			logStatistics(Logs.StatsResult.FAIL, bean);
		}
		// 获取 item 列表
		JSONArray itemList=null;
		try {
			itemList = ItemListParseHelper.get(json, ParserConfigProviders.getItemListConfig(bean).getAuctions(), 2);
			Logs.maybeChangedRuleLogger.info("ShopUrl:{}, itemListCount:{}",bean.getShopUrl(),itemList == null ? 0 : itemList.size());
		} catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("can not find itemList from itemlist.htm, maybe invalid ParseRule:"	+ bean, e);
			logStatistics(Logs.StatsResult.FAIL, bean);
		}
		// 没有数据
		if (itemList == null) {
			Logs.maybeChangedRuleLogger.error("can not find itemList from itemlist.htm, maybe invalid ParseRule:" + bean);
			logStatistics(Logs.StatsResult.FAIL, bean);
		}	
		
		String auctionUrlType = "http://item.taobao.com/item.htm?id=";		
		if(bean.getShopType() == ShopType.TAOBAO){
			auctionUrlType = "http://item.taobao.com/item.htm?id=";		
		}
		else if(bean.getShopType() == ShopType.TMALL){
			auctionUrlType = "http://detail.tmall.com/item.htm?id=";		
		}
		
		for (int i = 0; i < (itemList).size(); i++) {
			final JSONObject item = (itemList).getJSONObject(i);
			
			final Object auctionId = ItemListParseHelper.get(item, ParserConfigProviders.getItemListConfig(bean).getAuctionId());
			final String auctionUrl = auctionUrlType + auctionId.toString();
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("i={}, itemUrl={}, itemId={}, total={}", i,auctionUrl,auctionId,(itemList).size());
			}
			final ItemTaskBean tb = TaskBeanFactory.createItemTaskBean(bean, auctionId, auctionUrl);
			itemTaskList.add(tb);
//			if (TaskQueues.getParseItemTaskQueue().add(tb.toJSONBytes())) {
//				TaskCounters.incrementAndGet(this.bean.getTaskId());
//				cntAdded++;
//			} else {
//				Logs.unpredictableLogger.warn(
//						"Failed to submit ParseItemTask: {}", tb);
//			}
		}		
		if (itemTaskList.size()>0) {
			logStatistics(Logs.StatsResult.SUCESS, bean);
			return itemTaskList;
		}		
		return null;
	}
	
	@Override
	protected void persistent(List<ItemTaskBean> entity) {
		bsm.signal(new ItemTaskBeans(entity));
		final ItemListProcess itemListProcess = new ItemListProcess();
		itemListProcess.setShopId(getBean().getShopId());
		itemListProcess.setItemCount(entity.size());
		final ItemListProcessEvent event = new ItemListProcessEvent(ItemListProcessEventType.INIT);
		event.setInitData(itemListProcess);
		bsm.signal(event);
	}

	@Override
	protected void furtherCrawl(List<ItemTaskBean> entity) {
		final int cntAdded = 0;
		WItemTaskBean wbean;
		for (final ItemTaskBean item : entity) {
			if (Strategies.W.equals(strategy.getStrategy())) {
				wbean = TaskBeanFactory.createWItemTaskBean(item);
				bsm.signal(wbean);
			}
			else if (Strategies.V2.equals(strategy)) {
				// TODO
			}
			else {
				bsm.signal(item);
			}
		}
		if(cntAdded < entity.size()) {
			Logs.statistics
					.warn("parser_itemlist. shopId:{}, listUrl:{}, total:{}, current:{}, reason:{}",
							getBean().getOuerShopId(), getBean().getShopUrl(),
							entity.size(), cntAdded,
							Logs.StatsResult.PARTIAL);
		}
		
		try {
			if (entity.size() >= Config.instance()
					.getUrl2EndSize()) {
				// set shop process partially
				final ItemListProcessEvent event = new ItemListProcessEvent(ItemListProcessEventType.PARTIALLY);
				event.setShopId(getBean().getShopId());
				event.setOuerShopId(getBean().getOuerShopId());
				event.setPartially(true);
				bsm.signal(event);
				
				final Matcher mc = pattern.matcher(getBean().getRequestUrl());
				mc.find();
				final int url2StartSize = Integer.parseInt(mc.group(1));
				final int url2EndSize = url2StartSize
						+ Config.instance().getUrl2EndSize();

				final ItemListTaskBean tb = getBean().clone();
				tb.setRefererUrl(getBean().getRequestUrl());
				tb.setRequestUrl(ParserConfigProviders.getItemListConfig(getBean()).getUrl2(
						String.valueOf(url2StartSize),
						String.valueOf(url2EndSize), getBean().getTUserId()));
				tb.setRetryTimes(0);
				bsm.signal(tb);

				if (LOG.isDebugEnabled()) {
					LOG.debug("currentStartSize={}, currentEndPage={}",
							url2StartSize, url2EndSize);
				}
			} else {
				// reset shop process partially
				final ItemListProcessEvent event = new ItemListProcessEvent(ItemListProcessEventType.PARTIALLY);
				event.setShopId(getBean().getShopId());
				event.setOuerShopId(getBean().getOuerShopId());
				event.setPartially(false);
				bsm.signal(event);
			}
		} catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error(
					"can not find pageInfo, maybe invalid ParseRule: "
							+ getBean(), e);
			logStatistics(Logs.StatsResult.FAIL, getBean());
			return;
		}
		logStatistics(Logs.StatsResult.ASYNC, getBean());
	}	 

}
