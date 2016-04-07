package com.vdlm.spider.bean;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.vdlm.spider.DeviceType;
import com.vdlm.spider.ParserType;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.parser.config.ParserConfigProviders;

/**
 *
 * @author: chenxi
 */

public class TaskBeanFactory {
	
	public static ShopTaskBean createShopTaskBean(String ouerUserId,
		    											String ouerShopId,
		    											String url,
		    											ShopType shopType) {
		final ShopTaskBean bean = new ShopTaskBean();
		bean.setOuerUserId(StringUtils.defaultIfBlank(ouerUserId, "0"));
		bean.setOuerShopId(StringUtils.defaultIfBlank(ouerShopId, "0"));
		bean.setShopType(shopType);
		bean.setDeviceType(DeviceType.OTHER);
		bean.setMobilePhone(StringUtils.EMPTY);
		bean.setRnd(StringUtils.EMPTY);
		bean.setRequestUrl(url);
		return bean;
	}
	
	public static ItemListTaskBean createItemListTaskBean(Long shopId,
																String ouerUserId,
															    String ouerShopId,
															    String url,
															    ShopType shopType) {
		final ItemListTaskBean bean = new ItemListTaskBean();
		bean.setShopId(shopId);
		bean.setOuerUserId(StringUtils.defaultIfBlank(ouerUserId, "0"));
		bean.setOuerShopId(StringUtils.defaultIfBlank(ouerShopId, "0"));
		bean.setShopType(shopType);
		bean.setDeviceType(DeviceType.OTHER);
		bean.setRequestUrl(url);
		return bean;
	}
	
	public static ItemListTaskBean createItemListTaskBean(Long shopId, ItemListTaskBean bean) {
		final ItemListTaskBean task = new ItemListTaskBean();
		task.setShopId(shopId);
		task.setOuerShopId(bean.getOuerShopId());
		task.setOuerUserId(bean.getOuerUserId());
		task.setShopType(bean.getShopType());
		task.setDeviceType(bean.getDeviceType());
		task.setReqFrom(bean.getReqFrom());

		task.setShopUrl(bean.getShopUrl());
		task.setShopName(bean.getShopName());
		task.setNickname(bean.getNickname());
		task.setTUserId(bean.getTUserId());
		task.setOption(bean.getOption());
		
		ParserConfigProviders.setItemListProperties(task);
		return task;
	}
	
	public static ItemTaskBean createItemTaskBean(ItemListTaskBean bean, 
														Object auctionId, 
														String auctionUrl) {
		final ItemTaskBean task = new ItemTaskBean();
		BeanUtils.copyProperties(bean, task);
		task.setItemId(auctionId.toString());
		task.setItemUrl(auctionUrl.toString());
		task.setRequestUrl(auctionUrl.toString());
		task.setRefererUrl(null);
		task.setRetryTimes(0);
		task.setParserType(ParserType.item(bean.getShopType()));
		return task;
	}
	
	public static ItemTaskBean createItemTaskBean(String ouerUserId,
														String ouerShopId,
														ShopType shopType,
														String itemId, 
														String itemUrl) {
		final ItemTaskBean task = new ItemTaskBean();
		task.setOuerShopId(ouerShopId);
		task.setOuerUserId(ouerUserId);
		task.setItemId(itemId);
		task.setItemUrl(itemId);
		task.setRequestUrl(itemUrl);
		task.setRefererUrl(null);
		task.setRetryTimes(0);
		task.setParserType(ParserType.item(shopType));
		return task;
	}
	
	public static WItemTaskBean createWItemTaskBean(ItemTaskBean bean) {
		final WItemTaskBean task = new WItemTaskBean();
		BeanUtils.copyProperties(bean, task);
		task.setItemUrl(WItemTaskBean.WDETAIL_PREFIX + bean.getItemId());
		task.setRequestUrl(WItemTaskBean.WDETAIL_PREFIX + bean.getItemId());
		return task;
	}
	
	public static DescTaskBean createDescTaskBean(ItemTaskBean bean, 
													    Long itemTaskId, 
													    String apiDescUrl,
													    int imgIndex) {
		final DescTaskBean task = new DescTaskBean();
		BeanUtils.copyProperties(bean, task);
		// TODO?
		if (!apiDescUrl.startsWith("http:") && apiDescUrl.startsWith("//")) {
			apiDescUrl = "http:" + apiDescUrl;
		}
		task.setRequestUrl(apiDescUrl);
		task.setItemTaskId(itemTaskId);
		task.setDescUrl(apiDescUrl);
		task.setImgIndex(imgIndex);
		return task;
	}
	
	public static WDescTaskBean createWDescTaskBean(ItemTaskBean bean, 
														    Long itemTaskId, 
														    String apiDescUrl,
														    int imgIndex) {
		final WDescTaskBean task = new WDescTaskBean();
		BeanUtils.copyProperties(bean, task);
		// TODO?
		if (!apiDescUrl.startsWith("http:") && apiDescUrl.startsWith("//")) {
		apiDescUrl = "http:" + apiDescUrl;
		}
		task.setRequestUrl(apiDescUrl);
		task.setItemTaskId(itemTaskId);
		task.setDescUrl(apiDescUrl);
		task.setImgIndex(imgIndex);
		return task;
	}
	
//	public static ImgTaskBean createImgTaskBean(Item item, 
//													    String apiDescUrl,
//													    int imgIndex) {
//		final ImgTaskBean task = new ImgTaskBean();
//		task.setOuerShopId(item.getOuerShopId());
//		task.setOuerUserId(item.getOuerUserId());
//		task.setShopType(item.getShopType());
//		task.setItemId(item.getItemId());
//		task.setItemUrl(item.getItemUrl());
//		if (!apiDescUrl.startsWith("http:") && apiDescUrl.startsWith("//")) {
//			apiDescUrl = "http:" + apiDescUrl;
//		}
//		task.setRequestUrl(apiDescUrl);
//		task.setItemTaskId(item.getId());
//		return task;
//	}
	
	public static ImgTaskBean createImgTaskBean(ItemTaskBean bean, 
													  Long itemTaskId, 
													  Img img) {
		final ImgTaskBean task = new ImgTaskBean();
		BeanUtils.copyProperties(bean, task);
		String imgUrl = img.getImgUrl();
		if (!imgUrl.startsWith("http:") && imgUrl.startsWith("//")) {
			imgUrl = "http:" + imgUrl;
		}
		task.setRequestUrl(imgUrl);
		task.setImg(img);
		img.setItemId(itemTaskId);
		return task;
	}
	
	public static DescTaskBean createDescTaskBean(Item item, 
		    String apiDescUrl,
		    int imgIndex) {
		final DescTaskBean task = new DescTaskBean();
		task.setOuerShopId(item.getOuerShopId());
		task.setOuerUserId(item.getOuerUserId());
		task.setShopType(item.getShopType());
		task.setItemId(item.getItemId());
		task.setItemUrl(item.getItemUrl());
		if (!apiDescUrl.startsWith("http:") && apiDescUrl.startsWith("//")) {
		apiDescUrl = "http:" + apiDescUrl;
		}
		task.setRequestUrl(apiDescUrl);
		task.setItemTaskId(item.getId());
		task.setDescUrl(apiDescUrl);
		task.setImgIndex(imgIndex);
		return task;
	}
	
	public static SkuTaskBean createSkuTaskBean(ItemTaskBean bean,
													 Long itemTaskId, 
													 String remoteUrl,
													 List<Sku> skus,
													 Map<String, Sku> skuStocks,
													 Map<String, Sku> skuPrices,
													 String referUrl,
													 String referIp,
													 String userAgent) {
		final SkuTaskBean task = new SkuTaskBean();
		BeanUtils.copyProperties(bean, task);	 
		task.setItemTaskId(itemTaskId);
		task.setTaskId(bean.getItemId());	
		if (!remoteUrl.startsWith("http:") && remoteUrl.startsWith("//")) {
			remoteUrl = "http:" + remoteUrl;
		}
		task.setRequestUrl(remoteUrl);
		task.setSkuUrl(remoteUrl);
		task.setSkus(skus);
		task.setSkuStocks(skuStocks);
		task.setSkuPrices(skuPrices);
		
		task.setRefererUrl(referUrl);
		task.setReferIp(referIp);
		task.setUserAgent(userAgent);
		return task;
	}
}
