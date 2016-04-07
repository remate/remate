package com.vdlm.spider.dao.sql;

/**
 *
 * @author: chenxi
 */

public abstract class SqlItemProcess {

	// 插入 item_process
	public final static String INSERT_ITEM_PROCESS =
		"INSERT INTO item_process (\n" +
		"	shop_id,\n" +
		"	item_id,\n" +
		"	desc_parsed,\n" +
		"   sku_parsed,\n" +
		"	group_img_count,\n" +
		"	sku_img_count,\n" +
		"	detail_img_count,\n" +
		"	cur_group_img_count,\n" +
		"	cur_sku_img_count,\n" +
		"	cur_detail_img_count,\n" +
		"	type,\n" +
		"	sync_option,\n" +
		"	create_at,\n" +
		"   update_at\n" +
		")\n" +
		"VALUES\n" +
		"	(\n" +
		"		:shop_id,\n" +
		"		:item_id,\n" +
		"		:desc_parsed,\n" +
		"   	:sku_parsed,\n" +
		"		:group_img_count,\n" +
		"		:sku_img_count,\n" +
		"		:detail_img_count,\n" +
		"		:cur_group_img_count,\n" +
		"		:cur_sku_img_count,\n" +
		"		:cur_detail_img_count\n," +
		"		:type,\n" +
		"		:sync_option,\n" +
		"		:create_at,\n" +
		"   	:update_at\n" +
		"	)";
	
	public final static String SET_DESC_PARSED =
		"UPDATE item_process\n" +
		"SET desc_parsed =?, update_at = ?\n" +
		"WHERE\n" +
		"	item_id =?";
	
	public final static String RESET_DESC_PARSED =
			"UPDATE item_process\n" +
			"SET desc_parsed =b'0', cur_detail_img_count=0, update_at = ?\n" +
			"WHERE\n" +
			"	item_id =?";
	
	public final static String SET_SKU_PARSED =
		"UPDATE item_process\n" +
		"SET sku_parsed =?, update_at = ?\n" +
		"WHERE\n" +
		"	item_id =?";
	
	public final static String INC_GROUP_IMG_COUNT =
		"UPDATE item_process\n" +
		"SET cur_group_img_count = cur_group_img_count + 1, update_at = ?\n" +
		"WHERE\n" +
		"	item_id =?";
	
	public final static String INC_SKU_IMG_COUNT =
		"UPDATE item_process\n" +
		"SET cur_sku_img_count = cur_sku_img_count + 1, update_at = ?\n" +
		"WHERE\n" +
		"	item_id =?";
	
	public final static String INC_DETAIL_IMG_COUNT =
		"UPDATE item_process\n" +
		"SET cur_detail_img_count = cur_detail_img_count + 1, update_at = ?\n" +
		"WHERE\n" +
		"	item_id =?";
	
	public final static String RESET_GROUP_IMG_COUNT =
			"UPDATE item_process\n" +
			"SET cur_group_img_count = 0, update_at = ?\n" +
			"WHERE\n" +
			"	item_id =?";
		
	public final static String RESET_SKU_IMG_COUNT =
			"UPDATE item_process\n" +
			"SET cur_sku_img_count = 0, update_at = ?\n" +
			"WHERE\n" +
			"	item_id =?";
		
	public final static String RESET_DETAIL_IMG_COUNT =
			"UPDATE item_process\n" +
			"SET cur_detail_img_count = 0, update_at = ?\n" +
			"WHERE\n" +
			"	item_id =?";
	
	public final static String UPDATE_GRPUP_IMG_COUNT =
			"UPDATE item_process\n" +
			"SET cur_group_img_count = 0, group_img_count = ?, type = ?, update_at = ?\n" +
			"WHERE\n" +
			"	item_id =?";
	
	public final static String UPDATE_SKU_IMG_COUNT =
			"UPDATE item_process\n" +
			"SET cur_sku_img_count = 0, sku_img_count = ?, type = ?, update_at = ?\n" +
			"WHERE\n" +
			"	item_id =?";
	
	public final static String UPDATE_GRPUP_SKU_IMG_COUNT =
			"UPDATE item_process\n" +
			"SET group_img_count = ?, sku_img_count = ?, type = ?, update_at = ?\n" +
			"WHERE\n" +
			"	item_id =?";
	
	public final static String UPDATE_SPIDE_TYPE =
			"UPDATE item_process\n" +
			"SET type = ?, update_at = ?\n" +
			"WHERE\n" +
			"	item_id =?";
	
	public final static String UPDATE_DETAIL_IMG_COUNT =
			"UPDATE item_process\n" +
			"SET detail_img_count = ?, update_at = ?\n" +
			"WHERE\n" +
			"	item_id =?";
	
	public final static String GET_ITEM_PROCESS =
		"SELECT\n" +
		"   id, \n" +
		"   shop_id, \n" +
		"   item_id, \n" +
		"	desc_parsed,\n" +
		"   sku_parsed,\n" +
		"	group_img_count,\n" +
		"	sku_img_count,\n" +
		"	detail_img_count,\n" +
		"	cur_group_img_count,\n" +
		"	cur_sku_img_count,\n" +
		"	cur_detail_img_count,\n" +
		"	type,\n" +
		"	sync_option\n" +
		"FROM\n" +
		"	item_process\n" +
		"WHERE\n" +
		"	item_id =?";

	public final static String UPDATE_ITEM_PROCESS =
			"UPDATE item_process\n" +
			"SET shop_id=?, desc_parsed =?, sku_parsed =?, group_img_count =?, sku_img_count =?, detail_img_count=?, "
			+ "cur_group_img_count=?, cur_sku_img_count=?, cur_detail_img_count=?, type=?, sync_option=?, update_at=?\n" +
			"WHERE\n" +
			"	item_id =?";
	
	public final static String EXIST_ITEM_PROCESS =
			"SELECT\n" +
			"	id\n" +
			"FROM\n" +
			"	item_process\n" +
			"WHERE\n" +
			"	item_id =?\n";
	
	public final static String DELETE_ITEM_PROCESS =
			"DELETE FROM item_process WHERE item_id=?";
	
	public final static String GET_ITEM_IDS = 
			"SELECT\n" +
			"	item_id\n" +
			"FROM\n" +
			"	item_process\n" +
			"WHERE\n" +
			"	shop_id =?\n";
	
	public final static String GET_SHOP_ID = 
			"SELECT\n" +
			"	shop_id\n" +
			"FROM\n" +
			"	item_process\n" +
			"WHERE\n" +
			"	item_id = (SELECT id from item where ouer_shop_id=? item_id=?)\n";
}
