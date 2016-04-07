package com.vdlm.spider.htmlparser;

import org.htmlparser.tags.CompositeTag;

public class H1Tag extends CompositeTag {

	private static final long serialVersionUID = -3148986160681735797L;

	private static final String mIds[] = { "h1" };
	private static final String mEndTagEnders[] = { "h1" };

	public H1Tag() {
	}

	public String[] getIds() {
		return mIds;
	}

	public String[] getEndTagEnders() {
		return mEndTagEnders;
	}
}
