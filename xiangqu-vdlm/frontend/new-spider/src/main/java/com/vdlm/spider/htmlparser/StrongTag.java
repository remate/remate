package com.vdlm.spider.htmlparser;

import org.htmlparser.tags.CompositeTag;

public class StrongTag extends CompositeTag {

	private static final long serialVersionUID = -1270805994777037139L;
	private static final String mIds[] = { "strong" };
	private static final String mEndTagEnders[] = { "strong" };

	public StrongTag() {
	}

	public String[] getIds() {
		return mIds;
	}

	public String[] getEndTagEnders() {
		return mEndTagEnders;
	}
}
