package com.vdlm.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public abstract class WebSecurityConfigurationAware extends
		WebAppConfigurationAware {

	@Inject
	private FilterChainProxy springSecurityFilterChain;

	private final static String SEC_CONTEXT_ATTR = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

	public final static String CURRENT_USER_MOBILE = "13312345678";
	public final static String CURRENT_USER_PWD = "13312345678";

	protected SessionWrapper sessionLogined;

	@Before
	public void before() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilters(
				this.springSecurityFilterChain).build();

		// 注册和登录当前用户
		register(CURRENT_USER_MOBILE, CURRENT_USER_PWD);
		login(CURRENT_USER_MOBILE, CURRENT_USER_PWD);
	}

	protected void login(final String u, String p) throws Exception {
		ResultMatcher matcher = new ResultMatcher() {
			public void match(MvcResult mvcResult) throws Exception {
				HttpSession session = mvcResult.getRequest().getSession();
				SecurityContext securityContext = (SecurityContext) session
						.getAttribute(SEC_CONTEXT_ATTR);
				Assert.assertEquals(securityContext.getAuthentication()
						.getName(), u);
			}
		};
		mockMvc.perform(post("/signin_check").param("u", u).param("p", p))
				.andExpect(redirectedUrl("/signined")).andExpect(matcher)
				.andDo(new ResultHandler() {

					@Override
					public void handle(MvcResult result) throws Exception {
						sessionLogined = new SessionWrapper(result.getRequest()
								.getSession());
					}
				});
	}

	protected void register(String phone, String p) throws Exception {
		mockMvc.perform(
				post("/register").param("mobile", phone).param("pwd", p)
						.param("smsCode", "123456")).andExpect(
				MockMvcResultMatchers.jsonPath("$.data.phone").value(phone));
	}
}
