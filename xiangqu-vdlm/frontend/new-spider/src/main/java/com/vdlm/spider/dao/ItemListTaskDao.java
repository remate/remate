package com.vdlm.spider.dao;

import java.util.Date;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.vdlm.spider.TaskStatus;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.dao.sql.SqlItemListTask;

/**
 *
 * @author: chenxi
 */

@Component
public class ItemListTaskDao extends BaseJdbcTemplateDAO {

	public Long insert(final ItemListTaskBean entity) {
		final MapSqlParameterSource paramSource = new MapSqlParameterSource();
		/*
		 * `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `shop_url` varchar(100) DEFAULT NULL,
  `nickname` varchar(100) DEFAULT NULL,
  `shop_name` varchar(100) DEFAULT NULL,
  `ouer_shop_id` varchar(32) DEFAULT NULL,
  `ouer_user_id` varchar(32) DEFAULT NULL,
  `t_user_id` varchar(32) DEFAULT NULL,
  `shop_type` varchar(16) DEFAULT NULL,
  `parser_type` varchar(16) DEFAULT NULL,
  `request_url` varchar(100) DEFAULT NULL,
  `req_from` varchar(16) DEFAULT NULL,
  `device_type` varchar(16) DEFAULT NULL,
  `retry` bit(1) NOT NULL DEFAULT b'1',
  `retry_incr` bit(1) NOT NULL DEFAULT b'1',
  `retry_times` int(10) unsigned DEFAULT NULL,
  `status` tinyint(10) unsigned DEFAULT NULL,
  `create_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
		 */
		final Date now = new Date();
		paramSource.addValue("shop_url", entity.getShopUrl());
		paramSource.addValue("nickname", entity.getNickname());
		paramSource.addValue("shop_name", entity.getShopName());
		paramSource.addValue("ouer_shop_id", entity.getOuerShopId());
		paramSource.addValue("ouer_user_id", entity.getOuerUserId());
		paramSource.addValue("t_user_id", entity.getTUserId());
		paramSource.addValue("shop_type", entity.getShopType().name());
		paramSource.addValue("parser_type", entity.getParserType().name());
		paramSource.addValue("request_url", entity.getRequestUrl());
		paramSource.addValue("req_from", entity.getReqFrom().name());
		paramSource.addValue("device_type", entity.getDeviceType().name());
		paramSource.addValue("retry", entity.isRetry());
		paramSource.addValue("retry_incr", entity.isRetryIncr());
		paramSource.addValue("retry_times", entity.getRetryTimes());
		paramSource.addValue("status", TaskStatus.ENQUEUED.ordinal());
		paramSource.addValue("create_at", now);
		paramSource.addValue("update_at", now);

		final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());

		if (t.update(SqlItemListTask.INSERT_ITEMLIST_TASK, paramSource, 
				generatedKeyHolder, new String[] { "id" }) <= 0) {
			return null;
		}

		return generatedKeyHolder.getKey().longValue();
	}
	
	public String exist(final String shopId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlItemListTask.EXIST_ITEMLIST_TASK, shopId);
		if (rs.next()) {
			return rs.getString("ouer_shop_id");
		}
		return null;
	}
	
	public String existNoPartially(final String shopId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlItemListTask.EXIST_ITEMLIST_WITH_PARTIALLY, shopId, false);
		if (rs.next()) {
			return rs.getString("ouer_shop_id");
		}
		return null;
	}
	
	public int updateStatus(final String shopId, int status) {
		return getJdbcTemplate().update(SqlItemListTask.UPDATE_ITEMLIST_TASK_STATUS, status, new Date(), shopId);
	}
	
	public int deleteOne(final String shopId) {
		return getJdbcTemplate().update(SqlItemListTask.DELETE_ITEMLIST_TASK, shopId);
	}
	
	public int deleteOne(final Long shopId) {
		return getJdbcTemplate().update(SqlItemListTask.DELETE_ITEMLIST_TASK_JOIN, shopId);
	}
	
	public int updatePartially(final String shopId, boolean partially) {
		return getJdbcTemplate().update(SqlItemListTask.SET_PARTIALLY, partially, new Date(), shopId);
	}
}
