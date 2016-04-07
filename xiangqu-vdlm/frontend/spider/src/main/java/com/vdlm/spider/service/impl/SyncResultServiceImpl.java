/**
 * 
 */
package com.vdlm.spider.service.impl;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Shop;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.service.SyncResultService;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:21:33 PM Aug 7, 2014
 */
@Component("syncResultService")
public class SyncResultServiceImpl implements SyncResultService, ApplicationContextAware {

	ApplicationContext applicationContext;

	@Override
	public void syncItem(Item item, List<Sku> skus, List<Img> imgs) throws Exception {
		final SyncResultService service = this.getService(item.getReqFrom());
		if (service != null) {
			service.syncItem(item, skus, imgs);
		}
	}

	SyncResultService getService(ReqFrom reqFrom) {
		SyncResultService service = null;
		switch (reqFrom) {
		case KKKD:
			service = this.applicationContext.getBean(SyncResultToKkkdService.class);
			break;
		case XIANGQU:
			service = this.applicationContext.getBean(SyncResultToXiangquService.class);
			break;
		default:
			System.err.println("impossible to getto here");
		}
		return service;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void syncShop(Shop shop) throws Exception {
		final SyncResultService service = this.getService(shop.getReqFrom());
		if (service != null) {
			service.syncShop(shop);
		}
	}

}
