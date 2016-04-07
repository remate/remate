package com.vdlm.proxycrawler.third;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Sets;
import com.vdlm.proxycrawler.AbstractProxyCrawler;
import com.vdlm.proxycrawler.PageCrawlStrategy;

/**
 *
 * @author: chenxi
 */

public class XiciNnProxyCrawler extends AbstractProxyCrawler {

	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private final Set<Integer> pages = Sets.newHashSet();
	
	public XiciNnProxyCrawler() throws Exception {
		super();
	}
	
	@Override
	protected void parse(String content, int page) {
		final Document doc = Jsoup.parse(content);
		final Element curPage = doc.select(".current").first();
		int realPage = 0;
		if (curPage != null) {
			realPage = Integer.valueOf(curPage.text());
			if (pages.contains(realPage)) {
				LOG.warn("the page {} has been already parsed", realPage);
				return;
			}
		}
		
		final Element table = doc.select("table").first();
		final Elements trs = table.select("tr");
		String ip;
		int port;
		String type;
		Date checkTime = null;
		for (final Element tr : trs) {
			if (!tr.hasAttr("class")) {
				continue;
			}
			type = tr.select("td").get(ThirdProxyConstants.XICI_TD_POS_TYPE).text();
			if (!"HTTP".equals(type)) {
				continue;
			}
			ip = tr.select("td").get(ThirdProxyConstants.XICI_TD_POS_IP).text();
			port = Integer.valueOf(tr.select("td")
					.get(ThirdProxyConstants.XICI_TD_POS_PORT).text());

			try {
				checkTime = dateFormat.parse(tr.select("td").last().text());
				String checkTimeStr = checkTime.toString();
				if (checkTimeStr.startsWith("00")) {
					checkTimeStr = checkTimeStr.replaceFirst("00", "20");
					checkTime = dateFormat.parse(checkTimeStr);
				}
			} catch (final Throwable t) {
				LOG.warn("failed to parse check time", t.getMessage());
			}
			super.sendProxy(ip, port, type, checkTime, realPage);
		}
	}

	@Override
	protected String getThreadName() {
		return "xici nn proxy crawler";
	}

	@Override
	protected String getProxyUrl(int page) {
		return ThirdProxyConstants.XICI_NN_URL + page;
	}

	@Override
	protected String getProxyUrl() {
		return ThirdProxyConstants.XICI_NN_URL;
	}

	@Override
	protected PageCrawlStrategy getPageCrawlStrategy() {
		return PageCrawlStrategy.RANDOM;
	}

	@Override
	protected long getDelay() {
		return 1;
	}

	@Override
	protected int praseTotalPages(String content) {
		final Document doc = Jsoup.parse(content);
		final Element pagination = doc.getElementsByAttributeValue("class", "pagination").first();
		final int aSize = pagination.select("a").size();
		return Integer.valueOf(pagination.select("a").get(aSize - 2).text());
	}
	
	@Override
	protected String getSource() {
		return "xici nn";
	}
	
}
