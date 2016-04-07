package com.vdlm.spider.dao.sql;


/**
 *
 * @author: chenxi
 */

public abstract class SqlItemTask {

	public final static String GET_ITEM_TASK =
			"SELECT\n" +
					"	id,\n" +
					"	item_id,\n" +
					"   item_url,\n" +
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
			"FROM\n" +
			"	item_task\n" +
			"WHERE\n" +
			"	item_id =?";
	
	public final static String GET_ITEM_TASK_AUDIT =
			"SELECT\n" +
					"	id,\n" +
					"	item_id,\n" +
					"   item_url,\n" +
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
					"	create_at\n" +
			"FROM\n" +
			"	item_task_audit\n" +
			"WHERE\n" +
			"	item_id =?";
	
//	public final static String INSERT_ITEM_TASK = 
//			"INSERT INTO item_task (\n" +
//					"	item_id,\n" +
//					"   item_url,\n" +
//					"	shop_url,\n" +
//					"	nickname,\n" +
//					"	shop_name,\n" +
//					"	ouer_shop_id,\n" +
//					"	ouer_user_id,\n" +
//					"	t_user_id,\n" +
//					"	shop_type,\n" +
//					"	parser_type,\n" +
//					"	request_url,\n" +
//					"	req_from,\n" +
//					"	device_type,\n" +
//					"	retry,\n" +
//					"	retry_incr,\n" +
//					"	retry_times,\n" +
//					"	status,\n" +
//					"	create_at,\n" +
//					"   update_at\n" +
//					")\n" +
//					"VALUES\n" +
//					"	(\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"   	?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"		?,\n" +
//					"   	?\n" +
//					"	)";
	
	public final static String INSERT_ITEM_TASK = 
			"INSERT INTO item_task (\n" +
					"	item_id,\n" +
					"   item_url,\n" +
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
					"		:item_id,\n" +
					"		:item_url,\n" +
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
	
	public final static String INSERT_ITEM_TASKS = 
			"INSERT INTO item_task_audit (\n" +
					"	item_id,\n" +
					"   item_url,\n" +
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
					"	create_at\n" +
					")\n" +
					"VALUES\n" +
					"	(\n" +
					"		?,\n" +
					"		?,\n" +
					"   	?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?,\n" +
					"		?\n" +
					"	)";
	
//	public final static String INSERT_ITEM_TASKS = 
//			"INSERT INTO item_task_audit (\n" +
//					"	item_id,\n" +
//					"   item_url,\n" +
//					"	shop_url,\n" +
//					"	nickname,\n" +
//					"	shop_name,\n" +
//					"	ouer_shop_id,\n" +
//					"	ouer_user_id,\n" +
//					"	t_user_id,\n" +
//					"	shop_type,\n" +
//					"	parser_type,\n" +
//					"	request_url,\n" +
//					"	req_from,\n" +
//					"	device_type,\n" +
//					"	retry,\n" +
//					"	retry_incr,\n" +
//					"	retry_times,\n" +
//					"	status,\n" +
//					"	create_at\n" +
//					")\n" +
//					"VALUES\n" +
//					"	(\n" +
//					"		:item_id,\n" +
//					"		:item_url,\n" +
//					"   	:shop_url,\n" +
//					"		:nickname,\n" +
//					"		:shop_name,\n" +
//					"		:ouer_shop_id,\n" +
//					"		:ouer_user_id,\n" +
//					"		:t_user_id,\n" +
//					"		:shop_type\n," +
//					"		:parser_type,\n" +
//					"		:request_url,\n" +
//					"		:req_from,\n" +
//					"		:device_type,\n" +
//					"		:retry,\n" +
//					"		:retry_incr,\n" +
//					"		:retry_times\n," +
//					"		:status,\n" +
//					"		:create_at\n" +
//					"	)";
	
	public final static String EXIST_ITEM_TASK =
					"SELECT\n" +
					"	item_id\n" +
					"FROM\n" +
					"	item_task\n" +
					"WHERE\n" +
					"   ouer_shop_id=? AND\n" +
					"	item_id =?\n";
	
	public final static String EXIST_ITEM_TASK_STATUS =
			"SELECT\n" +
			"	item_id\n" +
			"FROM\n" +
			"	item_task\n" +
			"WHERE\n" +
			"	item_id =? and status=?\n";
	
	public final static String UPDATE_ITEM_TASK_STATUS =
			"UPDATE item_task\n" +
			"SET status =?, update_at = ?\n" +
			"WHERE\n" +
			"	item_id =?\n";
	
	public final static String DELETE_ITEM_TASK =
			"DELETE FROM item_task WHERE ouer_shop_id=? AND item_id=?";
	
	public final static String DELETE_ITEM_TASK_JOIN = 
			"DELETE from item_task where item_id = (select item_id from item WHERE id=?)";
	
	public final static String DELETE_ITEM_TASK_BY_SHOPID = 
			"DELETE from item_task where item_id in (select item_id from item WHERE ouer_shop_id=?)";
}
