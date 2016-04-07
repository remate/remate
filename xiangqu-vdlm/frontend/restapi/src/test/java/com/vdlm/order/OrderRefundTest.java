package com.vdlm.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import com.alibaba.fastjson.JSONObject;
import com.vdlm.config.WebSecurityConfigurationAware;
import com.vdlm.dal.mybatis.IdTypeHandler;

public class OrderRefundTest extends WebSecurityConfigurationAware{

	@Test
	public void testLoad() throws Exception{
		MvcResult result = mockMvc.perform(get("/order/"+IdTypeHandler.encode(121279)).session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void testList() throws Exception{
		MvcResult result = mockMvc.perform(
				get("/order/refund/list")
				.session(sessionLogined))
				.andReturn();
		String resp = result.getResponse().getContentAsString();
		System.out.println(resp);
		
		JSONObject json = JSONObject.parseObject(resp);
		Assert.assertEquals(200, json.getIntValue("errorCode"));
		System.out.println(result.getResponse().getContentAsString());
	}
	
	
	
	@Test
	public void testListByKey() throws Exception{
		MvcResult result = mockMvc.perform(
				post("/order/listByKey/SUCCESS")
				.param("key", "200")
				.session(sessionLogined)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void testRefundFee() throws Exception{
		//String orderId = null;
		String goodsFee = null;
		String fullGoodsFee = null;
		String logisticsFee = null;
		String fullLogisticsFee = null;
		
		MvcResult result = mockMvc.perform(
				post("/order/refundFee")
//				.param("orderId", orderId)
				
				.param("goodsFee", goodsFee)
				.param("fullGoodsFee", fullGoodsFee)
				.param("logisticsFee", logisticsFee)
				.param("fullLogisticsFee", fullLogisticsFee)
				.session(sessionLogined)
				).andReturn();
		String resp = result.getResponse().getContentAsString();
		System.out.println(resp);
		
		JSONObject json = JSONObject.parseObject(resp);
		Assert.assertEquals(200, json.getIntValue("errorCode"));
		
	}
}
