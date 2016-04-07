package com.vdlm.spider.bean.parial;

import java.util.List;

import com.vdlm.spider.bean.ItemTaskBean;

/**
 *
 * @author: chenxi
 */

public class PItemFieldsTaskBean extends ItemTaskBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5364645043271004664L;

	private List<String> fields;

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
}
