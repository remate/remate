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

import com.vdlm.spider.entity.Img;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 2:44:05 PM Jul 23, 2014
 */
@Component
public class ImgDao extends BaseJdbcTemplateDAO {
	
	public int deleteList(final Long itemId) {
		return getJdbcTemplate().update(SqlStatics.DELETE_IMG, itemId);
	}
	
	public int insert(final Img entity) {
		return getJdbcTemplate().execute(SqlStatics.INSERT_IMG, new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				setStatement(ps, entity);
				return ps.executeUpdate();
			}
		});
	}

	void setStatement(final PreparedStatement ps, final Img entity) throws SQLException {
		//		img_url,
		//		img,
		//		item_id,
		//		order_num,
		//		md5
		int index = 0;
		ps.setString(++index, entity.getImgUrl());
		ps.setString(++index, entity.getImg());
		ps.setLong(++index, entity.getItemId());
		ps.setInt(++index, entity.getOrderNum());
		ps.setString(++index, entity.getMd5());
		ps.setInt(++index, entity.getType());
	}

	public int[] insert(final List<Img> entities) {
		return getJdbcTemplate().batchUpdate(SqlStatics.INSERT_IMG, new BatchPreparedStatementSetter() {

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
