package com.vdlm.spider.htmlparser;

import org.htmlparser.tags.CompositeTag;

public class DtTag extends CompositeTag {

	private static final long serialVersionUID = -9109780791315898612L;
	
	private static final String mIds[] = {"dt"};
	private static final String mEndTagEnders[] = {"dt"};

	public DtTag() {
	}

	public String[] getIds() {
		return mIds;
	}
	public String[] getEndTagEnders() {
		return mEndTagEnders;
	}
}
