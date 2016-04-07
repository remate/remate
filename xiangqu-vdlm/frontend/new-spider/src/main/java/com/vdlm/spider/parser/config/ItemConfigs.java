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
public class ItemConfigs {

	static final String CONFIG_FORMAT = "/config/item/%s.properties";

	// 新增类型请添加至数组末位
	static final String[] TYPES = { "taobao", "tmall" };

	static final Map<String, ItemConfig> CONFIGS = new HashMap<String, ItemConfig>();
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
						System.err.println("Error to reload ItemConfig:" + type);
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

	static ItemConfig create(Properties properties) {
		final ItemConfig config = new ItemConfig();
		config.setRemoteUrlStartWith(properties.getProperty("remoteUrlStartWith"));
		config.setRemotePriceKeyword(properties.getProperty("remotePriceKeyword"));
		config.setRemoteStockKeyword(properties.getProperty("remoteStockKeyword"));
		config.setRemotePriceKey(properties.getProperty("remotePriceKey"));
		config.setRemoteStockKey(properties.getProperty("remoteStockKey"));

		config.setRemoteDefaultPriceKeyword(properties.getProperty("remoteDefaultPriceKeyword"));
		config.setRemoteDefaultStockKeyword(properties.getProperty("remoteDefaultStockKeyword"));
		config.setRemoteDefaultPriceKey(properties.getProperty("remoteDefaultPriceKey"));
		config.setRemoteDefaultStockKey(properties.getProperty("remoteDefaultStockKey"));

		config.setPriceKeyword(properties.getProperty("priceKeyword"));
		config.setStockKeyword(properties.getProperty("stockKeyword"));
		config.setPriceKey(properties.getProperty("priceKey"));
		config.setStockKey(properties.getProperty("stockKey"));
		config.setSkuId(properties.getProperty("skuId"));

		config.setImgSize(Integer.parseInt(StringUtils.defaultIfBlank(properties.getProperty("imgSize"), "9")));

		config.setIdName(StringUtils.defaultIfBlank(properties.getProperty("idName"), "id"));
		config.setApiDescKey(properties.getProperty("apiDescKey"));

		assertConfig(config);

		return config;
	}

	static void assertConfig(ItemConfig config) {
		//TODO 检验信息合法性
	}

	static File getFile(String type) {
		final String name = String.format(CONFIG_FORMAT, type);
		final URL url = ItemConfigs.class.getResource(name);
		if (url == null) {
			throw new InitializedException("can not find local ItemConfig:" + name);
		}
		return new File(url.getFile());
	}

	static ItemConfig create(String type) {
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
			throw new InitializedException("Error to load ItemConfig from local:" + file.getAbsolutePath(), e);
		}
		finally {
			IOUtils.closeQuietly(reader);
		}

		final ItemConfig config = create(properties);

		CONFIGS.put(type, config);
		LAST_MODIFIEDS.put(type, lastModified);

		return config;
	}

	static ItemConfig getOrCreate(String type) {
		final ItemConfig config = CONFIGS.get(type);
		if (config != null) {
			return config;
		}

		return create(type);
	}

	public static ItemConfig getOrCreateTaobaoItemConfig() {
		return getOrCreate(TYPES[0]);
	}

	public static ItemConfig getOrCreateTmallItemConfig() {
		return getOrCreate(TYPES[1]);
	}

	public static ItemConfig getOrCreateItemConfig(ShopType shopType) {
		switch (shopType) {
		case TAOBAO:
			return getOrCreateTaobaoItemConfig();
		case TMALL:
			return getOrCreateTmallItemConfig();
		default:
			return null;
		}
	}
}
