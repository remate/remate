/**
 * sudaw copy right 1.0 
 */
package org.springframework.security.web.authentication.rememberme;

import java.util.Date;

import javax.sql.DataSource;

/**
 * JdbcTokenRepositoryImplEx.java
 * 
 * @author Ju
 */
public class JdbcTokenRepositoryImplEx extends JdbcTokenRepositoryImpl {
    private String updateTokenSql = DEF_UPDATE_TOKEN_SQL;

    public JdbcTokenRepositoryImplEx() {
		super();
	}

	public JdbcTokenRepositoryImplEx(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        getJdbcTemplate().update(updateTokenSql, tokenValue, lastUsed, series);
    }

}
