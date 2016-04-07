/**
 * 
 */
package com.vdlm.spider.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;

import com.vdlm.spider.htmlparser.HtmlParserStatics;
import com.vdlm.spider.parser.config.ParserConfig;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:27:09 AM Jul 19, 2014
 */
public class NodeFilters {

	public static NodeFilter getOrCreateNodeFilter(Map<String, ParserConfig> configs) {
		final OrFilter filter = new OrFilter();

		final List<NodeFilter> filters = new ArrayList<NodeFilter>(configs.size());

		for (Map.Entry<String, ParserConfig> entry : configs.entrySet()) {
			if (entry.getValue().getIsChild()) {
				continue;
			}
			if (StringUtils.endsWith(entry.getKey(), "-extract")) {
				continue;
			}
			filters.add(getOrCreateNodeFilter(entry.getValue()));
		}

		filter.setPredicates(filters.toArray(HtmlParserStatics.NODE_FILTER));

		return filter;
	}

	public static NodeFilter getOrCreateNodeFilter(Collection<ParserConfig> configs) {
		final OrFilter filter = new OrFilter();

		final List<NodeFilter> filters = new ArrayList<NodeFilter>(configs.size());

		for (ParserConfig entry : configs) {
			filters.add(getOrCreateNodeFilter(entry));
		}

		filter.setPredicates(filters.toArray(HtmlParserStatics.NODE_FILTER));

		return filter;
	}

	public static NodeFilter getOrCreateNodeFilter(final ParserConfig config) {
		final NodeFilter f1 = new TagNameFilter(config.getTag());
		if (StringUtils.isBlank(config.getAttrsName())) {
			return f1;
		}

		//		final NodeFilter f2 = new HasAttributeFilter(config.getAttrsName(), value);

		final NodeFilter f2 = new NodeFilter() {

			private static final long serialVersionUID = -979507946689437650L;

			@Override
			public boolean accept(Node node) {
				return ParserUtils.match(node, config);
			}
		};

		return new AndFilter(f1, f2);
	}
}
