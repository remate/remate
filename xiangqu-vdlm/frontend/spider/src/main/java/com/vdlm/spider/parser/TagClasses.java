/**
 * 
 */
package com.vdlm.spider.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 1:21:54 PM Jul 18, 2014
 */
public class TagClasses {

	static final Logger LOG = LoggerFactory.getLogger(TagClasses.class);

	static final Map<String, Class<?>> CLASSES = new HashMap<String, Class<?>>();

	static {
		loadTagsFromLocal();
	}

	static void loadTagsFromLocal() {
		final Properties props = new Properties();
		try {
			props.load(TagClasses.class.getResourceAsStream("/parser/tag-class.properties"));

			for (Map.Entry<Object, Object> entry : props.entrySet()) {

				if (LOG.isInfoEnabled()) {
					LOG.info("{} = {}", entry.getKey(), entry.getValue());
				}

				CLASSES.put(entry.getKey().toString(), Class.forName(entry.getValue().toString()));
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Error to load Tags from local", e);
		}
	}

	public static Class<?> getTagClass(String tag) {
		return CLASSES.get(tag);
	}
}
