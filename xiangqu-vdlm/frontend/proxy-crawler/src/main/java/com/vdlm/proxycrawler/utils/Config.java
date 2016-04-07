/**
 * 
 */
package com.vdlm.proxycrawler.utils;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:34:37 AM Jul 21, 2014
 */
public class Config {

	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	private int retryTimes;
	private long retryTimeout;

	private int vpsSize;
	private long vpsTimeout;

	private long cacheTimeout;
	private long shopUrlCacheTimeout;
	private long notifyThreshold;

	private String defaultImg;
	private long cacheOptTimeout;

	private int url2StartSize;
	private int url2EndSize;

	private static final Config HOLDER = new Config();

	static File file;
	static long lastModified;

	static {
		final URL url = Config.class.getResource("/config.properties");
		if (url == null) {
			throw new InitializedException("can not find config.properties");
		}

		file = new File(url.getFile());

		try {
			reload();
		} catch (Exception e) {
			throw new InitializedException("Error to load from "
					+ file.getAbsolutePath(), e);
		}

		final ScheduledExecutorService exec = Executors
				.newSingleThreadScheduledExecutor();

		exec.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				try {
					reload();
				} catch (Exception e) {
					LOG.warn(
							"Error to reload Config from "
									+ file.getAbsolutePath(), e);
				}
			}
		}, 15, 15, TimeUnit.SECONDS);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				exec.shutdown();
			}
		}));
	}

	static void reload() throws Exception {
		if (file.lastModified() <= lastModified) {
			return;
		}

		final Properties properties = new Properties();

		FileReader reader = null;
		try {
			reader = new FileReader(file);
			properties.load(reader);

			HOLDER.setRetryTimes(Integer.parseInt(StringUtils.defaultIfBlank(
					properties.getProperty("retryTimes"), "5")));
			HOLDER.setRetryTimeout(Long.parseLong(StringUtils.defaultIfBlank(
					properties.getProperty("retryTimeout"), "86400000")));
			HOLDER.setVpsTimeout(Long.parseLong(StringUtils.defaultIfBlank(
					properties.getProperty("vpsTimeout"), "1800000")));
			HOLDER.setCacheTimeout(Long.parseLong(StringUtils.defaultIfBlank(
					properties.getProperty("cacheTimeout"), "86400000")));
			HOLDER.setShopUrlCacheTimeout(Long.parseLong(StringUtils
					.defaultIfBlank(
							properties.getProperty("shopUrlCacheTimeout"),
							"1800000")));
			HOLDER.setNotifyThreshold(Long.parseLong(StringUtils
					.defaultIfBlank(properties.getProperty("notifyThreshold"),
							"300000")));

			HOLDER.setVpsSize(Integer.parseInt(StringUtils.defaultIfBlank(
					properties.getProperty("vpsSize"), "1")));

			HOLDER.setDefaultImg(properties.getProperty("defaultImg"));

			HOLDER.setCacheOptTimeout(Long.parseLong(StringUtils
					.defaultIfBlank(properties.getProperty("cacheOptTimeout"),
							"5000")));

			HOLDER.setUrl2StartSize(Integer.parseInt(StringUtils
					.defaultIfBlank(properties.getProperty("url2StartSize"),
							"0")));
			HOLDER.setUrl2EndSize(Integer.parseInt(StringUtils.defaultIfBlank(
					properties.getProperty("url2EndSize"), "127")));

			if (LOG.isInfoEnabled()) {
				LOG.info("reload Config: " + JSON.toJSONString(HOLDER));
			}

			lastModified = file.lastModified();
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	public static Config instance() {
		return HOLDER;
	}

	public long getShopUrlCacheTimeout() {
		return shopUrlCacheTimeout;
	}

	public void setShopUrlCacheTimeout(long shopUrlCacheTimeout) {
		this.shopUrlCacheTimeout = shopUrlCacheTimeout;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public long getRetryTimeout() {
		return retryTimeout;
	}

	public void setRetryTimeout(long retryTimeout) {
		this.retryTimeout = retryTimeout;
	}

	public long getVpsTimeout() {
		return vpsTimeout;
	}

	public void setVpsTimeout(long vpsTimeout) {
		this.vpsTimeout = vpsTimeout;
	}

	public long getCacheTimeout() {
		return cacheTimeout;
	}

	public void setCacheTimeout(long cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}

	public int getVpsSize() {
		return vpsSize;
	}

	public void setVpsSize(int vpsSize) {
		this.vpsSize = vpsSize;
	}

	public String getDefaultImg() {
		return defaultImg;
	}

	public void setDefaultImg(String defaultImg) {
		this.defaultImg = defaultImg;
	}

	public long getCacheOptTimeout() {
		return cacheOptTimeout;
	}

	public void setCacheOptTimeout(long cacheOptTimeout) {
		this.cacheOptTimeout = cacheOptTimeout;
	}

	public long getNotifyThreshold() {
		return notifyThreshold;
	}

	public void setNotifyThreshold(long notifyThreshold) {
		this.notifyThreshold = notifyThreshold;
	}

	public int getUrl2StartSize() {
		return url2StartSize;
	}

	public void setUrl2StartSize(int url2StartSize) {
		this.url2StartSize = url2StartSize;
	}

	public int getUrl2EndSize() {
		return url2EndSize;
	}

	public void setUrl2EndSize(int url2EndSize) {
		this.url2EndSize = url2EndSize;
	}
}
