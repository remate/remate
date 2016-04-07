/**
 * 
 */
package com.vdlm.spider.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.rubyeye.xmemcached.MemcachedClient;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vdlm.service.product.ProductService;
import com.vdlm.spider.Config;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.dao.ShopDao;
import com.vdlm.spider.entity.Shop;
import com.vdlm.spider.utils.Logs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:11:44 AM Aug 8, 2014
 */
@Component("spider.shopService")
public class ShopService {

	private ShopDao dao;
	private SyncResultService syncResultService;
	private MemcachedClient memcachedSpiderClient;
	
	@Autowired
	private ProductService productService;

	@Autowired
	@Qualifier("syncResultService")
	public void setSyncResultService(SyncResultService syncResultService) {
		this.syncResultService = syncResultService;
	}

	@Autowired
	public void setMemcachedSpiderClient(MemcachedClient memcachedSpiderClient) {
		this.memcachedSpiderClient = memcachedSpiderClient;
	}

	@Autowired
	public void setDao(ShopDao dao) {
		this.dao = dao;
	}

	public static final String KEY_FORMAT = "ist_rf_%s_uid_%s_sid_%s_stp_%s_uid_%s_sid_%s";

	long existCache(ReqFrom reqFrom, String ouerUserId, String ouerShopId, ShopType shopType, String userId,
			String shopId) {
		final String key = String.format(KEY_FORMAT, reqFrom, ouerUserId, ouerShopId, shopType, userId, shopId);
		final String md5 = DigestUtils.md5Hex(key);
		try {
			return this.memcachedSpiderClient.incr(md5, 1, 0, Config.instance().getCacheOptTimeout(), (int) (Config
					.instance().getCacheTimeout() / 1000));
		}
		catch (Exception e) {
			//TODO 异常情况放弃?
			Logs.unpredictableLogger.error("Error to find by [" + key + "] from Memcached", e);
			return -1;
		}
	}

	/**
	 * <pre>
	 * 保存shop信息，如果存在则更新。
	 * 同步至第三方库
	 * </pre>
	 * @param entity
	 */
	@Transactional(rollbackFor = Exception.class)
	public void save(Shop entity) {
		// check
		final long exist = this.existCache(entity.getReqFrom(), entity.getOuerUserId(), entity.getOuerShopId(),
				entity.getShopType(), entity.getUserId(), entity.getShopId());
		if (exist != 0) {
			//ignore 缓存有效期内，禁止重复操作
			return;
		}
		
		// delete vdlm product of 4none by ouershopid 2015-6-3
		// delete method after the fix
		this.productService.clear4noneProductByShopId(entity.getOuerShopId());

		// double check
		// 查询数据库判断
		final Long id = this.exist(entity.getReqFrom(), entity.getOuerUserId(), entity.getOuerShopId(),
				entity.getShopType(), entity.getUserId(), entity.getShopId());

		if (id == null) {
			this.dao.insert(entity);
		}
		// 更新
		else {
			entity.setId(id);//必须添加这行
			this.dao.update(entity);
		}

		try {
			this.syncResultService.syncShop(entity);
		}
		catch (Exception ignore) {
			// not care
		}
	}

	/**
	 * <pre>
	 * 判断是否存在该店铺，返回ID
	 * </pre>
	 * @param reqFrom
	 * @param ouerUserId
	 * @param ouerShopId
	 * @param shopType
	 * @param userId
	 * @param shopId
	 * @return
	 */
	public Long exist(ReqFrom reqFrom, String ouerUserId, String ouerShopId, ShopType shopType, String userId,
			String shopId) {
		return dao.exist(reqFrom, ouerUserId, ouerShopId, shopType, userId, shopId);
	}

	/**
	 * <pre>
	 * 获取店铺列表
	 * </pre>
	 * @param reqFrom
	 * @param ouerUserId
	 * @param ouerShopId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Shop> queryForList(ReqFrom reqFrom, String ouerUserId, String ouerShopId) {
		final SqlRowSet rs = dao.queryForRowSet(reqFrom, ouerUserId, ouerShopId);
		if (!rs.last()) {
			return Collections.EMPTY_LIST;
		}
		final List<Shop> results = new ArrayList<Shop>(rs.getRow());

		rs.beforeFirst();
		while (rs.next()) {
			final Shop result = new Shop();
			result.setName(rs.getString("name"));
			result.setNickname(rs.getString("nickname"));
			result.setShopType(ShopType.valueOf(rs.getInt("shop_type")));
			result.setShopUrl(rs.getString("shop_url"));
			result.setScore(rs.getString("score"));
			results.add(result);
		}
		return results;
	}
}
