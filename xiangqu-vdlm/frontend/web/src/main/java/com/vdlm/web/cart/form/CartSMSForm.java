package com.vdlm.web.cart.form;

/**
 * 购物车查看的表单
 * @author tonghu
 *
 */
public class CartSMSForm {
    
    //用于验证客户的手机号  因特价商品购买需求增加 2015-01-14 seker
    private String mobile = "";
    
    private String smsCode = "";

    public String getSmsCode() {
        return smsCode;
    }

    public void setShopId(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
