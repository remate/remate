package com.vdlm.proxycrawler.dao;

/**
 *
 * @author: chenxi
 */

public abstract class SqlProxy {

	public final static String INSERT_PROXY =
			"INSERT INTO `proxy` (\n" +
			"	ip,\n" +
			"	port,\n" +
			"	type,\n" +
			"	status,\n" +
			"	check_time,\n" +
			"	cur_page,\n" +
			"	source,\n" +
			"	check_time_2_cur,\n" +
			"   create_at,\n" +
			"   update_at\n" +
			")\n" +
			"VALUES\n" +
			"	(\n" +
			"	:ip,\n" +
			"	:port,\n" +
			"	:type,\n" +
			"	:status,\n" +
			"	:check_time,\n" +
			"	:cur_page,\n" +
			"	:source,\n" +
			"	:check_time_2_cur,\n" +
			"   :create_at,\n" +
			"   :update_at\n" +
			"	)";
	
	public final static String EXIST_PROXY =
			"SELECT\n" +
					"	id\n" +
					"FROM\n" +
					"	proxy\n" +
					"WHERE\n" +
					"	ip =?\n" +
					"AND port =?";
	
	public final static String UPDATE_PROXY =
			"UPDATE proxy\n" +
			"SET status =?, check_time =?, cur_page =?, source =?, check_time_2_cur=?, update_at =?\n" +
			"WHERE\n" +
			"	ip =?\n" +
			"AND port =?";
	
	public final static String GET_ONE_PROXY =
			"SELECT\n" +
					"	ip, port\n" +
					"FROM\n" +
					"	proxy\n" +
					"WHERE\n" +
					"	status =?\n" +
					"ORDER BY RAND() limit 1";
}
