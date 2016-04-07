package com.vdlm.spider.dao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.vdlm.spider.dao.sql.SqlDesc;
import com.vdlm.spider.dao.sql.SqlStatics;
import com.vdlm.spider.entity.Desc;
import com.vdlm.spider.entity.Desc.DescFragments;
import com.vdlm.spider.utils.ObjectConvertUtils;

/**
 *
 * @author: chenxi
 */

@Component
public class DescDao extends BaseJdbcTemplateDAO {
//
//	@Autowired
//	private ObjectConverter objectConverter;
	
	public Long insert(final Desc entity) throws IOException {
		final MapSqlParameterSource paramSource = new MapSqlParameterSource();
		/*
		 "	item_id,\n" +
			"	desc_url,\n" +
			"   details,\n" +
			"	fragments,\n" +
			"	create_at,\n" +
			"   update_at\n" +
		 */
		final Date now = new Date();
		paramSource.addValue("item_id", entity.getItemId());
		paramSource.addValue("desc_url", entity.getDescUrl());
		paramSource.addValue("details", entity.getDetails());
		paramSource.addValue("fragments", ObjectConvertUtils.toString(entity.getFragments()));
		paramSource.addValue("create_at", now);
		paramSource.addValue("update_at", now);

		final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());

		if (t.update(SqlDesc.INSERT_DESC, paramSource, 
				generatedKeyHolder, new String[] { "id" }) <= 0) {
			return null;
		}

		return generatedKeyHolder.getKey().longValue();
	}
	
	public Desc queryOneDesc(Long itemId) {
		return getJdbcTemplate().queryForObject(SqlDesc.GET_DESC, new RowMapper<Desc>() {

			@Override
			public Desc mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				final Desc result = new Desc();
				result.setItemId(rs.getLong("item_id"));
				result.setDescUrl(rs.getString("desc_url"));
				result.setDetails(rs.getString("details"));
				try {
					result.setFragments(ObjectConvertUtils.fromString(rs.getString("fragments"), DescFragments.class));
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return result;
			}
			
		}, itemId);
	}
	
	public int deleteDesc(final Long itemId) {
		return getJdbcTemplate().update(SqlDesc.DELETE_DESC, itemId);
	}
	
	public Long exist(final Long itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlDesc.EXIST_DESC, itemId);
		if (rs.next()) {
			return rs.getLong("id");
		}
		return null;
	}
	
	public int update(final Desc entity) throws Exception {
		return getJdbcTemplate().update(SqlDesc.UPDATE_DESC, entity.getDescUrl(), 
				entity.getDetails(), ObjectConvertUtils.toString(entity.getFragments()), new Date(), entity.getItemId());
	}
	
	@SuppressWarnings("unchecked")
	public List<Desc> queryDesc(Long itemId) {
		final SqlRowSet rs = getJdbcTemplate().queryForRowSet(SqlStatics.GET_DESC,  itemId);
		if (!rs.last()) {
			return Collections.EMPTY_LIST;
		}
		final List<Desc> results = new ArrayList<Desc>(rs.getRow());
		
		rs.beforeFirst();
		while (rs.next()) {
			final Desc desc = new Desc();
			desc.setId(rs.getLong("id"));
			desc.setDescUrl(rs.getString("desc_url"));
			desc.setItemId(rs.getLong("item_id"));
			desc.setDetails(rs.getString("details"));
			results.add(desc);
		}
		return results;
	}
	
}
