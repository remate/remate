package com.vdlm.proxycrawler.third;

import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.vdlm.proxycrawler.AbstractProxyCrawler;
import com.vdlm.proxycrawler.PageCrawlStrategy;

/**
 *
 * @author: chenxi
 */

public class XiciHomeProxyCrawler extends AbstractProxyCrawler {
	
	public XiciHomeProxyCrawler() throws Exception {
		super();
	}

	@Override
	protected void parse(String content, int page) {
		final Document doc = Jsoup.parse(content);
		
		final Element table = doc.getElementById("ip_list");
		final Elements trs = table.select("tr");
		String ip;
		int port;
		String type;
		Date checkTime = null;
		for (final Element tr : trs) {
			if (!tr.hasAttr("class") || tr.attr("class").equals("subtitle")) {
				continue;
			}
			type = tr.select("td").get(ThirdProxyConstants.XICI_HOME_TD_POS_TYPE).text();
			if (!"HTTP".equals(type)) {
				continue;
			}
			ip = tr.select("td").get(ThirdProxyConstants.XICI_HOME_TD_POS_IP).text();
			port = Integer.valueOf(tr.select("td")
					.get(ThirdProxyConstants.XICI_HOME_TD_POS_PORT).text());

			checkTime = new Date();
			super.sendProxy(ip, port, type, checkTime, 0);
		}
	}

	@Override
	protected String getThreadName() {
		return "xici home proxy crawler";
	}

	@Override
	protected String getProxyUrl(int page) {
		return ThirdProxyConstants.XICI_HOME_URL;
	}

	@Override
	protected String getProxyUrl() {
		return ThirdProxyConstants.XICI_HOME_URL;
	}

	@Override
	protected PageCrawlStrategy getPageCrawlStrategy() {
		return PageCrawlStrategy.ONCE;
	}

	@Override
	protected long getDelay() {
		return 60 * 60 * 24;
	}

	@Override
	protected int praseTotalPages(String content) {
		return 1;
	}

	@Override
	protected String getSource() {
		return "xici home";
	}

}
