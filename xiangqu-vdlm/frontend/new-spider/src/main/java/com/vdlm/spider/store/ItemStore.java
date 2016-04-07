/**
 * 
 */
package com.vdlm.spider.store;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import net.rubyeye.xmemcached.MemcachedClient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.biz.vo.UpLoadFileVO;
import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.dal.type.FileBelong;
import com.vdlm.spider.Config;
import com.vdlm.spider.ItemStatus;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.SpideItemType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.dao.DescDao;
import com.vdlm.spider.dao.ImgDao;
import com.vdlm.spider.dao.ItemDao;
import com.vdlm.spider.dao.SkuDao;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.event.ItemEvent;
import com.vdlm.spider.event.ItemEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.utils.Logs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 9:27:52 AM Jul 22, 2014
 */
//@Component
public class ItemStore implements BusSignalListener<ItemEvent> {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private ItemDao itemDao;
	private SkuDao skuDao;
	private ImgDao imgDao;
	private DescDao descDao;

	private HttpClientProvider provider;
//
//	private Qiniu qiniu;
//	
	private ResourceFacade resourceFacade;
	private MemcachedClient memcachedClient;
	private SyncResultService syncResultService;
	
	public ItemStore(BusSignalManager bsm) {
		bsm.bind(ItemEvent.class, this);
	}

	@Autowired
	@Qualifier("syncResultService")
	public void setSyncResultService(SyncResultService syncResultService) {
		this.syncResultService = syncResultService;
	}

	@Autowired
	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}

	@Autowired
	public void setResourceFacade(ResourceFacade resourceFacade) {
		this.resourceFacade = resourceFacade;
	}

	@Autowired
	public void setProvider(HttpClientProvider provider) {
		this.provider = provider;
	}

	@Autowired
	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	@Autowired
	public void setSkuDao(SkuDao skuDao) {
		this.skuDao = skuDao;
	}

	@Autowired
	public void setImgDao(ImgDao imgDao) {
		this.imgDao = imgDao;
	}

	public ItemDao getItemDao() {
		return itemDao;
	}

	public DescDao getDescDao() {
		return descDao;
	}

	@Autowired
	public void setDescDao(DescDao descDao) {
		this.descDao = descDao;
	}

	/**
	 * <pre>
	 * 判断库中是否已经存在此item
	 * </pre>
	 * @param reqFrom
	 * @param ouerUserId
	 * @param ouerShopId
	 * @param shopType
	 * @param itemId
	 * @return
	 */
//	public Long exist(ReqFrom reqFrom, String ouerUserId, String ouerShopId, ShopType shopType, String itemId) {
//		return this.itemDao.exist(reqFrom, ouerUserId, ouerShopId, shopType, itemId);
//	}
	
	public Long exist(String ouerShopId, String itemId) {
		return this.itemDao.exist(ouerShopId, itemId);
	}
	
	public Long exist(Long itemId) {
		return this.itemDao.exist(itemId);
	}

	public static final String KEY_FORMAT = "ist_rf_%s_uid_%s_sid_%s_stp_%s_itd_%s";

	long existCache(ReqFrom reqFrom, String ouerUserId, String ouerShopId, ShopType shopType, String itemId) {
		final String key = String.format(KEY_FORMAT, reqFrom, ouerUserId, ouerShopId, shopType, itemId);
		final String md5 = DigestUtils.md5Hex(key);
		try {
			return this.memcachedClient.incr(md5, 1, 0, Config.instance().getCacheOptTimeout(), (int) (Config
					.instance().getCacheTimeout() / 1000));
		}
		catch (final Exception e) {
			//TODO 异常情况放弃?
			Logs.unpredictableLogger.error("Error to find by [" + key + "] from Memcached", e);
			return -1;
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	void insert0(Item item) throws Exception {
		// 插入 item ///////////////////////////////////////////
		Long itemId = null;
		try {
			//itemId = this.itemDao.insert(item);
			itemId = this.itemDao.insert(item);
			item.setId(itemId);
		}
		catch (final Exception e) {
			Logs.unpredictableLogger.error("Error to insert item:" + JSON.toJSONString(item), e);
			throw e;
		}
		///////////////////////////////////////////////////

		if (itemId == null) {
			Logs.unpredictableLogger.error("Failed to insert item[{}]", JSON.toJSONString(item));
			return;
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	void insert0(Item item, List<Sku> skus) throws Exception {
		// 插入 item ///////////////////////////////////////////
		Long itemId = null;
		try {
			//itemId = this.itemDao.insert(item);
			itemId = this.itemDao.insert(item);
			item.setId(itemId);
		}
		catch (final Exception e) {
			Logs.unpredictableLogger.error("Error to insert item:" + JSON.toJSONString(item), e);
			throw e;
		}
		///////////////////////////////////////////////////

		if (itemId == null) {
			Logs.unpredictableLogger.error("Failed to insert item[{}],skus:[{}]", JSON.toJSONString(item),
					JSON.toJSONString(skus));
			return;
		}

		// 插入 sku 和 img
		this.insert0(item.getShopType(), itemId, skus);
	}
	
	@Transactional(rollbackFor = Exception.class)
	void insert0(ShopType shopType, Long itemId, List<Sku> skus) throws Exception {
		// 插入 sku ///////////////////////////////////////////////
		if (CollectionUtils.isNotEmpty(skus)) {
			for (final Sku sku : skus) {
				sku.setItemId(itemId);
			}
			try {
				if(skus.size() > 0 && skus != null) {
					this.skuDao.insert(skus);
				}
			}
			catch (final Exception e) {
				Logs.unpredictableLogger.error("Error to insert skus:" + JSON.toJSONString(skus), e);
				throw e;
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	void insert0(Item item, List<Sku> skus, List<Img> imgs) throws Exception {
		// 插入 item ///////////////////////////////////////////
		Long itemId = null;
		try {
			//itemId = this.itemDao.insert(item);
			itemId = this.itemDao.insert(item, imgs.size());
		}
		catch (final Exception e) {
			Logs.unpredictableLogger.error("Error to insert item:" + JSON.toJSONString(item), e);
			throw e;
		}
		///////////////////////////////////////////////////

		if (itemId == null) {
			Logs.unpredictableLogger.error("Failed to insert item[{}],skus:[{}],imgs:[{}]", JSON.toJSONString(item),
					JSON.toJSONString(skus), JSON.toJSONString(imgs));
			return;
		}

		// 插入 sku 和 img
		this.insert0(item.getShopType(), itemId, skus, imgs);
	}

	@Transactional(rollbackFor = Exception.class)
	void insert0(ShopType shopType, Long itemId, List<Sku> skus, List<Img> imgs) throws Exception {
		// 插入 sku ///////////////////////////////////////////////
		if (CollectionUtils.isNotEmpty(skus)) {
			for (final Sku sku : skus) {
				sku.setItemId(itemId);
			}
			try {
				if(skus.size() > 0 && skus != null) {
					this.skuDao.insert(skus);
				}
			}
			catch (final Exception e) {
				Logs.unpredictableLogger.error("Error to insert skus:" + JSON.toJSONString(skus), e);
				throw e;
			}
		}
		//////////////////////////////////////////////////////

		// 插入 img /////////////////////////////////////////////
		if (CollectionUtils.isNotEmpty(imgs)) {
			for (final Img img : imgs) {
				img.setItemId(itemId);
				//TODO 图片下载上传步骤 考虑用单独job执行
				this.uploadImg(img, shopType);
			}
			try {
				this.imgDao.insert(imgs);
			}
			catch (final Exception e) {
				Logs.unpredictableLogger.error("Error to insert imgs:" + JSON.toJSONString(imgs), e);
				throw e;
			}
		}
		////////////////////////////////////////////////
	}

	public void uploadImg(Img img, ShopType shopType) {
		//TODO 图片上传可以做成异步
		// 先从淘宝下载图片，再上传至QiNiu/////////////////////////////////////////
		// 允许多次上传，QiNiu会做md5验证
		InputStream input = null;
		a: for (int i = 0; i < Config.instance().getRetryTimes(); i++) {
			try {
				input = this.downloadImg(shopType, img);
			}
			catch (final Exception e) {
				Logs.unpredictableLogger.error("Failed to download img:" + JSON.toJSONString(img), e);
			}

			if (input == null) {
				continue a;
			}

			for (int j = 0; j < Config.instance().getRetryTimes(); j++) {
				try {
					this.uploadImg(input, img);
				}
				catch (final Exception e) {
					Logs.unpredictableLogger.error("Failed to upload img:" + JSON.toJSONString(img), e);
				}

				if (StringUtils.isNotBlank(img.getImg())) {
					break a;
				}
			}
		}
		//////////////////////////////////////////////////////////

		// 静静地
		IOUtils.closeQuietly(input);
	}
	
	@Transactional(rollbackFor = Exception.class)
	void update(Item item) throws Exception {
		// 下架商品，仅更新状态
		if (item.getStatus() != null && item.getStatus() == Statics.SOLD_OUT) {
			this.itemDao.update(item.getId(), item.getStatus());
			return;
		}

		// 更新 item
		this.itemDao.update(item);
		// 删除 sku 和 img
		this.skuDao.deleteList(item.getId());
		this.imgDao.deleteList(item.getId());
		// delete desc
		this.descDao.deleteDesc(item.getId());
	}
	
	@Transactional(rollbackFor = Exception.class)
	void update(Item item, List<Sku> skus, boolean onlyUpdateSkus) throws Exception {
		// 下架商品，仅更新状态
		if (item.getStatus() != null && item.getStatus() == Statics.SOLD_OUT) {
			this.itemDao.update(item.getId(), item.getStatus());
			return;
		}

		// 更新 item
		//this.itemDao.update(item);
		this.itemDao.update(item);

		// 删除 sku 和 img
		this.skuDao.deleteList(item.getId());
		if (onlyUpdateSkus) {
			// when someone invoke manually update skus info, we should not delete the group images
			this.imgDao.deleteList(item.getId(), 2);
		} else {
			this.imgDao.deleteList(item.getId());
		}

		// 插入 sku 和 img
		this.insert0(item.getShopType(), item.getId(), skus);
	}

	@Transactional(rollbackFor = Exception.class)
	void update(Item item, List<Sku> skus, List<Img> imgs) throws Exception {
		// 下架商品，仅更新状态
		if (item.getStatus() != null && item.getStatus() == Statics.SOLD_OUT) {
			this.itemDao.update(item.getId(), item.getStatus());
			return;
		}

		// 更新 item
		//this.itemDao.update(item);
		this.itemDao.update(item, imgs.size());

		// 删除 sku 和 img
		this.skuDao.deleteList(item.getId());
		this.imgDao.deleteList(item.getId());

		// 插入 sku 和 img
		this.insert0(item.getShopType(), item.getId(), skus, imgs);
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void save(long itemId, List<Sku> skus) throws Exception {
		try {
			if(skus == null || skus.size() == 0) {
				return;
			}
			final Long id = this.exist(itemId);
			if (id == null) {
				return;
			}
			skuDao.deleteList(id);
			for (final Sku sku : skus) {
				sku.setItemId(id);
			}
			this.skuDao.insert(skus);
		} catch (final Exception e) {
			Logs.unpredictableLogger.error("Error to insert skus:" + JSON.toJSONString(skus), e);
			throw e;
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void save(Item item) {
//		// check
//		final long exist = this.existCache(item.getReqFrom(), item.getOuerUserId(), item.getOuerShopId(),
//					item.getShopType(), item.getItemId());
//		if (exist != 0) {
//			//ignore 缓存有效期内，禁止重复操作
//			log.debug("Cache valid，duplicateb operate, itemid:" + item.getItemId());
//			item.setTooQuick(true);
//			final Long id = this.exist(item.getReqFrom(), item.getOuerUserId(), item.getOuerShopId(), item.getShopType(),
//					item.getItemId());
//			item.setId(id);
//			return;
//		}
		
		// double check
		// 查询数据库判断
//		final Long id = this.exist(item.getReqFrom(), item.getOuerUserId(), item.getOuerShopId(), item.getShopType(),
//						item.getItemId());
		final Long id = this.exist(item.getOuerShopId(), item.getItemId());
		
		// 新增
		if (id == null) {
			try {
				this.insert0(item);
			}
			catch (final Exception e) {
				Logs.unpredictableLogger.error("Failed to insert item[{}]",
								JSON.toJSONString(item));
				return;
			}
		}
		// 更新
		else {
			item.setId(id);//必须添加这行
			try {
				this.update(item);
			}
			catch (final Exception e) {
				Logs.unpredictableLogger.error("Failed to update item[{}]",
								JSON.toJSONString(item));
				return;
			}
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void save(Item item, List<Sku> skus, boolean onlyUpdateSkus) {
//		// check
//		final long exist = this.existCache(item.getReqFrom(), item.getOuerUserId(), item.getOuerShopId(),
//				item.getShopType(), item.getItemId());
//		if (exist != 0) {
//			//ignore 缓存有效期内，禁止重复操作
//			log.debug("Cache valid，duplicateb operate, itemid:" + item.getItemId());
//			return;
//		}

		// double check
		// 查询数据库判断
//		final Long id = this.exist(item.getReqFrom(), item.getOuerUserId(), item.getOuerShopId(), item.getShopType(),
//				item.getItemId());
		final Long id = this.exist(item.getOuerShopId(), item.getItemId());
		// 新增
		if (id == null) {
			try {
				this.insert0(item, skus);
			}
			catch (final Exception e) {
				Logs.unpredictableLogger.error("Failed to insert item[{}],skus:[{}]",
						JSON.toJSONString(item), JSON.toJSONString(skus));
				return;
			}
		}
		// 更新
		else {
			item.setId(id);//必须添加这行
			try {
				this.update(item, skus, onlyUpdateSkus);
			}
			catch (final Exception e) {
				Logs.unpredictableLogger.error("Failed to update item[{}],skus:[{}]",
						JSON.toJSONString(item), JSON.toJSONString(skus));
				log.error("failed to save item and skus", e);
				return;
			}
		}
	}

	/**
	 * <pre>
	 * 保存 item 以及 sku 等信息
	 * 参数禁止为 null
	 * </pre>
	 * @param item
	 * @param skus
	 * @param imgs
	 */
	@Transactional(rollbackFor = Exception.class)
	public void save(Item item, List<Sku> skus, List<Img> imgs) {
		// check
		final long exist = this.existCache(item.getReqFrom(), item.getOuerUserId(), item.getOuerShopId(),
				item.getShopType(), item.getItemId());
		if (exist != 0) {
			//ignore 缓存有效期内，禁止重复操作
			log.debug("Cache valid，duplicateb operate, itemid:" + item.getItemId());
			return;
		}

		// double check
		// 查询数据库判断
//		final Long id = this.exist(item.getReqFrom(), item.getOuerUserId(), item.getOuerShopId(), item.getShopType(),
//				item.getItemId());
		final Long id = this.exist(item.getOuerShopId(), item.getItemId());

		// 新增
		if (id == null) {
			try {
				this.insert0(item, skus, imgs);
			}
			catch (final Exception e) {
				Logs.unpredictableLogger.error("Failed to insert item[{}],skus:[{}],imgs:[{}]",
						JSON.toJSONString(item), JSON.toJSONString(skus), JSON.toJSONString(imgs));
				return;
			}
		}
		// 更新
		else {
			item.setId(id);//必须添加这行
			try {
				this.update(item, skus, imgs);
			}
			catch (final Exception e) {
				Logs.unpredictableLogger.error("Failed to update item[{}],skus:[{}],imgs:[{}]",
						JSON.toJSONString(item), JSON.toJSONString(skus), JSON.toJSONString(imgs));
				return;
			}
		}

		try {
			this.syncResultService.syncItem(item, skus, imgs, SpideItemType.ITEM, 1);
		}
		catch (final Exception ignore) {
			// not care
		}
	}

	/**
	 * <pre>
	 * 从淘宝下载图片
	 * </pre>
	 * @param shopType
	 * @param img
	 */
	public InputStream downloadImg(ShopType shopType, Img img) {
		//TODO 无需IP流控?
		final String ip = this.provider.getIpPools().randomSafeAvaliableIp(shopType);

		final HttpClientInvoker invoker = this.provider.provide(shopType, img.getImgUrl(), null, ip);

		final HttpInvokeResult result = invoker.invoke();

		if (result.isOK() && result.getContent() != null) {
			return new ByteArrayInputStream(result.getContent());
		}
		else {
			Logs.unpredictableLogger.error("Failed to download img:" + JSON.toJSONString(img) + ", HttpInvokeResult:"
					+ result + ", proxy ip:" + ip, result.getException());
		}
		return null;
	}

	/**
	 * <pre>
	 * 上传至QINIU
	 * </pre>
	 * @param input
	 * @param img
	 */
	public void uploadImg(InputStream input, Img img) {
		UpLoadFileVO result = null;
		try {
			result = this.resourceFacade.uploadFileStream(input, FileBelong.PRODUCT);
		}
		catch (final Exception e) {
			Logs.unpredictableLogger.error("Error to upload img:" + JSON.toJSONString(img) + " to QiNiu", e);
		}
		if (result != null) {
			img.setImg(result.getId());
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void uploadImgAndSave(InputStream input, Img img) throws Exception {
		UpLoadFileVO result = null;
		if (input != null) {
			try {
				result = this.resourceFacade.uploadFileStream(input, FileBelong.PRODUCT);
			}
			catch (final Exception e) {
				Logs.unpredictableLogger.error("Error to upload img:" + JSON.toJSONString(img) + " to QiNiu", e);
			}
			if (result != null) {
				img.setImg(result.getId());
			} else {
				img.setStatus(2);
			}
			IOUtils.closeQuietly(input);
		} else {
			img.setStatus(1);
		}
		try {
			this.imgDao.insert(img);
		}
		catch (final Exception e) {
			Logs.unpredictableLogger.error("Error to insert imgs:" + JSON.toJSONString(img), e);
			throw e;
		}
	}

	@Override
	public void signalFired(ItemEvent signal) {
		final ItemEventType type = signal.getType();
		if (ItemEventType.SAVE_ITEM.equals(type)) {
			save(signal.getItem());
			return;
		}
		if (ItemEventType.UPLOAD_IMG.equals(type)) {
			try {
				uploadImgAndSave(signal.getImgInput(), signal.getImg());
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if (ItemEventType.TOLERANCE_IMG.equals(type)) {
			try {
				uploadImgAndSave(null, signal.getImg());
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if (ItemEventType.SAVE_SKUS.equals(type)) {
			try {
				save(signal.getItemId(), signal.getSkus());
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (ItemEventType.SAVE_ITEM_SKUS.equals(type)) {
			save(signal.getItem(), signal.getSkus(), signal.isOnlyUpdateSkus());
		}
		if (ItemEventType.MAX_RETRIES.equals(type)) {
			failed(signal.getItemId(), signal.getFailedDesc());
		}
		if (ItemEventType.NEED_RETRY.equals(type)) {
			final boolean retry = needRetry(signal.getItemId());
			signal.setNeedRetry(retry);
		}
		
	}
	
	private void failed(Long itemId, String failedDesc) {
		itemDao.updateCompleted(ItemStatus.FAILED.ordinal(), itemId, failedDesc);
	}
	
	private boolean needRetry(Long itemId) {
		final int completed = itemDao.queryCompleted(itemId);
		if (ItemStatus.FAILED.ordinal() == completed) {
			return false;
		}
		return true;
	}
}
