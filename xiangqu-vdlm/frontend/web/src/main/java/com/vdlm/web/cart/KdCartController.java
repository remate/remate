package com.vdlm.web.cart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.interceptor.Token;
import com.vdlm.service.pricing.vo.CartPricingResultVO;
import com.vdlm.service.pricing.vo.PricingResultVO;
import com.vdlm.web.ResponseObject;
import com.vdlm.web.cart.form.AddToCartForm;
import com.vdlm.web.cart.form.CartForm;
import com.vdlm.web.cart.form.CartNextForm;
import com.vdlm.web.cart.form.CartPricingForm;

@Controller
public class KdCartController extends CartController {
    
    @RequestMapping(value = "/cart")
    public String cart(@ModelAttribute CartForm form, HttpServletRequest req, Device device, Model model) {
        return super.cart(form, req, device, model);
    }

    @ResponseBody
    @RequestMapping(value = "/cart/add")
    public ResponseObject<Integer> add(@Valid @ModelAttribute AddToCartForm form, HttpServletRequest req, Errors errors) {
        return super.add(form, req, errors, "kkkd.com", "wap");
    }
    
    @ResponseBody
    @RequestMapping(value = "/cart/buy")
    public ResponseObject<Boolean> buy(@ModelAttribute AddToCartForm form, HttpServletRequest req, Model model) {
        return super.buy(form, req, model);
    }
    
    @RequestMapping(value = "/cart/checkout")
    public String checkout(String skuId, @RequestParam(defaultValue = "1") Integer amount, HttpServletRequest request, HttpServletResponse response, Device device, Model model) {
        return super.checkout(skuId, amount, request, response, device, model);
    }
    
    @ResponseBody
    @RequestMapping(value = "/cart/count")
    public ResponseObject<Integer> count(String userId, String partner, String extUserId) {
        if (StringUtils.isNotEmpty(userId)) {
            return super.count(userId);
        } else if (StringUtils.isNotEmpty(partner) &&  StringUtils.isNotEmpty(extUserId)) {
            return super.count(partner, extUserId);
        } else {
            return super.count();
        }
    }
    
    @ResponseBody
    @RequestMapping(value = "/cart/update")
    public ResponseObject<Boolean> update(@RequestParam String skuId, @RequestParam int amount, HttpServletRequest req) {
        return super.update(skuId, amount, req);
    }
    
    @ResponseBody
    @RequestMapping(value = "/cart/delete")
    public ResponseObject<Boolean> delete(String itemId) {
        return super.delete(itemId);
    }
    
    @ResponseBody
    @RequestMapping(value = "/cart/validate")
    public ResponseObject<Boolean> validate(HttpServletRequest req) {
        return super.validate(req);
    }
    
    @RequestMapping(value = "/cart/next")
    @Token(save = true)
    public String next(@ModelAttribute CartNextForm form, Device device, Model model, HttpServletRequest request, HttpServletResponse response) {
        return super.next(form, request, device, model, response);
//        User user = getCurrentUser();
//        if (user != null && StringUtils.isNotEmpty(user.getPartner())) {
//            return "redirect:/order/" + order.getId();
//        } else {
//            
//        }
    }
    
    @RequestMapping(value = "/cart/pricing")
    @ResponseBody
    public ResponseObject<PricingResultVO> pricing(@Valid @ModelAttribute CartPricingForm form, HttpServletRequest req, Model model) {
        return super.pricing(form, req, model);
    }
    
    @RequestMapping(value = "/cart/pricing-groupby-shop")
    @ResponseBody
    public ResponseObject<CartPricingResultVO> pricingGroupByShop(@Valid @ModelAttribute CartPricingForm form, HttpServletRequest req, Model model) {
        return super.pricingGroupByShop(form, req, model);
    }
}
