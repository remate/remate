package com.vdlm.spider.dao.sql;

/**
 *
 * @author: chenxi
 */

public abstract class SqlItemListProcess {

	public final static String INSERT_ITEMLIST_PROCESS =
			"INSERT INTO itemlist_process (\n" +
			"	shop_id,\n" +
			"	item_count,\n" +
			"	cur_item_count,\n" +
			"	partially,\n" +
			"	type,\n" +
			"	create_at,\n" +
			"   update_at\n" +
			")\n" +
			"VALUES\n" +
			"	(\n" +
			"		:shop_id,\n" +
			"		:item_count,\n" +
			"   	:cur_item_count,\n" +
			"		:partially,\n" +
			"		:type,\n" +
			"		:create_at,\n" +
			"   	:update_at\n" +
			"	)";
		
		
	public final static String INC_ITEM_COUNT =
			"UPDATE itemlist_process\n" +
			"SET cur_item_count = cur_item_count + 1, update_at = ?\n" +
			"WHERE\n" +
			"	shop_id =?";
	
	public final static String DEC_ITEM_COUNT =
			"UPDATE itemlist_process\n" +
			"SET cur_item_count = cur_item_count - 1, type=?, update_at = ?\n" +
			"WHERE\n" +
			"	shop_id = (SELECT shop_id from item_process where item_id=?)";
	
	public final static String SET_PARTIALLY =
			"UPDATE itemlist_process\n" +
			"SET partially = ?, update_at = ?\n" +
			"WHERE\n" +
			"	shop_id =?";
	
	public final static String ADD_ITEM_COUNT =
			"UPDATE itemlist_process\n" +
			"SET item_count = item_count + ?, update_at = ?\n" +
			"WHERE\n" +
			"	shop_id =?";
		
	public final static String GET_ITEMLIST_PROCESS =
			"SELECT\n" +
			"   shop_id, \n" +
			"	item_count,\n" +
			"   cur_item_count,\n" +
			"	type,\n" +
			"	partially\n" +
			"FROM\n" +
			"	itemlist_process\n" +
			"WHERE\n" +
			"	shop_id =?";

	public final static String UPDATE_ITEMLIST_PROCESS =
				"UPDATE itemlist_process\n" +
				"SET item_count =?, cur_item_count =?, partially=?, type=?, update_at=?\n" +
				"WHERE\n" +
				"	shop_id =?";
		
	public final static String EXIST_ITEMLIST_PROCESS =
				"SELECT\n" +
				"	id\n" +
				"FROM\n" +
				"	itemlist_process\n" +
				"WHERE\n" +
				"	shop_id =?\n";
		
	public final static String DELETE_ITEMLIST_PROCESS =
				"DELETE FROM itemlist_process WHERE shop_id=?";
}
