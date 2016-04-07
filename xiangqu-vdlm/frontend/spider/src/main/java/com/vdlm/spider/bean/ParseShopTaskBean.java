/**
 * 
 */
package com.vdlm.spider.bean;

import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.util.ThreadLocalCache;
import com.vdlm.spider.ShopType;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 6:41:55 PM Jul 16, 2014
 */
public class ParseShopTaskBean extends TaskBean {

	private static final long serialVersionUID = 1188390789510416002L;

	private String ouerUserId;
	private String ouerShopId;

	private ShopType shopType;
	private String shopUrl;
	private String shopName;

	private String requestUrl;
	private String refererUrl;
	
	private String nickname;
	private String tUserId;

	// 级数 
	private int level = 0;

	// itemlist /////////////

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getNickname() {
		return nickname;
	}

	public int getLevel() {
		return level;
	}

	public int increaseLevelAndGet() {
		return ++level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public ShopType getShopType() {
		return shopType;
	}

	public void setShopType(ShopType shopType) {
		this.shopType = shopType;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getRefererUrl() {
		return refererUrl;
	}

	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}

	public String getOuerUserId() {
		return ouerUserId;
	}

	public void setOuerUserId(String ouerUserId) {
		this.ouerUserId = ouerUserId;
	}

	public String getOuerShopId() {
		return ouerShopId;
	}

	public void setOuerShopId(String ouerShopId) {
		this.ouerShopId = ouerShopId;
	}

	public String getShopUrl() {
		return shopUrl;
	}

	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}
	
	public String getTUserId(){
		return tUserId;
	}
	
	public void setTUserId(String tUserId){
		this.tUserId = tUserId;
	}

	@Override
	public ParseShopTaskBean clone() {
		final ParseShopTaskBean bean = new ParseShopTaskBean();
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

	public static ParseShopTaskBean parse(byte[] bytes) {

		return JSON.parseObject(bytes, 0, bytes.length, ThreadLocalCache.getUTF8Decoder(), ParseShopTaskBean.class);
	}
}
