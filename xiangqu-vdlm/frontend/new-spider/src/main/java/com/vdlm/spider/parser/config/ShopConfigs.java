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
public class ShopConfigs {

	static final String CONFIG_FORMAT = "/config/shop/%s.properties";

	// 新增类型请添加至数组末位
	static final String[] TYPES = { "taobao", "tmall" };

	static final Map<String, ShopConfig> CONFIGS = new HashMap<String, ShopConfig>();
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

	static ShopConfig create(Properties properties) {
		final ShopConfig config = new ShopConfig();
		config.setSearchUrl(properties.getProperty("searchUrl"));
		config.setIndexUrl(properties.getProperty("indexUrl"));
		config.setItemUrl(properties.getProperty("itemUrl"));
		config.setItemUrlStartsWith(properties.getProperty("itemUrlStartsWith"));
		config.setUrlEndsWith(properties.getProperty("urlEndsWith"));

		final String ignoreParameters = properties.getProperty("ignoreParameters");

		if (StringUtils.isNotBlank(ignoreParameters)) {
			config.setIgnoreParameters(ignoreParameters.split(","));
		}

		config.setMaxLevel(Integer.parseInt(properties.getProperty("maxLevel")));
		config.setCharset(properties.getProperty("charset"));
		config.setSmsTpl(properties.getProperty("smsTpl"));
		config.setPushTplTitle(properties.getProperty("pushTplTitle"));
		config.setPushTplContent(properties.getProperty("pushTplContent"));
		config.setPushTplImg(properties.getProperty("pushTplImg"));

		assertConfig(config);

		return config;
	}

	static void assertConfig(ShopConfig config) {
		//TODO 检验信息合法性
	}

	static File getFile(String type) {
		final String name = String.format(CONFIG_FORMAT, type);
		final URL url = ShopConfigs.class.getResource(name);
		if (url == null) {
			throw new InitializedException("can not find local ItemConfig:" + name);
		}
		return new File(url.getFile());
	}

	static ShopConfig create(String type) {
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

		final ShopConfig config = create(properties);

		CONFIGS.put(type, config);
		LAST_MODIFIEDS.put(type, lastModified);

		return config;
	}

	static ShopConfig getOrCreate(String type) {
		final ShopConfig config = CONFIGS.get(type);
		if (config != null) {
			return config;
		}

		return create(type);
	}

	public static ShopConfig getOrCreateTaobaoShopConfig() {
		return getOrCreate(TYPES[0]);
	}

	public static ShopConfig getOrCreateTmallShopConfig() {
		return getOrCreate(TYPES[1]);
	}

	public static ShopConfig getOrCreateShopConfig(ShopType shopType) {
		switch (shopType) {
		case TAOBAO:
			return getOrCreateTaobaoShopConfig();
		case TMALL:
			return getOrCreateTmallShopConfig();
		default:
			return null;
		}
	}
}
