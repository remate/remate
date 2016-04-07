package com.vdlm.spider.bean.parial;

import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.task.TaskType;

/**
 *
 * @author: chenxi
 */

public class PItemGroupImgTaskBean extends ItemTaskBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7744513399712827016L;

	public PItemGroupImgTaskBean() {
		setTaskType(TaskType.MANUALLY);
	}
	
	@Override
	public String getTaskName() {
		return "partial_item_group_img";
	}
}
