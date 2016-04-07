/**
 * 
 */
package com.vdlm.spider.bean;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.util.ThreadLocalCache;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:00:41 AM Jul 30, 2014
 */
public class SmsMessageTaskBean implements Serializable {

	private static final long serialVersionUID = 3730030601247089154L;

	private String phone;
	private String content;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public static SmsMessageTaskBean parse(byte[] bytes) {

		return JSON.parseObject(bytes, 0, bytes.length, ThreadLocalCache.getUTF8Decoder(), SmsMessageTaskBean.class);
	}

}
