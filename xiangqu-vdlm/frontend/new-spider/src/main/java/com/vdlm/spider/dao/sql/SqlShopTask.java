package com.vdlm.spider.dao.sql;

/**
 *
 * @author: chenxi
 */

public abstract class SqlShopTask {

	public final static String INSERT_SHOP_TASK = 
			"INSERT INTO shop_task (\n" +
					"	shop_url,\n" +
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
	
	public final static String EXIST_SHOP_TASK =
					"SELECT\n" +
					"	ouer_shop_id\n" +
					"FROM\n" +
					"	shop_task\n" +
					"WHERE\n" +
					"	ouer_shop_id =?\n";
	
	public final static String SET_PARTIALLY =
			"UPDATE shop_task\n" +
			"SET partially = ?, update_at = ?\n" +
			"WHERE\n" +
			"	ouer_shop_id =?\n";
					
	
	public final static String EXIST_SHOP_WITH_PARTIALLY =
			"SELECT\n" +
			"	ouer_shop_id\n" +
			"FROM\n" +
			"	shop_task\n" +
			"WHERE\n" +
			"	ouer_shop_id =? and partially=?\n";
	
	public final static String UPDATE_SHOP_TASK_STATUS =
			"UPDATE shop_task\n" +
			"SET status =?, update_at = ?\n" +
			"WHERE\n" +
			"	ouer_shop_id =?\n";
	
	public final static String DELETE_SHOP_TASK =
			"DELETE from shop_task where ouer_shop_id = ?";
	
	public final static String DELETE_SHOP_TASK_JOIN = 
			"DELETE from shop_task where ouer_shop_id = (select ouer_shop_id from shop WHERE id=?)";
	
	public final static String QUERY_CREATE_AT = "SELECT\n" +
			"	create_at\n" +
			"FROM\n" +
			"	shop_task\n" +
			"WHERE\n" +
			"	ouer_shop_id =?\n";
}
