package com.vdlm.msg;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import com.vdlm.config.WebSecurityConfigurationAware;

public class HelperTest extends WebSecurityConfigurationAware {
	
	@Test
	public void HelperList() throws Exception {
		MvcResult result = mockMvc.perform(
				get("/message/helper/list").session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
}
