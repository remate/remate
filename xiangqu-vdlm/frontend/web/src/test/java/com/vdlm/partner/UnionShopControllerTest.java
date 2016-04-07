package com.vdlm.partner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Calendar;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import com.alibaba.fastjson.JSONObject;
import com.vdlm.config.WebSecurityConfigurationAware;
import com.vdlm.service.user.UserService;
import com.vdlm.utils.MD5Util;

public class UnionShopControllerTest extends WebSecurityConfigurationAware {

	@Autowired
	private UserService userService;
	
//	@Test
//	public void listU() throws Exception {
//		FileInputStream fis = new FileInputStream("C:\\Users\\huxaya\\Desktop\\IOS.jpg");
//		MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
//		String unionId="ajt3wz5y", page = "0", size = "20";
////		String fileMd5 = MD5Util.MD5Encode(multipartFile.getInputStream());
//		String a = MD5Util.MD5Encode(new FileInputStream("C:\\Users\\huxaya\\Desktop\\IOS.jpg"));
//		System.out.println(a);
//		String sign = MD5Util.MD5Encode("page="+page+"&size="+size+"&unionId=" + unionId + "19r4e7s12356297t07eda94cf7b35193", "UTF-8");
//
//		MvcResult result = mockMvc.perform(
//				fileUpload("/partner/_f/u")
//				.file("file[0]", multipartFile.getBytes())
////				.file("file", multipartFile.getBytes())
////				.file(multipartFile)
////				.contentType(MediaType.MULTIPART_FORM_DATA)
//				.param("unionId", unionId)
//				.param("belong", FileBelong.PRODUCT.toString())
//				.param("sign", sign)
//				).andReturn();
//		String resp = result.getResponse().getContentAsString();
//		System.out.println(resp);
//		
//		JSONObject json = JSONObject.parseObject(resp);
//		Assert.assertEquals(200, json.getIntValue("errorCode"));
//	}
	
	@Test
	public void orderSubmitTest() throws Exception{
		String unionId="cayiubv3";
		String name = "ffffabc";
//		String[] imgs = new String[]{, "qn|xaya|Fova8f4fD6wwo_avjzg49BH2xHUx"};
		String status = "ONSALE";
		String description = "descriptionmengxiangtest";
		String imgs = "qn|xaya|Frbejsh36iveEFpI_WwJOUpkDs6F";
		String imgs1 = "qn|xaya|Fova8f4fD6wwo_avjzg49BH2xHUx";
		String spec = "无";
		String price = "222";
		String amount = "99999";
		String recommend = "0";
		
		String signParam = "description="+description
//				+"&id=59l36djf"
				+"&imgs[0]="+imgs
				+"&imgs[1]="+imgs1
				+"&name="+name
				+"&recommend="+recommend
				+"&skus[0].amount="+amount
				+"&skus[0].price="+price
				+"&skus[0].spec="+spec
				+"&status="+status
				+"&union_id=" + unionId + "19r4e7s12356297t07eda94cf7b35193"; 
		
		String sign = MD5Util.MD5Encode(signParam, "UTF-8");
		
		MvcResult result = mockMvc.perform(
				get("partner/order/submit")
//				.param("id", "59l36djf")
				.param("union_id", unionId)
				.param("name", name)
				.param("imgs[0]", imgs)
				.param("imgs[1]", imgs1)
				.param("recommend", "0")
				.param("status", status)
				.param("description", description)
				.param("skus[0].spec", spec)
				.param("skus[0].price", price)
				.param("skus[0].amount", amount)
				.param("sign", sign)
				).andReturn();
		String resp = result.getResponse().getContentAsString();
		System.out.println(resp);
		
		JSONObject json = JSONObject.parseObject(resp);
		Assert.assertEquals(200, json.getIntValue("errorCode"));
	}
}
