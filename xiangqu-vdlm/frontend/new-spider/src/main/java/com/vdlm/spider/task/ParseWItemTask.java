package com.vdlm.spider.task;

import java.util.List;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.ItemStatus;
import com.vdlm.spider.bean.ImgTaskBean;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.bean.TaskBeanFactory;
import com.vdlm.spider.bean.WDescTaskBean;
import com.vdlm.spider.bean.WItemTaskBean;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.ImgFactory;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.ItemProcess;
import com.vdlm.spider.event.ItemEvent;
import com.vdlm.spider.event.ItemEventType;
import com.vdlm.spider.event.ItemProcessEvent;
import com.vdlm.spider.event.ItemProcessEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.task.ParseWItemTask.WItemWrapper;
import com.vdlm.spider.utils.ObjectConvertUtils;
import com.vdlm.spider.wdetail.EntityPlenter;
import com.vdlm.spider.wdetail.ItemResponse;
import com.vdlm.spider.wdetail.parser.ApiStackValueParser;
import com.vdlm.spider.wdetail.parser.WItem;

/**
 *
 * @author: chenxi
 */

public class ParseWItemTask extends ParsableTask<WItemWrapper> {

	static class WItemWrapper {
		private final WItem wItem;
		private final Item item;
		
		public WItemWrapper(WItem wItem, Item item) {
			this.wItem = wItem;
			this.item = item;
		}

		public WItem getwItem() {
			return wItem;
		}

		public Item getItem() {
			return item;
		}
		
	}
	
	public ParseWItemTask(BusSignalManager bsm,
			HttpClientInvoker invoker, HttpInvokeResult result,
			CrawlableTask<? extends ItemListTaskBean> retriable) {
		super(bsm, invoker, result, retriable);
	}


	@Override
	protected WItemWrapper parse() {
		final WItemTaskBean bean = (WItemTaskBean) getBean();		
		final Item item = new Item();
		item.setOuerUserId(bean.getOuerUserId());
		item.setOuerShopId(bean.getOuerShopId());
		item.setShopType(bean.getShopType());
		item.setItemUrl(bean.getItemUrl());
		item.setItemId(bean.getItemId());
		item.setReqFrom(bean.getReqFrom());		
		
		ItemResponse resp;
		try {
			resp = ObjectConvertUtils.fromString(result.getContentStringAndReset(), ItemResponse.class);
		} catch (final Exception e) {
			LOG.error("parse witem failed", e);
			return null;
		}
		final WItem wItem = ApiStackValueParser.parseItem(resp);
		if (wItem == null) {
			return null;
		}
		EntityPlenter.plent(item, wItem);
		
		// TODO
		return new WItemWrapper(wItem, item);
	}

	@Override
	protected void persistent(WItemWrapper entity) {
		final Item item = entity.getItem();
		final WItem wItem = entity.getwItem();
		final ItemEvent event = new ItemEvent(ItemEventType.SAVE_ITEM_SKUS);
		event.setItem(item);
		event.setSkus(wItem.getSkus());
		bsm.signal(event);
		
		if (!(ItemStatus.SUCCESS.ordinal() == item.getCompleted())) {
			final WItemTaskBean bean = (WItemTaskBean) getBean();
			// init ItemProcess object
			final ItemProcess itemProcess = new ItemProcess();
			itemProcess.setShopId(getBean().getShopId());
			itemProcess.setItemId(item.getId());
			itemProcess.setSkuParsed(true);
			itemProcess.setGroupImgCount(wItem.getGroupImgs().size());
			itemProcess.setSkuImgCount(wItem.getSkuImgs().size());
			itemProcess.setOption(getBean().getOption());
			ItemProcessEvent ipe;
			if (!bean.isAdd()) {
				ipe = new ItemProcessEvent(ItemProcessEventType.REINIT);
			} else {
				ipe = new ItemProcessEvent(ItemProcessEventType.INIT);
			}
			ipe.setInitData(itemProcess);
			bsm.signal(ipe);
		}
	}

	@Override
	protected void furtherCrawl(WItemWrapper entity) {
		final Item item = entity.getItem();
		final WItem wItem = entity.getwItem();
		final List<Img> groupImgs = ImgFactory.createWGroupImgs(wItem.getGroupImgs());
		final ItemTaskBean bean = (ItemTaskBean) getBean();		
		ImgTaskBean imgTask;
		for (final Img img : groupImgs) {
			imgTask = TaskBeanFactory.createImgTaskBean(bean, item.getId(), img);
			bsm.signal(imgTask);
		}
		
		final List<Img> skuImgs = ImgFactory.createWSkuImgs(wItem.getSkuImgs(), groupImgs.size());
		for (final Img img : skuImgs) {
			imgTask = TaskBeanFactory.createImgTaskBean(bean, item.getId(), img);
			bsm.signal(imgTask);
		}
		
		if (wItem.getApiDescUrl() != null) {
			final WDescTaskBean task = TaskBeanFactory.createWDescTaskBean((ItemTaskBean) getBean(), 
					item.getId(), wItem.getApiDescUrl(), groupImgs.size() + skuImgs.size());
			if (entity != null && entity.getwItem() != null)
				task.setProps(entity.getwItem().getProps());
			else if (entity != null && entity.getItem() != null)
				task.setProps(entity.getItem().getProps());
			bsm.signal(task);
		}
	}
}
