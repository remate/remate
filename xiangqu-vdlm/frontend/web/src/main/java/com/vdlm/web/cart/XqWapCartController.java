package com.vdlm.web.cart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestOperations;

import com.alibaba.fastjson.JSONObject;
import com.vdlm.dal.model.BankItemsMap;
import com.vdlm.dal.model.PayBankWay;
import com.vdlm.dal.model.User;
import com.vdlm.interceptor.Token;
import com.vdlm.web.ResponseObject;
import com.vdlm.web.cart.form.AddToCartForm;
import com.vdlm.web.cart.form.CartForm;
import com.vdlm.web.cart.form.CartNextForm;

@Controller
@RequestMapping("/xiangqu/wap")
public class XqWapCartController extends CartController {
    
    private static final String TEMPLATE_PREFIX = "xiangqu/wap";
    
    @Value("${xiangqu.web.site}")
    private String xiangquWebSite;
    @Autowired
	private RestOperations restTemplate;
    
    @RequestMapping(value = "/cart")
    public String cart(@ModelAttribute CartForm form, HttpServletRequest req, Device device, Model model) {
        super.cart(form, req, device, model);
        User user = getCurrentUser();
        Boolean isBind=isXqUserBindMobile(user.getExtUserId());
        model.addAttribute("havePhone", isBind);//是否有手机号
        return TEMPLATE_PREFIX + "/cart";
    }
    
    private boolean isXqUserBindMobile(String xqUserId) {
        String xqBindURL = xiangquWebSite + "/user/check/bindPhone?userId=" + xqUserId;
        String res = restTemplate.getForObject(xqBindURL, String.class, new Object[]{});
        JSONObject json = JSONObject.parseObject(res);
        return json.getIntValue("code") == 200;
    }

    @RequestMapping(value = "/cart/add")
    @ResponseBody
    public ResponseObject<Integer> add(@Valid @ModelAttribute AddToCartForm form, HttpServletRequest req, Errors errors) {
        return super.add(form, req, errors, "xiangqu.com", "wap");
    }
    
    @RequestMapping(value = "/cart/checkout")
    public String checkout(String skuId, @RequestParam(defaultValue = "0") Integer amount, HttpServletRequest request, HttpServletResponse response, Device device, Model model) {
        return super.checkout(skuId, amount, request, response, device, model);
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
        
        model.addAttribute("xiangquWebSite", xiangquWebSite);
        
        return TEMPLATE_PREFIX + "/cart_next";
    }
    
    
}
