/**
 * 
 */
package com.vdlm.spider.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.vdlm.spider.dao.sql.SqlStatics;
import com.vdlm.spider.entity.Sku;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:10:34 PM Jul 21, 2014
 */
@Component
public class SkuDao extends BaseJdbcTemplateDAO {
	
	public int deleteList(final Long itemId) {
		return getJdbcTemplate().update(SqlStatics.DELETE_SKU, itemId);
	}

	public int insert(final Sku entity) {
		return getJdbcTemplate().execute(SqlStatics.INSERT_SKU, new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				setStatement(ps, entity);
				return ps.executeUpdate();
			}
		});
	}

	void setStatement(final PreparedStatement ps, final Sku entity) throws SQLException {
		//		item_id,
		//		spec,
		//		price,
		//		amount,
		//		orig_spec
		int index = 0;
		ps.setLong(++index, entity.getItemId());
		ps.setString(++index, entity.getSpec());
		ps.setDouble(++index, entity.getPrice());
		ps.setInt(++index, entity.getAmount());
		ps.setString(++index, entity.getOrigSpec());
		ps.setString(++index, entity.getImgUrl());
		ps.setString(++index, entity.getSkuId());
	}

	public int[] insert(final List<Sku> entities) {
		return getJdbcTemplate().batchUpdate(SqlStatics.INSERT_SKU, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				setStatement(ps, entities.get(index));
			}

			@Override
			public int getBatchSize() {
				return entities.size();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<Sku> querySkus(Long itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlStatics.GET_SKU,  itemId);
		if (!rs.last()) {
			return Collections.EMPTY_LIST;
		}
		final List<Sku> results = new ArrayList<Sku>(rs.getRow());
		rs.beforeFirst();
		while (rs.next()) {
			final Sku sku = new Sku();
			sku.setId(rs.getLong("id"));
			sku.setItemId(rs.getLong("item_id"));
			sku.setSpec(rs.getString("spec"));
			sku.setPrice(rs.getDouble("price"));
			sku.setAmount(rs.getInt("amount"));
			sku.setSkuId(rs.getString("sku_id"));
			results.add(sku);
		}
		return results;
	}
	
}
