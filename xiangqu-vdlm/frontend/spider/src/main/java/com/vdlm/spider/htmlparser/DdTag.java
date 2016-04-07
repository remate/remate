package com.vdlm.spider.htmlparser;

import org.htmlparser.tags.CompositeTag;

public class DdTag extends CompositeTag {

	private static final long serialVersionUID = -405005382398648473L;
	private static final String mIds[] = { "dd" };
	private static final String mEndTagEnders[] = { "dd" };

	public DdTag() {
	}

	public String[] getIds() {
		return mIds;
	}

	public String[] getEndTagEnders() {
		return mEndTagEnders;
	}
}
