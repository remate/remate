/**
 * 
 */
package com.vdlm.spider.dao.sql;


/**
 * <pre>
 * 禁止格式化此类
 * </pre>
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:27:18 PM Jul 21, 2014
 */
public interface SqlStatics {

	// 插入 item
	String INSERT_ITEM =
	"INSERT INTO item (\n" +
	"	ouer_user_id,\n" +
	"	ouer_shop_id,\n" +
	"	name,\n" +
	"	price,\n" +
	"	amount,\n" +
	"	item_url,\n" +
	"	shop_type,\n" +
	"	item_id,\n" +
	"	status,\n" +
	"	req_from,\n" +
	"	user_id,\n" +
	"	shop_id,\n" +
	"   details,\n" +
	"   sku_props,\n" +
	"   imgs_count\n" +
	")\n" +
	"VALUES\n" +
	"	(\n" +
	"		:ouer_user_id,\n" +
	"		:ouer_shop_id,\n" +
	"		:name,\n" +
	"		:price,\n" +
	"		:amount,\n" +
	"		:item_url,\n" +
	"		:shop_type,\n" +
	"		:item_id,\n" +
	"		:status,\n" +
	"		:req_from,\n" +
	"		:user_id,\n" +
	"		:shop_id,\n" +
	"       :details,\n" +
	"       :sku_props,\n" +
	"       :imgs_count\n" +
	"	)";
	
	String INSERT_ITEM_ONLY =
			"INSERT INTO item (\n" +
			"	ouer_user_id,\n" +
			"	ouer_shop_id,\n" +
			"	name,\n" +
			"	price,\n" +
			"	amount,\n" +
			"	item_url,\n" +
			"	shop_type,\n" +
			"	item_id,\n" +
			"	status,\n" +
			"	req_from,\n" +
			"	user_id,\n" +
			"	shop_id,\n" +
			"   details,\n" +
			"   sku_props\n" +
			")\n" +
			"VALUES\n" +
			"	(\n" +
			"		:ouer_user_id,\n" +
			"		:ouer_shop_id,\n" +
			"		:name,\n" +
			"		:price,\n" +
			"		:amount,\n" +
			"		:item_url,\n" +
			"		:shop_type,\n" +
			"		:item_id,\n" +
			"		:status,\n" +
			"		:req_from,\n" +
			"		:user_id,\n" +
			"		:shop_id,\n" +
			"       :details,\n" +
			"       :sku_props\n" +
			"	)";
	
	// 更新 item
	String UPDATE_ITEM =
	"UPDATE item\n" +
	"SET name =?, price =?, amount =?, status =?, sku_props = ?, imgs_count =?\n" +
	"WHERE\n" +
	"	id =?";
	
	// 更新 item
	String UPDATE_ITEM_ONLY =
		"UPDATE item\n" +
		"SET name =?, price =?, amount =?, status =?, sku_props=?, update_at =?\n" +
		"WHERE\n" +
		"	id =?";
	
	// 更新 item状态
	String UPDATE_ITEM_STATUS =
	"UPDATE item\n" +
	"SET status =?\n" +
	"WHERE\n" +
	"	id =?";
	
	// 插入 sku
	String INSERT_SKU =
	"INSERT INTO sku (\n" +
	"	item_id,\n" +
	"	spec,\n" +
	"	price,\n" +
	"	amount,\n" +
	"	orig_spec,\n" +
	"	img_url,\n" +
	"	sku_id\n" +
	")\n" +
	"VALUES\n" +
	"	(?,?,?,?,?,?,?)";
	
	// 判断是否存在此 item
	String EXIST_ITEM =
	"SELECT\n" +
	"	id\n" +
	"FROM\n" +
	"	item\n" +
	"WHERE\n" +
	"	req_from =?\n" +
	"AND ouer_user_id =?\n" +
	"AND ouer_shop_id =?\n" +
	"AND shop_type =?\n" +
	"AND item_id =?";
	
	// 判断是否存在此 item
	String EXIST_ITEM_NEW =
		"SELECT\n" +
		"	id\n" +
		"FROM\n" +
		"	item\n" +
		"WHERE\n" +
		"	ouer_shop_id =?\n" +
		"AND item_id =?";
	
	String SIMPLE_EXIST_ITEM =
	"SELECT\n" +
	"	id\n" +
	"FROM\n" +
	"	item\n" +
	"WHERE\n" +
	"	id =?\n";
	
	String SIMPLE_EXIST_ITEM_BY_THIRDID =
			"SELECT\n" +
			"	id\n" +
			"FROM\n" +
			"	item\n" +
			"WHERE\n" +
			"	item_id =?\n";
	
	// 插入 img
	String INSERT_IMG =
	"INSERT INTO img (\n" +
	"	img_url,\n" +
	"	img,\n" +
	"	item_id,\n" +
	"	order_num,\n" +
	"	md5,\n" +
	"   type,\n" +
	"   status\n" +
	")\n" +
	"VALUES\n" +
	"	(?,?,?,?,?,?,?)";
	
	String QUERY_IMG_CNT = 
			"SELECT COUNT(*) FROM img \n" +
			"WHERE\n" +
			"	item_id =?\n" +
			" AND	type =?\n";
	
	// 插入 shop
	String INSERT_SHOP =
	"INSERT INTO shop (\n" +
	"	ouer_user_id,\n" +
	"	ouer_shop_id,\n" +
	"	req_from,\n" +
	"	user_id,\n" +
	"	shop_id,\n" +
	"	shop_url,\n" +
	"	shop_type,\n" +
	"	name,\n" +
	"	nickname,\n" +
	"	score,\n" +
	"	update_at\n" +
	")\n" +
	"VALUES\n" +
	"	(\n" +
	"		:ouer_user_id,\n" +
	"		:ouer_shop_id,\n" +
	"		:req_from,\n" +
	"		:user_id,\n" +
	"		:shop_id,\n" +
	"		:shop_url,\n" +
	"		:shop_type,\n" +
	"		:name,\n" +
	"		:nickname,\n" +
	"		:score,\n" +
	"		:update_at\n" +
	"	)";
	
	// 更新 shop
	String UPDATE_SHOP =
	"UPDATE shop\n" +
	"SET name =?, nickname =?, score =?, update_at =?\n" +
	"WHERE\n" +
	"	id =?";
	
	// 是否存在 shop
	String EXIST_SHOP =
	"SELECT\n" +
	"	id\n" +
	"FROM\n" +
	"	shop\n" +
	"WHERE\n" +
	"	req_from =?\n" +
	"AND ouer_user_id =?\n" +
	"AND ouer_shop_id =?\n" +
	"AND shop_type =?\n" +
	"AND user_id =?\n" +
	"AND shop_id =?";
	
	// TODO 获取店铺列表, 在解决搬家task中一个用户不能有多个记录之前一个卖家只返回一条淘宝店铺信息
	String SHOP_LIST =
	"SELECT\n" +
	"	name,\n" +
	"	nickname,\n" +
	"	shop_type,\n" +
	"	shop_url,\n" +
	"	score\n" +
	"FROM\n" +
	"	shop\n" +
	"WHERE\n" +
	"	req_from =?\n" +
	"AND ouer_shop_id =?\n" +
	" ORDER BY create_at desc limit 1";
	
	// 删除 sku
	String DELETE_SKU =
	"DELETE FROM sku WHERE item_id=?";
	
	// 删除 img
	String DELETE_IMG =
	"DELETE FROM img WHERE item_id=?";
	
	String GET_ITEM =
			"SELECT\n" +
					"   id,\n" +
					"   ouer_user_id,\n" +
					"   ouer_shop_id,\n" +
					"   name,\n" +
					"   price,\n" +
					"   amount,\n" +
					"   item_url,\n" +
					"   shop_type,\n" +
					"   item_id,\n" +
					"   status,\n" +
					"   req_from,\n" +
					"   user_id,\n" +
					"   shop_id,\n" +
					"   details,\n" +
					"   sku_props,\n" +
					"   completed\n" +
			"FROM\n" +
			"	item\n" +
			"WHERE\n" +
			"	id =?";
	
	String GET_SKU =
			"SELECT\n" +
					"   id,\n" +
					"   item_id,\n" +
					"   spec,\n" +
					"   price,\n" +
					"   amount,\n" +
					"   sku_id\n" +
			"FROM\n" +
			"	sku\n" +
			"WHERE\n" +
			"	item_id =?";
	
	String GET_IMG =
			"SELECT\n" +
					"   id,\n" +
					"   img_url,\n" +
					"   img,\n" +
					"   item_id,\n" +
					"   order_num,\n" +
					"   md5,\n" +
					"   type\n" +
			"FROM\n" +
			"	img\n" +
			"WHERE\n" +
			"	item_id =? and img is not null and status = 0 order by type, order_num";
	
	String GET_DESC =
			"SELECT\n" +
					"   id,\n" +
					"   item_id,\n" +
					"   desc_url,\n" +
					"   details,\n" +
					"   fragments\n" +
			"FROM\n" +
			"	`desc`\n" +
			"WHERE\n" +
			"	item_id =?";
	
	String GET_ITEM_4_TASK =
			"SELECT\n" +
					"   id,\n" + 
					"	ouer_user_id,\n" +
					"	ouer_shop_id,\n" +
					"	shop_type,\n" +
					"   req_from,\n" +
					"	item_id,\n" +
					"	item_url\n" +
			"FROM\n" +
			"	item\n" +
			"WHERE\n" +
			" ouer_shop_id=? and	item_id =?";
	
	String DELETE_IMG_BY_TYPE =
		"DELETE FROM img WHERE item_id=? and type=?";
	
	// 更新 item状态
	String UPDATE_ITEM_COMPLETED =
		"UPDATE item\n" +
		"SET completed =?, `desc`=?\n" +
		"WHERE\n" +
		"	id =?";
	
	// 更新 item状态
	String GET_ITEM_COMPLETED =
			"SELECT completed\n" +
					"from item\n" +
			"WHERE\n" +
			"	id =?";
	
	// 插入 taskLog  
		String INSERT_TASKLOG =
		"INSERT INTO taskLog (\n" +
		"	taskId,\n" +
		"	ouer_user_id,\n" +
		"	ouer_shop_id,\n" +
		"	req_from,\n" +
		"	shopType,\n" +
		"	status\n" +	 
		")\n" +
		"VALUES\n" +
		"	(\n" +
		"		:taskId,\n" +
		"		:ouer_user_id,\n" +
		"		:ouer_shop_id,\n" +
		"		:req_from,\n" +
		"		:shopType,\n" + 
		"		:status\n" + 
		"	)";
		
		// 更新 TaskLog状态
		String UPDATE_TASKLOG =
			"UPDATE taskLog\n" +
			"SET status =?, update_at=?\n" +
			"WHERE\n" +
			"	taskId =?";
		
		// 读取TaskLog
		String GET_TASHLOG =
				"SELECT\n" +
				"	id,\n" +
				"	taskId,\n" +
				"   ouer_user_id,\n" +
				"	ouer_shop_id,\n" +
				"	req_from,\n" +
				"	shopType,\n" +
				"	status,\n" +
				"	create_at,\n" +
				"	update_at\n" +
				"FROM\n" +
				"	`tasklog`\n" +
				"WHERE\n" +
				"	taskId =?";

		String QUERY_SHOPID = "SELECT id from shop where ouer_shop_id = ?";
		
		String QUERY_OUER_IDS = "SELECT ouer_shop_id, ouer_user_id from shop where id = ?";
		
		//String QUERY_ONE_ITEMID = "SELECT item_id from item where ouer_shop_id = ? order by RAND() limit 1";
		String QUERY_ONE_ITEMID = "SELECT item_id from item ti, shop ts\n" +
					" where ti.ouer_shop_id = ? and ts.ouer_shop_id = ? \n" + 
				 " and ts.shop_url like '%s' \n" +
				 " and ti.shop_id = ts.shop_id\n" +
				 " order by RAND() limit 1";
		
}
