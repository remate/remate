//package com.vdlm.spider.dao;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Date;
//
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.jdbc.support.GeneratedKeyHolder;
//import org.springframework.jdbc.support.KeyHolder;
//import org.springframework.jdbc.support.rowset.SqlRowSet;
//import org.springframework.stereotype.Component;
//
//import com.vdlm.spider.dao.sql.SqlShopProcess;
//import com.vdlm.spider.entity.ShopProcess;
//
///**
// *
// * @author: chenxi
// */
//
//@Component
//public class ShopProcessDao extends BaseJdbcTemplateDAO {
//
//	public Long insert(final ShopProcess entity) {
//		final MapSqlParameterSource paramSource = new MapSqlParameterSource();
//
//		final Date now = new Date();
//		paramSource.addValue("shop_id", entity.getShopId());
//		paramSource.addValue("item_count", entity.getItemCount());
//		paramSource.addValue("cur_item_count", entity.getCurItemCount());
//		paramSource.addValue("partially", entity.isPartially());
//		paramSource.addValue("create_at", now);
//		paramSource.addValue("update_at", now);
//
//		final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
//
//		final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());
//
//		if (t.update(SqlShopProcess.INSERT_SHOP_PROCESS, paramSource, 
//				generatedKeyHolder, new String[] { "id" }) <= 0) {
//			return null;
//		}
//
//		return generatedKeyHolder.getKey().longValue();
//	}
//	
//	public Long exist(final Long shopId) {
//		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlShopProcess.EXIST_SHOP_PROCESS, shopId);
//		if (rs.next()) {
//			return rs.getLong("id");
//		}
//		return null;
//	}
//	
//	public ShopProcess queryOneItemProcess(Long shopId) {
//		return getJdbcTemplate().queryForObject(SqlShopProcess.GET_SHOP_PROCESS, new RowMapper<ShopProcess>() {
//
//			@Override
//			public ShopProcess mapRow(ResultSet rs, int rowNum)
//					throws SQLException {
//				final ShopProcess result = new ShopProcess();
//				result.setShopId(rs.getLong("shop_id"));
//				result.setItemCount(rs.getInt("item_count"));
//				result.setCurItemCount(rs.getInt("cur_item_count"));
//				result.setPartially(rs.getBoolean("partially"));
//				return result;
//			}
//			
//		}, shopId);
//	}
//	
//	public int update(final ShopProcess entity) {
//		return getJdbcTemplate().update(SqlShopProcess.UPDATE_SHOP_PROCESS, entity.getItemCount(), 
//				entity.getCurItemCount(), entity.isPartially(), new Date(), entity.getShopId());
//	}
//	
//	public int delete(final Long shopId) {
//		return getJdbcTemplate().update(SqlShopProcess.DELETE_SHOP_PROCESS, shopId);
//	}
//	
//}
