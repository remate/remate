package com.vdlm.web.cart;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.vdlm.dal.model.OrderRequest;
import com.vdlm.dal.model.UserCouponsVO;
import com.vdlm.dal.type.CouponPlatform;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.utils.DeviceUA;
import com.vdlm.utils.DeviceUAUtils;
import com.vdlm.utils.UserAgent;
import com.vdlm.utils.UserAgentUtils;
import com.vdlm.web.ResponseObject;

@Controller
public class XqCartController extends CartController {

	@RequestMapping("/ouer/cart/coupon/list")
	@ResponseBody
	public ResponseObject<UserCouponsVO> getCouponXqList(HttpServletRequest req,
			@RequestParam(value = "skuJson") String skuStr,
			@RequestParam(value = "zoneId", required = false) String zoneId) {
		Map<String, Integer> skuMap = JSON.parseObject(skuStr, new TypeReference<Map<String, Integer>>() {
		});
		DeviceUA ua = DeviceUAUtils.buildOrderUserDevice(req);
		String  userId = getCurrentUser().getId();
		if(org.springframework.util.StringUtils.isEmpty(userId)){
			return new ResponseObject<UserCouponsVO>(GlobalErrorCode.INVALID_ARGUMENT);
		}
		CouponPlatform platform = null;
		if (isRequestFromApp(ua)) {
			platform = CouponPlatform.MOBILE;
		}
		UserCouponsVO uc = this.getUserOrderValidCoupons(skuMap, zoneId, userId, platform, ua.getChannel());
		return new ResponseObject<UserCouponsVO>(uc);
	}

	protected boolean isRequestFromApp(DeviceUA ua) {
		if (StringUtils.containsIgnoreCase(ua.getPlatform(), "ios")
				|| StringUtils.containsIgnoreCase(ua.getPlatform(), "android")) {
			return true;
		}
		return false;
	}

	public UserCouponsVO getUserOrderValidCoupons(Map<String, Integer> skuMap, String zoneId, String userId,
			CouponPlatform platform, String channel) {
		UserCouponsVO coupons = this.couponService.getUserOrderValidCoupons(new OrderRequest(skuMap, zoneId),
				userId.toString(), getEntity(platform, CouponPlatform.class), channel);
		return getEntity(coupons, UserCouponsVO.class);
	}

	static <K, V> V getEntity(K from, Class<V> clazz) {
		String json = JSON.toJSONString(from);
		return JSON.parseObject(json, clazz);
	}
}
