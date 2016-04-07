package com.vdlm.spider.bean.parial;

import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.task.TaskType;

/**
 *
 * @author: chenxi
 */

public class PItemDescTaskBean extends ItemTaskBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7153519246702813347L;

	public PItemDescTaskBean() {
		setTaskType(TaskType.MANUALLY);
	}
	
	@Override
	public String getTaskName() {
		return "partial_item_desc";
	}
}
