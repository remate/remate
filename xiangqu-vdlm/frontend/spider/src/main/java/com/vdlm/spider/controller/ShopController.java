/**
 * 
 */
package com.vdlm.spider.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ResponseObject;
import com.vdlm.spider.service.ShopService;
import com.vdlm.spider.utils.MapTools;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:35:01 PM Aug 18, 2014
 */
@Controller
@RequestMapping("/shop")
public class ShopController extends BaseController {

	private ShopService shopService;

	@Autowired
	public void setShopService(ShopService shopService) {
		this.shopService = shopService;
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
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/exists")
	public ResponseObject<Map<String, Object>> exists(
			@RequestParam(value = "ouerUserId", required = true) String ouerUserId,
			@RequestParam(value = "ouerShopId", required = true) String ouerShopId,
			@RequestParam(value = "reqFrom", required = false, defaultValue = "1") Integer reqFromValue) {
		if (log.isDebugEnabled()) {
			log.debug("ouerUserId={}, ouerShopId={}, reqFrom={}", ouerUserId, ouerShopId, reqFromValue);
		}

		final ReqFrom reqFrom;
		try {
			reqFrom = ReqFrom.valueOf(reqFromValue);
		}
		catch (Exception e) {
			log.error("invalid Request Parameters: reqFrom={}", reqFromValue);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 501));
		}

		final Map<String, Object> result = new HashMap<String, Object>();
		result.put("statusCode", 200);
		result.put("results", shopService.queryForList(reqFrom, ouerUserId, ouerShopId));
		if (log.isDebugEnabled()) {
			log.debug("shop/exists: ouerUserId={}, ouerShopId={}, reqFrom={}, result={}", ouerUserId, ouerShopId, reqFromValue, result.toString());
		}

		return new ResponseObject<Map<String, Object>>(result);
	}
}
