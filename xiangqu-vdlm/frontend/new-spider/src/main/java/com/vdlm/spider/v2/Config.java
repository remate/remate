package com.vdlm.spider.v2;

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
import com.vdlm.spider.InitializedException;
 
public class Config {

	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	private long taobaoShopPageId;
	private long taobaoItemListPageId;
	private long taobaoItemPageId;
	private long tmallShopPageId;
	private long tmallItemListPageId;
	private long tmallItemPageId;
	private String notifyType;
	private String submitUrl;
	private String notifyUrl;	
	private String taobaoItemListUrl;
	private String tmallItemListUrl;

	private static final Config HOLDER = new Config();
	
	static File file;
	static long lastModified;

	static {
		final URL url = Config.class.getResource("/spider2.0-config.properties");
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

			HOLDER.setTaobaoShopPageId(Integer.parseInt(StringUtils.defaultIfBlank(
					properties.getProperty("taobaoShopPageId"), "")));
			HOLDER.setTaobaoItemPageId(Integer.parseInt(StringUtils.defaultIfBlank(
					properties.getProperty("taobaoItemPageId"), "")));
			HOLDER.setTaobaoItemListPageId(Integer.parseInt(StringUtils.defaultIfBlank(
					properties.getProperty("taobaoItemListPageId"), "")));
			HOLDER.setTmallShopPageId(Integer.parseInt(StringUtils.defaultIfBlank(
					properties.getProperty("tmallShopPageId"), "")));
			HOLDER.setTmallItemListPageId(Integer.parseInt(StringUtils.defaultIfBlank(
					properties.getProperty("tmallItemListPageId"), "")));
			HOLDER.setTmallItemPageId(Integer.parseInt(StringUtils.defaultIfBlank(
					properties.getProperty("tmallItemPageId"), "")));
			HOLDER.setNotifyType(StringUtils.defaultIfBlank(
					properties.getProperty("notifyType"), "URL"));
			HOLDER.setSubmitUrl(StringUtils.defaultIfBlank(
					properties.getProperty("submitUrl"), ""));
			HOLDER.setNotifyUrl(StringUtils.defaultIfBlank(
					properties.getProperty("notifyUrl"),""));
			HOLDER.setTaobaoItemListUrl(StringUtils.defaultIfBlank(
					properties.getProperty("taobaoItemListUrl"), ""));
			HOLDER.setTmallItemListUrl(StringUtils.defaultIfBlank(
					properties.getProperty("tmallItemListUrl"),""));

			if (LOG.isDebugEnabled()) {
				LOG.debug("reload Config: " + JSON.toJSONString(HOLDER));
			}

			lastModified = file.lastModified();
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	public static Config instance() {
		return HOLDER;
	}

	public long getTaobaoShopPageId() {
		return taobaoShopPageId;
	}

	public void setTaobaoShopPageId(int taobaoShopPageId) {
		this.taobaoShopPageId = taobaoShopPageId;
	}

	public long getTaobaoItemPageId() {
		return taobaoItemPageId;
	}

	public void setTaobaoItemPageId(int taobaoItemPageId) {
		this.taobaoItemPageId = taobaoItemPageId;
	}

	public long getTmallShopPageId() {
		return tmallShopPageId;
	}

	public void setTmallShopPageId(int tmallShopPageId) {
		this.tmallShopPageId = tmallShopPageId;
	}

	public long getTmallItemPageId() {
		return tmallItemPageId;
	}

	public void setTmallItemPageId(int tmallItemPageId) {
		this.tmallItemPageId = tmallItemPageId;
	}

	public String getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}

	public String getSubmitUrl() {
		return submitUrl;
	}

	public void setSubmitUrl(String submitUrl) {
		this.submitUrl = submitUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	
	public long getTaobaoItemListPageId() {
		return taobaoItemListPageId;
	}

	public void setTaobaoItemListPageId(long taobaoItemListPageId) {
		this.taobaoItemListPageId = taobaoItemListPageId;
	}

	public long getTmallItemListPageId() {
		return tmallItemListPageId;
	}

	public void setTmallItemListPageId(long tmallItemListPageId) {
		this.tmallItemListPageId = tmallItemListPageId;
	}
	
	public String getTaobaoItemListUrl() {
		return taobaoItemListUrl;
	}

	public void setTaobaoItemListUrl(String taobaoItemListUrl) {
		this.taobaoItemListUrl = taobaoItemListUrl;
	}

	public String getTmallItemListUrl() {
		return tmallItemListUrl;
	}

	public void setTmallItemListUrl(String tmallItemListUrl) {
		this.tmallItemListUrl = tmallItemListUrl;
	}

}
