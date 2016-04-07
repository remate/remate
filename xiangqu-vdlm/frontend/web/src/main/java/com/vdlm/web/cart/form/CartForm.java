package com.vdlm.web.cart.form;

import java.util.List;
/**
 * 购物车查看的表单
 * @author tonghu
 *
 */
public class CartForm {
    
    private List<String> skuIds;
    
    //用于验证客户的手机号  因特价商品购买需求增加 2015-01-14 seker
    private String mobile = "";
    
    // 店铺id
    private String shopId;
    // 单件skuId的数量，仅仅当skuIds只有一件的时候有效
    private Integer amount;

    public List<String> getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(List<String> skuIds) {
        this.skuIds = skuIds;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    
    
}
