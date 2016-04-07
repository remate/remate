package com.vdlm.spider.htmlparser;

import org.htmlparser.tags.CompositeTag;

public class EmTag extends CompositeTag {

	private static final long serialVersionUID = 4366170532070319367L;

	private static final String mIds[] = { "em" };
	private static final String mEndTagEnders[] = { "em" };

	public EmTag() {
	}

	public String[] getIds() {
		return mIds;
	}

	public String[] getEndTagEnders() {
		return mEndTagEnders;
	}
}
