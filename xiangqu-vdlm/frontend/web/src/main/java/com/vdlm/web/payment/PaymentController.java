package com.vdlm.web.payment;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vdlm.dal.model.CashierItem;
import com.vdlm.dal.model.Order;
import com.vdlm.dal.model.User;
import com.vdlm.dal.status.PaymentStatus;
import com.vdlm.dal.type.PaymentMode;
import com.vdlm.dal.type.UserPartnerType;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.interceptor.Token;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.outpay.PaymentRequestVO;
import com.vdlm.service.outpay.PaymentResponseClientVO;
import com.vdlm.service.outpay.PaymentResponseVO;
import com.vdlm.service.outpay.ThirdPartyPayment;
import com.vdlm.service.outpay.impl.AliPaymentImpl;
import com.vdlm.service.outpay.impl.AliPaymentPCImpl;
import com.vdlm.service.outpay.impl.SumPaymentImpl;
import com.vdlm.service.outpay.impl.TenPaymentImpl;
import com.vdlm.service.outpay.impl.UmPaymentImpl;
import com.vdlm.service.outpay.impl.WxJsApiPaymentImpl;
import com.vdlm.service.outpay.impl.tenpay.CommonUtil;
import com.vdlm.web.BaseController;
import com.vdlm.web.vo.Json;

@Controller
public class PaymentController extends BaseController {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ThirdPartyPayment aliPayment;

	@Autowired
	private ThirdPartyPayment tenPayment;
	
	@Autowired
	private ThirdPartyPayment umPayment;
	
	@Autowired
	private ThirdPartyPayment sumPayment;
	
	@Autowired
	private ThirdPartyPayment xqPayment;

	@Autowired
	private ThirdPartyPayment esunPayment;
	
	@Autowired
	private CashierService cashierService;

	@Autowired
	private OrderService orderService;

//	@Value("${xiangqu.cart.host.url}")
//    String xiangquHost;
	
	@Autowired
	private ThirdPartyPayment aliPaymentPc;
	
	@RequestMapping("/pay/alipayPc")
	public void alipayPc(HttpServletRequest req, HttpServletResponse resp, @Validated @ModelAttribute PaymentRequestVO request, Model model) {
		log.info("alipay pc pay request bizNo=[" + request.getTradeNo() + "]");
		try {
			BigDecimal paidFee = this.cashierService.loadPaidFee(request.getTradeNo());
			request.setTotalFee(paidFee);
			aliPaymentPc.payRequest(req, resp, request);
		} catch (Exception e) {
			log.error("alipayPc支付请求失败，tradeNo=" + request.getTradeNo(), e);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "支付请求失败，tradeNo=" + request.getTradeNo(), e);
		}
	}
	
	@RequestMapping(AliPaymentPCImpl.call_back_url)
	public String alipayPcCallback(HttpServletRequest req, HttpServletResponse resp, RedirectAttributes redirectAttrs) {
		log.info("alipay pc call back url is called bizNo=[" + req.getParameter("out_trade_no") + "]");
		PaymentResponseVO paymentVO = aliPaymentPc.payCallback(req, resp);
		if (paymentVO == null) {
			log.warn("payment alipy pc response is null! bizNo=[" + req.getParameter("out_trade_no") + "]");
			redirectAttrs.addAttribute("paidStatus", "fail");
		}else{
			redirectAttrs.addAttribute("paidStatus", "success");
		}
		redirectAttrs.addAttribute("bizType", "order");
		redirectAttrs.addAttribute("bizNo", req.getParameter("out_trade_no"));
		redirectAttrs.addAttribute("paymentMode", PaymentMode.ALIPAY);
		
		return getCallbackRedirectUrl(req.getParameter("out_trade_no"));// "redirect:/cashier/callback/" + req.getParameter("out_trade_no");
	}
	
	@RequestMapping(WxJsApiPaymentImpl.callBack_url)
	public String weiXinCallBack(@RequestParam(value = "bizNo")String bizNo, Model model) {
		User user = getCurrentUser();
		List<CashierItem> items = cashierService.listByBizNo(bizNo);
		for (int i = 0, arraySize = items.size(); i < arraySize; i++) {
			if (!items.get(i).getUserId().equalsIgnoreCase(user.getId())) {
				throw new BizException(GlobalErrorCode.UNAUTHORIZED, "非法操作");
			}
		}
		String[] batchBizNos = items.get(0).getBatchBizNos().split(",");
		List<OrderVO> orders = new ArrayList<OrderVO>();
		OrderVO order = null;
		for(String orderNo : batchBizNos){
			order = orderService.loadByOrderNo(orderNo);
			orders.add(order);
		}
		model.addAttribute("order", order);
		model.addAttribute("orders", orders);
		
		return "/xiangqu/wap/order_success";
	}
	
	@RequestMapping(AliPaymentPCImpl.notify_url)
	public void alipayPcNotify(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		log.info("alipay pc notify url is called bizNo=[" + req.getParameter("out_trade_no") + "]");
		PaymentResponseVO paymentVO = aliPaymentPc.payNotify(req, resp);
		if (paymentVO == null){
			responseBody("fail", resp);
			log.info("alipay notify url is failed bizNo=[" + req.getParameter("out_trade_no") + "]");
			return;
		}

		responseBody("success", resp);
	}
	
	/**
	 * 协议支付，跳转到短信页面，类似umpay
	 * 协议支付 第一步
	 * @param req
	 * @param resp
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/pay/umpay/agreement")
	public String umpayAgreement(HttpServletRequest req, HttpServletResponse resp, @Validated @ModelAttribute PaymentRequestVO request, Model model) {
		
		try {
			request.setUserId(super.getCurrentUser().getId());
			PaymentResponseVO paymentVO = umPayment.payRequest(req, resp, request);
			model.addAttribute("paymentVO", paymentVO);
		} catch (Exception e) {
			// log.error("支付请求失败，tradeNo=" + request.getTradeNo(), e);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "支付请求失败，tradeNo=" + request.getTradeNo()+","+e.getMessage(), e);
		}
		return "pay/sms";
	}
	
	/**
	 * 协议支付再次发送短信
	 * 
	 * @param req
	 * @param resp
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/pay/umpay/sendSmsAgain")
	public Json sendSmsAgain(HttpServletRequest req, HttpServletResponse resp, @Validated @ModelAttribute PaymentRequestVO request) {
		Json json = new Json();
		try {
			umPayment.sendSmsAgain(request);
			json.setMsg("发送成功");
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("再次发送失败;" + e.getMessage());
			log.error("支付请求再次发送短信失败，tradeNo=" + request.getTradeNo()+","+e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 首次支付
	 * @param req
	 * @param resp
	 * @param request
	 * @param model
	 */
	@RequestMapping("/pay/umpay")
	public void umpay(HttpServletRequest req, HttpServletResponse resp, @Validated @ModelAttribute PaymentRequestVO request, Model model) {
		
		try {
			BigDecimal paidFee = this.cashierService.loadPaidFee(request.getTradeNo());
			request.setTotalFee(paidFee);
			request.setUserId(super.getCurrentUser().getId());
			umPayment.payRequest(req, resp, request);
		} catch (Exception e) {
			// log.error("支付请求失败，tradeNo=" + request.getTradeNo(), e);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "支付请求失败，tradeNo=" + request.getTradeNo(), e);
		}
	}
	
	/**
	 * 输入短信后的确认页面，类似callback操作
	 * 协议支付 第二步
	 * @param req
	 * @param resp
	 * @param request
	 * @param model
	 */
	@ResponseBody
	@RequestMapping("/pay/umpayConfirm")
	public Json umpayConfirm(HttpServletRequest req, HttpServletResponse resp, @Validated @ModelAttribute PaymentRequestVO request, Model model) {
		log.info("umpay call back url is called " + request);
		Json json = new Json();
		PaymentResponseVO paymentVO = null;
		try {
		    User user = getCurrentUser();
			request.setUserId(user.getId()); 
			paymentVO = umPayment.payConfirm(req, resp, request);
			json.setObj(paymentVO.getBillNo());
			json.setRc(Json.RC_SUCCESS);
			json.setMsg("支付成功");
			log.info("umpay call back url is success " + request);
			
		} catch (Exception e) {
			log.info("umpay call back url is failed " + request);
			json.setRc(Json.RC_FAILURE);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 协议支付成功后跳转
	 * 协议支付  第三次
	 * @param req
	 * @param resp
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/pay/umpayConfirmDone")
	public String umpayConfirmDone(HttpServletRequest req, HttpServletResponse resp, String orderNo, RedirectAttributes redirectAttrs) {
//		OrderVO order = orderService.loadByOrderNo(orderNo);
//		model.addAttribute("order", order);
//		
//		if(UserPartnerType.XIANGQU == order.getPartnerType())
//			return "/xiangqu/pay_call_back";
//		return "/pay/call_back";
		
		redirectAttrs.addAttribute("paidStatus", "success");
		redirectAttrs.addAttribute("bizType", "order");
		redirectAttrs.addAttribute("bizNo", orderNo);
		redirectAttrs.addAttribute("paymentMode", PaymentMode.UMPAY);
		//return "redirect:/cashier/callback/" + orderNo;
		return getCallbackRedirectUrl(orderNo);
	}

	@RequestMapping(UmPaymentImpl.call_back_url)
	public String umpayCallback(HttpServletRequest req, HttpServletResponse resp, RedirectAttributes redirectAttrs) {
		
		log.info("umpay call back url is called orderNo=[" + req.getParameter("order_id") + "]");
		PaymentResponseVO paymentVO = umPayment.payCallback(req, resp);
		if (paymentVO == null) {
			log.warn("payment response is null! orderNo=[" + req.getParameter("order_id") + "]");
			redirectAttrs.addAttribute("paidStatus", "fail");
		}else{
			redirectAttrs.addAttribute("paidStatus", "success");
		}
		redirectAttrs.addAttribute("bizType", "order");
		redirectAttrs.addAttribute("bizNo", req.getParameter("order_id"));
		redirectAttrs.addAttribute("paymentMode", PaymentMode.UMPAY);
		
		//return "redirect:/cashier/callback/" + req.getParameter("order_id");
		return getCallbackRedirectUrl(req.getParameter("order_id"));
	}
	
	/**
	 * 首次支付和协议首次最后的notify
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	@RequestMapping(UmPaymentImpl.notify_url)
	public void umpayNotify(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("umpay notify url is called bizNo=[" + req.getParameter("order_id") + "]");
		PaymentResponseVO paymentVO = umPayment.payNotify(req, resp);
		if (paymentVO.getBillStatus() != PaymentStatus.SUCCESS){
			log.error("umpay notify url is failed bizNo=[" + req.getParameter("order_id") + "]");
		}else
			log.info("umpay notify url is success bizNo=[" + req.getParameter("order_id") + "]");
		
	    String html = "<html><head><META NAME=\"MobilePayPlatform\" CONTENT=\""+ paymentVO.getRespDate() + "\" /> <title>result</title></head></html>";
		responseBody(html, resp);
	}
	
	@RequestMapping("/pay/umRefund")
	public void umRefund(HttpServletRequest req, HttpServletResponse resp, @Validated @ModelAttribute PaymentRequestVO request, Model model) {
		try {
			PaymentResponseVO paymentVO = umPayment.payRefund(req, resp, request);
			umPayment.savePayResult( paymentVO );
		} catch (Exception e) {
			// log.error("支付请求失败，tradeNo=" + request.getTradeNo(), e);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "退款请求失败，tradeNo=" + request.getTradeNo(), e);
		}
	}
	
	@RequestMapping(UmPaymentImpl.refund_notify_url)
	public void umpayRefundNotify(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		log.info("umpay refund notify url is called");
		PaymentResponseVO paymentVO = umPayment.payNotify(req, resp);
		if (paymentVO == null){
			responseBody("fail", resp);
			log.info("umpay refund notify url is failed");
			return;
		}
		log.info(paymentVO.toString());
		boolean success = false;//payCallback(paymentVO, PaymentMode.UMPAY, model);
		log.info("umpay notify url is " + ((success? "success" : "fail")));
		if (success) {
			umPayment.savePayResult( paymentVO );
		}
//		boolean success = true;
//		responseBody(success ? "success" : "fail", resp);
	}
	
	@RequestMapping("/pay/sumpay")
	public void sumpay(HttpServletRequest req, HttpServletResponse resp, @Validated @ModelAttribute PaymentRequestVO request, Model model){
		try {
			sumPayment.payRequest(req, resp, request);
		} catch (Exception e) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "支付请求失败，tradeNo=" + request.getTradeNo(), e);
		}
	}
	
	@RequestMapping(SumPaymentImpl.call_back_url)
	public String sumpayCallback(HttpServletRequest req, HttpServletResponse resp, Model model) {
		log.info("alipay call back url is called");
		PaymentResponseVO paymentVO = aliPayment.payCallback(req, resp);
//		payCallback(paymentVO, PaymentMode.ALIPAY, model);
		OrderVO order = (OrderVO)model.asMap().get("order");
		if(UserPartnerType.XIANGQU == order.getPartnerType())
			return "/xiangqu/pay_call_back";
		return "/pay/call_back";
	}
	
	@RequestMapping(SumPaymentImpl.notify_url)
	public void sumpayNotify(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		log.info("alipay notify url is called");
		PaymentResponseVO paymentVO = aliPayment.payNotify(req, resp);
		if (paymentVO == null){
			responseBody("fail", resp);
			log.info("alipay notify url is failed");
			return;
		}
		log.info(paymentVO.toString());
		boolean success = false;//payCallback(paymentVO, PaymentMode.ALIPAY, model);
		log.info("alipay notify url is " + ((success? "success" : "fail")));
		if (success) {
			aliPayment.savePayResult( paymentVO );
		}
		responseBody(success ? "success" : "fail", resp);
	}
	
	@RequestMapping("/pay/alipay")
	public void alipay(HttpServletRequest req, HttpServletResponse resp, @Validated @ModelAttribute PaymentRequestVO request, Model model) {
		try {
			BigDecimal paidFee = this.cashierService.loadPaidFee(request.getTradeNo());
			request.setTotalFee(paidFee);
			aliPayment.payRequest(req, resp, request);
		} catch (Exception e) {
			log.error("支付请求失败，tradeNo=" + request.getTradeNo(), e);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "支付请求失败，tradeNo=" + request.getTradeNo(), e);
		}
	}
	
	
	@RequestMapping("/pay/esunpay")
	public void esunpay(HttpServletRequest req, HttpServletResponse resp, @Validated @ModelAttribute PaymentRequestVO request, Model model) {
		try {
			esunPayment.payRequest(req, resp, request);
		} catch (Exception e) {
			log.error("支付请求失败，tradeNo=" + request.getTradeNo(), e);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "支付请求失败，tradeNo=" + request.getTradeNo(), e);
		}
	}
	
	@RequestMapping("/pay/esun/notify")
	public void esunpayNotify(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		log.info("esunpay notify url is called bizNo=[" + req.getParameter("out_trade_no") + "]");
		for (Enumeration names = req.getParameterNames(); names
				.hasMoreElements(); ) {
			String name = (String) names.nextElement();
			String values = req.getParameter(name);
			System.out.println("name:" + name + "  values=" + values);
		}
		PaymentResponseVO paymentVO = aliPayment.payNotify(req, resp);
		if (paymentVO == null){
			responseBody("fail", resp);
			log.info("alipay notify url is failed bizNo=[" + req.getParameter("out_trade_no") + "]");
			return;
		}

		responseBody("success", resp);
	}
	

	protected String getCallbackRedirectUrl(String bizNo){
//		Order order = orderService.loadByOrderNo(bizNo);
//		List<CashierItem> items = cashierService.listByBizNo(bizNo);
//		if(UserPartnerType.XIANGQU == items.get(0).getPartnerType() ){
//			return "redirect:"+xiangquHost+"/cashier/callback/" + bizNo;
//		}else{
//		}
		return "redirect:/cashier/callback/" + bizNo;
	}
	
	@RequestMapping(AliPaymentImpl.call_back_url)
	public String alipayCallback(HttpServletRequest req, HttpServletResponse resp, RedirectAttributes redirectAttrs) {
		log.info("alipay call back url is called orderNo=[" + req.getParameter("out_trade_no") + "]");
		PaymentResponseVO paymentVO = aliPayment.payCallback(req, resp);
		if (paymentVO == null) {
			log.warn("payment response is null! orderNo=[" + req.getParameter("out_trade_no") + "]");
			redirectAttrs.addAttribute("paidStatus", "fail");
		}else{
			redirectAttrs.addAttribute("paidStatus", "success");
		}
		redirectAttrs.addAttribute("bizType", "order");
		redirectAttrs.addAttribute("bizNo", req.getParameter("out_trade_no"));
		redirectAttrs.addAttribute("paymentMode", PaymentMode.ALIPAY);
		
		return getCallbackRedirectUrl(req.getParameter("out_trade_no"));// "redirect:/cashier/callback/" + req.getParameter("out_trade_no");
		//payCallback(paymentVO, PaymentMode.ALIPAY, model);
		//OrderVO order = (OrderVO)model.asMap().get("order");
		
//		
//		if (paymentVO == null) {
//			log.warn("payment response is null!");
//			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "payment response is null!");
//		}
//		boolean success = true;
//		OrderVO order = orderService.loadByOrderNo(paymentVO.getBillNo());
//		if(order == null){
//			log.warn("order is null!");
//			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "order is null!");
//		}
//		
//		if (OrderStatus.SUBMITTED.equals(order.getStatus())) {
//			success = processPayment(paymentVO, mode);
//		}
//		model.addAttribute("paymentResponse", paymentVO);
//		model.addAttribute("order", order);
//		model.addAttribute("payStatus", success);
//		return success;
	}
	
	@RequestMapping(AliPaymentImpl.notify_url_app)
	public void alipayNotifyApp(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		log.info("alipay app notify url is called notify_data=[" + req.getParameter("notify_data") + "]");
		
		PaymentResponseVO paymentVO = aliPayment.payNotifyApp(req, resp);
		if (paymentVO == null){
			responseBody("fail", resp);
			log.info("alipay app notify url is failed notify_data=[" + req.getParameter("notify_data") + "]");
			return;
		}

		responseBody("success", resp);
	}
	
	@RequestMapping(AliPaymentImpl.notify_url)
	@Token(save = true)
	public void alipayNotify(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		log.info("alipay notify url is called notify_data=[" + req.getParameter("notify_data") + "]");
		
		PaymentResponseVO paymentVO = aliPayment.payNotify(req, resp);
		if (paymentVO == null){
			responseBody("fail", resp);
			log.info("alipay notify url is failed notify_data=[" + req.getParameter("notify_data") + "]");
			return;
		}

		responseBody("success", resp);
	}

	@RequestMapping(AliPaymentImpl.merchant_url + "/{orderId}")
	public String alipayUndone(@PathVariable("orderId") String orderId, Model model) {
		// TODO 该方法须重写
		List<CashierItem> items = cashierService.listByBizNo(orderId);
		String batchBizNos = items.get(0).getBatchBizNos();
		String orderNo = batchBizNos.split(",")[0];
		OrderVO order = orderService.loadByOrderNo(orderNo);
		model.addAttribute("order", order);
		
		return "/pay/undone";
	}

	@RequestMapping("/pay/xiangqu")
	public String xiangqu(HttpServletRequest req, HttpServletResponse resp, 
			@Validated @ModelAttribute PaymentRequestVO request, RedirectAttributes redirectAttrs) {
		log.info("xqpay payrequest url is called request=[" + request + "]");
		
		try {
			xqPayment.payRequest(req, resp, request);
			
		} catch (Exception e) {
			log.error("想去支付请求失败，request=" + request, e);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "想去支付请求失败，tradeNo=" + request.getTradeNo(), e);
		}
		redirectAttrs.addAttribute("bizType", "order");
		redirectAttrs.addAttribute("cashierItemId", request.getCashierItemId());
		redirectAttrs.addAttribute("paymentMode", PaymentMode.XIANGQU);
		redirectAttrs.addAttribute("paidStatus", "success");//如果失败，则直接抛异常
		
		//return "redirect:/cashier/callback/"+request.getTradeNo();
		return getCallbackRedirectUrl(request.getTradeNo());
	}
	
	protected void responseBody(String html, HttpServletResponse resp) throws IOException {
		PrintWriter writer = resp.getWriter();
		try {
			writer.print(html);
			writer.flush();
		} finally {
			writer.close();
		}
	}

//	private boolean payCallback(PaymentResponseVO paymentVO, PaymentMode mode, Model model) {
//		if (paymentVO == null) {
//			log.warn("payment response is null!");
//			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "payment response is null!");
////			return false; // invalid
//		}
//		boolean success = true;
//		OrderVO order = orderService.loadByOrderNo(paymentVO.getBillNo());
//		if(order == null){
//			log.warn("order is null!");
//			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "order is null!");
//		}
//		
//		if (OrderStatus.SUBMITTED.equals(order.getStatus())) {
//			success = processPayment(paymentVO, mode);
//		}
//		model.addAttribute("paymentResponse", paymentVO);
//		model.addAttribute("order", order);
//		model.addAttribute("payStatus", success);
//		return success;
//	}

//	private boolean processPayment(PaymentResponseVO paymentVO, PaymentMode mode) {
//		switch (paymentVO.getBillStatus()) {
//			case SUCCESS:
//			case FINISHED:
//				try {
//					orderService.pay(paymentVO.getBillNo(), mode, paymentVO.getBillNo());
//				} catch (BizException e) {
//					log.error("alipay callback payment, orderService returns false, " + paymentVO);
//					return false;
//				}
//				return true;
//			default:
//				log.warn("alipay callback payment, " + paymentVO);
//				// do nothing
//				return false;
//		}
//	}
//	
//	@RequestMapping("/pay/callback")
//	public String callback(String orderId, Model model) {
//		 OrderVO order = orderService.loadVO(orderId);
//		 model.addAttribute("order", order);
//		 return "/xiangqu/pay_call_back";
//	}
	
	/**
	 * 财付通的特殊性，需要返回一个xml结构的订单信息
	 * 
	 * @throws IOException 
	 */
	@RequestMapping("/pay/wxpay/notify")
	public void urlNotify(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		tenPayment.payCallback(req, resp);
	}
	
	public static String getIpAddr(HttpServletRequest request) { 
	    String ip = request.getHeader("x-forwarded-for");    
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");    
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getHeader("WL-Proxy-Client-IP");    
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getHeader("HTTP_CLIENT_IP");    
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");    
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getRemoteAddr();    
        }
        return ip; 
	}
	
	@RequestMapping("/pay/tenpay")
	public String tenpay(HttpServletRequest req, HttpServletResponse resp, @ModelAttribute PaymentRequestVO request, Model model) {
		try {
			BigDecimal paidFee = this.cashierService.loadPaidFee(request.getTradeNo());
			request.setTotalFee(paidFee);
			request.setRemoteHost(getIpAddr(req));
			PaymentResponseClientVO response = tenPayment.sign(request);
//			OrderVO order = orderService.loadByOrderNo(request.getTradeNo());
			List<CashierItem> items = cashierService.listByBizNo(request.getTradeNo());
			String[] orderNos = items.get(0).getBatchBizNos().split(",");
			List<Order> orders = new ArrayList<Order>();
			for(String orderNo : orderNos){
				orders.add(orderService.loadByOrderNo(orderNo));
			}
			model.addAttribute("payUrl", response.getPayInfo().get("code_url"));
			model.addAttribute("payment", request);
			model.addAttribute("orders", orders);
			return "/pay/weixinQrcode";
		} catch (Exception e) {
			// log.error("支付请求失败，tradeNo=" + request.getTradeNo(), e);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "支付请求失败，tradeNo=" + request.getTradeNo(), e);
		}
	}
	
	@RequestMapping(TenPaymentImpl.notify_url)
	public void tenpayNotify(HttpServletRequest req, HttpServletResponse resp, @RequestBody String body) throws IOException {
		req.setAttribute("wxNotify", body);
		PaymentResponseVO paymentVO = tenPayment.payNotify(req, resp);
//		if (paymentVO == null){
//			responseBody("fail", resp);
//			return;
//		}
//		boolean success = payCallback(paymentVO, PaymentMode.WEIXIN, model);
		HashMap<String, String> xml = new HashMap<String, String>();
		xml.put("return_code", "SUCCESS");
		xml.put("return_msg", "OK");
		responseBody(CommonUtil.ArrayToXml(xml), resp);
	}
}
