package com.vdlm.spider.task.partial;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.TaskBeanFactory;
import com.vdlm.spider.bean.WDescTaskBean;
import com.vdlm.spider.bean.parial.PItemDescTaskBean;
import com.vdlm.spider.event.PItemEvent;
import com.vdlm.spider.event.PItemEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.task.CrawlableTask;
import com.vdlm.spider.task.ParsableTask;
import com.vdlm.spider.task.partial.ParsePDescTask.ItemDescInfo;
import com.vdlm.spider.utils.ObjectConvertUtils;
import com.vdlm.spider.wdetail.ItemResponse;
import com.vdlm.spider.wdetail.parser.ApiStackValueParser;
import com.vdlm.spider.wdetail.parser.WItem;

/**
 *
 * @author: chenxi
 */

public class ParsePDescTask extends ParsableTask<ItemDescInfo> {

	static class ItemDescInfo {
		private final PItemEvent event;
		private final String remoteDescUrl;
		
		private final String props;
		
		public ItemDescInfo(PItemEvent event, String remoteDescUrl, String props) {
			this.event = event;
			this.remoteDescUrl = remoteDescUrl;
			this.props = props;
		}

		public PItemEvent getEvent() {
			return event;
		}

		public String getRemoteDescUrl() {
			return remoteDescUrl;
		}

		public String getProps() {
			return props;
		}
		
	}
	
	public ParsePDescTask(BusSignalManager bsm, HttpClientInvoker invoker,
			HttpInvokeResult result,
			CrawlableTask<? extends ItemListTaskBean> retriable) {
		super(bsm, invoker, result, retriable);
	}

	@Override
	protected ItemDescInfo parse() {
		final PItemDescTaskBean bean = (PItemDescTaskBean) getBean();
		
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
		final PItemEvent event = new PItemEvent(PItemEventType.UPDATE_DESC, bean.getItemId());
//		return new ItemDescInfo(event, wItem.getApiDescUrl());
		return new ItemDescInfo(event, resp.getData().getDescInfo().getFullDescUrl(), wItem.getProps());
	}

	@Override
	protected void persistent(ItemDescInfo entity) {
		bsm.signal(entity.getEvent());
	}

	@Override
	protected void furtherCrawl(ItemDescInfo entity) {
		final WDescTaskBean task = TaskBeanFactory.createWDescTaskBean((PItemDescTaskBean) getBean(), 
				entity.event.getItemInfo().getId(), entity.remoteDescUrl, 
				entity.event.getItemInfo().getGroupImgCnt() + entity.event.getItemInfo().getSkuImgCnt());
		task.setProps(entity.getProps());
		bsm.signal(task);
	}

}
