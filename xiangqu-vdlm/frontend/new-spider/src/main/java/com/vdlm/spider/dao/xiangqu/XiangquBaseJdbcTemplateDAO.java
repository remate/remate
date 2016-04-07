package com.vdlm.spider.dao.xiangqu;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:23:53 PM Jul 21, 2014
 */
public abstract class XiangquBaseJdbcTemplateDAO {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("xiangquDataSource")
	@Lazy
	public void setDataSource(final DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}