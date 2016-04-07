package com.vdlm.spider.task.partial;

import java.util.List;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ImgTaskBean;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.TaskBeanFactory;
import com.vdlm.spider.bean.parial.PItemSkusTaskBean;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.ImgFactory;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.event.ItemEvent;
import com.vdlm.spider.event.ItemEventType;
import com.vdlm.spider.event.PItemEvent;
import com.vdlm.spider.event.PItemEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.task.CrawlableTask;
import com.vdlm.spider.task.ParsableTask;
import com.vdlm.spider.task.partial.ParsePSkusTask.WItemWrapper;
import com.vdlm.spider.utils.ObjectConvertUtils;
import com.vdlm.spider.wdetail.EntityPlenter;
import com.vdlm.spider.wdetail.ItemResponse;
import com.vdlm.spider.wdetail.parser.ApiStackValueParser;
import com.vdlm.spider.wdetail.parser.WItem;

/**
 *
 * @author: chenxi
 */

public class ParsePSkusTask extends ParsableTask<WItemWrapper> {

	static class WItemWrapper {
		private final WItem wItem;
		private final Item item;
		private final PItemEvent event;
		
		public WItemWrapper(WItem wItem, Item item, PItemEvent event) {
			this.wItem = wItem;
			this.item = item;
			this.event = event;
		}

		public WItem getwItem() {
			return wItem;
		}

		public Item getItem() {
			return item;
		}

		public PItemEvent getEvent() {
			return event;
		}
		
	}
	
	public ParsePSkusTask(BusSignalManager bsm, HttpClientInvoker invoker,
			HttpInvokeResult result, CrawlableTask<? extends ItemListTaskBean> retriable) {
		super(bsm, invoker, result, retriable);
	}

	@Override
	protected WItemWrapper parse() {
		final PItemSkusTaskBean bean = (PItemSkusTaskBean) getBean();
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
		final PItemEvent pEvent = new PItemEvent(PItemEventType.UPDATE_SKUS, item.getItemId());
		return new WItemWrapper(wItem, item, pEvent);
	}

	@Override
	protected void persistent(WItemWrapper entity) {
		final Item item = entity.getItem();
		final WItem wItem = entity.getwItem();
		final ItemEvent event = new ItemEvent(ItemEventType.SAVE_ITEM_SKUS);
		event.setOnlyUpdateSkus(true);
		event.setItem(item);
		event.setSkus(wItem.getSkus());
		bsm.signal(event);
		
		final PItemEvent pEvent = entity.getEvent();
		pEvent.getItemInfo().setId(item.getId());
		pEvent.getItemInfo().setSkuImgCnt(wItem.getSkuImgs().size());
		bsm.signal(pEvent);
	}

	@Override
	protected void furtherCrawl(WItemWrapper entity) {
		final PItemSkusTaskBean bean = (PItemSkusTaskBean) getBean();		
		final WItem wItem = entity.getwItem();
		final List<Img> skuImgs = ImgFactory.createWSkuImgs(wItem.getSkuImgs(), entity.getEvent().getItemInfo().getGroupImgCnt());
		ImgTaskBean imgTask;
		for (final Img img : skuImgs) {
			imgTask = TaskBeanFactory.createImgTaskBean(bean, entity.getItem().getId(), img);
			bsm.signal(imgTask);
		}
	}

}
