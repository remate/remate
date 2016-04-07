package com.vdlm.spider.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.ResponseObject;
import com.vdlm.spider.dao.ItemDao;
import com.vdlm.spider.dao.ItemListTaskDao;
import com.vdlm.spider.dao.ItemTaskDao;
import com.vdlm.spider.dao.ShopDao;
import com.vdlm.spider.dao.ShopTaskDao;
import com.vdlm.spider.event.ItemProcessEvent;
import com.vdlm.spider.event.ItemProcessEventType;
import com.vdlm.spider.utils.MapTools;

/**
 *
 * @author: chenxi
 */

@Controller
@RequestMapping("/task")
public class TaskAdminController {

	@Autowired
	private BusSignalManager bsm;
	
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
	
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/cs")
	public ResponseObject<Map<String, Object>> cleanShopTask(@RequestParam(value = "ouerShopId", required = true) String ouerShopId) {
		itemTaskDao.deleteByShopId(ouerShopId);
		itemListTaskDao.deleteOne(ouerShopId);
		shopTaskDao.deleteOne(ouerShopId);
		final Long shopId = shopDao.queryForShopId(ouerShopId);
		if (shopId != null) {
			final ItemProcessEvent event = new ItemProcessEvent(ItemProcessEventType.CLEAN_LOCKS);
			event.setCleanShopId(shopId);
			bsm.signal(event);
		}
		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
	}
	
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/ci")
	public ResponseObject<Map<String, Object>> cleanItemTask(@RequestParam(value = "ouerShopId", required = true) String ouerShopId, 
			@RequestParam(value = "itemId", required = true) String itemId) {
		itemTaskDao.deleteByShopId(ouerShopId);
		itemListTaskDao.deleteOne(ouerShopId);
		shopTaskDao.deleteOne(ouerShopId);
		final Long id = itemDao.exist(ouerShopId, itemId);
		if (id != null) {
			final ItemProcessEvent event = new ItemProcessEvent(ItemProcessEventType.CLEAN_LOCK);
			event.setItemId(id);
			bsm.signal(event);
		}
		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
	}
}
