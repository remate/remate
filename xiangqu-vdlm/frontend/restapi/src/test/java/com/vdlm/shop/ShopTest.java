package com.vdlm.shop;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import com.vdlm.biz.url.UrlHelper;
import com.vdlm.config.WebSecurityConfigurationAware;
import com.vdlm.dal.mapper.ShopMapper;
import com.vdlm.dal.mybatis.IdTypeHandler;

public class ShopTest extends WebSecurityConfigurationAware {
	
	@Autowired
	private ShopMapper shopMapper;
	
	@Test
	public void testMine() throws Exception {
		MvcResult result = mockMvc.perform(
				post(UrlHelper.SHOP_URL_PREFIX + "/mine")
						.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void updateImg() throws Exception {
		MvcResult result = mockMvc.perform(
				post(UrlHelper.SHOP_URL_PREFIX + "/updateImg")
						.param("id", IdTypeHandler.encode(14023))
						.param("img", "Fnx9Ggez1z_FT2QBcIAzBanWDiA_")
						.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void updateName() throws Exception {
		MvcResult result = mockMvc.perform(
				post(UrlHelper.SHOP_URL_PREFIX + "/updateName")
						.param("id", IdTypeHandler.encode(14023))
						.param("name", "穿衣打扮模特店2")
						.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void updateWechat() throws Exception {
		MvcResult result = mockMvc.perform(
				post(UrlHelper.SHOP_URL_PREFIX + "/updateWechat")
						.param("id", IdTypeHandler.encode(14023))
						.param("wechat", "wechat1231")
						.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void updateDesc() throws Exception {
		MvcResult result = mockMvc.perform(
				post(UrlHelper.SHOP_URL_PREFIX + "/updateDesc")
						.param("id", IdTypeHandler.encode(14023))
						.param("description", "穿衣打扮模特店")
						.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void updateBulletin() throws Exception {
		MvcResult result = mockMvc.perform(
				post(UrlHelper.SHOP_URL_PREFIX + "/updateBulletin")
						.param("id", IdTypeHandler.encode(14023))
						.param("bulletin", "穿衣打扮模特店")
						.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void updateLocal() throws Exception {
		MvcResult result = mockMvc.perform(
				post(UrlHelper.SHOP_URL_PREFIX + "/updateLocal")
						.param("id", IdTypeHandler.encode(14023))
						.param("provinceId", "2")
						.param("cityId", "3")
						.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void updatePostageStatus() throws Exception {
		MvcResult result = mockMvc.perform(
				post(UrlHelper.SHOP_URL_PREFIX + "/updatePostageStatus")
						.param("id", IdTypeHandler.encode(14023))
						.param("postageStatus", "")
						.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void updatePostage() throws Exception {
		MvcResult result = mockMvc.perform(
				post(UrlHelper.SHOP_URL_PREFIX + "/updatePostage")
						.param("id", IdTypeHandler.encode(14023))
						.param("freeZoneId", "")
						.param("postage", "")
						.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	

	@Test
	public void testStatistics() throws Exception {
		MvcResult result = mockMvc.perform(
				post(UrlHelper.SHOP_URL_PREFIX + "/statistics")
						.session(sessionLogined)).andReturn();
		System.out.println("结果=" + result.getResponse().getContentAsString());
	}
}
