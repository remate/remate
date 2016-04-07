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
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.WItemTaskBean;
import com.vdlm.spider.bean.parial.PItemSkusTaskBean;
import com.vdlm.spider.dao.ItemDao;
import com.vdlm.spider.dao.ShopDao;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.utils.MapTools;

/**
 *
 * @author: chenxi
 */

@Controller
@RequestMapping("/moveSkus")
public class MoveSkusController extends BaseController {

	@Autowired
	private BusSignalManager bsm;
	
	@Autowired
	private ShopDao shopDao;
	
	@Autowired
	private ItemDao itemDao;
		
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/test")
	public ResponseObject<Map<String, Object>> moveItemDescManually(
			@RequestParam(value = "ouerShopId", required = true) String ouerShopId,
			@RequestParam(value = "itemId", required = true) String itemId,
			@RequestParam(value = "shopType", required = true, defaultValue = "1") int shopType) {		
		prepare(ouerShopId, itemId);
		
		final Long shopId = shopDao.queryForShopId(ouerShopId);
		if (shopId == null) {
			log.warn("cannot find shop with ouer shop id:" + ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 700, 
							"msg", "店铺搬家记录不存在, 请确保该店铺已操作过搬家操作"));
		}
		Item item = null;
		try {
			item = itemDao.queryOne4Task(ouerShopId, itemId);
		} catch (Exception e) {
		}
		if (item == null) {
			log.warn("cannot find item with third id:" + itemId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 700,
					"msg", "该淘宝商品搬家记录不存在,请先确定该商品已成功搬家"));
		}
		final PItemSkusTaskBean bean = new PItemSkusTaskBean();
		bean.setItemId(itemId);
		bean.setItemUrl(WItemTaskBean.WDETAIL_PREFIX + itemId);
		bean.setRequestUrl(WItemTaskBean.WDETAIL_PREFIX + itemId);
		bean.setShopType(ShopType.valueOf(shopType));
		bean.setOuerShopId(item.getOuerShopId());
		bean.setOuerUserId(item.getOuerUserId());
		bean.setReqFrom(item.getReqFrom());
		bsm.signal(bean);
		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
	}
}
