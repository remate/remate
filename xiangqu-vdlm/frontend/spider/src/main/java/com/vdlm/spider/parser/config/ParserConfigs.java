/**
 * 
 */
package com.vdlm.spider.parser.config;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.ini4j.Ini;
import org.ini4j.Profile;

import com.vdlm.spider.InitializedException;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 12:58:39 PM Jul 18, 2014
 */
public class ParserConfigs {

	static final String CONFIG_FORMAT = "/parser/%s.inf";

	// 新增类型请添加至数组末位
	static final String[] TYPES = { "taobao-item", "taobao-shop", "tmall-item", "tmall-shop", "taobao-shop-info",
			"tmall-shop-info", "taobao-auth", "tmall-auth" };

	static final Map<String, Map<String, ParserConfig>> CONFIGS = new HashMap<String, Map<String, ParserConfig>>();
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
						System.err.println("Error to reload Config:" + type);
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

	static ParserConfig create(Profile.Section section) {
		final ParserConfig config = new ParserConfig();
		config.setProp(section.get("prop"));
		try {
			config.setTag(String.valueOf(section.get("tag")));
		}
		catch (Exception e) {
			throw new InitializedException("not exist Tag >> " + section.toString());
		}
		final String isChild = section.get("is-child");
		if (StringUtils.isBlank(isChild)) {
			config.setIsChild(Boolean.FALSE);
		}
		else {
			try {
				config.setIsChild(Boolean.valueOf(isChild));
			}
			catch (Exception e) {
				config.setIsChild(Boolean.FALSE);
			}
		}

		config.setAttrsName(section.get("attrs-name"));
		config.setAttrsValue(section.get("attrs-value"));
		config.setExtractAttr(section.get("extract-attr"));
		config.setExtractKw(section.get("extract-kw"));

		assertConfig(config);

		return config;
	}

	static void assertConfig(ParserConfig config) {
		//TODO 检验信息合法性
	}

	static File getFile(String type) {
		final String name = String.format(CONFIG_FORMAT, type);
		final URL url = ParserConfigs.class.getResource(name);
		if (url == null) {
			throw new InitializedException("can not find local config:" + name);
		}
		return new File(url.getFile());
	}

	@SuppressWarnings("unchecked")
	static Map<String, ParserConfig> create(String type) {
		final File file = getFile(type);
		// 判断文件是否有变化
		final long lastModified = file.lastModified();
		final Long lm = LAST_MODIFIEDS.get(type);
		// 文件没变化，无需加载配置
		if (lm != null && lm >= lastModified) {
			return Collections.EMPTY_MAP;
		}

		Ini conf;
		try {
			conf = new Ini(file);
		}
		catch (Exception e) {
			throw new RuntimeException("can not create Ini4j by File:" + file.getAbsolutePath());
		}

		final Map<String, ParserConfig> configs = new HashMap<String, ParserConfig>();

		for (Map.Entry<String, Profile.Section> entry : conf.entrySet()) {
			final ParserConfig config = create(entry.getValue());
			config.setIndex(entry.getKey());
			configs.put(entry.getKey(), config);
		}

		CONFIGS.put(type, configs);
		LAST_MODIFIEDS.put(type, lastModified);

		return configs;
	}

	static Map<String, ParserConfig> getOrCreate(String type) {
		final Map<String, ParserConfig> configs = CONFIGS.get(type);
		if (configs != null) {
			return configs;
		}

		return create(type);
	}

	public static Map<String, ParserConfig> getOrCreateTaobaoItemConfig() {
		return getOrCreate(TYPES[0]);
	}

	public static Map<String, ParserConfig> getOrCreateTaobaoShopConfig() {
		return getOrCreate(TYPES[1]);
	}

	public static Map<String, ParserConfig> getOrCreateTmallItemConfig() {
		return getOrCreate(TYPES[2]);
	}

	public static Map<String, ParserConfig> getOrCreateTmallShopConfig() {
		return getOrCreate(TYPES[3]);
	}

	public static Map<String, ParserConfig> getOrCreateTaobaoShopInfoConfig() {
		return getOrCreate(TYPES[4]);
	}

	public static Map<String, ParserConfig> getOrCreateTmallShopInfoConfig() {
		return getOrCreate(TYPES[5]);
	}

	public static Map<String, ParserConfig> getOrCreateTaobaoAuthConfig() {
		return getOrCreate(TYPES[6]);
	}

	public static Map<String, ParserConfig> getOrCreateTmallAuthConfig() {
		return getOrCreate(TYPES[7]);
	}
}
