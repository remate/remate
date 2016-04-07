/**
 * 
 */
package com.vdlm.spider.dao.xiangqu;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import com.vdlm.spider.entity.Item;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:48:59 AM Aug 11, 2014
 */
@Component
public class XiangquItemDao extends XiangquBaseJdbcTemplateDAO {

	//	UPDATE product_pool
	//	SET title =?, price =?, url =?, image =?, taobao_time =?
	//	WHERE
	//		shop_id =?
	//	AND taobaoid =?
	public int update(final Item entity) {
		return getJdbcTemplate().update(XiangquSqlStatics.UPDATE_ITEM, entity.getName(), entity.getPrice(),
				entity.getItemUrl(), entity.getImgs(), new Timestamp(System.currentTimeMillis()), entity.getShopId(),
				entity.getItemId());
	}

	public int update(final String shopId, final String itemId, final Integer status) {
		return getJdbcTemplate().update(XiangquSqlStatics.UPDATE_ITEM_STATE, status,
				new Timestamp(System.currentTimeMillis()), shopId, itemId);
	}

	public int insert(final Item entity) {
		return getJdbcTemplate().execute(XiangquSqlStatics.INSERT_ITEM, new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				setStatement(ps, entity);
				return ps.executeUpdate();
			}
		});
	}

	void setStatement(final PreparedStatement ps, final Item entity) throws SQLException {
		//		shop_id,
		//		taobaoid,
		//		title,
		//		price,
		//		url,
		//		image,
		//		taobao_time
		int index = 0;
		ps.setString(++index, entity.getShopId());
		ps.setString(++index, entity.getItemId());
		ps.setString(++index, entity.getName());
		ps.setDouble(++index, entity.getPrice());
		ps.setString(++index, entity.getItemUrl());
		ps.setString(++index, entity.getImgs());
		ps.setTimestamp(++index, new Timestamp(System.currentTimeMillis()));
		ps.setString(++index, entity.getDetails());
	}

	public int[] insert(final List<Item> entities) {
		return getJdbcTemplate().batchUpdate(XiangquSqlStatics.INSERT_ITEM, new BatchPreparedStatementSetter() {

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
