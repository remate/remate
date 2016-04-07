package com.vdlm.spider.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.sql.visitor.functions.Now;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.spider.DataSource;
import com.vdlm.spider.DeviceType;
import com.vdlm.spider.ParserType;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ResponseObject;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.ShopTaskBean;
import com.vdlm.spider.dao.ItemDao;
import com.vdlm.spider.dao.ItemListTaskDao;
import com.vdlm.spider.dao.ItemTaskDao;
import com.vdlm.spider.dao.ShopDao;
import com.vdlm.spider.dao.ShopTaskDao;
import com.vdlm.spider.entity.Shop;
import com.vdlm.spider.event.ItemProcessEvent;
import com.vdlm.spider.event.ItemProcessEventType;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.task.TaskType;
import com.vdlm.spider.utils.MapTools;

/**
 *
 * @author: chenxi
 */

@Controller
//@RequestMapping("/moveProduct")
public class MoveShopController extends BaseController {

	@Autowired
	private BusSignalManager bsm;
	
	@Autowired
	private ShopDao shopDao;
	
	@Autowired
	private ShopTaskDao shopTaskDao;
	@Autowired
	private ItemListTaskDao itemListTaskDao;
	@Autowired
	private ItemTaskDao itemTaskDao;
	@Autowired
	private ItemDao itemDao;
	
	@Value("${spider.req.limit.seconds}")
	private final int spiderReqLimitSecs = 3600; // 1hour
	
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/moveProduct/lgnu")
	public ResponseObject<Map<String, Object>> submitTaskByUrl(
			@RequestParam(value = "ouerUserId", required = false) String ouerUserId,
			@RequestParam(value = "ouerShopId", required = true) String ouerShopId,
			@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "option", required = false, defaultValue = "1") Integer option,
			@RequestParam(value = "dataSource", required = false, defaultValue = "1") Integer dataSource,
			@RequestParam(value = "reqFrom", required = false, defaultValue = "1") Integer reqFromValue) {
		
		log.info("move by url ouerUserId={}, ouerShopId={}, url={}, option={}, reqFrom={}", 
					ouerUserId, ouerShopId, url, option, reqFromValue);
		// 因为搬家保存的淘宝店铺地址信息格式不规则, 需要做简单完整性处理(添加http:)
		url = ParserUtils.formatUrl(url);
		
		final ReqFrom reqFrom;
		final DataSource source;
		try {
			reqFrom = ReqFrom.valueOf(reqFromValue);
			source = DataSource.valueOf(dataSource);
		}
		catch (final Exception e) {
			log.error("invalid Request Parameters: reqFrom={}", reqFromValue);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", GlobalErrorCode.INVALID_ARGUMENT,
						"msg", "搬家请求来源错误:" + reqFromValue));
		}

		if (reqFrom == ReqFrom.KKKD && StringUtils.isBlank(ouerShopId)) {
			log.error("invalid Request Parameters: reqFrom={}, ouerUserId={}, ouerShopId={}", reqFromValue, ouerUserId, ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", GlobalErrorCode.INVALID_ARGUMENT,
						"msg", "搬家请求来源店铺信息不完整"));
		}

		final ShopType shopType = this.getShopTypeByUrl(url);
		if (shopType == null) {
			log.error("can not get ShopType, invalid Request Parameters: url={}", url);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", GlobalErrorCode.INVALID_ARGUMENT,
						"msg", "搬家地址不规则不能判断淘宝店铺类型:\n"+ url));
		}
		Date created = shopTaskDao.queryTaskDate(ouerShopId);
		if (created != null) {
			final long reqInterval = System.currentTimeMillis() - created.getTime();
			if (reqInterval < spiderReqLimitSecs * 1000) {
				log.warn("you cannot continuously submit move shop request within {} seconds  ouershopId:{}",
						spiderReqLimitSecs, ouerShopId);
				return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 700,
							"msg", "搬家请求过于频繁, 请于大约[" + (spiderReqLimitSecs/60 +5) + "]分钟后再试"));
			} else {
				log.info("submitted a move shop request within {} seconds", reqInterval / 1000);
				cleanShopTask(ouerShopId);
			}
		} else {
			// forcely invoke clean tasks to prevent old invalid data
			cleanShopTask(ouerShopId);
		}
		
		final String itemId = itemDao.queryForOneItemId(ouerShopId, url.replace("http:", "").replaceAll("/$", ""));
		if (itemId == null) {
			log.error("can not find an item, invalid Request Parameters: shopId={}, url={}", ouerShopId, url);
			// 考虑搬家失败的一种情况, 店铺信息已存在, 后面流程失败可能还没有任何item信息, 如果这里直接返回将无法再有真正搬家机会
//			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 700, 
//					"msg", "we should give it a try with url, whatever exits item in this shop or not..."));
		}

		final ShopTaskBean bean = new ShopTaskBean();
		if (ShopType.TMALL.equals(shopType))  bean.setUseProxy(false); //暂时解决天猫不能爬取问题, 线上爬虫代理需要优化
		bean.setOuerUserId(StringUtils.defaultIfBlank(ouerUserId, "0"));
		bean.setOuerShopId(StringUtils.defaultIfBlank(ouerShopId, "0"));
		bean.setShopType(shopType);
		bean.setParserType(ParserType.item(bean.getShopType()));
		bean.setDeviceType(DeviceType.OTHER);
		bean.setReqFrom(reqFrom);
		bean.setMobilePhone(StringUtils.EMPTY);
		bean.setRnd(StringUtils.EMPTY);
		bean.setRequestUrl(url);
		bean.setDataSource(source);
		bean.setShopUrl(url);
		log.info("move by url mobile data with itemId:{}  ouer_shop_id:{}", itemId, ouerShopId);
		// http->https问题运维已帮忙解决, 因数tmall手淘数据拿不到店铺地址,入口处暂时还是用pc数据, 同时还需要打开CrawlShopTask.java WDETAIL_PREFIX
		if (DataSource.MOBILE.equals(source) && itemId != null)
			bean.setItemId(itemId);  // 有值说明是用店铺地址找到其下的商品使用手淘接口,否则说明在用店铺地址去试, 优先用前者
		else 
			bean.setItemId(null); // move by url  pc data
		bean.setOption(option);
		bsm.signal(bean);
		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
	}
	
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/moveProduct/rnd")
	public ResponseObject<Map<String, Object>> submitTaskByRnd(
			@RequestParam(value = "ouerUserId", required = false) String ouerUserId,
			@RequestParam(value = "ouerShopId", required = true) String ouerShopId,
			@RequestParam(value = "rnd", required = true) Integer rnd,
			@RequestParam(value = "itemId", required = true) String itemId,
			@RequestParam(value = "shopType", required = true, defaultValue = "3") int shopTypeValue,
			@RequestParam(value = "deviceType", required = false, defaultValue = "3") Integer deviceTypeValue,
			@RequestParam(value = "option", required = false, defaultValue = "1") Integer option,
			@RequestParam(value = "dataSource", required = false, defaultValue = "1") Integer dataSource,
			@RequestParam(value = "reqFrom", required = false, defaultValue = "1") Integer reqFromValue) {
		
		log.info("move by rnd ouerUserId={}, ouerShopId={}, rnd={}, option={}, reqFrom={}, shopType={}, deviceType={}, reqFromValue={}",
							ouerUserId, ouerShopId, rnd, option, reqFromValue, shopTypeValue, deviceTypeValue, reqFromValue);
		final ShopType shopType;
		final DeviceType deviceType;
		final ReqFrom reqFrom;
		final DataSource source;
		try {
			shopType = ShopType.valueOf(shopTypeValue);
			deviceType = DeviceType.valueOf(deviceTypeValue);
			reqFrom = ReqFrom.valueOf(reqFromValue);
			source = DataSource.valueOf(dataSource);
		}
		catch (final Exception e) {
			log.error("invalid Request Parameters: shopType={}, deviceType={}, reqFrom={}, ouerShopId={}",
					shopTypeValue, deviceTypeValue, reqFromValue, ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", GlobalErrorCode.INVALID_ARGUMENT, 
						"msg", "搬家请求参数错误"));
		}

		if (reqFrom == ReqFrom.KKKD && StringUtils.isBlank(ouerShopId)) {
			log.error("invalid Request Parameters: reqFrom={}, ouerUserId={}, ouerShopId={}", reqFromValue, ouerUserId, ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", GlobalErrorCode.INVALID_ARGUMENT,
						"msg", "搬家请求来源店铺信息不完整"));
		}
		
		final ShopTaskBean bean = new ShopTaskBean();
		bean.setOuerUserId(StringUtils.defaultIfBlank(ouerUserId, "0"));
		bean.setOuerShopId(StringUtils.defaultIfBlank(ouerShopId, "0"));
		bean.setShopType(shopType);
		bean.setParserType(ParserType.item(bean.getShopType()));
		bean.setDeviceType(DeviceType.OTHER);
		bean.setReqFrom(reqFrom);
		bean.setMobilePhone(StringUtils.EMPTY);
		bean.setRnd(this.formatRnd(rnd));  // 测试环境和本地为空串(直接通过验证)但不为null(为null说明是再次搬家), 生产为5位数字
		bean.setItemId(itemId);
		bean.setDataSource(source);
		bean.setRequestUrl(StringUtils.EMPTY);
		bean.setShopUrl(StringUtils.EMPTY);
		bean.setOption(option);
		bsm.signal(bean);
		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
	}
	
	/*
	 * 手工搬家接口
	 */
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/mtest")
	public ResponseObject<Map<String, Object>> mSubmitTaskByUrl(
			@RequestParam(value = "ouerUserId", required = false) String ouerUserId,
			@RequestParam(value = "ouerShopId", required = true) String ouerShopId,
			@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "option", required = false, defaultValue = "1") Integer option,
			@RequestParam(value = "reqFrom", required = false, defaultValue = "1") Integer reqFromValue,
			@RequestParam(value = "dataSource", required = false, defaultValue = "1") Integer dataSource,
			@RequestParam(value = "useProxy", required = false, defaultValue = "true") boolean useProxy) {
		
		log.info("manual move url ={}, ouerUserId={}, ouerShopId={}, url={}, option={}, reqFrom={}, useProxy={}", 
					url, ouerUserId, ouerShopId, url, option, reqFromValue, useProxy);
		
		url = ParserUtils.formatUrl(url);
		
		final ReqFrom reqFrom;
		final DataSource source;
		try {
			reqFrom = ReqFrom.valueOf(reqFromValue);
			source = DataSource.valueOf(dataSource);
		}
		catch (final Exception e) {
			log.error("invalid Request Parameters: reqFrom={}", reqFromValue);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", GlobalErrorCode.INVALID_ARGUMENT,
					"msg", "搬家请求来源错误:" + reqFromValue));
		}

		if (reqFrom == ReqFrom.KKKD && StringUtils.isBlank(ouerShopId)) {
			log.error("invalid Request Parameters: reqFrom={}, ouerUserId={}, ouerShopId={}", reqFromValue, ouerUserId, ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", GlobalErrorCode.INVALID_ARGUMENT,
					"msg", "搬家请求来源店铺信息不完整"));
		}

		final ShopType shopType = this.getShopTypeByUrl(url);
		if (shopType == null) {
			log.error("can not get ShopType, invalid Request Parameters: url={}", url);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", GlobalErrorCode.INVALID_ARGUMENT,
					"msg", "搬家地址不规则不能判断淘宝店铺类型:\n"+ url));
		}
		final Date created = shopTaskDao.queryTaskDate(ouerShopId);
		if (created != null) {
			final long reqInterval = System.currentTimeMillis() - created.getTime();
			if (reqInterval < spiderReqLimitSecs * 1000) {
				log.warn("you cannot continuously submit move shop request within {} seconds ouerShopId:{}", 
							spiderReqLimitSecs, ouerShopId);
				return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 700,
						"msg", "搬家请求过于频繁, 请于[" + (spiderReqLimitSecs/60 +5) + "]分钟后再试"));
			} else {
				log.info("submitted a move shop request within {} seconds", reqInterval / 1000);
				cleanShopTask(ouerShopId);
			}
		} else {
			// forcely invoke clean tasks to prevent old invalid data
			cleanShopTask(ouerShopId);
		}
		
		final String itemId = itemDao.queryForOneItemId(ouerShopId, url.replace("http:", ""));
		if (itemId == null) {
			log.error("can not find an item, invalid Request Parameters: shopId={}, url={}", ouerShopId, url);
		}

		final ShopTaskBean bean = new ShopTaskBean();
		bean.setTaskType(TaskType.MANUALLY);
		bean.setOuerUserId(StringUtils.defaultIfBlank(ouerUserId, "0"));
		bean.setOuerShopId(StringUtils.defaultIfBlank(ouerShopId, "0"));
		bean.setShopType(shopType);
		bean.setParserType(ParserType.item(bean.getShopType()));
		bean.setDeviceType(DeviceType.OTHER);
		bean.setReqFrom(reqFrom);
		bean.setMobilePhone(StringUtils.EMPTY);
		bean.setRnd(StringUtils.EMPTY); // 用于区分是第一次搬家还是再次搬家
		bean.setRequestUrl(url);
		bean.setShopUrl(url);
		log.info("move mtest mobile data with itemId:{}  ouer_shop_id:{}", itemId, ouerShopId);
		if (DataSource.MOBILE.equals(source) && itemId != null) 
			bean.setItemId(itemId);  // 有值说明是用店铺地址找到其下的商品使用手淘接口,否则说明在用店铺地址去试, 优先用前者
		else 
			bean.setItemId(null);
		if (ShopType.TMALL.equals(shopType))  bean.setUseProxy(false); //暂时解决天猫不能爬取问题, 线上爬虫代理需要优化
		else bean.setUseProxy(useProxy);
		bean.setOption(option);
		bsm.signal(bean);
		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
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
	 * @param ouerUserId
	 * @param ouerShopId
	 * @param reqFromValue
	 * @return
	 */
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/shop/exists")
	public ResponseObject<Map<String, Object>> exists(
			@RequestParam(value = "ouerUserId", required = true) String ouerUserId,
			@RequestParam(value = "ouerShopId", required = true) String ouerShopId,
			@RequestParam(value = "reqFrom", required = false, defaultValue = "1") Integer reqFromValue) {
		
		log.info("shop exists ouerUserId={}, ouerShopId={}, reqFrom={}", ouerUserId, ouerShopId, reqFromValue);

		final ReqFrom reqFrom;
		try {
			reqFrom = ReqFrom.valueOf(reqFromValue);
		}
		catch (final Exception e) {
			log.error("invalid Request Parameters: reqFrom={}", reqFromValue);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", GlobalErrorCode.INVALID_ARGUMENT,
					"msg", "搬家请求来源错误:" + reqFromValue));
		}

		final Map<String, Object> result = new HashMap<String, Object>();
		result.put("statusCode", 200);
		result.put("results", queryForList(reqFrom, ouerUserId, ouerShopId));
		if (log.isDebugEnabled()) {
			log.debug("shop/exists: ouerUserId={}, ouerShopId={}, reqFrom={}, result={}", ouerUserId, ouerShopId, reqFromValue, result.toString());
		}

		return new ResponseObject<Map<String, Object>>(result);
	}
	
	/**
	 * <pre>
	 * 获取店铺列表
	 * </pre>
	 * @param reqFrom
	 * @param ouerUserId
	 * @param ouerShopId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Shop> queryForList(ReqFrom reqFrom, String ouerUserId, String ouerShopId) {
		final SqlRowSet rs = shopDao.queryForRowSet(reqFrom, ouerUserId, ouerShopId);
		if (!rs.last()) {
			return Collections.EMPTY_LIST;
		}
		final List<Shop> results = new ArrayList<Shop>(rs.getRow());

		rs.beforeFirst();
		while (rs.next()) {
			final Shop result = new Shop();
			result.setName(rs.getString("name"));
			result.setNickname(rs.getString("nickname"));
			result.setShopType(ShopType.valueOf(rs.getInt("shop_type")));
			String shopUrl = rs.getString("shop_url");
			if (shopUrl != null && shopUrl.startsWith("//")) {
				shopUrl = "http:" + shopUrl;
			}
			result.setShopUrl(rs.getString("shop_url"));
			result.setScore(rs.getString("score"));
			results.add(result);
		}
		return results;
	}
	
	/**
	 * <pre>
	 * rnd小于10000无需验证title，方便测试
	 * </pre>
	 * @param rnd
	 * @return
	 */
	String formatRnd(Integer rnd) {
		return rnd == null ? StringUtils.EMPTY : (rnd < 10000 ? StringUtils.EMPTY : rnd.toString());
	}

	private void cleanShopTask(String ouerShopId) {
		itemTaskDao.deleteByShopId(ouerShopId);
		itemListTaskDao.deleteOne(ouerShopId);
		shopTaskDao.deleteOne(ouerShopId);
		final Long shopId = shopDao.queryForShopId(ouerShopId);
		if (shopId != null) {
			final ItemProcessEvent event = new ItemProcessEvent(ItemProcessEventType.CLEAN_LOCKS);
			event.setCleanShopId(shopId);
			bsm.signal(event);
		}
	}
}
