/**
 * 
 */
package com.vdlm.spider.store;

import java.util.List;

import com.vdlm.spider.SpideItemType;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Shop;
import com.vdlm.spider.entity.Sku;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:22:05 PM Aug 7, 2014
 */
public interface SyncResultService {

	/**
	 * <pre>
	 * 同步店铺信息
	 * </pre>
	 * @param shop
	 * @throws Exception
	 */
	void syncShop(Shop shop) throws Exception;

	/**
	 * <pre>
	 * 同步item
	 * </pre>
	 * @param item
	 * @param skus
	 * @param imgs
	 * @throws Exception
	 */
	void syncItem(Item item, List<Sku> skus, List<Img> imgs, SpideItemType type, Integer option) throws Exception;
}
