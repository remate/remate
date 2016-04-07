/**
 * 
 */
package com.vdlm.spider.bean;

import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.util.ThreadLocalCache;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 6:41:55 PM Jul 16, 2014
 */
public class ParseShopInfoBean extends ParseShopTaskBean {

	private static final long serialVersionUID = -6726651114846615302L;

	private String rnd;
	private String mobilePhone;
	private String itemId;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getRnd() {
		return rnd;
	}

	public void setRnd(String rnd) {
		this.rnd = rnd;
	}

	@Override
	public ParseShopInfoBean clone() {
		final ParseShopInfoBean bean = new ParseShopInfoBean();
		BeanUtils.copyProperties(this, bean);
		return bean;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public byte[] toJSONBytes() {
		SerializeWriter out = new SerializeWriter();

		try {
			JSONSerializer serializer = new JSONSerializer(out);

			serializer.write(this);

			return out.toBytes("UTF-8");
		}
		finally {
			out.close();
		}
	}

	public static ParseShopInfoBean parse(byte[] bytes) {

		return JSON.parseObject(bytes, 0, bytes.length, ThreadLocalCache.getUTF8Decoder(), ParseShopInfoBean.class);
	}
}
