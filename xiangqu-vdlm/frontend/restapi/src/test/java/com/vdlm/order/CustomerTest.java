package com.vdlm.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import com.vdlm.config.WebSecurityConfigurationAware;

public class CustomerTest extends WebSecurityConfigurationAware{

	@Test
	public void testList() throws Exception{
		MvcResult result = mockMvc.perform(get("/customer/list").session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void testListByKey() throws Exception{
		MvcResult result = mockMvc.perform(post("/customer/listByKey").param("key", "阿").session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
}
