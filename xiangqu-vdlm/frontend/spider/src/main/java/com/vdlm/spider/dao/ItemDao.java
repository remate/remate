/**
 * 
 */
package com.vdlm.spider.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.entity.Item;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:24:14 PM Jul 21, 2014
 */
@Component
public class ItemDao extends BaseJdbcTemplateDAO {

	public int update(final Item entity, final int imgsCount) {
		return getJdbcTemplate().update(SqlStatics.UPDATE_ITEM,
				entity.getName(), entity.getPrice(), entity.getAmount(),
				entity.getStatus(), imgsCount, entity.getItemUrl(),
				entity.getShopType().getValue(), entity.getId());
	}

	public int update(final Long id, final Integer status) {
		return getJdbcTemplate().update(SqlStatics.UPDATE_ITEM_STATUS, status, id);
	}

	public Long insert(final Item entity, final int imgsCount) {
		final MapSqlParameterSource paramSource = new MapSqlParameterSource();
		//		ouer_user_id,
		//		ouer_shop_id,
		//		name,
		//		price,
		//		amount,
		// item_url
		// shop_type
		// item_id
		// status
		// req_from
		// user_id
		// shop_id
		paramSource.addValue("ouer_user_id", entity.getOuerUserId());
		paramSource.addValue("ouer_shop_id", entity.getOuerShopId());
		paramSource.addValue("name", entity.getName());
		paramSource.addValue("price", entity.getPrice());
		paramSource.addValue("amount", entity.getAmount());
		paramSource.addValue("item_url", entity.getItemUrl());
		paramSource.addValue("shop_type", entity.getShopType().getValue());
		paramSource.addValue("item_id", entity.getItemId());
		paramSource.addValue("status", entity.getStatus());
		paramSource.addValue("req_from", entity.getReqFrom().getValue());
		paramSource.addValue("user_id", entity.getUserId());
		paramSource.addValue("shop_id", entity.getShopId());
		paramSource.addValue("details", entity.getDetails());
		paramSource.addValue("imgs_count", imgsCount);

		final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());

		if (t.update(SqlStatics.INSERT_ITEM, paramSource, generatedKeyHolder, new String[] { "id" }) <= 0) {
			return null;
		}

		return generatedKeyHolder.getKey().longValue();
	}

	/**
	 * <pre>
	 * 判断库中是否已经存在此item
	 * </pre>
	 * @param reqFrom
	 * @param ouerUserId
	 * @param ouerShopId
	 * @param shopType
	 * @param itemId
	 * @return
	 */
	public Long exist(final ReqFrom reqFrom, final String ouerUserId, final String ouerShopId, final String itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlStatics.EXIST_ITEM, reqFrom.getValue(), ouerUserId,
				ouerShopId, itemId);
		if (rs.next()) {
			return rs.getLong("id");
		}
		return null;
	}

}
