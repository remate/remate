package com.vdlm.spider.dao.sql;

/**
 *
 * @author: chenxi
 */

public abstract class SqlItemListTask {

	public final static String INSERT_ITEMLIST_TASK = 
			"INSERT INTO itemlist_task (\n" +
					"	shop_url,\n" +
					"	nickname,\n" +
					"	shop_name,\n" +
					"	ouer_shop_id,\n" +
					"	ouer_user_id,\n" +
					"	t_user_id,\n" +
					"	shop_type,\n" +
					"	parser_type,\n" +
					"	request_url,\n" +
					"	req_from,\n" +
					"	device_type,\n" +
					"	retry,\n" +
					"	retry_incr,\n" +
					"	retry_times,\n" +
					"	status,\n" +
					"	create_at,\n" +
					"   update_at\n" +
					")\n" +
					"VALUES\n" +
					"	(\n" +
					"   	:shop_url,\n" +
					"		:nickname,\n" +
					"		:shop_name,\n" +
					"		:ouer_shop_id,\n" +
					"		:ouer_user_id,\n" +
					"		:t_user_id,\n" +
					"		:shop_type\n," +
					"		:parser_type,\n" +
					"		:request_url,\n" +
					"		:req_from,\n" +
					"		:device_type,\n" +
					"		:retry,\n" +
					"		:retry_incr,\n" +
					"		:retry_times\n," +
					"		:status,\n" +
					"		:create_at,\n" +
					"   	:update_at\n" +
					"	)";
	
	public final static String EXIST_ITEMLIST_TASK =
					"SELECT\n" +
					"	ouer_shop_id\n" +
					"FROM\n" +
					"	itemlist_task\n" +
					"WHERE\n" +
					"	ouer_shop_id =?\n";
	
	public final static String SET_PARTIALLY =
			"UPDATE itemlist_task\n" +
			"SET partially = ?, update_at = ?\n" +
			"WHERE\n" +
			"	ouer_shop_id =?\n";
					
	
	public final static String EXIST_ITEMLIST_WITH_PARTIALLY =
			"SELECT\n" +
			"	ouer_shop_id\n" +
			"FROM\n" +
			"	itemlist_task\n" +
			"WHERE\n" +
			"	ouer_shop_id =? and partially=?\n";
	
	public final static String UPDATE_ITEMLIST_TASK_STATUS =
			"UPDATE itemlist_task\n" +
			"SET status =?, update_at = ?\n" +
			"WHERE\n" +
			"	ouer_shop_id =?\n";
	
	public final static String DELETE_ITEMLIST_TASK = 
			"DELETE from itemlist_task where ouer_shop_id = ?";
	
	public final static String DELETE_ITEMLIST_TASK_JOIN = 
			"DELETE from itemlist_task where ouer_shop_id = (select ouer_shop_id from shop WHERE id=?)";
}
