/**
 * 
 */
package com.vdlm.spider.parser.config;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.vdlm.spider.InitializedException;
import com.vdlm.spider.ShopType;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 12:23:11 AM Jul 20, 2014
 */
public class ItemListConfigs {

	static final String CONFIG_FORMAT = "/config/itemlist/%s.properties";

	// 新增类型请添加至数组末位
	static final String[] TYPES = { "taobao", "tmall" };

	static final Map<String, ItemListConfig> CONFIGS = new HashMap<String, ItemListConfig>();
	static final Map<String, Long> LAST_MODIFIEDS = new HashMap<String, Long>();

	static {
		for (String type : TYPES) {
			create(type);
		}

		final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

		exec.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				for (String type : TYPES) {
					try {
						create(type);
					}
					catch (Exception e) {
						System.err.println("Error to reload ItemListConfig:" + type);
					}
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

	static ItemListConfig create(Properties properties) {
		final ItemListConfig config = new ItemListConfig();
		config.setUrl0(properties.getProperty("url0"));
		config.setUrl1(properties.getProperty("url1"));
		config.setUrl2(properties.getProperty("url2"));
		config.setSuccessStatusCode(StringUtils.defaultIfBlank(properties.getProperty("successStatusCode"), "200"));
		config.setRefererUrl(properties.getProperty("refererUrl"));

		config.setStatusCode(properties.getProperty("statusCode"));
		config.setTotalPage(properties.getProperty("totalPage"));
		config.setCurrentPage(properties.getProperty("currentPage"));
		config.setPageSize(properties.getProperty("pageSize"));
		config.setItemList(properties.getProperty("itemList"));
		config.setItemUrl(properties.getProperty("itemUrl"));
		config.setItemId(properties.getProperty("itemId"));
		config.setAuctions(properties.getProperty("auctions"));
		config.setAuctionId(properties.getProperty("auctionId"));

		assertConfig(config);

		return config;
	}

	static void assertConfig(ItemListConfig config) {
		//TODO 检验信息合法性
	}

	static File getFile(String type) {
		final String name = String.format(CONFIG_FORMAT, type);
		final URL url = ItemListConfigs.class.getResource(name);
		if (url == null) {
			throw new InitializedException("can not find local ItemListConfig:" + name);
		}
		return new File(url.getFile());
	}

	static ItemListConfig create(String type) {
		final File file = getFile(type);
		// 判断文件是否有变化
		final long lastModified = file.lastModified();
		final Long lm = LAST_MODIFIEDS.get(type);
		// 文件没变化，无需加载配置
		if (lm != null && lm >= lastModified) {
			return CONFIGS.get(type);
		}

		final Properties properties = new Properties();
		Reader reader = null;
		try {
			reader = new FileReader(file);
			properties.load(reader);
		}
		catch (Exception e) {
			throw new InitializedException("Error to load ItemListConfig from local:" + file.getAbsolutePath(), e);
		}
		finally {
			IOUtils.closeQuietly(reader);
		}

		final ItemListConfig config = create(properties);

		CONFIGS.put(type, config);
		LAST_MODIFIEDS.put(type, lastModified);

		return config;
	}

	static ItemListConfig getOrCreate(String type) {
		final ItemListConfig config = CONFIGS.get(type);
		if (config != null) {
			return config;
		}

		return create(type);
	}

	public static ItemListConfig getOrCreateTaobaoItemListConfig() {
		return getOrCreate(TYPES[0]);
	}

	public static ItemListConfig getOrCreateTmallItemListConfig() {
		return getOrCreate(TYPES[1]);
	}

	public static ItemListConfig getOrCreateItemListConfig(ShopType shopType) {
		switch (shopType) {
		case TAOBAO:
			return getOrCreateTaobaoItemListConfig();
		case TMALL:
			return getOrCreateTmallItemListConfig();
		default:
			return null;
		}
	}
}
