package com.vdlm.spider.task.partial;

import java.util.List;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.parial.PItemFieldsTaskBean;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.event.ItemEvent;
import com.vdlm.spider.event.ItemEventType;
import com.vdlm.spider.event.SyncItemFieldsEvent;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.task.CrawlableTask;
import com.vdlm.spider.task.ParsableTask;
import com.vdlm.spider.utils.ObjectConvertUtils;
import com.vdlm.spider.wdetail.EntityPlenter;
import com.vdlm.spider.wdetail.ItemResponse;
import com.vdlm.spider.wdetail.parser.ApiStackValueParser;
import com.vdlm.spider.wdetail.parser.WItem;

/**
 *
 * @author: chenxi
 */

public class ParsePItemFieldsTask extends ParsableTask<Item> {

	public ParsePItemFieldsTask(BusSignalManager bsm,
			HttpClientInvoker invoker, HttpInvokeResult result,
			CrawlableTask<? extends ItemListTaskBean> retriable) {
		super(bsm, invoker, result, retriable);
	}

	@Override
	protected Item parse() {
		final PItemFieldsTaskBean bean = (PItemFieldsTaskBean) getBean();
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
		return item;
	}

	@Override
	protected void persistent(Item entity) {
		// update item anyway
		final ItemEvent event = new ItemEvent(ItemEventType.SAVE_ITEM);
		event.setItem(entity);
		bsm.signal(event);
		// then sync essential fields
		final PItemFieldsTaskBean bean = (PItemFieldsTaskBean) getBean();
		final List<String> fields = bean.getFields();
		final SyncItemFieldsEvent syncEvent = new SyncItemFieldsEvent(bean.getItemId(), fields);
		bsm.signal(syncEvent);
	}

	@Override
	protected void furtherCrawl(Item entity) {
		// TODO Auto-generated method stub
		
	}

}
