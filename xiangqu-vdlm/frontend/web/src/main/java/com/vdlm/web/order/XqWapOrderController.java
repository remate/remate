package com.vdlm.web.order;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.CashierItem;
import com.vdlm.dal.type.PaymentMode;
import com.vdlm.interceptor.Token;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.outpay.PaymentRequestVO;
import com.vdlm.service.outpay.PaymentResponseClientVO;
import com.vdlm.service.outpay.ThirdPartyPayment;
import com.vdlm.service.pricing.vo.OrderPricingVO;
import com.vdlm.service.user.UserService;
import com.vdlm.web.ResponseObject;
import com.vdlm.web.order.form.XqOrderSumbitForm;
import com.vdlm.web.payment.PaymentController;

@Controller
@RequestMapping("/xiangqu/wap")
public class XqWapOrderController extends XqOrderController {

	@Autowired
	private CashierService cashierService;
	
	@Autowired
	private ThirdPartyPayment wxJsApiPayment;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	
	
	@Value("${site.web.host.name}")
	private String hostName;

	@RequestMapping(value = "/order/submit")
	@Token(remove = true)
	public String submit(@ModelAttribute XqOrderSumbitForm form,
			@CookieValue(value = "union_id", defaultValue = "") String unionId,
			@RequestParam(value = "tuid", defaultValue = "") String tuId, Errors errors, Device device, Model model,
			HttpServletRequest request, @RequestHeader(value = "referer", required = false) String referer) {
		log.info("vdlm/web/order/XqWapOrderController/order/submit");
		return super.submit(form, unionId, tuId, errors, device, model, request, referer);
	}

	/**
	 * 临时支持微信内支付的接口
	 * @author Tier <QQ:871898560>
	 * @dateTime 2016年1月11日,下午5:38:14
	 * @param form
	 * @param unionId
	 * @param tuId
	 * @param errors
	 * @param device
	 * @param model
	 * @param request
	 * @param referer
	 */
	@RequestMapping(value = "/order/submit/wx")
//	@Token(remove = true)
	@ResponseBody
	public ResponseObject<PaymentResponseClientVO> submitWXJs(@ModelAttribute XqOrderSumbitForm form,
			@CookieValue(value = "union_id", defaultValue = "") String unionId,
			@CookieValue(value = "tu_id", defaultValue = "") String tuId, Errors errors, Device device, Model model,
			HttpServletRequest req, @RequestHeader(value = "referer", required = false) String referer) {
		
		if(!enableWxJsApiPay(req))
			throw new BizException(GlobalErrorCode.UNKNOWN, "需要在高于5.0版本的微信中发起");
		
		//订单提交，包括优惠券使用
		Map<String, String> m = super.doSubmit(form, unionId, tuId, errors, device, model, req, referer);
		String bizNo = m.get("pNo");
		CashierItem item = cashierService.nextCashierItem(bizNo);
		//未支付
		if(null == item){
			return null;
		}
		try {
			PaymentRequestVO request = new PaymentRequestVO();
	        request.setCashierItemId(item.getId());
	        request.setTradeNo(item.getBizNo());
	        request.setPayType(PaymentMode.WEIXIN);
	        request.setSubject("想去网：PO="+item.getBizNo());
	        String openId = userService.getWeixinPayOpenId();
	        if(null == openId) throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "openId为空");
	        request.setOpenId(openId);
	        request.setTradeUrl(hostName + "/order/" + cashierService.findDefaultBizId(item.getBizNo()));
			BigDecimal paidFee = this.cashierService.loadPaidFee(request.getTradeNo());
			request.setTotalFee(paidFee);
			request.setRemoteHost(PaymentController.getIpAddr(req));
			request.setRequestUrl(referer);
			PaymentResponseClientVO response = wxJsApiPayment.sign(request);
			return new ResponseObject<>(response);
		} catch (Exception e) {
			// log.error("支付请求失败，tradeNo=" + request.getTradeNo(), e);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "支付请求失败，tradeNo=" + bizNo, e);
		}
	}
	
	/*
	 * 微信二次支付
	 * param orderId
	 * */
	@RequestMapping(value = "/order/pay/wx")
	@ResponseBody
	public ResponseObject<PaymentResponseClientVO> submitWXJsAgain(
			@RequestParam(value = "orderId")String orderId, HttpServletRequest req, 
			@RequestHeader(value = "referer", required = false) String referer) {
		
		if(!enableWxJsApiPay(req))
			throw new BizException(GlobalErrorCode.UNKNOWN, "需要在高于5.0版本的微信中发起");
		
		String bizNo = this.orderService.load(orderId).getPayNo();
		if (StringUtils.isBlank(bizNo))
			return null;
		
		CashierItem item = cashierService.nextCashierItem(bizNo);
		if(null == item)
			return null;
		
		try {
			PaymentRequestVO request = new PaymentRequestVO();
	        request.setCashierItemId(item.getId());
	        request.setTradeNo(item.getBizNo());
	        request.setPayType(PaymentMode.WEIXIN);
	        request.setSubject("想去网：PO="+item.getBizNo());
	        String openId = userService.getWeixinPayOpenId();
	        if(null == openId) throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "openId为空");
	        request.setOpenId(openId);
	        request.setTradeUrl(hostName + "/order/" + cashierService.findDefaultBizId(item.getBizNo()));
			BigDecimal paidFee = this.cashierService.loadPaidFee(request.getTradeNo());
			request.setTotalFee(paidFee);
			request.setRemoteHost(PaymentController.getIpAddr(req));
			request.setRequestUrl(referer);
			PaymentResponseClientVO response = wxJsApiPayment.sign(request);
			return new ResponseObject<>(response);
		} catch (Exception e) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "支付请求失败，tradeNo=" + bizNo, e);
		}
	}
	
	private boolean enableWxJsApiPay(HttpServletRequest req){
		String ua = req.getHeader("User-Agent");
		if(!ua.contains("MicroMessenger"))
			return false;
		
		String[] ss = ua.split(" ");
		for(String s:ss){
			if(!StringUtils.isBlank(s.trim())&&s.startsWith("MicroMessenger")){
				String[] ts = s.split("/");
				String wxVersion = ts[1].split("\\.")[0];
				if(StringUtils.isBlank(wxVersion.trim()))
					return false;
				Integer i = Integer.parseInt(wxVersion);
				if(i>=5)
					return true;
			}
		}
		return false;
	}

	@RequestMapping(value = "/order/{id}")
	public String orderPay(@PathVariable("id") String orderId, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		String redirect = redirectDomain(request, response);
		if (StringUtils.isNotEmpty(redirect)) {
			return redirect;
		}

		model.addAttribute("message", "");
		return "catalog/rusherror";
	}

	@RequestMapping(value = "/paid/view")
	public String getPaidView(Model model) {
		String imgUrl = globalConfig.getProperty("wap.paidview.imgsrc", "");
		String pageUrl = globalConfig.getProperty("wap.paidview.pageurl", "");
		
		model.addAttribute("imgUrl", imgUrl);
		model.addAttribute("pageUrl", pageUrl);
		return "xiangqu/wap/order_success";
	}
	
	@ResponseBody
	@RequestMapping(value = "/order/calculateFee")
	public ResponseObject<OrderPricingVO> calculateTotalPrice(
			@RequestParam(value = "skuJson")String skuJson,
			@RequestParam(value = "zoneId")String addressId,
			@RequestParam(value = "couponJson", required = false)String couponJson) {
		return super.calculateTotalPrice(skuJson, addressId, couponJson);
	}
}
