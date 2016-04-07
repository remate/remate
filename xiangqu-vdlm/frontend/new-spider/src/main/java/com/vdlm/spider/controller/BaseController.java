/**
 * 
 */
package com.vdlm.spider.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.dao.ItemDao;
import com.vdlm.spider.dao.ItemListTaskDao;
import com.vdlm.spider.dao.ItemTaskDao;
import com.vdlm.spider.dao.ShopTaskDao;
import com.vdlm.spider.event.ItemProcessEvent;
import com.vdlm.spider.event.ItemProcessEventType;
import com.vdlm.spider.parser.config.ShopConfigs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:48:00 PM Aug 12, 2014
 */
public class BaseController {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private BusSignalManager bsm;
	
	@Autowired
	private ShopTaskDao shopTaskDao;
	@Autowired
	private ItemListTaskDao itemListTaskDao;
	@Autowired
	private ItemTaskDao itemTaskDao;
	@Autowired
	private ItemDao itemDao;

	/**
	 * <pre>
	 * 根据店铺URL获取店铺类型
	 * </pre>
	 * @param url
	 * @return
	 */
	public ShopType getShopTypeByUrl(String url) {
		// 去掉末尾 '/'
		final String shopUrl;
		if (url.startsWith(Statics.HTTP_URL_PREFIX)) {
			final int index = url.indexOf('/', Statics.HTTP_URL_PREFIX.length() + 1);
			if (index == -1) {
				shopUrl = url;
			}
			else {
				shopUrl = url.substring(0, index);
			}
		}
		else {
			final int index = url.indexOf('/');
			if (index == -1) {
				shopUrl = Statics.HTTP_URL_PREFIX + url;
			}
			else {
				shopUrl = Statics.HTTP_URL_PREFIX + url.substring(0, index);
			}
		}
		// taobao.com
		if (shopUrl.endsWith(ShopConfigs.getOrCreateTaobaoShopConfig().getUrlEndsWith())) {
			return ShopType.TAOBAO;
		}
		// tmall.com
		else if (shopUrl.endsWith(ShopConfigs.getOrCreateTmallShopConfig().getUrlEndsWith())) {
			return ShopType.TMALL;
		}
		else {
			return null;
		}
	}
	
	protected void prepare(String ouerShopId, String itemId) {
		log.info("prepare for shopId {}, itemId {}", ouerShopId, itemId);
		int deletes = itemTaskDao.deleteByShopId(ouerShopId);
		log.info("deletes {} item task for shop {}", deletes, ouerShopId);
		deletes = itemListTaskDao.deleteOne(ouerShopId);
		log.info("deletes {} itemlist task for shop {}", deletes, ouerShopId);
		deletes = shopTaskDao.deleteOne(ouerShopId);
		log.info("deletes {} shop task for shop {}", deletes, ouerShopId);
		final Long id = itemDao.exist(ouerShopId, itemId);
		if (id != null) {
			final ItemProcessEvent event = new ItemProcessEvent(ItemProcessEventType.CLEAN_LOCK);
			event.setItemId(id);
			bsm.signal(event);
			log.info("sent clean locks event for item {} ", itemId);
		}
	}
}
