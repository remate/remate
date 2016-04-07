package com.vdlm.spider.biz;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.DeviceType;
import com.vdlm.spider.ParserType;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ResponseObject;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.bean.ShopTaskBean;
import com.vdlm.spider.bean.WItemTaskBean;
import com.vdlm.spider.bean.parial.PItemDescTaskBean;
import com.vdlm.spider.bean.parial.PItemGroupImgTaskBean;
import com.vdlm.spider.dao.DescDao;
import com.vdlm.spider.dao.ImgDao;
import com.vdlm.spider.dao.ItemDao;
import com.vdlm.spider.dao.ItemListTaskDao;
import com.vdlm.spider.dao.ItemProcessDao;
import com.vdlm.spider.dao.ItemTaskDao;
import com.vdlm.spider.dao.ShopDao;
import com.vdlm.spider.dao.ShopTaskDao;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.parser.config.ShopConfigs;
import com.vdlm.spider.task.TaskType;
import com.vdlm.spider.utils.MapTools;

/**
 *
 * @author: chenxi
 */

@Component
public class SpideMover {

	private final static Logger LOG = LoggerFactory.getLogger(SpideMover.class);
	
	@Autowired
	private BusSignalManager bsm;
	
	@Autowired
	private ShopTaskDao shopTaskDao;
	@Autowired
	private ItemListTaskDao itemListTaskDao;
	@Autowired
	private ItemTaskDao itemTaskDao;
	@Autowired
	private ShopDao shopDao;
	@Autowired
	private ItemProcessDao itemProcessDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private DescDao descDao;
	@Autowired
	private ImgDao imgDao;
	
	
	public ResponseObject<Map<String, Object>> move(String itemId, String ouerUserId, String ouerShopId, int shopType, boolean add) {
		final Long shopId = itemProcessDao.queryShopId(ouerShopId, itemId);
		if (shopId == null) {
			LOG.warn("cannot find shop with ouer shop id: {}", ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 999));
		}
		if (shopTaskDao.exist(ouerShopId) != null) {
			LOG.warn("shop task: {} is already exist.", ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 999));
		}
		if (itemListTaskDao.exist(ouerShopId) != null) {
			LOG.warn("itemlist task: {} is already exist.", ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 999));
		}
		if (itemTaskDao.exist(ouerShopId, itemId) != null) {
			LOG.warn("item task: {} is already exist.", itemId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 999));
		}
		
		final WItemTaskBean bean = new WItemTaskBean();
		bean.setTaskType(TaskType.MANUALLY);
		bean.setOuerUserId(StringUtils.defaultIfBlank(ouerUserId, "0"));
		bean.setOuerShopId(StringUtils.defaultIfBlank(ouerShopId, "0"));
		bean.setShopType(ShopType.valueOf(shopType));
		bean.setParserType(ParserType.item(bean.getShopType()));
		bean.setDeviceType(DeviceType.OTHER);
		bean.setReqFrom(ReqFrom.KKKD);
		bean.setMobilePhone(StringUtils.EMPTY);
		bean.setRnd(StringUtils.EMPTY);
		bean.setItemId(itemId);
		bean.setShopId(shopId);
		bean.setRequestUrl(WItemTaskBean.WDETAIL_PREFIX + itemId);
		bean.setItemUrl(WItemTaskBean.WDETAIL_PREFIX + itemId);
		bean.setTaskType(TaskType.MANUALLY);
		if (!add) {
			bean.setAdd(false);
		}
		if (bean != null) {
			bsm.signal(bean);
		}
		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
	}
	
	public ResponseObject<Map<String, Object>> moveItemDescManually(String ouerShopId, String itemId, int shopType) {		
		final Item item = itemDao.queryOne4Task(ouerShopId, itemId);
		if (item == null) {
			LOG.warn("cannot find item with third id:" + itemId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 999));
		}
		final PItemDescTaskBean bean = new PItemDescTaskBean();
		bean.setItemId(itemId);
		bean.setItemUrl(WItemTaskBean.WDETAIL_PREFIX + itemId);
		bean.setRequestUrl(WItemTaskBean.WDETAIL_PREFIX + itemId);
		bean.setShopType(ShopType.valueOf(shopType));
		bsm.signal(bean);
		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
	}
	
	public ResponseObject<Map<String, Object>> moveItemGroupImgsManually(String ouerShopId, String itemId, int shopType) {	
		final Item item = itemDao.queryOne4Task(ouerShopId, itemId);
		if (item == null) {
			LOG.warn("cannot find item with third id:" + itemId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 999));
		}
		final PItemGroupImgTaskBean bean = new PItemGroupImgTaskBean();
		bean.setItemId(itemId);
		bean.setItemUrl(WItemTaskBean.WDETAIL_PREFIX + itemId);
		bean.setRequestUrl(WItemTaskBean.WDETAIL_PREFIX + itemId);
		bean.setShopType(ShopType.valueOf(shopType));
		bsm.signal(bean);
		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
	}
	
	public ResponseObject<Map<String, Object>> mSubmitTaskByUrl(String ouerUserId, 
			String ouerShopId, String url, Integer reqFromValue) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("ouerUserId={}, ouerShopId={}, url={}, reqFrom={}", ouerUserId, ouerShopId, url, reqFromValue);
		}
		
		url = ParserUtils.formatUrl(url);
		
		final ReqFrom reqFrom;
		try {
			reqFrom = ReqFrom.valueOf(reqFromValue);
		}
		catch (final Exception e) {
			LOG.error("invalid Request Parameters: reqFrom={}", reqFromValue);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 501));
		}

		if (reqFrom == ReqFrom.KKKD && (StringUtils.isBlank(ouerUserId) || StringUtils.isBlank(ouerShopId))) {
			LOG.error("invalid Request Parameters: reqFrom={}, ouerUserId={}, ouerShopId={}", reqFromValue, ouerUserId, ouerShopId);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 501));
		}

		final ShopType shopType = this.getShopTypeByUrl(url);
		if (shopType == null) {
			LOG.error("can not get ShopType, invalid Request Parameters: url={}", url);
			return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 502));
		}

		final ShopTaskBean bean = new ShopTaskBean();
		bean.setTaskType(TaskType.MANUALLY);
		bean.setOuerUserId(StringUtils.defaultIfBlank(ouerUserId, "0"));
		bean.setOuerShopId(StringUtils.defaultIfBlank(ouerShopId, "0"));
		bean.setShopType(shopType);
		bean.setParserType(ParserType.item(bean.getShopType()));
		bean.setDeviceType(DeviceType.OTHER);
		bean.setReqFrom(reqFrom);
		bean.setMobilePhone(StringUtils.EMPTY);
		bean.setRnd(StringUtils.EMPTY);
		bean.setRequestUrl(url);
		bsm.signal(bean);
		return new ResponseObject<Map<String, Object>>(MapTools.asHashMap("statusCode", 200));
	}
	
	public ShopType getShopTypeByUrl(String url) {
		// 以 http:// 开头
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
		if (shopUrl.endsWith(ShopConfigs.getOrCreateTaobaoShopConfig().getUrlEndsWith())) {
			return ShopType.TAOBAO;
		}
		else if (shopUrl.endsWith(ShopConfigs.getOrCreateTmallShopConfig().getUrlEndsWith())) {
			return ShopType.TMALL;
		}
		else {
			return null;
		}
	}
}
