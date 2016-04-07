package com.vdlm.spider.bean.parial;

import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.task.TaskType;

/**
 *
 * @author: chenxi
 */

public class PItemSkusTaskBean extends ItemTaskBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7082057394801225767L;

	public PItemSkusTaskBean() {
		setTaskType(TaskType.MANUALLY);
	}
	
	@Override
	public String getTaskName() {
		return "partial_item_skus";
	}
}
