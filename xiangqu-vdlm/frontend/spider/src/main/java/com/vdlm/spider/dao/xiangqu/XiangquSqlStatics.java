/**
 * 
 */
package com.vdlm.spider.dao.xiangqu;

/**
 * <pre>
 * 禁止格式化此类
 * </pre>
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:58:14 AM Aug 11, 2014
 */
public interface XiangquSqlStatics {

	// 插入shop
	String INSERT_SHOP =
	"INSERT INTO product_shop (\n" +
	"	id,\n" +
	"	category,\n" +
	"	name,\n" +
	"	nick,\n" +
	"	url,\n" +
	"	score,\n" +
	"	last_get_time,\n" +
	"	taobao_time\n" +
	")\n" +
	"VALUES\n" +
	"	(?,?,?,?,?,?,?,?)";
	
	// 更新shop
	String UPDATE_SHOP =
	"UPDATE product_shop\n" +
	"SET name =?, nick =?, score =?, last_get_time =?\n" +
	"WHERE\n" +
	"	id =?";
	
	// 插入item
	String INSERT_ITEM =
	"INSERT INTO product_pool (\n" +
	"	shop_id,\n" +
	"	third_id,\n" +
	"	title,\n" +
	"	price,\n" +
	"	url,\n" +
	"	image,\n" +
	"	taobao_time,\n" +
	"   description\n" +
	")\n" +
	"VALUES\n" +
	"	(?,?,?,?,?,?,?,?)";
	
	// 更新item
	String UPDATE_ITEM =
	"UPDATE product_pool\n" +
	"SET title =?, price =?, url =?, image =?, taobao_time =?\n" +
	"WHERE\n" +
	"	shop_id =?\n" +
	"AND third_id =?";
	
	// 更新item状态
	String UPDATE_ITEM_STATE =
	"UPDATE product_pool\n" +
	"SET state =?, taobao_time =?\n" +
	"WHERE\n" +
	"	shop_id =?\n" +
	"AND third_id =?";
}
