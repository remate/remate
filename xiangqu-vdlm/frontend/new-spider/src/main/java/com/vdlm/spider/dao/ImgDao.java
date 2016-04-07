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
	
	public int deleteList(final Long itemId, int type) {
		return getJdbcTemplate().update(SqlStatics.DELETE_IMG_BY_TYPE, itemId, type);
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
		ps.setInt(++index, entity.getStatus());
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
	
	public int queryImgCount(Long itemId, int type) {
		return getJdbcTemplate().queryForInt(SqlStatics.QUERY_IMG_CNT, itemId, type);
	}
	
	@SuppressWarnings("unchecked")
	public List<Img> queryImgs(Long itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlStatics.GET_IMG,  itemId);
		if (!rs.last()) {
			return (List<Img>) Collections.EMPTY_LIST;
		}
		final List<Img> results = new ArrayList<Img>(rs.getRow());
		
		rs.beforeFirst();
		while (rs.next()) {
			final Img img = new Img();
			img.setId(rs.getLong("id"));
			img.setImg(rs.getString("img"));
			img.setImgUrl(rs.getString("img_url"));
			img.setItemId(rs.getLong("item_id"));
			img.setOrderNum(rs.getInt("order_num"));
			img.setMd5(rs.getString("md5"));
			img.setType(rs.getInt("type"));
			results.add(img);
		}
		return results;
	}
	
}
