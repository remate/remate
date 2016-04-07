/**
 * 
 */
package com.vdlm.spider.parser.config;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 12:45:32 PM Jul 18, 2014
 */
public class ParserConfig implements Serializable {
	private static final long serialVersionUID = -3669975488063458697L;
	private String index;
	private String prop;
	private String tag;
	private String attrsName;
	private String attrsValue;
	private String extractAttr;
	private Boolean isChild;
	private String extractKw;

	public String getExtractKw() {
		return extractKw;
	}

	public void setExtractKw(String extractKw) {
		this.extractKw = extractKw;
	}

	public Boolean getIsChild() {
		return isChild;
	}

	public void setIsChild(Boolean isChild) {
		this.isChild = isChild;
	}

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getAttrsName() {
		return attrsName;
	}

	public void setAttrsName(String attrsName) {
		this.attrsName = attrsName;
	}

	public String getAttrsValue() {
		return attrsValue;
	}

	public void setAttrsValue(String attrsValue) {
		this.attrsValue = attrsValue;
	}

	public String getExtractAttr() {
		return extractAttr;
	}

	public void setExtractAttr(String extractAttr) {
		this.extractAttr = extractAttr;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] { this.prop, this.tag, this.attrsName, this.attrsValue, this.extractAttr,
				this.isChild });
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != this.getClass()) {
			return false;
		}

		final ParserConfig that = (ParserConfig) obj;
		if (!StringUtils.equals(this.prop, that.getProp())) {
			return false;
		}
		if (this.tag != that.getTag()) {
			return false;
		}
		if (!StringUtils.equals(this.attrsName, that.getAttrsName())) {
			return false;
		}
		if (!StringUtils.equals(this.attrsValue, that.getAttrsValue())) {
			return false;
		}
		if (!StringUtils.equals(this.extractAttr, that.getExtractAttr())) {
			return false;
		}
		if (this.isChild == null && that.getIsChild() != null) {
			return false;
		}
		if (this.isChild != null && that.getIsChild() == null) {
			return false;
		}
		if (this.isChild.booleanValue() != that.getIsChild().booleanValue()) {
			return false;
		}
		return true;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
}
