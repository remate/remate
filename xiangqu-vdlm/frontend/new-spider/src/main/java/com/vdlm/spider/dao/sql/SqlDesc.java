package com.vdlm.spider.dao.sql;

/**
 *
 * @author: chenxi
 */

public abstract class SqlDesc {

	public final static String INSERT_DESC =
			"INSERT INTO `desc` (\n" +
			"	item_id,\n" +
			"	desc_url,\n" +
			"   details,\n" +
			"	fragments,\n" +
			"	create_at,\n" +
			"   update_at\n" +
			")\n" +
			"VALUES\n" +
			"	(\n" +
			"		:item_id,\n" +
			"		:desc_url,\n" +
			"   	:details,\n" +
			"		:fragments,\n" +
			"		:create_at,\n" +
			"   	:update_at\n" +
			"	)";
	
	public final static String GET_DESC =
			"SELECT\n" +
			"	item_id,\n" +
			"	desc_url,\n" +
			"   details,\n" +
			"	fragments\n" +
			"FROM\n" +
			"	`desc`\n" +
			"WHERE\n" +
			"	item_id =?";
	
	public final static String DELETE_DESC =
			"DELETE FROM `desc` WHERE item_id=?";
	
	public final static String EXIST_DESC =
			"SELECT\n" +
			"	id\n" +
			"FROM\n" +
			"	`desc`\n" +
			"WHERE\n" +
			"	item_id =?\n";
	
	public final static String UPDATE_DESC =
			"UPDATE `desc`\n" +
			"SET desc_url =?, details =?, fragments =?, update_at=?\n" +
			"WHERE\n" +
			"	item_id =?";
}
