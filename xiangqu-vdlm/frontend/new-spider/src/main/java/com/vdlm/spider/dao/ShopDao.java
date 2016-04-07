/**
 * 
 */
package com.vdlm.spider.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.dao.sql.SqlStatics;
import com.vdlm.spider.entity.Shop;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:25:51 AM Aug 8, 2014
 */
@Component
public class ShopDao extends BaseJdbcTemplateDAO {

	//	UPDATE spider.shop
	//	SET name =?, nickname =?, score =?, update_at =?
	//	WHERE
	//		id =?
	public int update(final Shop entity) {
		return getJdbcTemplate().update(SqlStatics.UPDATE_SHOP, entity.getName(), entity.getNickname(),
				entity.getScore(), new Timestamp(System.currentTimeMillis()), entity.getId());
	}

	public Long insert(final Shop entity) {
		final MapSqlParameterSource paramSource = new MapSqlParameterSource();
		//		ouer_user_id,
		//		ouer_shop_id,
		//		req_from,
		//		user_id,
		//		shop_id,
		//		shop_url,
		//		shop_type,
		//		name,
		//		nickname,
		//		score
		//		update_at
		paramSource.addValue("ouer_user_id", StringUtils.defaultIfBlank(entity.getOuerUserId(), "0"));
		paramSource.addValue("ouer_shop_id", StringUtils.defaultIfBlank(entity.getOuerShopId(), "0"));
		paramSource.addValue("req_from", entity.getReqFrom().getValue());
		paramSource.addValue("user_id", entity.getUserId());
		paramSource.addValue("shop_id", entity.getShopId());
		paramSource.addValue("shop_url", entity.getShopUrl());
		paramSource.addValue("shop_type", entity.getShopType().getValue());
		paramSource.addValue("name", entity.getName());
		paramSource.addValue("nickname", entity.getNickname());
		paramSource.addValue("score", entity.getScore());
		paramSource.addValue("update_at", new Timestamp(System.currentTimeMillis()));

		final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());

		if (t.update(SqlStatics.INSERT_SHOP, paramSource, generatedKeyHolder, new String[] { "id" }) <= 0) {
			return null;
		}

		return generatedKeyHolder.getKey().longValue();
	}

	/**
	 * <pre>
	 * 判断是否存在该店铺，返回ID
	 * </pre>
	 * @param reqFrom
	 * @param ouerUserId
	 * @param ouerShopId
	 * @param shopType
	 * @param userId
	 * @param shopId
	 * @return
	 */
	public Long exist(ReqFrom reqFrom, String ouerUserId, String ouerShopId, ShopType shopType, String userId,
			String shopId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlStatics.EXIST_SHOP, reqFrom.getValue(), ouerUserId,
				ouerShopId, shopType.getValue(), userId, shopId);
		if (rs.next()) {
			return rs.getLong("id");
		}
		return null;
	}

	/**
	 * <pre>
	 * 获取店铺列表
	 * </pre>
	 * @param reqFrom
	 * @param ouerUserId
	 * @param ouerShopId
	 * @return
	 */
	public SqlRowSet queryForRowSet(ReqFrom reqFrom, String ouerUserId, String ouerShopId) {
		return getJdbcTemplate().queryForRowSet(SqlStatics.SHOP_LIST, reqFrom.getValue(), ouerShopId);
	}
	
	public Long queryForShopId(String shopId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlStatics.QUERY_SHOPID, shopId);
		if (rs.next()) {
			return rs.getLong("id");
		}
		return null;
	}
	
	public Shop queryForOuerIds(Long shopId) {
		return getJdbcTemplate().queryForObject(SqlStatics.QUERY_OUER_IDS, new RowMapper<Shop>() {

			@Override
			public Shop mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				final Shop result = new Shop();
				result.setOuerShopId(rs.getString("ouer_shop_id"));
				result.setOuerUserId(rs.getString("ouer_user_id"));
				return result;
			}
			
		}, shopId);
	}
}
