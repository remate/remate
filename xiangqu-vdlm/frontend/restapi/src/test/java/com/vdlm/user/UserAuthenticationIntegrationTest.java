package com.vdlm.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.vdlm.config.WebSecurityConfigurationAware;

public class UserAuthenticationIntegrationTest extends
		WebSecurityConfigurationAware {

	// @Test
	public void requiresAuthentication() throws Exception {
	}

	@Test
	public void userAuthenticates() throws Exception {
		mockMvc.perform(get("/signined").session(sessionLogined)).andExpect(
				MockMvcResultMatchers.jsonPath("$.data.phone").value(
						CURRENT_USER_MOBILE));
	}
	
}
