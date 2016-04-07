package com.vdlm.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import com.vdlm.config.WebSecurityConfigurationAware;
import com.vdlm.dal.type.SynchronousSource;

public class ProductListTest extends WebSecurityConfigurationAware {

	@Test
	public void delayPorductList() throws Exception {
		MvcResult result = mockMvc.perform(
				get("/product/list/delay").session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void productSave() throws Exception{
		MvcResult result = mockMvc.perform(
				get("/product/save")
				.param("shopId", "8rp81sat")
				.param("userId", "3icjhdkq")
				.param("name", "nccncncn")
				.param("imgs", new String[]{"qn|xaya|Frbejsh36iveEFpI_WwJOUpkDs6F"})
				.param("recommend", "0")
				.param("status", "ONSALE")
				.param("description", "cjcjccnnd")
				.param("forsaleDate", "0")
				.param("skus[0].spec", "无")
				.param("skus[0].price", "111.0")
				.param("skus[0].amount", "111")
				.param("tags[0].tag", "攻击力了")
				.param("category", "3377")
				.param("isDelay", "0")
				.param("delayAt", "0")
				.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void synchronous() throws Exception{
		List<String> aaa = new ArrayList<String>();
		aaa.add("aa48");
		aaa.add("y8xn");
		MvcResult result = mockMvc.perform(
				get("/product/synchronous")
				.param("ids", "aa48")
				.param("ids", "y8xn")
				.param("sourceVal", SynchronousSource.XIANGQU.toString())
				.param("examineRet", "1")
				.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
}
