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
import com.vdlm.spider.bean.parial.PItemDescTaskBean;
import com.vdlm.spider.dao.DescDao;
import com.vdlm.spider.dao.ImgDao;
import com.vdlm.spider.dao.ItemDao;
import com.vdlm.spider.dao.ItemProcessDao;
import com.vdlm.spider.dao.ShopDao;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.ItemProcess;
import com.vdlm.spider.utils.MapTools;

/**
 *
 * @author: chenxi
 */

@Controller
@RequestMapping("/moveDesc")
public class MoveDescController extends BaseController {

	@Autowired
	private BusSignalManager bsm;
	
	// for test
	@Autowired
	private ShopDao shopDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private DescDao descDao;
	@Autowired
	private ImgDao imgDao;
	@Autowired
	private ItemProcessDao itemProcessDao;
	
//	@ResponseBody
//	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/test")
//	public ResponseObject<Map<String, Object>> moveItemDescManually(@RequestParam(value = "itemId", required = true) String itemId,
//																		  @RequestParam(value = "apiDescUrl", required = true) String apiDescUrl,
//																		  @RequestParam(value = "imgIndex", required = true) int imgIndex) {
//		final Item item = itemDao.queryOne4Task(itemId);
//		if (item == null) {
//			log.warn("cannot find item with third id:" + itemId);
//			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 999));
//		}
//		
//		final DescTaskBean bean = TaskBeanFactory.createDescTaskBean(item, apiDescUrl, imgIndex);
//		bsm.signal(bean);
//		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
//	}
//	
//	@ResponseBody
//	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/test2")
//	public ResponseObject<Map<String, Object>> moveItemDescManually2(
//			@RequestParam(value = "itemId", required = true) String itemId,
//			@RequestParam(value = "itemUrl", required = true) String itemUrl,
//			@RequestParam(value = "shopType", required = true) int shopType) {
//		final Item item = itemDao.queryOne4Task(itemId);
//		if (item == null) {
//			log.warn("cannot find item with third id:" + itemId);
//			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 999));
//		}
//		
//		final PItemDescTaskBean bean = new PItemDescTaskBean();
//		bean.setItemId(itemId);
//		bean.setItemUrl(itemUrl);
//		bean.setRequestUrl(itemUrl);
//		bean.setShopType(ShopType.valueOf(shopType));
//		bsm.signal(bean);
//		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
//	}
	
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
		if (itemProcessDao.exist(item.getId()) == null) {
			final ItemProcess ip = new ItemProcess();
			ip.setItemId(item.getId());
			ip.setShopId(shopId);
			ip.setGroupImgCount(0);
			ip.setSkuImgCount(0);
			ip.setDetailImgCount(99);
			ip.setCurGroupImgCount(0);
			ip.setCurSkuImgCount(0);
			ip.setCurDetailImgCount(0);
			ip.setSkuParsed(true);
			ip.setDescParsed(false);
			itemProcessDao.insert(ip);
		}
		
		final PItemDescTaskBean bean = new PItemDescTaskBean();
		bean.setItemId(itemId);
		bean.setItemUrl(WItemTaskBean.WDETAIL_PREFIX + itemId);
		bean.setRequestUrl(WItemTaskBean.WDETAIL_PREFIX + itemId);
		bean.setShopType(ShopType.valueOf(shopType));
		bsm.signal(bean);
		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
	}
}
