package com.vdlm.web.order;

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
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vdlm.dal.model.BankItemsMap;
import com.vdlm.dal.model.Order;
import com.vdlm.dal.model.PayBankWay;
import com.vdlm.interceptor.Token;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.payBank.PayBankService;
import com.vdlm.service.pricing.CouponService;
import com.vdlm.service.pricing.CouponVO;
import com.vdlm.utils.UserAgent;
import com.vdlm.utils.UserAgentUtils;
import com.vdlm.web.order.form.XqOrderSumbitForm;

@Controller
@RequestMapping("/xiangqu/web")
public class XqWebOrderController extends XqOrderController {
    
	@Autowired
	private PayBankService payBankService;

	@Autowired
	private CouponService couponService;
	
	@Autowired
	private OrderService orderService;
	
    @Value("${xiangqu.web.head.url}")
    private String xiangquWebHeadUrl; 
    
    @Value("${xiangqu.web.foot.url}")
    private String xiangquWebFootUrl;

    @RequestMapping(value = "/order/submit")
    @Token(remove = true)
    public String submit(@ModelAttribute XqOrderSumbitForm form, 
    		@CookieValue(value = "union_id", defaultValue = "") String unionId,
    		@RequestParam(value = "tuid", defaultValue = "") String tuId,
            Errors errors,  Device device, Model model, HttpServletRequest request, @RequestHeader(value="referer", required=false) String referer) {
    	log.info("vdlm/web/order/XqWebOrderController/order/submit");
        return super.submit(form, unionId, tuId, errors, device, model, request, referer);
    }
    
    
    @RequestMapping(value = "/order/{id}")
    public String orderPay(@PathVariable("id")String orderId, Model model, HttpServletRequest request, HttpServletResponse response) {
        String redirect = redirectDomain(request, response);
        if (StringUtils.isNotEmpty(redirect)) {
            return redirect;
        }
        
        super.orderPay(orderId, model, request);
        
        List<PayBankWay> hotBanksCreditCard = payBankService.queryHotPayBanksCreditCard();
		List<PayBankWay> allBanksCreditCard = payBankService.queryAllPayBanksCreditCard();
		List<BankItemsMap> creditCardBanks = null;
		creditCardBanks = trans4Show(hotBanksCreditCard, allBanksCreditCard);

		List<PayBankWay> hotBanksDebitCard = payBankService.queryHotPayBanksDebitCard();
		List<PayBankWay> allBanksDebitCard = payBankService.queryAllPayBanksDebitCard();
		List<BankItemsMap> debitCardBanks = null;
		debitCardBanks = trans4Show(hotBanksDebitCard, allBanksDebitCard);

		model.addAttribute("creditCardBanks", creditCardBanks);
		model.addAttribute("debitCardBanks", debitCardBanks);
        
        model.addAttribute("xiangquWebHeadUrl", xiangquWebHeadUrl);
        model.addAttribute("xiangquWebFootUrl", xiangquWebFootUrl);
        
        Order order = orderService.load(orderId);
        UserAgent ua = UserAgentUtils.getUserAgent(request);
        Map<String, List<CouponVO>> couponMap = couponService.listValidsWithExt(order.getTotalFee(), order.getOrderNo(), ua.getChannel());
        model.addAttribute("XQ_FIRST", couponMap.containsKey("XQ.FIRST")? couponMap.get("XQ.FIRST").get(0) : null);
        model.addAttribute("XQ_HONGBAO", couponMap.containsKey("XQ.HONGBAO")? couponMap.get("XQ.HONGBAO") : null);
//        List<CouponVO> vos = couponMap.containsKey("XQ.HONGBAO")? couponMap.get("XQ.HONGBAO") : null;
   		model.addAttribute("coupons", couponService.dealCoupons(couponMap));
        
        
        return "xiangqu/web/cart/order-pay";
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
