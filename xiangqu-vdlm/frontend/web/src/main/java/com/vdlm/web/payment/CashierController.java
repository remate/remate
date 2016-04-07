package com.vdlm.web.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vdlm.dal.model.BankItemsMap;
import com.vdlm.dal.model.CashierItem;
import com.vdlm.dal.model.PayBankWay;
import com.vdlm.dal.model.User;
import com.vdlm.dal.type.PaymentChannel;
import com.vdlm.dal.type.PaymentMode;
import com.vdlm.dal.type.UserPartnerType;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.outpay.PaymentRequestVO;
import com.vdlm.service.payBank.PayBankService;
import com.vdlm.service.pricing.CouponService;
import com.vdlm.web.BaseController;

@Controller
public class CashierController extends BaseController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CouponService couponService;
	
    @Autowired
    private CashierService cashierService;
	
    @Autowired
	private PayBankService payBankService;
    
	@Value("${site.web.host.name}")
	private String hostName;
	
	@Value("${xiangqu.web.site}")
	private String xqHostName;
	
	/**
	 * 想去支付
	 * @param item
	 * @param bizNo
	 * @param redirectAttrs
	 * @return
	 */
	private String xiqngqu(CashierItem item, RedirectAttributes redirectAttrs){
		User user = super.getCurrentUser();
		if(!"xiangqu".equals(user.getPartner())){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "用户类型错误 userId=" + user.getId());
		}
		redirectAttrs.addAttribute("cashierItemId", item.getId());
		redirectAttrs.addAttribute("couponId", item.getCouponId());
		redirectAttrs.addAttribute("outUserId", user.getExtUserId());
		redirectAttrs.addAttribute("productId", item.getProductId());
		redirectAttrs.addAttribute("tradeNo", item.getBizNo());
		// redirectAttrs.addAttribute("subject", orderItem.getProductName());
		redirectAttrs.addAttribute("subject", item.getBizNo());
		redirectAttrs.addAttribute("totalFee", item.getAmount());
		return "redirect:/pay/xiangqu";
	}
	
	/**
	 * 支付宝支付
	 * @param item
	 * @param bizNo
	 * @param redirectAttrs
	 * @return
	 */
	private String alipay(CashierItem item, RedirectAttributes redirectAttrs, Device device){
		redirectAttrs.addAttribute("cashierItemId", item.getId());
	    redirectAttrs.addAttribute("tradeNo", item.getBizNo());
        redirectAttrs.addAttribute("subject", item.getBizNo());
//        redirectAttrs.addAttribute("subject", order.getOrderNo());
        redirectAttrs.addAttribute("tradeUrl", hostName + "/order/" + cashierService.findDefaultBizId(item.getBizNo()));
        redirectAttrs.addAttribute("totalFee", item.getAmount());
        if(device.isMobile()){
        	return "redirect:/pay/alipay";
        }else{
        	return "redirect:/pay/alipayPc";
        }
	}
	
	private String alipayPc(CashierItem item, RedirectAttributes redirectAttrs){
		redirectAttrs.addAttribute("cashierItemId", item.getId());
	    redirectAttrs.addAttribute("tradeNo", item.getBizNo());
        redirectAttrs.addAttribute("subject", item.getBizNo());
//        redirectAttrs.addAttribute("subject", order.getOrderNo());
        redirectAttrs.addAttribute("tradeUrl", hostName + "/order/" + cashierService.findDefaultBizId(item.getBizNo()));
        redirectAttrs.addAttribute("totalFee", item.getAmount());
		return "redirect:/pay/alipayPc";
	}
	
	/**
	 * 微信支付
	 * @param item
	 * @param bizNo
	 * @param redirectAttrs
	 * @return
	 */
	private String tenpay(CashierItem item, RedirectAttributes redirectAttrs){
		redirectAttrs.addAttribute("cashierItemId", item.getId());
	    redirectAttrs.addAttribute("tradeNo", item.getBizNo());
        redirectAttrs.addAttribute("subject", item.getBizNo());
        redirectAttrs.addAttribute("tradeUrl", hostName + "/order/" + cashierService.findDefaultBizId(item.getBizNo()));
        redirectAttrs.addAttribute("totalFee", item.getAmount());
		return "redirect:/pay/tenpay";
	}
	
	private String union(CashierItem item, RedirectAttributes redirectAttrs){
//		OrderItem orderItem = order.getOrderItems().get(0);
		if(StringUtils.isNotBlank(item.getAgreementId()) && !"0".equals(item.getAgreementId())){
			//协议支付
			redirectAttrs.addAttribute("cashierItemId", item.getId());
			redirectAttrs.addAttribute("tradeNo", item.getBizNo());
	        redirectAttrs.addAttribute("subject", item.getBizNo());
	        redirectAttrs.addAttribute("tradeUrl", hostName + "/order/" + cashierService.findDefaultBizId(item.getBizNo()));
	        redirectAttrs.addAttribute("totalFee", item.getAmount());
	        redirectAttrs.addAttribute("payAgreementId", item.getAgreementId());
	        redirectAttrs.addAttribute("bankCode", item.getBankCode());
	        redirectAttrs.addAttribute("cardType", item.getPaymentChannel());
			return "redirect:/pay/umpay/agreement";
		}else if(StringUtils.isNotBlank(item.getBankCode())){
			//首次支付
			redirectAttrs.addAttribute("cashierItemId", item.getId());
			redirectAttrs.addAttribute("tradeNo", item.getBizNo());
	        redirectAttrs.addAttribute("subject", item.getBizNo());
	        redirectAttrs.addAttribute("tradeUrl", hostName + "/order/" + cashierService.findDefaultBizId(item.getBizNo()));
	        redirectAttrs.addAttribute("totalFee", item.getAmount());
	        redirectAttrs.addAttribute("payAgreementId", item.getAgreementId());
	        redirectAttrs.addAttribute("bankCode", item.getBankCode());
	        redirectAttrs.addAttribute("cardType", item.getBankName());
			return "redirect:/pay/umpay";
		}else{
			//未选择银行卡
			redirectAttrs.addAttribute("cashierItemId", item.getId());
			redirectAttrs.addAttribute("productId", item.getProductId());
			redirectAttrs.addAttribute("tradeNo", item.getBizNo());
			redirectAttrs.addAttribute("subject", item.getBizNo());
			redirectAttrs.addAttribute("totalFee", item.getAmount());
			redirectAttrs.addAttribute("cashierItemId", item.getId());
			return "redirect:/pay/unionBankList";
		}
	}
	
	private String nextUrl(CashierItem item, String bizNo, String bizType, Device device, Model model, RedirectAttributes redirectAttrs){
		if("order".equals(bizType)){
			//TODO应该调交易系统
			if(item == null){
				List<CashierItem> items = cashierService.listByBizNo(bizNo);
				String[] batchBizNos = items.get(0).getBatchBizNos().split(",");
//				String orderNo = batchBizNos[0];
//				OrderVO order = orderService.loadByOrderNo(orderNo);
//				model.addAttribute("order", order);
				List<OrderVO> orders = new ArrayList<OrderVO>();
				OrderVO order = null;
				for(String orderNo : batchBizNos){
					order = orderService.loadByOrderNo(orderNo);
					orders.add(order);
				}
				model.addAttribute("order", order);
				model.addAttribute("orders", orders);
				
				if (device != null && (device.isMobile() || device.isTablet())) {
					return "/xiangqu/wap/order_success";
				}
				
				if(UserPartnerType.XIANGQU == order.getPartnerType()){
					if(device.isNormal()) {
					    return "redirect:" + xqHostName + "/order/pay/"+order.getId()+".html?status=1";
					} else {
					    return "/xiangqu/pay_call_back";
					}
				}
				return "/pay/call_back";
			}
			
			switch (item.getPaymentMode()) {
			case XIANGQU:
				return xiqngqu(item, redirectAttrs);
			case ALIPAY:
				return alipay(item, redirectAttrs, device);
			case ALIPAY_PC:
				return alipayPc(item, redirectAttrs);				
			case UNION:
			case UMPAY:
				return union(item, redirectAttrs);
			case WEIXIN:
				return tenpay(item, redirectAttrs);
			default:
				return null;
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/cashier/callback/{bizNo}")
	public String payCallback(HttpServletRequest req, @PathVariable("bizNo") String bizNo, String bizType, 
			PaymentMode paymentMode, String paidStatus, String cashierItemId, Device device, Model model, RedirectAttributes redirectAttrs) {

		if("success".equals(paidStatus)){
			CashierItem nextCashierItem = null;
			try{
				nextCashierItem = cashierService.paid(bizNo, bizType, paymentMode, cashierItemId);
			}catch(BizException e){
				throw e;
			}catch(UnexpectedRollbackException e1){
				log.error("cashier paid failed bizNo=[{}], [{}]", bizNo, e1);
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "支付失败");
			}
			return nextUrl(nextCashierItem, bizNo, bizType, device, model, redirectAttrs);
		}else{
			List<CashierItem> items = cashierService.listByBizNo(bizNo);
			String[] batchBizNos = items.get(0).getBatchBizNos().split(",");
			String orderNo = batchBizNos[0];
			OrderVO order = orderService.loadByOrderNo(orderNo);
				
			if(UserPartnerType.XIANGQU == order.getPartnerType()){
				return "redirect:" + xqHostName + "/order/pay/"+order.getId()+".html?status=0";
			}else{
			
				//跳转到失败页面
				log.error("cashier failed bizNo=[" + bizNo + "] paymentMode=[" + paymentMode + "]" );
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "支付失败");
			}
		}
	}
	
	@RequestMapping(value = "/cashier/notify/{bizNo}")
	public void payNotify(@PathVariable("bizNo") String bizNo, String bizType, 
			PaymentMode paymentMode, RedirectAttributes redirectAttrs) {
		cashierService.paid(bizNo, bizType, paymentMode);
	}
	
	/**
	 * 单个订单支付
	 * @param bizNo
	 * @param bizType
	 * @param model
	 * @param redirectAttrs
	 * @return
	 */
	@RequestMapping(value = "/cashier/{bizNo}")
	public String pay(@PathVariable("bizNo") String bizNo, String bizType, String batchBizNos,  
			Device device, Model model, RedirectAttributes redirectAttrs) {
		CashierItem item = cashierService.nextCashierItem(bizNo);
		return nextUrl(item, bizNo, bizType, device, model, redirectAttrs);
	}
	
	/**
	 * 批量支付(多订单合并支付)
	 * @param bizNo
	 * @param bizType
	 * @param model
	 * @param redirectAttrs
	 * @return
	 */
	@RequestMapping(value = "/cashier")
	public String batchPay(String bizNos, String bizType, Device device, Model model, RedirectAttributes redirectAttrs) {
		
		CashierItem item = cashierService.nextCashierItem(bizNos);
		return nextUrl(item, bizNos, bizType, device, model, redirectAttrs);
	}
	
	/**
	 * 选择银行卡后回调
	 * @param bizNo
	 * @param bizType
	 * @param model
	 * @param redirectAttrs
	 * @return
	 */
	@RequestMapping(value = "/cashier/{bizNo}/{cashierItemId}/{cardType}/{bankCode}/{paymentMode}")
	public String bank(@PathVariable("bizNo") String bizNo,@PathVariable("cashierItemId") String cashierItemId, 
			@PathVariable("cardType")PaymentChannel cardType, @PathVariable("bankCode")String bankCode,
			@PathVariable("paymentMode")PaymentMode paymentMode, Model model, RedirectAttributes redirectAttrs) {
		CashierItem item = new CashierItem();
		item.setId(cashierItemId);
//		item.setPaymentChannel(cardType);
		item.setBankName(cardType.toString());
		item.setBankCode(bankCode);
		item.setPaymentMode(paymentMode);
		cashierService.update(item);
		return "redirect:/cashier/" + bizNo + "?bizType=order";
	}
	
	@RequestMapping(value = "/pay/unionBankList")
	public String queryPayBankForShow(HttpServletRequest req, HttpServletResponse resp,	@Validated @ModelAttribute PaymentRequestVO request, Model mode) {
		String tradeNo = request.getTradeNo();
		String subject = request.getSubject();
		String tradeUrl = request.getTradeUrl();
		String cashierItemId = req.getParameter("cashierItemId");
		CashierItem cashierItem = cashierService.load(cashierItemId);
		BigDecimal totalFee = request.getTotalFee();
//		OrderVO order = orderService.loadByOrderNo(tradeNo);
		List<PayBankWay> hotBanksCreditCard = payBankService.queryHotPayBanksCreditCard();
		List<PayBankWay> allBanksCreditCard = payBankService.queryAllPayBanksCreditCard();
		List<BankItemsMap> creditCardBanks = null;
		creditCardBanks = trans4Show(hotBanksCreditCard, allBanksCreditCard);
		
		List<PayBankWay> hotBanksDebitCard = payBankService.queryHotPayBanksDebitCard();
		List<PayBankWay> allBanksDebitCard = payBankService.queryAllPayBanksDebitCard();
		List<BankItemsMap> debitCardBanks = null;
		debitCardBanks = trans4Show(hotBanksDebitCard, allBanksDebitCard);
		
		mode.addAttribute("tradeNo", tradeNo);
		mode.addAttribute("tradeUrl", tradeUrl);
		mode.addAttribute("subject", subject);
		mode.addAttribute("totalFee", totalFee);
		mode.addAttribute("cashierItemId", req.getParameter("cashierItemId"));
		mode.addAttribute("creditCardBanks", creditCardBanks);
		mode.addAttribute("debitCardBanks", debitCardBanks);
		if(StringUtils.equalsIgnoreCase(cashierItem.getPartner(), "xiangqu"))
			return "xiangqu/unionBankList";
		else
			return "pay/unionBankList";
	}

	
	private List<BankItemsMap> trans4Show(List<PayBankWay> hotBanks, List<PayBankWay> allBanks){
		List<BankItemsMap> result = new ArrayList<BankItemsMap>();
		if (hotBanks != null && hotBanks.size() > 0) {
			BankItemsMap map = new BankItemsMap();
			map.setKeyName("hot");
			map.setValueList(hotBanks);
			result.add(map);
		}

		List<String> keys = new ArrayList<String>();
		Map<String, List<PayBankWay>> temp = new HashMap<String, List<PayBankWay>>();
		if (allBanks != null && allBanks.size() > 0) {
			for (int i = 0; i < allBanks.size(); i++) {
				PayBankWay bean = allBanks.get(i);
				String key = bean.getStartWith();
				if (!keys.contains(key)) {
					keys.add(key);
					List<PayBankWay> list = new ArrayList<PayBankWay>();
					list.add(bean);
					temp.put(key, list);
				} else {
					temp.get(key).add(bean);
				}
			}
		}
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			List<PayBankWay> list = temp.get(key);
			BankItemsMap map = new BankItemsMap();
			map.setKeyName(key);
			map.setValueList(list);
			result.add(map);
		}
		return result;
	}
}
