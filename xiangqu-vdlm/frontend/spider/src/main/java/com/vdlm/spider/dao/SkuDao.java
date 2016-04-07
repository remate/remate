/**
 * 
 */
package com.vdlm.spider.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

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
}
