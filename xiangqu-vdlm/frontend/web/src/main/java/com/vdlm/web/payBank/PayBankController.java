package com.vdlm.web.payBank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vdlm.dal.model.BankItemsMap;
import com.vdlm.dal.model.PayBankWay;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.outpay.PaymentRequestVO;
import com.vdlm.service.payBank.PayBankService;
import com.vdlm.web.BaseController;

/**
 * 收银台银行卡支付，获取银行相关信息
 * 
 * @author charon
 */
@Controller
public class PayBankController extends BaseController {

	@Autowired
	private PayBankService payBankService;
	
	@Autowired
    private CashierService cashierService;

//	@RequestMapping(value = "/paybank/submit")
//	public String payAction(HttpServletRequest req,
//			HttpServletResponse resp,
//			@Validated @ModelAttribute PayBankForm request, Model mode){
//		List<CashierItem> couponItem = createCashierChannel(request);
//		cashierService.save(couponItem, request.getTradeNo());
//		return "redirect:/cashier/" + request.getTradeNo() + "?bizType=order";
////		Field error in object 'paymentRequestVO' on field 'subject': 不能为null]
////				Field error in object 'paymentRequestVO' on field 'tradeNo': rejected value [null]; codes [NotNull.paymentRequestVO.tradeNo,NotNull.tradeNo,NotNull.java.lang.String,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [paymentRequestVO.tradeNo,tradeNo]; arguments []; default message [tradeNo]]; default message [不能为null]
////				Field error in object 'paymentRequestVO' on field 'totalFee': rejected value [null]; codes [NotNull.paymentRequestVO.totalFee,NotNull.totalFee,NotNull.java.math.BigDecimal,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [paymentRequestVO.totalFee,totalFee]; arguments []; default message [totalFee]]; default message [不能为null]] on: , {}
//	}
	
	@RequestMapping(value = "/pay/bankList/{cardType}")
	public String queryCreditCardBankForShow(@PathVariable("cardType") String cardType, HttpServletRequest req,
			HttpServletResponse resp, RedirectAttributes redirectAttrs, Model mode){
		List<PayBankWay> hotBanksCard = null;
		List<PayBankWay> allBanksCard = null;
		mode.addAttribute("tradeNo", req.getParameter("tradeNo"));
		mode.addAttribute("totalFee", req.getParameter("totalFee"));
		if(cardType.equalsIgnoreCase("creditCard")){
			hotBanksCard = payBankService.queryHotPayBanksCreditCard();
			allBanksCard = payBankService.queryAllPayBanksCreditCard();
		}else if(cardType.equalsIgnoreCase("debitCard")){
			hotBanksCard = payBankService.queryHotPayBanksDebitCard();
			allBanksCard = payBankService.queryAllPayBanksDebitCard();
		}else{
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "未知的卡类型："+cardType);
		}
		List<BankItemsMap> bankList = trans4Show(hotBanksCard, allBanksCard);
		mode.addAttribute("bankList", bankList);
		return "pay/cardBanks";
	}
	
	@RequestMapping(value = "/pay/paybankList")
	public String queryPayBankForShow(HttpServletRequest req,
			HttpServletResponse resp,
			@Validated @ModelAttribute PaymentRequestVO request, Model mode) {
		String tradeNo = request.getTradeNo();
		String subject = request.getSubject();
		String tradeUrl = request.getTradeUrl();
		BigDecimal totalFee = request.getTotalFee();
		
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
		mode.addAttribute("creditCardBanks", creditCardBanks);
		mode.addAttribute("debitCardBanks", debitCardBanks);
		return "pay/bankList";
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
	
//	private List<CashierItem> createCashierChannel(PayBankForm form){
//		List<CashierItem> items = new ArrayList<CashierItem>();
//		if(form.getTotalFee().compareTo(BigDecimal.ZERO)==1){
//			//记录支付
//			CashierItem item = new CashierItem();
//			item.setBizNo(form.getTradeNo());
//			item.setBankCode(form.getBankCode());
//			item.setBankName(form.getBankName());
//			item.setAmount(form.getTotalFee());
////			item.setThirdVoucherName("");
////			item.setThirdVoucherId("");
////			item.setThirdVouchers(BigDecimal.ZERO);
//			item.setAgreementId(null);
//			item.setPaymentMode(form.getPayType());
//			item.setStatus(PaymentStatus.PENDING);
//			if(form.getCardType()==BankCardType.CREDITCARD){
//	    		item.setPaymentChannel(PaymentChannel.CREDITCARD);
//	    	}else if(form.getCardType()==BankCardType.DEBITCARD){
//	    		item.setPaymentChannel(PaymentChannel.DEBITCARD);
//	    	}else{
//	    		item.setPaymentChannel(PaymentChannel.PLATFORM);
//	    	}
//			items.add(item);
//		}
//        return items;
//	}
}
