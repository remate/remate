package com.vdlm.spider.htmlparser;

import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.ParserFeedback;

public class HtmlParser extends Parser {

	private static final long serialVersionUID = 3789550098478877205L;

	private static PrototypicalNodeFactory factory = null;

	// 注册自定义标签
	static {
		factory = new PrototypicalNodeFactory();
		factory.registerTag(new DtTag());
		factory.registerTag(new H1Tag());
		factory.registerTag(new H3Tag());
		factory.registerTag(new UlTag());
		factory.registerTag(new LiTag());
		factory.registerTag(new EmTag());
		factory.registerTag(new StrongTag());
		factory.registerTag(new IframeTag());
		factory.registerTag(new DdTag());
	}

	public HtmlParser() {
		super();
		setNodeFactory(factory);
	}

	public HtmlParser(Lexer lexer, ParserFeedback fb) {
		super(lexer, fb);
		setNodeFactory(factory);
	}

}
