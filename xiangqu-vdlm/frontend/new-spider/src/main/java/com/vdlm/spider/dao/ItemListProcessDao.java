package com.vdlm.spider.dao;

import java.util.Date;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.vdlm.spider.SpideItemType;
import com.vdlm.spider.dao.sql.SqlItemListProcess;
import com.vdlm.spider.entity.ItemListProcess;

/**
 *
 * @author: chenxi
 */

@Component
public class ItemListProcessDao extends BaseJdbcTemplateDAO {

	public Long insert(final ItemListProcess entity) {
		final MapSqlParameterSource paramSource = new MapSqlParameterSource();

		final Date now = new Date();
		paramSource.addValue("shop_id", entity.getShopId());
		paramSource.addValue("item_count", entity.getItemCount());
		paramSource.addValue("cur_item_count", entity.getCurItemCount());
		paramSource.addValue("partially", entity.isPartially());
		paramSource.addValue("type", entity.getType().ordinal());
		paramSource.addValue("create_at", now);
		paramSource.addValue("update_at", now);

		final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());

		if (t.update(SqlItemListProcess.INSERT_ITEMLIST_PROCESS, paramSource, 
				generatedKeyHolder, new String[] { "id" }) <= 0) {
			return null;
		}

		return generatedKeyHolder.getKey().longValue();
	}
	
	public Long exist(final Long shopId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlItemListProcess.EXIST_ITEMLIST_PROCESS, shopId);
		if (rs.next()) {
			return rs.getLong("id");
		}
		return null;
	}
	
	public ItemListProcess queryOneItemProcess(Long shopId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlItemListProcess.GET_ITEMLIST_PROCESS, shopId);
		if (rs.next()) {
			final ItemListProcess result = new ItemListProcess();
			result.setShopId(rs.getLong("shop_id"));
			result.setItemCount(rs.getInt("item_count"));
			result.setCurItemCount(rs.getInt("cur_item_count"));
			result.setPartially(rs.getBoolean("partially"));
			result.setType(SpideItemType.fromOrdinal(rs.getInt("type")));
			return result;
		}
		return null;
	}
	
	public int update(final ItemListProcess entity) {
		return getJdbcTemplate().update(SqlItemListProcess.UPDATE_ITEMLIST_PROCESS, entity.getItemCount(), 
				entity.getCurItemCount(), entity.isPartially(), entity.getType().ordinal(), new Date(), entity.getShopId());
	}
	
	public int delete(final Long shopId) {
		return getJdbcTemplate().update(SqlItemListProcess.DELETE_ITEMLIST_PROCESS, shopId);
	}
	
	public int incItemCount(final Long shopId) {
		return getJdbcTemplate().update(SqlItemListProcess.INC_ITEM_COUNT, new Date(), shopId);
	}
	
	public int decItemCount(final Long itemId, SpideItemType type) {
		return getJdbcTemplate().update(SqlItemListProcess.DEC_ITEM_COUNT, type.ordinal(), new Date(), itemId);
	}
	
	public int updatePartially(final Long shopId, boolean partially) {
		return getJdbcTemplate().update(SqlItemListProcess.SET_PARTIALLY, partially, new Date(), shopId);
	}
	
	public int addItemCount(final Long shopId, int count) {
		return getJdbcTemplate().update(SqlItemListProcess.ADD_ITEM_COUNT, count, new Date(), shopId);
	}
}
