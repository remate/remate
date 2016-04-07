/**
 * 
 */
package com.vdlm.spider.bean;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.util.ThreadLocalCache;
import com.vdlm.spider.DeviceType;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:00:41 AM Jul 30, 2014
 */
public class PushMessageTaskBean implements Serializable {

	private static final long serialVersionUID = -5523283623468210424L;
	private String title;
	private String content;
	private String img;
	private DeviceType deviceType;
	private String userId;

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public static PushMessageTaskBean parse(byte[] bytes) {

		return JSON.parseObject(bytes, 0, bytes.length, ThreadLocalCache.getUTF8Decoder(), PushMessageTaskBean.class);
	}

}
