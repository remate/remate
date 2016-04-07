package com.vdlm.spider.task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import com.google.common.collect.Maps;

/**
 *
 * @author: chenxi
 */

public class CrawlableTaskRetryManager {

	private final static String RETRY_CONFIG_FILE = "retry.properties";
	
	private final static String SHOP_RETRY_TIMES = "shop.retry.times";
	private final static String ITEMLIST_RETRY_TIMES = "itemlist.retry.times";
	private final static String ITEM_RETRY_TIMES = "item.retry.times";
	private final static String SKU_RETRY_TIMES = "sku.retry.times";
	private final static String DESC_RETRY_TIMES = "desc.retry.times";
	private final static String IMG_RETRY_TIMES = "img.retry.times";
	// w
	private final static String WITEM_RETRY_TIMES = "witem.retry.times";
	private final static String WDESC_RETRY_TIMES = "wdesc.retry.times";
	
	private final Map<String, Integer> retries = Maps.newHashMap();
	
	@PostConstruct
	public void init() {
		final Properties properties = new Properties();
        final InputStream resource = Thread.currentThread()
                                     .getContextClassLoader()
                                     .getResourceAsStream(RETRY_CONFIG_FILE);
        try {
            properties.load(resource);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load " + RETRY_CONFIG_FILE + " under classpath", e);
        }
        
        retries.put(CrawlShopTask.class.getName(), Integer.valueOf(properties.getProperty(SHOP_RETRY_TIMES)));
        retries.put(CrawlItemListTask.class.getName(), Integer.valueOf(properties.getProperty(ITEMLIST_RETRY_TIMES)));
        retries.put(CrawlItemTask.class.getName(), Integer.valueOf(properties.getProperty(ITEM_RETRY_TIMES)));
        retries.put(CrawlSkuTask.class.getName(), Integer.valueOf(properties.getProperty(SKU_RETRY_TIMES)));
        retries.put(CrawlDescTask.class.getName(), Integer.valueOf(properties.getProperty(DESC_RETRY_TIMES)));
        retries.put(CrawlImgTask.class.getName(), Integer.valueOf(properties.getProperty(IMG_RETRY_TIMES)));
        // w
        retries.put(CrawlWItemTask.class.getName(), Integer.valueOf(properties.getProperty(WITEM_RETRY_TIMES)));
        retries.put(CrawlWDescTask.class.getName(), Integer.valueOf(properties.getProperty(WDESC_RETRY_TIMES)));
	}
	
	public int getMaxRetryTime(String taskName) {
		return retries.get(taskName);
	}
}
