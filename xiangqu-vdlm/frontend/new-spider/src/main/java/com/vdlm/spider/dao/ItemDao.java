/**
 * 
 */
package com.vdlm.spider.dao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Item.SkuProps;
import com.vdlm.spider.utils.ObjectConvertUtils;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:24:14 PM Jul 21, 2014
 */
@Component
public class ItemDao extends BaseJdbcTemplateDAO {

	public int update(final Item entity) throws Exception {
		return getJdbcTemplate().update(SqlStatics.UPDATE_ITEM_ONLY, entity.getName(), entity.getPrice(),
				entity.getAmount(), entity.getStatus(), ObjectConvertUtils.toString(entity.getSkuProps()), new Date(), entity.getId());
	}
	
	public int update(final Item entity, final int imgsCount) throws Exception {
		return getJdbcTemplate().update(SqlStatics.UPDATE_ITEM, entity.getName(), entity.getPrice(),
				entity.getAmount(), entity.getStatus(), ObjectConvertUtils.toString(entity.getSkuProps()), imgsCount, entity.getId());
	}

	public int update(final Long id, final Integer status) {
		return getJdbcTemplate().update(SqlStatics.UPDATE_ITEM_STATUS, status, id);
	}
	
	public Item queryOne4Task(String ouerShopId, String thirdItemId) {
		return getJdbcTemplate().queryForObject(SqlStatics.GET_ITEM_4_TASK, new RowMapper<Item>() {

			@Override
			public Item mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				final Item result = new Item();
				if (rs.getRow() > 0) {
					result.setId(rs.getLong("id"));
					result.setOuerUserId(rs.getString("ouer_user_id"));
					result.setOuerShopId(rs.getString("ouer_shop_id"));
					result.setShopType(ShopType.fromName(rs.getString("shop_type")));
					result.setReqFrom(ReqFrom.valueOf(rs.getInt("req_from")));
					result.setItemId(rs.getString("item_id"));
					result.setItemUrl(rs.getString("item_url"));
					return result;
				}
				return null;
			}
			
		}, ouerShopId, thirdItemId);
	}
	
	public Item queryOneItem(Long itemId) {
		return getJdbcTemplate().queryForObject(SqlStatics.GET_ITEM, new RowMapper<Item>() {

			@Override
			public Item mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				final Item result = new Item();
				result.setId(rs.getLong("id"));
				result.setOuerUserId(rs.getString("ouer_user_id"));
				result.setOuerShopId(rs.getString("ouer_shop_id"));
				result.setName(rs.getString("name"));
				result.setPrice(rs.getDouble("price"));
				result.setAmount(rs.getInt("amount"));
				result.setItemUrl(rs.getString("item_url"));
				result.setShopType(ShopType.fromName(rs.getString("shop_type")));
				result.setItemId(rs.getString("item_id"));
				result.setStatus(rs.getInt("status"));
				result.setReqFrom(ReqFrom.fromName(rs.getString("req_from")));
				result.setUserId(rs.getString("user_id"));
				result.setShopId(rs.getString("shop_id"));
				result.setDetails(rs.getString("details"));
				try {
					result.setSkuProps(ObjectConvertUtils.fromString(rs.getString("sku_props"), SkuProps.class));
				} catch (final IOException e) {
					e.printStackTrace();
				}
				result.setCompleted(rs.getInt("completed"));
				
				return result;
			}
			
		}, itemId);
	}
	
	public Long insert(final Item entity) throws IOException {
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
		paramSource.addValue("sku_props", ObjectConvertUtils.toString(entity.getSkuProps()));
		//paramSource.addValue("sku_props", entity.getSkuProps());

		final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());

		if (t.update(SqlStatics.INSERT_ITEM_ONLY, paramSource, generatedKeyHolder, new String[] { "id" }) <= 0) {
			return null;
		}

		return generatedKeyHolder.getKey().longValue();
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
		paramSource.addValue("sku_props", entity.getSkuProps());
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
//	public Long exist(final ReqFrom reqFrom, final String ouerUserId, final String ouerShopId, final ShopType shopType,
//			final String itemId) {
//		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlStatics.EXIST_ITEM, reqFrom.getValue(), ouerUserId,
//				ouerShopId, shopType.getValue(), itemId);
//		if (rs.next()) {
//			return rs.getLong("id");
//		}
//		return null;
//	}
	
	public Long exist(final String ouerShopId, final String itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlStatics.EXIST_ITEM_NEW,
				ouerShopId, itemId);
		if (rs.next()) {
			return rs.getLong("id");
		}
		return null;
	}
	
	public Long exist(final Long itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlStatics.SIMPLE_EXIST_ITEM, itemId);
		if (rs.next()) {
			return rs.getLong("id");
		}
		return null;
	}
	
	public Long exist(final String itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlStatics.SIMPLE_EXIST_ITEM_BY_THIRDID, itemId);
		if (rs.next()) {
			return rs.getLong("id");
		}
		return null;
	}
	
	public int updateCompleted(int completed, Long id) {
		return getJdbcTemplate().update(SqlStatics.UPDATE_ITEM_COMPLETED, completed, "SUCCESS", id);
	}
	
	public int updateCompleted(int completed, Long id, String desc) {
		return getJdbcTemplate().update(SqlStatics.UPDATE_ITEM_COMPLETED, completed, desc, id);
	}
	
	public int queryCompleted(Long id) {
		return getJdbcTemplate().queryForInt(SqlStatics.GET_ITEM_COMPLETED, id);
	}
	
	public String queryForOneItemId(String shopId, String url) {
		//System.out.println(String.format(SqlStatics.QUERY_ONE_ITEMID, "%" + url + "%"));
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(String.format(SqlStatics.QUERY_ONE_ITEMID, "%" + url + "%"), shopId, shopId);
		if (rs.next()) {
			return rs.getString("item_id");
		}
		return null;
	}
}
