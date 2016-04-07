/**
 * 
 */
package com.vdlm.spider.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.vdlm.spider.bean.ParseItemTaskBean;
import com.vdlm.spider.biz.MoveItemBiz;
import com.vdlm.spider.queue.TaskQueues;
import com.vdlm.spider.utils.MapTools;

/**
 * <pre>
 * 仅仅搬家item
 * </pre>
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:08:08 PM Jul 19, 2014
 */
@Controller
@RequestMapping("/moveItem")
public class MoveItemController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(MoveItemController.class);

	private MoveItemBiz moveItemBiz;

	@Autowired
	public void setMoveItemBiz(MoveItemBiz moveItemBiz) {
		this.moveItemBiz = moveItemBiz;
	}

	/**
	 * <pre>
	 * 根据商品URL搬家
	 * 异步处理
	 * statusCode
	 * 5开头 - controller层抛出
	 * 6开头 - biz层抛出
	 * 7开头 - service层抛出
	 * 8开头 - dao层抛出
	 * 200 - 正常
	 * </pre>
	 * @param ouerUserId
	 * @param ouerShopId
	 * @param url
	 * @param reqFromValue
	 * @return
	 */
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/url/async")
	public ResponseObject<Map<String, Object>> asyncByUrl(
			@RequestParam(value = "ouerUserId", required = false) String ouerUserId,
			@RequestParam(value = "ouerShopId", required = false) String ouerShopId,
			@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "reqFrom", required = false, defaultValue = "1") Integer reqFromValue) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("ouerUserId={}, ouerShopId={}, url={}, reqFrom={}", ouerUserId, ouerShopId, url, reqFromValue);
		}

		final ReqFrom reqFrom;
		try {
			reqFrom = ReqFrom.valueOf(reqFromValue);
		}
		catch (Exception e) {
			log.error("invalid Request Parameters: reqFrom={}", reqFromValue);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 501));
		}

		if (reqFrom == ReqFrom.KKKD && (StringUtils.isBlank(ouerUserId) || StringUtils.isBlank(ouerShopId))) {
			log.error("invalid Request Parameters: reqFrom={}, ouerUserId={}, ouerShopId={}", reqFromValue, ouerUserId, ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 501));
		}

		final ShopType shopType = this.getShopTypeByUrl(url);
		if (shopType == null) {
			log.error("can not get ShopType, invalid Request Parameters: url={}", url);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 502));
		}

		final ParseItemTaskBean bean = new ParseItemTaskBean();
		bean.setOuerUserId(StringUtils.defaultIfBlank(ouerUserId, "0"));
		bean.setOuerShopId(StringUtils.defaultIfBlank(ouerShopId, "0"));
		bean.setShopType(shopType);
		bean.setDeviceType(DeviceType.OTHER);
		bean.setReqFrom(reqFrom);
		bean.setItemUrl(url);
		
		if (TaskQueues.getParseItemTaskQueue().add(bean.toJSONBytes())) {
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
		}
		else {
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 503));
		}
	}

	/**
	 * <pre>
	 * 根据商品URL搬家
	 * 同步处理
	 * statusCode
	 * 5开头 - controller层抛出
	 * 6开头 - biz层抛出
	 * 7开头 - service层抛出
	 * 8开头 - dao层抛出
	 * 200 - 正常
	 * </pre>
	 * @param ouerUserId
	 * @param ouerShopId
	 * @param url
	 * @param reqFromValue
	 * @return
	 */
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/url/sync")
	public ResponseObject<Map<String, Object>> syncByUrl(
			@RequestParam(value = "ouerUserId", required = false) String ouerUserId,
			@RequestParam(value = "ouerShopId", required = false) String ouerShopId,
			@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "reqFrom", required = false, defaultValue = "1") Integer reqFromValue) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("ouerUserId={}, ouerShopId={}, url={}, reqFrom={}", ouerUserId, ouerShopId, url, reqFromValue);
		}

		final ReqFrom reqFrom;
		try {
			reqFrom = ReqFrom.valueOf(reqFromValue);
		}
		catch (Exception e) {
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 501));
		}

		if (reqFrom == ReqFrom.KKKD && (StringUtils.isBlank(ouerUserId) || StringUtils.isBlank(ouerShopId))) {
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 501));
		}

		final ShopType shopType = this.getShopTypeByUrl(url);
		if (shopType == null) {
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 502));
		}

		final ParseItemTaskBean bean = new ParseItemTaskBean();
		bean.setOuerUserId(StringUtils.defaultIfBlank(ouerUserId, "0"));
		bean.setOuerShopId(StringUtils.defaultIfBlank(ouerShopId, "0"));
		bean.setShopType(shopType);
		bean.setDeviceType(DeviceType.OTHER);
		bean.setReqFrom(reqFrom);
		bean.setItemUrl(url);

		return new ResponseObject<Map<String, Object>>(moveItemBiz.move(bean));
	}
}
