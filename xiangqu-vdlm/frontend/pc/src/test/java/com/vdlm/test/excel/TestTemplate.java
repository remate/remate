package com.vdlm.test.excel;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

import com.vdlm.config.WebSecurityConfigurationAware;
import com.vdlm.dal.excel.model.Order;
import com.vdlm.utils.excel.ExcelTemplate;
import com.vdlm.utils.excel.ExcelUtil;

public class TestTemplate extends WebSecurityConfigurationAware {
	@Test
	public void test01() {
		ExcelTemplate et = ExcelTemplate.getInstance().readTemplateByClasspath(
				"/excel.template/default.xls");
		et.createNewRow();
		et.createCell("1111");
		et.createCell("aaaa");
		et.createCell("a1");
		et.createCell("a2a2");
		et.createNewRow();
		et.createCell("22222");
		et.createCell("cccc");
		et.createCell("a1");
		et.createCell("a2a2");
		et.createNewRow();
		et.createCell("3333");
		et.createCell(123);
		et.createCell("a1");
		et.createCell(new Date());
		et.createNewRow();
		et.createCell("4444");
		et.createCell("eeee");
		et.createCell("a1");
		et.createCell("a2a2");
		Map<String, String> map = new HashMap<String, String>();
		map.put("title", "测试用例标题");
		map.put("date", new Date().toString());
		map.put("dept", "运维产品开发部");
		et.replaceFinalData(map);
		et.insertSer();
		et.writeToFile("C:/Users/Think/Desktop/test01.xls");
	}

	@Test
	public void test02() {
		List<Order> orders = new ArrayList<Order>();
//		orders.add(new Order("11111", new Date()));
//		orders.add(new Order("22222", new Date()));
//		orders.add(new Order("33333", new Date()));
//		orders.add(new Order("44444", new Date()));
		Map<String, String> datas = new HashMap<String, String>();
		datas.put("title", "测试");
		datas.put("date", new Date().toString());
		datas.put("dept", "tttttt");

		try {
			ExcelUtil.getInstance().exportObj2ExcelByTemplate(datas,
					"/excel.template/dufault.xls", "C:/Users/Think/Desktop/test.xls", orders,
					Order.class, true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRead() {
		try {
			List<Object> os = ExcelUtil.getInstance().readExcel2ObjsByPath(
					"C:/Users/Think/Desktop/test.xls", Order.class, 1, 2);
			for (Object o : os) {
				System.out.println(o);
			}
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
