/**
 * 
 */
package com.vdlm.spider.dao;

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
	"       :imgs_count\n" +
	"	)";
	
	// 更新 item
	String UPDATE_ITEM =
	"UPDATE item\n" +
	"SET name =?, price =?, amount =?, status =?,\n " +
	"imgs_count =?, item_url = ?, shop_type = ?, update_at = now()\n" +
	"WHERE\n" +
	"	id =?";
	
	// 更新 item状态
	String UPDATE_ITEM_STATUS =
	"UPDATE item\n" +
	"SET status =?, update_at = now()\n" +
	"WHERE\n" +
	"	id =?";
	
	// 插入 sku
	String INSERT_SKU =
	"INSERT INTO sku (\n" +
	"	item_id,\n" +
	"	spec,\n" +
	"	price,\n" +
	"	amount,\n" +
	"	orig_spec\n" +
	")\n" +
	"VALUES\n" +
	"	(?,?,?,?,?)";
	
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
	"AND item_id =?";
	
	// 插入 img
	String INSERT_IMG =
	"INSERT INTO img (\n" +
	"	img_url,\n" +
	"	img,\n" +
	"	item_id,\n" +
	"	order_num,\n" +
	"	md5,\n" +
	"   type\n" +
	")\n" +
	"VALUES\n" +
	"	(?,?,?,?,?,?)";
	
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
	
	// 获取店铺列表
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
	"AND ouer_user_id =?\n" +
	"AND ouer_shop_id =?";
	
	// 删除 sku
	String DELETE_SKU =
	"DELETE FROM sku WHERE item_id=?";
	
	// 删除 img
	String DELETE_IMG =
	"DELETE FROM img WHERE item_id=?";
}
