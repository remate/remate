/**
 * 
 */
package com.vdlm.spider.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.spider.DeviceType;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ResponseObject;
import com.vdlm.spider.ShopType;
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
import com.vdlm.spider.utils.MapTools;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 6:02:04 PM Sep 2, 2014
 */
@Controller
@RequestMapping("/item")
public class ItemController extends BaseController {

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

	/**
	 * <pre>
	 * 判断是否搬家过
	 * statusCode
	 * 5开头 - controller层抛出
	 * 6开头 - biz层抛出
	 * 7开头 - service层抛出
	 * 8开头 - dao层抛出
	 * 200 - 正常
	 * </pre>
	 * @param url
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/parse")
	public ResponseObject<Map<String, Object>> parse(@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "reqFrom", required = false, defaultValue = "1") Integer reqFromValue)
			throws UnsupportedEncodingException {
		if (log.isDebugEnabled()) {
			log.debug("url={}, reqFrom={}", url, reqFromValue);
		}

		final ReqFrom reqFrom;
		try {
			reqFrom = ReqFrom.valueOf(reqFromValue);
		}
		catch (Exception e) {
			log.error("invalid Request Parameters: reqFrom={}", reqFromValue);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 501));
		}

		final ShopType shopType = this.getShopTypeByUrl(url);
		if (shopType == null) {
			log.error("can not get ShopType, invalid Request Parameters: url={}", url);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 502));
		}

		final ParseItemTaskBean bean = new ParseItemTaskBean();
		bean.setOuerUserId("0");
		bean.setOuerShopId("0");
		bean.setShopType(shopType);
		bean.setDeviceType(DeviceType.OTHER);
		bean.setReqFrom(reqFrom);
		bean.setItemUrl(URLDecoder.decode(url, Statics.UTF8));
		bean.setRetry(false);//不需要重试

		final Map<String, Object> results = new HashMap<String, Object>();
		Parser parser = null;
		switch (shopType) {
		case TMALL:
			parser = new TmallItemParser(provider, bean, itemService) {
				@Override
				protected void persist(Item item, List<Sku> skus, List<Img> imgs) {
					ItemController.this.setResults(results, item, skus, imgs);
				}
			};
			break;
		default:
			parser = new TaobaoItemParser(provider, bean, itemService) {
				@Override
				protected void persist(Item item, List<Sku> skus, List<Img> imgs) {
					ItemController.this.setResults(results, item, skus, imgs);
				}
			};
		}

		parser.parse();

		final Map<String, Object> result = new HashMap<String, Object>();
		result.put("statusCode", 200);
		result.put("results", results);

		return new ResponseObject<Map<String, Object>>(result);
	}

	@SuppressWarnings("unchecked")
	void setResults(Map<String, Object> results, Item item, List<Sku> skus, List<Img> imgs) {
		if (skus == null) {
			skus = Collections.EMPTY_LIST;
		}
		if (imgs == null) {
			imgs = Collections.EMPTY_LIST;
		}

		// 下架商品
		if (item.getStatus() != null && item.getStatus() == Statics.SOLD_OUT) {
			results.put("item", item);
			results.put("skus", skus);
			results.put("imgs", imgs);
			return;
		}

		// 校验价格、sku信息 并 入库
		Double maxPrice = null;
		Integer amount = 0;
		for (Sku sku : skus) {
			if (sku.getAmount() == null) {
				sku.setAmount(99);
			}
			else {
				amount += sku.getAmount();
			}

			if (sku.getPrice() == null) {
				sku.setPrice(item.getPrice() == null ? 0 : item.getPrice());
			}
			else if (maxPrice == null || sku.getPrice() > maxPrice) {
				maxPrice = sku.getPrice();
			}
		}

		if (maxPrice != null) {
			item.setPrice(maxPrice);
		}
		if (item.getAmount() == null) {
			item.setAmount(amount);
		}

		if (item.getStatus() == null) {
			item.setStatus(Statics.NORMAL);
		}

		results.put("item", item);
		results.put("skus", skus);
		results.put("imgs", imgs);
	}
}
