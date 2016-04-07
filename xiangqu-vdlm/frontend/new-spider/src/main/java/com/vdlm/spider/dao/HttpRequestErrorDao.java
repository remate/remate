package com.vdlm.spider.dao;

import java.util.Date;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.vdlm.spider.dao.sql.SqlHttpRequestError;
import com.vdlm.spider.entity.HttpRequestError;

/**
 *
 * @author: chenxi
 */

@Component
public class HttpRequestErrorDao extends BaseJdbcTemplateDAO {

	public Long insert(final HttpRequestError entity) {
		final MapSqlParameterSource paramSource = new MapSqlParameterSource();

		paramSource.addValue("url", entity.getUrl());
		paramSource.addValue("status_code", entity.getStatusCode());
		paramSource.addValue("create_at", new Date());

		final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());

		if (t.update(SqlHttpRequestError.INSERT_HTTP_REQ_ERROR, paramSource, 
				generatedKeyHolder, new String[] { "id" }) <= 0) {
			return null;
		}

		return generatedKeyHolder.getKey().longValue();
	}
}
