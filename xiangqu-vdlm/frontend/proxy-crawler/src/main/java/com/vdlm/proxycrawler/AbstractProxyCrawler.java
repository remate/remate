package com.vdlm.proxycrawler;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.proxycrawler.utils.NamedThreadFactory;

/**
 *
 * @author: chenxi
 */

public abstract class AbstractProxyCrawler {

	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private BusSignalManager bsm;
	
	private final ScheduledExecutorService crawlProxyPool;
	
	public AbstractProxyCrawler() throws Exception {
		crawlProxyPool = Executors.newScheduledThreadPool(1, new NamedThreadFactory(getThreadName()));
	    crawlProxyPool.scheduleWithFixedDelay(new CrawlProxyTask(getPageCrawlStrategy(), crawlTotalPages(getProxyUrl())), 0, getDelay(), TimeUnit.SECONDS);
	}
	
	class CrawlProxyTask implements Runnable {

		private final PageCrawlStrategy pcs;
		private int tryPage = 1;
		private final int totalPages;
		
		CrawlProxyTask(PageCrawlStrategy pcs, int totalPages) {
			this.pcs = pcs;
			this.totalPages = totalPages;
		}
		
		@Override
		public void run() {
			LOG.info("try to crawl page {}...", tryPage);
			try {
				AbstractProxyCrawler.this.crawl(tryPage);
			} catch (final Exception e) {
				LOG.error("crawl page " + tryPage + " failed", e);
			}
			LOG.info("end crawling page {}", tryPage);
			nextPage();
		}
		
		private void nextPage() {
			if (PageCrawlStrategy.SEQUENCE.equals(pcs)) {
				if (tryPage == totalPages) {
					tryPage = 1;
				} else {
					tryPage++;
				}
			} else if (PageCrawlStrategy.RANDOM.equals(pcs)) {
				final Random random = new Random();
				tryPage = random.nextInt(totalPages);
			} else if (PageCrawlStrategy.ONCE.equals(pcs)) {
				// TODO
			}
		}
	}
	
	public void crawl(int page) throws Exception {
		final HttpClient client = new DefaultHttpClient();  
		final HttpGet method = new HttpGet(getProxyUrl(page));
		final HttpResponse response = client.execute(method);
		if (response.getStatusLine().getStatusCode() != 200) {
			LOG.warn("got status code {}", response.getStatusLine().getStatusCode());
			return;
		}
		final HttpEntity respEntity = response.getEntity();
		final String content = EntityUtils.toString(respEntity);
		EntityUtils.consume(respEntity);
		parse(content, page);
	}
	
	public int crawlTotalPages(String url) throws Exception {
		final HttpClient client = new DefaultHttpClient();  
		final HttpGet method = new HttpGet(url);
		final HttpResponse response = client.execute(method);
		if (response.getStatusLine().getStatusCode() != 200) {
			LOG.warn("got status code {}", response.getStatusLine().getStatusCode());
			return 0;
		}
		final HttpEntity respEntity = response.getEntity();
		final String content = EntityUtils.toString(respEntity);
		EntityUtils.consume(respEntity);
		return praseTotalPages(content);
	}
	
	protected void sendProxy(String ip, int port, String type, Date checkTime, int page) {
		final Proxy proxy = new Proxy();
		proxy.setIp(ip);
		proxy.setPort(port);
		proxy.setType(type);
		proxy.setCheckTime(checkTime);
		proxy.setCurrentPage(page);
		proxy.setSource(getSource());
		bsm.signal(proxy);
	}
	
	protected abstract void parse(String content, int page);
	protected abstract String getSource();
	protected abstract String getThreadName();
	protected abstract String getProxyUrl();
	protected abstract String getProxyUrl(int page);
	
	protected abstract PageCrawlStrategy getPageCrawlStrategy();
	protected abstract long getDelay();
	protected abstract int praseTotalPages(String content);
}
