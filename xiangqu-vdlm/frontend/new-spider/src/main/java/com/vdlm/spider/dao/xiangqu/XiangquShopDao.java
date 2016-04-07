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

import com.vdlm.spider.entity.Shop;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:56:44 AM Aug 11, 2014
 */
@Component
public class XiangquShopDao extends XiangquBaseJdbcTemplateDAO {

	//	UPDATE product_shop
	//	SET name =?, nick =?, score =?, last_get_time =?
	//	WHERE
	//		id =?
	public int update(final Shop entity) {
		return getJdbcTemplate().update(XiangquSqlStatics.UPDATE_SHOP, entity.getName(), entity.getNickname(),
				entity.getScore(), new Timestamp(System.currentTimeMillis()), entity.getShopId());
	}

	public int insert(final Shop entity) {
		return getJdbcTemplate().execute(XiangquSqlStatics.INSERT_SHOP, new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				setStatement(ps, entity);
				return ps.executeUpdate();
			}
		});
	}

	void setStatement(final PreparedStatement ps, final Shop entity) throws SQLException {
		//		id
		//		category
		//		name
		//		nick
		//		url
		//		score
		//		last_get_time
		//		taobao_time
		int index = 0;
		ps.setString(++index, entity.getShopId());
		ps.setString(++index, "4");
		ps.setString(++index, entity.getName());
		ps.setString(++index, entity.getNickname());
		ps.setString(++index, entity.getShopUrl());
		ps.setString(++index, entity.getScore());

		final Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		ps.setTimestamp(++index, currentTime);
		ps.setTimestamp(++index, currentTime);
	}

	public int[] insert(final List<Shop> entities) {
		return getJdbcTemplate().batchUpdate(XiangquSqlStatics.INSERT_SHOP, new BatchPreparedStatementSetter() {

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
