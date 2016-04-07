/**
 * 
 */
package com.vdlm.spider.task;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.vdlm.spider.Config;
import com.vdlm.spider.bean.ParseShopTaskBean;
import com.vdlm.spider.cache.UrlCounters;
import com.vdlm.spider.core.NamedThreadFactory;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.parser.Parser;
import com.vdlm.spider.parser.itemlist.TaobaoItemListParser;
import com.vdlm.spider.parser.itemlist.TmallItemListParser;
import com.vdlm.spider.queue.TaskQueues;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 1:49:57 AM Jul 20, 2014
 */
public class ParseShopTask extends AbstractTask {

	private HttpClientProvider provider;
	
	Date date = new Date();
	DateFormat df = DateFormat.getDateTimeInstance();

	public void setProvider(HttpClientProvider provider) {
		this.provider = provider;
	}

	HttpClientProvider getProvider() {
		return provider;
	}

	@Override
	ThreadFactory getThreadFactory() {
		return new NamedThreadFactory("ParseShopTask");
	}

	@Override
	void start() {
		for (int i = 0; i < this.getCorePoolSize(); i++) {
			this.getService().scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {
					try {
						ParseShopTask.this.start0();
					} catch (Exception e) {
						log.error("unpredictable exception", e);
					}
				}
			}, this.getFixedDelay() + i * 200, this.getFixedDelay(),
					TimeUnit.MILLISECONDS);
		}
	}

	void start0() {
		byte[] bytes = null;
		ParseShopTaskBean bean = null;
		Parser parser = null;
		try {
			bytes = TaskQueues.getParseShopTaskQueue().poll();
		} catch (Exception e) {
			log.error("error to poll queue:", e);
			return;
		}

		if (bytes == null) {
			return;
		}

		try {
			bean = ParseShopTaskBean.parse(bytes);
		} catch (Exception e) {
			log.error("Error to parse JSONString to ParseShopTaskBean: "
					+ new String(bytes), e);
			return;
		}

		// 没有可用的 vps
		if (!this.getProvider().getIpPools().hasAvaliableIp(bean.getShopType())) {
			TaskQueues.getParseShopTaskQueue().add(bytes);
			return;
		}

		// 不做重复操作，防止把自个玩死
		if (!UrlCounters.add(bean.getReqFrom(), bean.getOuerUserId(), bean
				.getOuerShopId(), bean.getRequestUrl(), Config.instance()
				.getShopUrlCacheTimeout())) {
			log.warn("url duplicateb in UrlCounters, url={}, now={}",
					bean.getShopUrl(), df.format(date));
			return;
		}

		switch (bean.getParserType()) {
		case TAOBAO_ITEMLIST:
			parser = new TaobaoItemListParser(this.getProvider(), bean);
			break;
		case TMALL_ITEMLIST:
			parser = new TmallItemListParser(this.getProvider(), bean);
			break;
		default:
			parser = null;
		}

		if (parser == null) {
			log.error("can not find ShopParser, ignore:" + bean.toString());
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("take from ShopTaskQueue:{}", bean);
		}

		try {
			parser.parse();
		} catch (Exception e) {
			log.error("Error to parse shop's items by " + bean.toString(), e);
			return;
		}
	}
}
