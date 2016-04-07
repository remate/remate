package com.vdlm.spider.htmlparser;

import org.htmlparser.tags.CompositeTag;

public class H3Tag extends CompositeTag {

	private static final long serialVersionUID = 5565185380454969349L;

	private static final String mIds[] = { "h3" };
	private static final String mEndTagEnders[] = { "h3" };

	public H3Tag() {
	}

	public String[] getIds() {
		return mIds;
	}

	public String[] getEndTagEnders() {
		return mEndTagEnders;
	}
}
