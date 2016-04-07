package com.vdlm.spider.wdetail;

import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.wdetail.parser.WItem;
import com.vdlm.spider.wdetail.parser.SkuInfo;

/**
 *
 * @author: chenxi
 */

public class EntityPlenter {

	public static void plent(Item item, WItem itemInfo) {
		item.setAmount(itemInfo.getAmount());
		item.setName(itemInfo.getName());
		item.setPrice(itemInfo.getPrice());
		item.setShopId(itemInfo.getShopId());
		item.setStatus(itemInfo.getStatus());
		item.setUserId(itemInfo.getUserId());
		item.setSkuProps(itemInfo.getSkuProps());
		item.setProps(itemInfo.getProps());
	}
	
	public static void plent(Sku sku, SkuInfo skuInfo) {
		sku.setAmount(skuInfo.getAmount());
		sku.setOrigSpec(skuInfo.getOrigSpec());
		sku.setPrice(skuInfo.getPrice());
		sku.setSpec(skuInfo.getSpec());
	}
}
