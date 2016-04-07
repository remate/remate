package com.vdlm.spider.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.vdlm.spider.DeviceType;
import com.vdlm.spider.ParserType;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.TaskStatus;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.dao.sql.SqlItemTask;

/**
 *
 * @author: chenxi
 */

@Component
public class ItemTaskDao extends BaseJdbcTemplateDAO {

	public Long insert(final ItemTaskBean entity) {
		final MapSqlParameterSource paramSource = new MapSqlParameterSource();
		/*
		 * `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
		 * `item_id` varchar(32) NOT NULL,
  `item_url` varchar(100) DEFAULT NULL,
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
		paramSource.addValue("item_id", entity.getItemId());
		paramSource.addValue("item_url", entity.getItemUrl());
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

		if (t.update(SqlItemTask.INSERT_ITEM_TASK, paramSource, 
				generatedKeyHolder, new String[] { "id" }) <= 0) {
			return null;
		}

		return generatedKeyHolder.getKey().longValue();
	}
	
	public int[] insert(final List<ItemTaskBean> entities) {
		final java.sql.Date now = new java.sql.Date(new Date().getTime());
		return getJdbcTemplate().batchUpdate(SqlItemTask.INSERT_ITEM_TASKS, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				setStatement(ps, entities.get(index), now);
			}

			@Override
			public int getBatchSize() {
				return entities.size();
			}
		});
	}
	
	void setStatement(final PreparedStatement ps, final ItemTaskBean entity, java.sql.Date date) throws SQLException {
		/*
		 * `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `item_id` varchar(32) NOT NULL,
  `item_url` varchar(100) DEFAULT NULL,
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
  `update_at`
		 */
		int index = 0;
		ps.setString(++index, entity.getItemId());
		ps.setString(++index, entity.getItemUrl());
		ps.setString(++index, entity.getShopUrl());
		ps.setString(++index, entity.getNickname());
		ps.setString(++index, entity.getShopName());
		ps.setString(++index, entity.getOuerShopId());
		ps.setString(++index, entity.getOuerUserId());
		ps.setString(++index, entity.getTUserId());
		ps.setString(++index, entity.getShopType().name());
		ps.setString(++index, entity.getParserType().name());
		ps.setString(++index, entity.getRequestUrl());
		ps.setString(++index, entity.getReqFrom().name());
		ps.setString(++index, entity.getDeviceType().name());
		ps.setBoolean(++index, entity.isRetry());
		ps.setBoolean(++index, entity.isRetryIncr());
		ps.setInt(++index, entity.getRetryTimes());
		ps.setInt(++index, TaskStatus.CREATED.ordinal());
		ps.setDate(++index, date);
	}
	
	public String exist(String shopId, final String itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlItemTask.EXIST_ITEM_TASK, shopId, itemId);
		if (rs.next()) {
			return rs.getString("item_id");
		}
		return null;
	}
	
	public String exist(final String itemId, int status) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlItemTask.EXIST_ITEM_TASK_STATUS, itemId);
		if (rs.next()) {
			return rs.getString("item_id");
		}
		return null;
	}
	
	public int updateStatus(final String itemId, int status) {
		return getJdbcTemplate().update(SqlItemTask.UPDATE_ITEM_TASK_STATUS, status, new Date(), itemId);
	}

	public ItemTaskBean queryOneItemTask(String itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlItemTask.GET_ITEM_TASK, itemId);
		if (rs.next()) {
			final ItemTaskBean result = new ItemTaskBean();
			result.setItemId(rs.getString("item_id"));
			result.setShopUrl(rs.getString("shop_url"));
			result.setNickname(rs.getString("nickname"));
			result.setShopName(rs.getString("shop_name"));
			result.setOuerShopId(rs.getString("ouer_shop_id"));
			result.setOuerUserId(rs.getString("ouer_user_id"));
			result.setTUserId(rs.getString("t_user_id"));
			result.setShopType(ShopType.fromName(rs.getString("shop_type")));
			result.setParserType(ParserType.fromName(rs.getString("parser_type")));
			result.setRequestUrl(rs.getString("request_url"));
			result.setReqFrom(ReqFrom.fromName(rs.getString("req_from")));
			result.setDeviceType(DeviceType.fromName(rs.getString("device_type")));	
			result.setRetry(rs.getBoolean("retry"));
			result.setRetryIncr(rs.getBoolean("retry_incr"));
			result.setRetryTimes(rs.getInt("retry_times"));
			return result;
		}
		return null;
	}
	
	public ItemTaskBean queryOneItemTaskFromAudit(String itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlItemTask.GET_ITEM_TASK_AUDIT, itemId);
		if (rs.next()) {
			final ItemTaskBean result = new ItemTaskBean();
			result.setItemId(rs.getString("item_id"));
			result.setItemUrl(rs.getString("item_url"));
			result.setShopUrl(rs.getString("shop_url"));
			result.setNickname(rs.getString("nickname"));
			result.setShopName(rs.getString("shop_name"));
			result.setOuerShopId(rs.getString("ouer_shop_id"));
			result.setOuerUserId(rs.getString("ouer_user_id"));
			result.setTUserId(rs.getString("t_user_id"));
			result.setShopType(ShopType.fromName(rs.getString("shop_type")));
			result.setParserType(ParserType.fromName(rs.getString("parser_type")));
			result.setRequestUrl(rs.getString("request_url"));
			result.setReqFrom(ReqFrom.fromName(rs.getString("req_from")));
			result.setDeviceType(DeviceType.fromName(rs.getString("device_type")));	
			result.setRetry(rs.getBoolean("retry"));
			result.setRetryIncr(rs.getBoolean("retry_incr"));
			result.setRetryTimes(rs.getInt("retry_times"));
			return result;
		}
		return null;
	}
	
	public int deleteOne(String shopId, final String itemId) {
		return getJdbcTemplate().update(SqlItemTask.DELETE_ITEM_TASK, shopId, itemId);
	}
	
	public int deleteOne(final Long itemId) {
		return getJdbcTemplate().update(SqlItemTask.DELETE_ITEM_TASK_JOIN, itemId);
	}
	
	public int deleteByShopId(String shopId) {
		return getJdbcTemplate().update(SqlItemTask.DELETE_ITEM_TASK_BY_SHOPID, shopId);
	}
}
