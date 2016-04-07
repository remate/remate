package com.vdlm.spider.dao.sql;

/**
 *
 * @author: chenxi
 */

public abstract class SqlHttpRequestError {

	public final static String INSERT_HTTP_REQ_ERROR =
			"INSERT INTO `http_request_error` (\n" +
			"	url,\n" +
			"	status_code,\n" +
			"   create_at\n" +
			")\n" +
			"VALUES\n" +
			"	(\n" +
			"	:url,\n" +
			"	:status_code,\n" +
			"   :create_at\n" +
			"	)";
}
