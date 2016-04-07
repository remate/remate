package com.vdlm.spider.htmlparser;

import org.htmlparser.tags.CompositeTag;

public class UlTag extends CompositeTag {

	private static final long serialVersionUID = 2357136700951236373L;

	private static final String mIds[] = { "ul" };
	private static final String mEndTagEnders[] = { "ul" };

	public UlTag() {
	}

	public String[] getIds() {
		return mIds;
	}

	public String[] getEndTagEnders() {
		return mEndTagEnders;
	}
}
