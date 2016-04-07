package com.vdlm.proxycrawler.dao;

import java.util.Date;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.vdlm.proxycrawler.Proxy;
import com.vdlm.proxycrawler.ProxyStatus;

/**
 *
 * @author: chenxi
 */

public class ProxyDao extends BaseJdbcTemplateDAO {

	public Long insert(final Proxy entity) {
		final MapSqlParameterSource paramSource = new MapSqlParameterSource();

		paramSource.addValue("ip", entity.getIp());
		paramSource.addValue("port", entity.getPort());
		paramSource.addValue("type", entity.getType());
		paramSource.addValue("status", entity.getStatus().ordinal());
		paramSource.addValue("check_time", entity.getCheckTime());
		paramSource.addValue("cur_page", entity.getCurrentPage());
		paramSource.addValue("source", entity.getSource());
		paramSource.addValue("check_time_2_cur", entity.isCheckTime2Current());
		paramSource.addValue("create_at", new Date());
		paramSource.addValue("update_at", new Date());

		final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());

		if (t.update(SqlProxy.INSERT_PROXY, paramSource, 
				generatedKeyHolder, new String[] { "id" }) <= 0) {
			return null;
		}

		return generatedKeyHolder.getKey().longValue();
	}
	
	public Long exist(final Proxy entity) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlProxy.EXIST_PROXY,
				entity.getIp(), entity.getPort());
		if (rs.next()) {
			return rs.getLong("id");
		}
		return null;
	}
	
	public int update(final Proxy entity) throws Exception {
		return getJdbcTemplate().update(SqlProxy.UPDATE_PROXY, entity.getStatus().ordinal(), entity.getCheckTime(),
				entity.getCurrentPage(), entity.getSource(), entity.isCheckTime2Current(),
				new Date(), entity.getIp(), entity.getPort());
	}
	
	public Proxy queryForOneProxy(ProxyStatus status) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlProxy.GET_ONE_PROXY, status.ordinal());
		if (rs.next()) {
			final Proxy result = new Proxy();
			result.setIp(rs.getString("ip"));
			result.setPort(rs.getInt("port"));
			return result;
		}
		return null;
	}
}
