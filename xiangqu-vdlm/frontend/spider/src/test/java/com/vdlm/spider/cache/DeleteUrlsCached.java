/**
 * 
 */
package com.vdlm.spider.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.parser.config.ItemConfigs;
import com.vdlm.spider.parser.config.ItemListConfigs;
import com.vdlm.spider.service.ItemService;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:00:16 PM Aug 5, 2014
 */
public class DeleteUrlsCached {

	ComboPooledDataSource ds;

	final String ouerUserId = "1ryau71d";
	final String ouerShopId = "1r5zbdox";
	final ReqFrom reqFrom = ReqFrom.KKKD;
	final ShopType shopType = ShopType.TAOBAO;

	MemcachedClient client;

	final Map<String, String> keys = new HashMap<String, String>();

	@Before
	public void before() throws Exception {
		//		List<InetSocketAddress> addressList = AddrUtil.getAddresses("10.8.100.2:11211");

		List<InetSocketAddress> addressList = AddrUtil.getAddresses("122.225.68.112:11211");

		MemcachedClientBuilder builder = new XMemcachedClientBuilder(addressList);
		client = builder.build();

		//		this.loadFromDb();
		this.loadFromFile();
	}

	void loadFromFile() throws Exception {
		File file = new File("C:\\Users\\Think\\Desktop\\item.txt");

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(file));

			String key = null;
			while (true) {
				final String line = br.readLine();
				if (line == null) {
					break;
				}

				key = UrlCounters.getKey(reqFrom, ouerUserId, ouerShopId, line.trim());
				key = DigestUtils.md5Hex(key);
				keys.put(key, line.trim());

				final String idName = ItemConfigs.getOrCreateItemConfig(shopType).getIdName();
				final String itemId = ParserUtils.getUrlParam(line.trim(), idName);
				key = String.format(ItemService.KEY_FORMAT, ouerUserId, ouerShopId, shopType, itemId);
				key = DigestUtils.md5Hex(key);
				keys.put(key, itemId);

				key = ItemCounters.getKey(reqFrom, ouerUserId, ouerShopId, shopType, itemId);
				key = DigestUtils.md5Hex(key);
				keys.put(key, " ItemCounters " + itemId);
			}
		}
		finally {
			IOUtils.closeQuietly(br);
		}
	}

	void loadFromDb() throws Exception {
		ds = new ComboPooledDataSource();
		ds.setDriverClass("com.mysql.jdbc.Driver");

		//		ds.setJdbcUrl("jdbc:mysql://10.8.100.2/vdlm?autoCommit=true&useUnicode=true&characterEncoding=utf-8");
		//		ds.setUser("vdlm");
		//		ds.setPassword("pwd!@#");

		ds.setJdbcUrl("jdbc:mysql://122.225.68.108/vdlm?autoCommit=true&useUnicode=true&characterEncoding=utf-8");
		ds.setUser("vdlm");
		ds.setPassword("weidian2014");

		final JdbcTemplate jt = new JdbcTemplate(ds);

		final String sql = "SELECT item_url,item_id,shop_type FROM spider.item where ouer_user_id=? AND ouer_shop_id=? AND name=?";
		final Object[] args = { ouerUserId, ouerShopId, "定制" };

		final SqlRowSet rs = jt.queryForRowSet(sql, args);

		String key = null;
		while (rs.next()) {
			key = UrlCounters.getKey(reqFrom, ouerUserId, ouerShopId, rs.getString("item_url"));
			key = DigestUtils.md5Hex(key);
			keys.put(key, rs.getString("item_url"));

			key = String.format(ItemService.KEY_FORMAT, ouerUserId, ouerShopId,
					ShopType.valueOf(rs.getInt("shop_type")), rs.getString("item_id"));
			key = DigestUtils.md5Hex(key);
			keys.put(key, rs.getString("item_id"));

			key = ItemCounters.getKey(reqFrom, ouerUserId, ouerShopId, shopType, rs.getString("item_id"));
			key = DigestUtils.md5Hex(key);
			keys.put(key, " ItemCounters " + rs.getString("item_id"));
		}
	}

	@After
	public void after() throws IOException {
		client.shutdown();
		if (ds != null) {
			ds.close();
		}
	}

	@Test
	public void testDelete() throws Exception {
		final String taobaoUrl = ItemListConfigs.getOrCreateItemListConfig(shopType).getUrl0("天使同学大卖铺");

		final String itemListKey = DigestUtils.md5Hex(UrlCounters.getKey(reqFrom, ouerUserId, ouerShopId, taobaoUrl));

		System.out.println("delete " + taobaoUrl + " : " + client.delete(itemListKey));

		for (Map.Entry<String, String> key : keys.entrySet()) {
			System.out.println("delete " + key.getValue() + " : " + client.delete(key.getKey()));
		}
	}
}
