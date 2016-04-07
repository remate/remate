/**
 * 
 */
package com.vdlm.spider.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.User;
import com.vdlm.service.user.UserService;
import com.vdlm.spider.DeviceType;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ResponseObject;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.ParseShopInfoBean;
import com.vdlm.spider.biz.MoveProductBiz;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.utils.MapTools;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:08:08 PM Jul 19, 2014
 */
@Controller
@RequestMapping("/moveProduct")
public class MoveProductController extends BaseController {

	private UserService userService;
	private MoveProductBiz moveProductBiz;

	@Autowired
	public void setMoveProductBiz(MoveProductBiz moveProductBiz) {
		this.moveProductBiz = moveProductBiz;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Transactional
	User getOuerUserById(String ouerUserId) {
		return this.userService.load(ouerUserId);
	}

	@Transactional
	User getOuerUserByLoginname(String loginname) {
		return this.userService.loadByLoginname(loginname);
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

	/**
	 * <pre>
	 * 根据随机码搬家
	 * 仅给快店使用
	 * statusCode
	 * 5开头 - controller层抛出
	 * 6开头 - biz层抛出
	 * 7开头 - service层抛出
	 * 8开头 - dao层抛出
	 * 200 - 正常
	 * </pre>
	 * @param ouerUserId
	 * @param ouerShopId
	 * @param rnd
	 * @param itemId
	 * @param shopType
	 * @param deviceType
	 * @return
	 */
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/rnd")
	public ResponseObject<Map<String, Object>> submitTaskByRnd(
			@RequestParam(value = "ouerUserId", required = true) String ouerUserId,
			@RequestParam(value = "ouerShopId", required = true) String ouerShopId,
			@RequestParam(value = "rnd", required = false) Integer rnd,
			@RequestParam(value = "itemId", required = true) String itemId,
			@RequestParam(value = "shopType", required = true, defaultValue = "3") int shopTypeValue,
			@RequestParam(value = "deviceType", required = false, defaultValue = "3") Integer deviceTypeValue) {
		if (log.isDebugEnabled()) {
			log.debug("ouerUserId={}, ouerShopId={}, rnd={}, itemId={}, shopType={}, deviceType={}", ouerUserId,
					ouerShopId, rnd, itemId, shopTypeValue, deviceTypeValue);
		}

		final ShopType shopType;
		try {
			shopType = ShopType.valueOf(shopTypeValue);
		}
		catch (Exception e) {
			log.error("invalid Request Parameters: shopType={}", shopTypeValue);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 501));
		}

		final DeviceType deviceType;
		try {
			deviceType = DeviceType.valueOf(deviceTypeValue);
		}
		catch (Exception e) {
			log.error("invalid Request Parameters: deviceType={}", deviceTypeValue);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 502));
		}

		// 检查用户信息
		final User user = this.getOuerUserById(ouerUserId);
		if (user == null || !ouerShopId.equals(user.getShopId())) {
			log.error("invalid Request Parameters: ouerUserId={}, ouerShopId={}", ouerUserId, ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 504));
		}

		final ParseShopInfoBean bean = new ParseShopInfoBean();
		bean.setOuerShopId(ouerShopId);
		bean.setOuerUserId(ouerUserId);
		bean.setShopType(shopType);
		bean.setDeviceType(deviceType);
		bean.setReqFrom(ReqFrom.KKKD);
		bean.setMobilePhone(user.getPhone());
		bean.setRnd(this.formatRnd(rnd));
		bean.setItemId(itemId);

		return new ResponseObject<Map<String, Object>>(moveProductBiz.moveByRnd(bean));
	}

	/**
	 * <pre>
	 * 根据店铺URL搬家
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
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/lgnu")
	public ResponseObject<Map<String, Object>> submitTaskByUrl(
			@RequestParam(value = "ouerUserId", required = false) String ouerUserId,
			@RequestParam(value = "ouerShopId", required = false) String ouerShopId,
			@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "reqFrom", required = false, defaultValue = "1") Integer reqFromValue) {
		if (log.isDebugEnabled()) {
			log.debug("ouerUserId={}, ouerShopId={}, url={}, reqFrom={}", ouerUserId, ouerShopId, url, reqFromValue);
		}
		
		url = ParserUtils.formatUrl(url);
		
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

		final ParseShopInfoBean bean = new ParseShopInfoBean();
		bean.setOuerUserId(StringUtils.defaultIfBlank(ouerUserId, "0"));
		bean.setOuerShopId(StringUtils.defaultIfBlank(ouerShopId, "0"));
		bean.setShopType(shopType);
		bean.setDeviceType(DeviceType.OTHER);
		bean.setReqFrom(reqFrom);
		bean.setMobilePhone(StringUtils.EMPTY);
		bean.setRnd(StringUtils.EMPTY);
		bean.setRequestUrl(url);

		return new ResponseObject<Map<String, Object>>(moveProductBiz.moveByUrl(bean));
	}

}
