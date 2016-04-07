package com.vdlm.proxycrawler.third;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

public class Daili5566ProxyCrawler extends AbstractProxyCrawler {

	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Daili5566ProxyCrawler() throws Exception {
		super();
	}
	
	@Override
	protected void parse(String content, int page) {
		final Document doc = Jsoup.parse(content);
		
		final Element table = doc.getElementsByAttributeValue("class", "table table-bordered table-striped").first();
		final Elements trs = table.select("tbody").first().select("tr");
		String ip;
		int port;
		String type;
		Date checkTime = null;
		for (final Element tr : trs) {
			type = tr.select("td").get(ThirdProxyConstants.DAILI5566_TD_POS_TYPE).text();
			if (!type.startsWith("HTTP")) {
				continue;
			}
			if (type.startsWith("HTTPS")) {
				continue;
			}
			ip = tr.select("td").get(ThirdProxyConstants.DAILI5566_TD_POS_IP).text();
			port = Integer.valueOf(tr.select("td")
					.get(ThirdProxyConstants.DAILI5566_TD_POS_PORT).text());

			try {
				checkTime = dateFormat.parse(tr.select("td").get(ThirdProxyConstants.DAILI5566_TD_POS_TIME).text());
				String checkTimeStr = checkTime.toString();
				if (checkTimeStr.startsWith("00")) {
					checkTimeStr = checkTimeStr.replaceFirst("00", "20");
					checkTime = dateFormat.parse(checkTimeStr);
				}
			} catch (final Throwable t) {
				LOG.warn("failed to parse check time", t.getMessage());
			}
			super.sendProxy(ip, port, type, checkTime, page);
		}
	}

	@Override
	protected String getThreadName() {
		return "daili5566 proxy crawler";
	}

	@Override
	protected String getProxyUrl(int page) {
		return ThirdProxyConstants.DAILI5566_URL + page;
	}

	@Override
	protected String getProxyUrl() {
		return ThirdProxyConstants.DAILI5566_URL;
	}

	@Override
	protected PageCrawlStrategy getPageCrawlStrategy() {
		return PageCrawlStrategy.SEQUENCE;
	}

	@Override
	protected long getDelay() {
		return 1;
	}

	@Override
	protected int praseTotalPages(String content) {
		final Document doc = Jsoup.parse(content);
		final Element listnav = doc.getElementById("listnav");
		final Element a = listnav.select("a").last();
		final String lastPageStr = a.attr("href");
		return Integer.valueOf(lastPageStr.substring(6));
	}

	@Override
	protected String getSource() {
		return "daili5566";
	}

}
