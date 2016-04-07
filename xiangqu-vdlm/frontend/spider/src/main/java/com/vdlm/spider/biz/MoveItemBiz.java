/**
 * 
 */
package com.vdlm.spider.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vdlm.spider.Statics;
import com.vdlm.spider.bean.ParseItemTaskBean;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.parser.Parser;
import com.vdlm.spider.parser.item.TaobaoItemParser;
import com.vdlm.spider.parser.item.TmallItemParser;
import com.vdlm.spider.service.ItemService;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 9:43:44 AM Aug 13, 2014
 */
@Component
public class MoveItemBiz {

	protected Logger log = LoggerFactory.getLogger(getClass());

	private HttpClientProvider provider;

	private ItemService itemService;

	@Autowired
	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	@Autowired
	public void setProvider(HttpClientProvider provider) {
		this.provider = provider;
	}

	public Map<String, Object> move(final ParseItemTaskBean bean) {
		final Map<String, Object> result = new HashMap<String, Object>(5);
		final Map<String, Object> itemMap = new HashMap<String, Object>(5);

		bean.setRetry(false);

		Parser parser;
		switch (bean.getShopType()) {
		case TAOBAO:
			parser = new TaobaoItemParser(provider, bean, itemService) {
				@Override
				protected void persist(Item item, List<Sku> skus, List<Img> imgs) {
					super.persist(item, skus, imgs);
					MoveItemBiz.this.parse(itemMap, item, skus, imgs);
				}
			};
			break;
		case TMALL:
			parser = new TmallItemParser(provider, bean, itemService) {
				@Override
				protected void persist(Item item, List<Sku> skus, List<Img> imgs) {
					super.persist(item, skus, imgs);
					MoveItemBiz.this.parse(itemMap, item, skus, imgs);
				}
			};
			break;
		default:
			result.put("statusCode", 601);
			return result;
		}

		parser.parse();

		result.put("statusCode", 200);
		result.put("item", itemMap);

		return result;
	}

	/**
	 * <pre>
	 * 拿解析结果
	 * </pre>
	 * @param result
	 * @param item
	 * @param skus
	 * @param imgs
	 */
	void parse(Map<String, Object> result, Item item, List<Sku> skus, List<Img> imgs) {
		// 仅更新状态
		if (item.getStatus() != null && item.getStatus() == Statics.SOLD_OUT) {
			result.put("status", Statics.SOLD_OUT);
			return;
		}

		// 设置图片 ////////////////////////////////
		if (CollectionUtils.isEmpty(imgs)) {
			item.setImgs(StringUtils.EMPTY);
		}
		else {
			int size = 0;
			for (Img img : imgs) {
				size += img.getImgUrl().length();
			}
			final StringBuilder sb = new StringBuilder(size);
			final int lastIndex = imgs.size() - 1;
			for (int i = 0; i < lastIndex; i++) {
				sb.append(imgs.get(i).getImgUrl()).append(';');
			}
			sb.append(imgs.get(lastIndex).getImgUrl());

			item.setImgs(sb.toString());
		}
		//////////////////////////////////////////////

		result.put("shopId", item.getShopId());
		result.put("itemId", item.getItemId());
		result.put("itemUrl", item.getItemUrl());
		result.put("name", item.getName());
		result.put("price", item.getPrice());
		result.put("amount", item.getAmount());
		result.put("img", item.getImgs());
	}
}
