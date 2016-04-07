package com.vdlm.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import com.vdlm.config.WebSecurityConfigurationAware;

public class UserIncomeAccountChange extends WebSecurityConfigurationAware {

	@Test
	public void incomeAccountChange() throws Exception{
		//userId
		MvcResult result = mockMvc.perform(
				get("/user/incomeAccount/change")
				.param("type", "1")
				.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void mineAccount() throws Exception{
		//userId
		MvcResult result = mockMvc.perform(
				get("/userBank/mineAccount")
				.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
}
