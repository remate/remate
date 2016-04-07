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
import org.springframework.stereotype.Component;

import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.dao.sql.SqlStatics;
import com.vdlm.spider.entity.TaskLog;

/**
 * @author amon 
 */
@Component
public class TaskLogDao extends BaseJdbcTemplateDAO {
	
	public long insert(final TaskLog entity) throws IOException  {
		final MapSqlParameterSource paramSource = new MapSqlParameterSource();	 
		paramSource.addValue("taskId", entity.getTaskId());
		paramSource.addValue("ouer_user_id", entity.getOuer_user_id());
		paramSource.addValue("ouer_shop_id", entity.getOuer_shop_id());
		paramSource.addValue("req_from", entity.getReq_from().getValue());
		paramSource.addValue("shopType", entity.getShopType().getValue());
		paramSource.addValue("status", entity.getStatus()); 
		final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		final NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());
		if (t.update(SqlStatics.INSERT_TASKLOG, paramSource, generatedKeyHolder, new String[] { "id" }) <= 0) {
			return 0;
		}
		return generatedKeyHolder.getKey().longValue();		 
	}
	
	public int update(final TaskLog entity) throws Exception {
		return getJdbcTemplate().update(SqlStatics.UPDATE_TASKLOG, entity.getStatus(), 
				 new Date(), entity.getTaskId());
	} 
	
	public TaskLog queryOneTaskLog(long taskId) {
		return getJdbcTemplate().queryForObject(SqlStatics.GET_TASHLOG, new RowMapper<TaskLog>() {
			@Override
			public TaskLog mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				final TaskLog result = new TaskLog();
				result.setId(rs.getLong("id"));
				result.setTaskId(rs.getLong("taskId"));
				result.setOuer_shop_id(rs.getString("ouer_shop_id"));
				result.setOuer_user_id(rs.getString("ouer_user_id")); 
				result.setReq_from(ReqFrom.valueOf(rs.getInt("req_from"))); 
				result.setShopType(ShopType.valueOf(rs.getInt("shopType"))); 
				result.setStatus(rs.getInt("status"));  
				return result;
			}			
		}, taskId);
	}	
}
