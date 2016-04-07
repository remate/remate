package com.vdlm.spider.dao;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.vdlm.spider.SpideItemType;
import com.vdlm.spider.dao.sql.SqlItemProcess;
import com.vdlm.spider.entity.ItemProcess;

/**
 *
 * @author: chenxi
 */

@Component
public class ItemProcessDao extends BaseJdbcTemplateDAO {

	public Long insert(final ItemProcess entity) {
		final MapSqlParameterSource paramSource = new MapSqlParameterSource();
		/*
		 * "	item_id,\n" +
		"	desc_parsed,\n" +
		"	group_img_count,\n" +
		"	sku_img_count,\n" +
		"	detail_img_count,\n" +
		"	cur_group_img_count,\n" +
		"	cur_sku_img_count,\n" +
		"	cur_detail_img_count,\n" +
		 */
		final Date now = new Date();
		paramSource.addValue("shop_id", entity.getShopId());
		paramSource.addValue("item_id", entity.getItemId());
		paramSource.addValue("desc_parsed", entity.isDescParsed());
		paramSource.addValue("sku_parsed", entity.isSkuParsed());
		paramSource.addValue("group_img_count", entity.getGroupImgCount());
		paramSource.addValue("sku_img_count", entity.getSkuImgCount());
		paramSource.addValue("detail_img_count", entity.getDetailImgCount());
		paramSource.addValue("cur_group_img_count", entity.getCurGroupImgCount());
		paramSource.addValue("cur_sku_img_count", entity.getCurSkuImgCount());
		paramSource.addValue("cur_detail_img_count", entity.getCurDetailImgCount());
		paramSource.addValue("type", entity.getType().ordinal());
		paramSource.addValue("sync_option", entity.getOption());
		paramSource.addValue("create_at", now);
		paramSource.addValue("update_at", now);

		final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());

		if (t.update(SqlItemProcess.INSERT_ITEM_PROCESS, paramSource, 
				generatedKeyHolder, new String[] { "id" }) <= 0) {
			return null;
		}

		return generatedKeyHolder.getKey().longValue();
	}
	
	public int update(final ItemProcess entity) {
		return getJdbcTemplate().update(SqlItemProcess.UPDATE_ITEM_PROCESS, entity.getShopId(),
				entity.isDescParsed(), entity.isSkuParsed(), entity.getGroupImgCount(), entity.getSkuImgCount(), 
				entity.getDetailImgCount(), entity.getCurGroupImgCount(), entity.getCurSkuImgCount(), 
				entity.getCurDetailImgCount(), entity.getType().ordinal(), entity.getOption(), new Date(), entity.getItemId());
	}
	
	public Long exist(final Long itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlItemProcess.EXIST_ITEM_PROCESS, itemId);
		if (rs.next()) {
			return rs.getLong("id");
		}
		return null;
	}
	
	public int setDescParsed(final Long itemId) {
		return getJdbcTemplate().update(SqlItemProcess.SET_DESC_PARSED, true, new Date(), itemId);
	}
	
	public int setSkuParsed(final Long itemId) {
		return getJdbcTemplate().update(SqlItemProcess.SET_SKU_PARSED, true, new Date(), itemId);
	}
	
	public int resetDescParsed(final Long itemId) {
		return getJdbcTemplate().update(SqlItemProcess.RESET_DESC_PARSED, new Date(), itemId);
	}
	
	public int resetSkuParsed(final Long itemId) {
		return getJdbcTemplate().update(SqlItemProcess.SET_SKU_PARSED, false, new Date(), itemId);
	}
	
	public int incGroupImgCount(final Long itemId) {
		return getJdbcTemplate().update(SqlItemProcess.INC_GROUP_IMG_COUNT, new Date(), itemId);
	}
	
	public int incSkuImgCount(final Long itemId) {
		return getJdbcTemplate().update(SqlItemProcess.INC_SKU_IMG_COUNT, new Date(), itemId);
	}
	
	public int incDetailImgCount(final Long itemId) {
		return getJdbcTemplate().update(SqlItemProcess.INC_DETAIL_IMG_COUNT, new Date(), itemId);
	}
	
	public int resetGroupImgCount(final Long itemId) {
		return getJdbcTemplate().update(SqlItemProcess.RESET_GROUP_IMG_COUNT, new Date(), itemId);
	}
	
	public int resetSkuImgCount(final Long itemId) {
		return getJdbcTemplate().update(SqlItemProcess.RESET_SKU_IMG_COUNT, new Date(), itemId);
	}
	
	public int resetDetailImgCount(final Long itemId) {
		return getJdbcTemplate().update(SqlItemProcess.RESET_DETAIL_IMG_COUNT, new Date(), itemId);
	}
	
	public int updateGroupImgCount(final Long itemId, int group) {
		return getJdbcTemplate().update(SqlItemProcess.UPDATE_GRPUP_IMG_COUNT, group, 
				SpideItemType.GROUP_IMG.ordinal(), new Date(), itemId);
	}
	
	public int updateSkuImgCount(final Long itemId, int sku) {
		return getJdbcTemplate().update(SqlItemProcess.UPDATE_SKU_IMG_COUNT, sku, 
				SpideItemType.SKUS.ordinal(), new Date(), itemId);
	}
	
	public int updateSpideType(final Long itemId, SpideItemType type) {
		return getJdbcTemplate().update(SqlItemProcess.UPDATE_SPIDE_TYPE, type.ordinal(), new Date(), itemId);
	}
	
	public int updateGroupSkuImgCount(final Long itemId, int group, int sku) {
		return getJdbcTemplate().update(SqlItemProcess.UPDATE_GRPUP_SKU_IMG_COUNT, group, sku, new Date(), itemId);
	}
	
	public int updateDetailImgCount(final Long itemId, int count) {
		return getJdbcTemplate().update(SqlItemProcess.UPDATE_DETAIL_IMG_COUNT, count, 
				new Date(), itemId);
	}
	
	public ItemProcess queryOneItemProcess(Long itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlItemProcess.GET_ITEM_PROCESS, itemId);
		if (rs.next()) {
			final ItemProcess result = new ItemProcess();
			result.setId(rs.getLong("id"));
			result.setShopId(rs.getLong("shop_id"));
			result.setItemId(rs.getLong("item_id"));
			result.setDescParsed(rs.getBoolean("desc_parsed"));
			result.setSkuParsed(rs.getBoolean("sku_parsed"));
			result.setGroupImgCount(rs.getInt("group_img_count"));
			result.setSkuImgCount(rs.getInt("sku_img_count"));
			result.setDetailImgCount(rs.getInt("detail_img_count"));
			result.setCurGroupImgCount(rs.getInt("cur_group_img_count"));
			result.setCurSkuImgCount(rs.getInt("cur_sku_img_count"));
			result.setCurDetailImgCount(rs.getInt("cur_detail_img_count"));
			result.setType(SpideItemType.fromOrdinal(rs.getInt("type")));
			result.setOption(rs.getInt("sync_option"));
			return result;
		}
		return null;
	}
	
	public int delete(final Long itemId) {
		return getJdbcTemplate().update(SqlItemProcess.DELETE_ITEM_PROCESS, itemId);
	}
	
	public List<Long> queryItemIds(final Long shopId) {
		final List<Long> results = Lists.newArrayList();
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlItemProcess.GET_ITEM_IDS,  shopId);
		if (!rs.last()) {
			return results;
		}

		rs.beforeFirst();
		while (rs.next()) {
			results.add(rs.getLong("item_id"));
		}
		return results;
	}
	
	public Long queryShopId(String ouerShopId, String itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlItemProcess.GET_SHOP_ID, ouerShopId, itemId);
		if (rs.next()) {
			return rs.getLong("shop_id");
		}
		return null;
	}
}
