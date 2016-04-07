package com.vdlm.spider.htmlparser;

import org.htmlparser.tags.CompositeTag;

public class LiTag extends CompositeTag {

	private static final long serialVersionUID = -125898931330606683L;

	private static final String mIds[] = { "li" };
	private static final String mEndTagEnders[] = { "li" };

	public LiTag() {
	}

	public String[] getIds() {
		return mIds;
	}

	public String[] getEndTagEnders() {
		return mEndTagEnders;
	}
}
