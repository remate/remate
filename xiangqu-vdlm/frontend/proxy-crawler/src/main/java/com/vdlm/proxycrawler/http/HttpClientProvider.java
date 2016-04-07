/**
 * 
 */
package com.vdlm.proxycrawler.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.proxycrawler.dao.ProxyDao;
import com.vdlm.proxycrawler.utils.InitializedException;
import com.vdlm.proxycrawler.utils.ShopType;
import com.vdlm.proxycrawler.utils.ThreadLocalRandom;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:46:09 AM Jul 19, 2014
 */
public class HttpClientProvider {

	private final Logger log = LoggerFactory.getLogger(getClass());

//	private IpPools ipPools;
	private List<String> userAgents;

	private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
	private String filePath;
	private final long lastModified = -1;
	CookieStoreProvider cookieStoreProvider;
	
	@Autowired
	private ProxyDao proxyDao;

	public void setCookieStoreProvider(CookieStoreProvider cookieStoreProvider) {
		this.cookieStoreProvider = cookieStoreProvider;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	String getFilePath() {
		return filePath;
	}

	List<String> loadFromConfig() {
		final File file = new File(this.getFilePath());
		if (file.lastModified() <= lastModified) {
			if (log.isDebugEnabled()) {
				log.debug("{} has not changed", this.getFilePath());
			}
			return null;
		}

		try {
			return FileUtils.readLines(file);
		}
		catch (final IOException e) {
			log.error("Error to read from " + file.getAbsolutePath(), e);
			return null;
		}
	}

	// must call this method once for initializing
	public HttpClientProvider init() {
		if (log.isInfoEnabled()) {
			log.info("start to init " + this.getClass().getSimpleName());
		}

		if (StringUtils.isBlank(this.filePath)) {
			final URL url = this.getClass().getResource("/http/user-agent.txt");
			if (url == null) {
				throw new InitializedException("can not find user-agent.txt");
			}

			this.filePath = url.getFile();
		}

		// 首次初始化
		this.userAgents = this.loadFromConfig();
		if (this.userAgents == null || this.userAgents.isEmpty()) {
			throw new InitializedException("Nothing to find from " + this.filePath);
		}

		this.exec.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				final List<String> userAgents = HttpClientProvider.this.loadFromConfig();
				if (userAgents != null && !userAgents.isEmpty()) {
					synchronized (HttpClientProvider.this) {
						HttpClientProvider.this.userAgents = userAgents;
					}
				}
			}
		}, 15, 15, TimeUnit.SECONDS);

		if (log.isInfoEnabled()) {
			log.info("success to init " + this.getClass().getSimpleName());
		}
		return this;
	}

	// must call this method once for destroying
	public void destroy() {
		if (log.isInfoEnabled()) {
			log.info("start to destroy " + this.getClass().getSimpleName());
		}

		this.exec.shutdown();

		if (log.isInfoEnabled()) {
			log.info("success to destroy " + this.getClass().getSimpleName());
		}
	}

//	public IpPools getIpPools() {
//		return ipPools;
//	}
//
//	public void setIpPools(IpPools ipPools) {
//		this.ipPools = ipPools;
//	}

	public HttpClientInvoker provide(final String url, final HttpClientInvoker refer) {
		return this.provide(refer.getShopType(), url, refer.getUrl(), refer.getIp(), refer.getUserAgent());
	}

//	public HttpClientInvoker provide(final ShopType shopType, final String url) {
//		return this.provide(shopType, url, null);
//	}

//	public HttpClientInvoker provideFromDB(final ShopType shopType, final String url, final String refer) {
//		final ProxyStatus status = (ShopType.TMALL.equals(shopType) ? ProxyStatus.TMALL_ACCEPT : ProxyStatus.TAOBAO_ACCEPT);
//		final Proxy proxy = proxyDao.queryForOneProxy(status);
//		if (proxy == null) {
//			return this.provide(shopType, url, refer);
//		}
//		
//		final String ip = proxy.getIp() + ":" + proxy.getPort();
//		return this.provide(shopType, url, refer, ip);
//	}

	public HttpClientInvoker provide(final ShopType shopType, final String url, final String refer, final String ip) {
		final String userAgent = this.getUserAgent();

		return this.provide(shopType, url, refer, ip, userAgent);
	}

	public HttpClientInvoker provide(final ShopType shopType, final String url, final String refer, final String ip, final String userAgent) {
		final HttpClientHolder httpClient = new PoolingHttpClientHolder(ip, userAgent, refer);

		final HttpClientInvoker invoker = new HttpClientInvoker();
		invoker.setProvider(this);
		invoker.setHttpClient(httpClient);
		invoker.setShopType(shopType);
		invoker.setUrl(url);
		invoker.setRefer(refer);
		invoker.setIp(ip);
		invoker.setUserAgent(userAgent);
		invoker.setCookieStoreProvider(this.cookieStoreProvider);

		return invoker;
	}

	String getUserAgent() {
		synchronized (this) {
			return this.userAgents.get(ThreadLocalRandom.current().nextInt(this.userAgents.size()));
		}
	}

	/**
	 * <pre>
	 * 做流控规则限制
	 * </pre>
	 * @param invoker
	 */
//	public void disable(HttpClientInvoker invoker) {
//		// 目前仅禁用ip
//		this.ipPools.disableIp(invoker.getShopType(), invoker.getIp());
//	}
//	
//	public HttpClientInvoker provide(final ShopType shopType, final String url, final String refer) {
//		final String ip = this.ipPools.getAvaliableIp(shopType);
//
//		return this.provide(shopType, url, refer, ip);
//	}
}
