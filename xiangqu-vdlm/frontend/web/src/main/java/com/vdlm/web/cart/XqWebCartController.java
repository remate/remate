package com.vdlm.web.cart;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vdlm.dal.model.BankItemsMap;
import com.vdlm.dal.model.PayBankWay;
import com.vdlm.dal.model.User;
import com.vdlm.interceptor.Token;
import com.vdlm.web.ResponseObject;
import com.vdlm.web.cart.form.AddToCartForm;
import com.vdlm.web.cart.form.CartForm;
import com.vdlm.web.cart.form.CartNextForm;

@Controller
@RequestMapping("/xiangqu/web")
public class XqWebCartController extends CartController {
    
    @Value("${xiangqu.web.head.url}")
    private String xiangquWebHeadUrl; 
    
    @Value("${xiangqu.web.foot.url}")
    private String xiangquWebFootUrl;
    
    @Value("${xiangqu.web.site}")
    private String xiangquWebSite;

    private static final String TEMPLATE_PREFIX = "xiangqu/web";
    
    @RequestMapping(value = "/cart")
    public String cart(HttpServletRequest request, HttpServletResponse response, @ModelAttribute CartForm form, Device device, Model model) {
//        User user = null;
//        try {
//            user = getCurrentUser();
//        } catch (Exception e) {
//        }
//        if (user == null || user.getLoginname().startsWith("CID")) {
//            Cookie[] cookies = request.getCookies();
//            if (cookies != null) {
//                for(Cookie cookie : cookies) {
//                    if (cookie.getName().equals("KDSESSID") && request.getSession().getId().equals(cookie.getValue())) { // 
//                        log.info("KDSESSID:" + cookie.getValue());
//                        // 删除子域的domain
//                        cookie.setPath("/");
//                        cookie.setMaxAge(0);
//                        response.addCookie(cookie);  
//                    }    
//                }
//            }
//            return "redirect:" + xiangquWebSite + "/user/cart";
//        }
        
        super.cart(form, request, device, model);
        model.addAttribute("xiangquWebHeadUrl", xiangquWebHeadUrl);
        model.addAttribute("xiangquWebFootUrl", xiangquWebFootUrl);
        
        model.addAttribute("xiangquWebSite", xiangquWebSite);
        
        return TEMPLATE_PREFIX + "/cart/cart";
    }
    
    @RequestMapping(value = "/cart/buy")
    public ResponseObject<Boolean>  buy(@ModelAttribute AddToCartForm form, HttpServletRequest req, Model model) {
        return super.buy(form, req, model);
    }
    
    @RequestMapping(value = "/cart/next")
    @Token(save = true)
    public String next(HttpServletRequest request, HttpServletResponse response, @ModelAttribute CartNextForm form, Device device, Model model) {
        String redirect = redirectDomain(request, response);
        if (StringUtils.isNotEmpty(redirect)) {
            return redirect;
        }
        
        User user = null;
        try {
            user = getCurrentUser();
        } catch (Exception e) {
        }
        
        if (user == null || user.getLoginname().startsWith("CID")) {
//            Cookie[] cookies = request.getCookies();
//            if (cookies != null) {
//                for(Cookie cookie : cookies) {
//                    if (cookie.getName().equals("KDSESSID") && request.getSession().getId().equals(cookie.getValue())) { // 
//                        log.info("KDSESSID:" + cookie.getValue());
//                        // 删除子域的domain
//                        cookie.setPath("/");
//                        cookie.setMaxAge(0);
//                        response.addCookie(cookie);  
//                    }    
//                }
//            }
            return "redirect:" + xiangquWebSite + "/user/cart";
        }
        
        super.next(form, request, device, model, response);
        
        List<PayBankWay> hotBanksCreditCard = payBankService.queryHotPayBanksCreditCard();
        List<PayBankWay> allBanksCreditCard = payBankService.queryAllPayBanksCreditCard();
        List<BankItemsMap> creditCardBanks = trans4Show(hotBanksCreditCard, allBanksCreditCard);
        
        List<PayBankWay> hotBanksDebitCard = payBankService.queryHotPayBanksDebitCard();
        List<PayBankWay> allBanksDebitCard = payBankService.queryAllPayBanksDebitCard();
        List<BankItemsMap> debitCardBanks = trans4Show(hotBanksDebitCard, allBanksDebitCard);
        
        model.addAttribute("creditCardBanks", creditCardBanks);
        model.addAttribute("debitCardBanks", debitCardBanks);
//        Map<String, List<CouponVO>> coupons = couponService.listValidsWithExt();
//        model.addAttribute("coupons", couponService.listValidsWithExt());
        
        // code in super class
//    	Map<String, List<CouponVO>> coupons = couponService.listValidsWithExt();
//    	model.addAttribute("XQ_FIRST", coupons.containsKey("XQ.FIRST")?coupons.get("XQ.FIRST").get(0):null);
//    	model.addAttribute("XQ_HONGBAO", coupons.containsKey("XQ.HONGBAO")?coupons.get("XQ.HONGBAO"):null);
        
        model.addAttribute("xiangquWebHeadUrl", xiangquWebHeadUrl);
        model.addAttribute("xiangquWebFootUrl", xiangquWebFootUrl);
        
        model.addAttribute("xiangquWebSite", xiangquWebSite);
        
        return TEMPLATE_PREFIX + "/cart/cart_next";
    }
    
//  @RequestMapping(value = "/cart/next")
//  public String next(@ModelAttribute CartNextForm form, HttpServletRequest req, Device device, Model model) {
//      super.next(form, req, device, model);
//      model.addAttribute("xiangquWebHeadUrl", xiangquWebHeadUrl);
//      model.addAttribute("xiangquWebFootUrl", xiangquWebFootUrl);
//      return "xiangqu/web/cart_next";
//  }
    
//    @ModelAttribute
//    public String getXiangquWebHeadUrl() {
//        return xiangquWebHeadUrl;
//    }
//
//    @ModelAttribute
//    public String getXiangquWebFootUrl() {
//        return xiangquWebFootUrl;
//    }
}
