package com.vdlm.spider.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.spider.DeviceType;
import com.vdlm.spider.ParserType;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ResponseObject;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.SpideItemType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.bean.WItemTaskBean;
import com.vdlm.spider.dao.ItemDao;
import com.vdlm.spider.dao.ItemListTaskDao;
import com.vdlm.spider.dao.ItemProcessDao;
import com.vdlm.spider.dao.ItemTaskDao;
import com.vdlm.spider.dao.ShopDao;
import com.vdlm.spider.dao.ShopTaskDao;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.event.ItemCompletedEvent;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.task.TaskType;
import com.vdlm.spider.utils.MapTools;
import com.vdlm.spider.utils.ObjectConvertUtils;
import com.vdlm.spider.wdetail.ItemResponse;
import com.vdlm.spider.wdetail.parser.ApiStackValueParser;
import com.vdlm.spider.wdetail.parser.WItem;

/**
 *
 * @author: chenxi
 */

@Controller
//@RequestMapping("/moveItem")
public class MoveItemController extends BaseController {

	@Autowired
	private BusSignalManager bsm;
	
	// for test
	@Autowired
	private ShopTaskDao shopTaskDao;
	@Autowired
	private ItemListTaskDao itemListTaskDao;
	@Autowired
	private ItemTaskDao itemTaskDao;
	@Autowired
	private ShopDao shopDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemProcessDao itemProcessDao;
	@Autowired
	private HttpClientProvider provider;
	
	
	/*
	 * 手工搬家接口
	 */
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/moveItem/wtest")
	public ResponseObject<Map<String, Object>> moveWItem(@RequestParam(value = "itemId", required = true) String itemId,
			@RequestParam(value = "ouerUserId", required = false) String ouerUserId,
			@RequestParam(value = "ouerShopId", required = true) String ouerShopId,
			@RequestParam(value = "shopType", required = true, defaultValue = "1") int shopType,
			@RequestParam(value = "option", required = false, defaultValue = "1") int option) {
		prepare(ouerShopId, itemId);
		
		final Long shopId = shopDao.queryForShopId(ouerShopId);
		if (shopId == null) {
			log.warn("cannot find shop with ouer shop id:" + ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 700, 
						"msg", "店铺搬家记录不存在, 请确保该店铺已操作过搬家操作"));
		}
		if (shopTaskDao.exist(ouerShopId) != null) {
			log.warn("shop task: {} is already exist.", ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 700,
						"msg", "该用户已有店铺搬家记录正在进行中, 请稍后再试"));
		}
		if (itemListTaskDao.exist(ouerShopId) != null) {
			log.warn("itemlist task: {} is already exist.", ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 700,
						"msg", "该用户已有商品列表搬家记录正在进行中, 请稍后再试"));
		}
		if (itemTaskDao.exist(ouerShopId, itemId) != null) {
			log.warn("item task: {} is already exist.", itemId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 700,
						"msg", "该用户该商品正在搬家进行中, 请稍后再试"));
		}
		// 手动搬家指定商品支持新增商品
		boolean add = false;
		final Long id = itemDao.exist(ouerShopId, itemId);
		if (id == null ) {
			add = true;
		} else if (itemProcessDao.exist(id) == null) {
			add = true;
		}
		
		final WItemTaskBean bean = new WItemTaskBean();
		bean.setTaskType(TaskType.MANUALLY);
		bean.setOuerUserId(StringUtils.defaultIfBlank(ouerUserId, "0"));
		bean.setOuerShopId(StringUtils.defaultIfBlank(ouerShopId, "0"));
		bean.setShopType(ShopType.valueOf(shopType));
		bean.setParserType(ParserType.item(bean.getShopType()));
		bean.setDeviceType(DeviceType.OTHER);
		bean.setReqFrom(ReqFrom.KKKD);
		bean.setMobilePhone(StringUtils.EMPTY);
		bean.setRnd(StringUtils.EMPTY);
		bean.setItemId(itemId);
		bean.setShopId(shopId);
		bean.setRequestUrl(WItemTaskBean.WDETAIL_PREFIX + itemId);
		bean.setItemUrl(WItemTaskBean.WDETAIL_PREFIX + itemId);
		bean.setTaskType(TaskType.MANUALLY);
		bean.setOption(option);
		bean.setAdd(add);
		if (bean != null) {
			bsm.signal(bean);
		}
		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
	}
	
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/moveItem/mSync")
	public ResponseObject<Boolean> syncTokkkd(@RequestParam(value = "itemId", required = true) String itemId,
			@RequestParam(value = "ouerShopId", required = true) String ouerShopId, 
			@RequestParam(value = "type", required = false, defaultValue = "1") int type, 
			@RequestParam(value = "option", required = false, defaultValue = "1") int option) {
		if (log.isDebugEnabled()) {
			log.debug("itemId={}, ouerShopId={}, type={}, option={}", itemId, ouerShopId, type, option);
		}
		
		try {
			final Item item =  itemDao.queryOne4Task(ouerShopId, itemId);
			if (item != null) {
				final ItemCompletedEvent event = new ItemCompletedEvent(item.getId(), SpideItemType.fromOrdinal(type), option);
				bsm.signal(event);
			} else {
				return new ResponseObject<Boolean>(false);
			}
		} catch (final Exception e) {
			log.error("syncTokkkd error", e);
			return new ResponseObject<Boolean>(false);
		}
		return new ResponseObject<Boolean>(true);
	}
	
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/item/parse")
	public ResponseObject<Map<String, Object>> parse(@RequestParam(value = "itemId", required = true) String itemId,
			@RequestParam(value = "reqFrom", required = false, defaultValue = "1") Integer reqFromValue) {
		final HttpClientInvoker invoker = this.provider.provide(ShopType.TAOBAO_OR_TMALL,
				WItemTaskBean.WDETAIL_PREFIX + itemId, null);
		final HttpInvokeResult result = invoker.invoke();
		ItemResponse resp;
		try {
			resp = ObjectConvertUtils.fromString(result.getContentStringAndReset(), ItemResponse.class);
			final WItem wItem = ApiStackValueParser.parseItem(resp);
			if (wItem == null) {
				return new ResponseObject<Map<String, Object>>("this item may not exist.", GlobalErrorCode.NOT_FOUND);
			}
			
			final Map<String, Object> results = new HashMap<String, Object>();
			results.put("title", wItem.getName());
			results.put("nick", resp.getData().getSeller().getNick());
			results.put("price", wItem.getPrice());
			results.put("images", wItem.getGroupImgs());
			if (Statics.SOLD_OUT == wItem.getStatus()) {
				results.put("soldOut", true);
			} else {
				results.put("soldOut", false);
			}
			
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("statusCode", 200);
			map.put("results", results);

			return new ResponseObject<Map<String, Object>>(map);
		} catch (final Exception e) {
			log.error("parse witem failed", e);
			return new ResponseObject<Map<String, Object>>(e.getMessage(), GlobalErrorCode.INTERNAL_ERROR);
		}
	}
	
}
