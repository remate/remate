package com.vdlm.spider.htmlparser;

import org.htmlparser.tags.CompositeTag;

public class IframeTag extends CompositeTag {

	private static final long serialVersionUID = -1638130636375546219L;
	private static final String mIds[] = { "iframe" };
	private static final String mEndTagEnders[] = { "iframe" };

	public IframeTag() {
	}

	public String[] getIds() {
		return mIds;
	}

	public String[] getEndTagEnders() {
		return mEndTagEnders;
	}
}
