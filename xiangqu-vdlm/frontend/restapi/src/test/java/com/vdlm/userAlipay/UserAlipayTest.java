package com.vdlm.userAlipay;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import com.vdlm.config.WebSecurityConfigurationAware;

public class UserAlipayTest extends WebSecurityConfigurationAware {

	@Test
	public void addUserAlipay() throws Exception{
		MvcResult result = mockMvc.perform(
				get("/userAlipay/save")
				.param("id","")
				.param("account", "13858088391")
				.param("name", "13858088391")
				.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void updateUserAlipay() throws Exception{
		MvcResult result = mockMvc.perform(
				get("/userAlipay/save")
				.param("id","5")
				.param("account", "13858888888")
				.param("name", "13858088391")
				.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
}
