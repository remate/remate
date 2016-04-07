/**
 * 
 */
package com.vdlm.spider.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.vdlm.spider.Config;
import com.vdlm.spider.bean.ParseItemTaskBean;
import com.vdlm.spider.cache.ItemCounters;
import com.vdlm.spider.cache.TaskCounters;
import com.vdlm.spider.cache.UrlCounters;
import com.vdlm.spider.core.NamedThreadFactory;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.parser.Parser;
import com.vdlm.spider.parser.item.TaobaoItemParser;
import com.vdlm.spider.parser.item.TmallItemParser;
import com.vdlm.spider.queue.TaskQueues;
import com.vdlm.spider.service.ItemService;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 1:50:42 AM Jul 20, 2014
 */
public class ParseItemTask extends AbstractTask {

	private HttpClientProvider provider;
	private ItemService itemService;

	public void setProvider(HttpClientProvider provider) {
		this.provider = provider;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	HttpClientProvider getProvider() {
		return provider;
	}

	@Override
	public int getCorePoolSize() {
		return Math.max(super.getCorePoolSize(), Config.instance().getVpsSize());
//		return 1;
	}

	@Override
	ThreadFactory getThreadFactory() {
		return new NamedThreadFactory("ParseItemTask");
	}

	@Override
	void start() {
		for (int i = 0; i < this.getCorePoolSize(); i++) {
			this.getService().scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {
					try {
						ParseItemTask.this.start0();
					}
					catch (Exception e) {
						log.error("unpredictable exception", e);
					}
				}
			}, this.getFixedDelay() + i * 200, this.getFixedDelay(), TimeUnit.MILLISECONDS);
		}
	}

	void start0() {
		byte[] bytes = null;
		ParseItemTaskBean bean = null;
		Parser parser = null;
		// while (true) {
		try {
			bytes = TaskQueues.getParseItemTaskQueue().poll();
		} catch (Exception e) {
			log.error("error to poll queue:", e);
			return;
		}

		if (bytes == null) {
			return;
		}

		try {
			bean = ParseItemTaskBean.parse(bytes);
		} catch (Exception e) {
			log.error("Error to parse JSONString to ParseItemTaskBean: "
					+ new String(bytes), e);
			return;
		}

		if (StringUtils.isBlank(bean.getItemId())) {
			TaskCounters.decrementAndGet(bean.getTaskId());
			log.error("Can not find itemId, ignore: {}", bean);
			return;
		}

		// 没有可用的 vps
		if (!this.getProvider().getIpPools().hasAvaliableIp(bean.getShopType())) {
			if (!TaskQueues.getParseItemTaskQueue().add(bytes)) {
				TaskCounters.decrementAndGet(bean.getTaskId());
			}
			return;
		}

		// 不做重复操作
		if (!UrlCounters.add(bean.getReqFrom(), bean.getOuerUserId(),
				bean.getOuerShopId(), bean.getItemUrl())) {
			log.warn("url duplicateb in UrlCounters, url={} ",
					bean.getItemId());
			TaskCounters.decrementAndGet(bean.getTaskId());
			return;
		}
		if (!ItemCounters.add(bean.getReqFrom(), bean.getOuerUserId(),
				bean.getOuerShopId(), bean.getShopType(), bean.getItemId())) {
			log.warn("item duplicateb in ItemCounters, url={} ",
					bean.getItemId());
			TaskCounters.decrementAndGet(bean.getTaskId());
			return;
		}

		switch (bean.getShopType()) {
		case TAOBAO:
			parser = new TaobaoItemParser(this.getProvider(), bean,
					this.itemService);
			break;
		case TMALL:
			parser = new TmallItemParser(this.getProvider(), bean,
					this.itemService);
			break;
		default:
			parser = null;
		}

		if (parser == null) {
			TaskCounters.decrementAndGet(bean.getTaskId());
			log.error("can not find ItemParser, ignore:" + bean.toString());
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("take from ShopTaskQueue:{}", bean);
		}

		try {
			parser.parse();
		} catch (Exception e) {
			log.error("Error to parse item's info by " + bean.toString(), e);
			return;
		}
		// }
	}
}
