package com.vdlm.spider.dao.sql;

/**
 *
 * @author: chenxi
 */

public abstract class SqlShopProcess {

	public final static String INSERT_SHOP_PROCESS =
			"INSERT INTO shop_process (\n" +
			"	shop_id,\n" +
			"	item_count,\n" +
			"	cur_item_count,\n" +
			"	partially,\n" +
			"	create_at,\n" +
			"   update_at\n" +
			")\n" +
			"VALUES\n" +
			"	(\n" +
			"		:shop_id,\n" +
			"		:item_count,\n" +
			"   	:cur_item_count,\n" +
			"		:partially,\n" +
			"		:create_at,\n" +
			"   	:update_at\n" +
			"	)";
		
		
	public final static String INC_ITEM_COUNT =
			"UPDATE shop_process\n" +
			"SET cur_item_count = cur_item_count + 1, update_at = ?\n" +
			"WHERE\n" +
			"	shop_id =?";
	
	public final static String SET_PARTIALLY =
			"UPDATE shop_process\n" +
			"SET partially = ?, update_at = ?\n" +
			"WHERE\n" +
			"	shop_id =?";
	
	public final static String ADD_ITEM_COUNT =
			"UPDATE shop_process\n" +
			"SET item_count = item_count + ?, update_at = ?\n" +
			"WHERE\n" +
			"	shop_id =?";
		
	public final static String GET_SHOP_PROCESS =
			"SELECT\n" +
			"   shop_id, \n" +
			"	item_count,\n" +
			"   cur_item_count,\n" +
			"	partially\n" +
			"FROM\n" +
			"	shop_process\n" +
			"WHERE\n" +
			"	shop_id =?";

	public final static String UPDATE_SHOP_PROCESS =
				"UPDATE shop_process\n" +
				"SET item_count =?, cur_item_count =?, partially=?, update_at=?\n" +
				"WHERE\n" +
				"	shop_id =?";
		
	public final static String EXIST_SHOP_PROCESS =
				"SELECT\n" +
				"	id\n" +
				"FROM\n" +
				"	shop_process\n" +
				"WHERE\n" +
				"	shop_id =?\n";
		
	public final static String DELETE_SHOP_PROCESS =
				"DELETE FROM shop_process WHERE shop_id=?";
}
