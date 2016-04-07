

package com.vdlm.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.vdlm.biz.authentication.LoginConfigurationException;
import com.vdlm.biz.rememberme.PersistentSplitTokenRememberMeServices;
import com.vdlm.service.user.UserService;

@Configuration
@EnableWebMvcSecurity
@ImportResource("classpath:META-INF/spring-security-context.xml")
class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource;

	@Autowired
	UserService userService;

	@Value("${rememberMeServices.key}")
	String remMeKey;

	@Value("${rememberMeServices.token.calidity.seconds}")
	int validSeconds;

	@Value("${rememberMeServices.token.concurrency.safe.seconds}")
	int concurrencySafeSeconds;
	
	@Autowired
	GlobalConfig globalConfig;

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource);
		return repo;
	}

	@Bean
	public RememberMeServices rememberMeServices() {
		PersistentSplitTokenRememberMeServices svc = new PersistentSplitTokenRememberMeServices(remMeKey, userService,
                persistentTokenRepository());

		// TokenBasedRememberMeServices svc = new TokenBasedRememberMeServices(remMeKey, userService);
		svc.setAlwaysRemember(true);
		svc.setTokenValiditySeconds(validSeconds);
		svc.setCookieName("kdAuthToken");
		svc.setConcurrencySafeSeconds(concurrencySafeSeconds);
		return svc;
	}

	@Bean
	public LoginUrlAuthenticationEntryPoint loginEntryPoint() throws LoginConfigurationException {
		return new LoginUrlAuthenticationEntryPoint(globalConfig.getProperty("xiangqu.sellerpc.login.url")) {

			@Override
			protected String buildRedirectUrlToLoginPage(HttpServletRequest request,
					HttpServletResponse response, AuthenticationException authException) {
				final String sellerpcLoginUrlFormat = globalConfig.getProperty("xiangqu.sellerpc.login.urlFormat");
				final String sellerpcLoginUrl = String.format(sellerpcLoginUrlFormat, globalConfig.getAndEncodeSellerpcAuthUrl(request.getRequestURL().toString()));
				
				return sellerpcLoginUrl;
			}
			
		};
	}
	
}

